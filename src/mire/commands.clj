(ns mire.commands
  (:use [mire rooms player util])
  (:use [clojure.contrib str-utils seq-utils]))

;; Command functions

(defn look "Get a description of the surrounding environs and its contents."
  []
  (str (:desc @*current-room*)
       "\nExits: " (keys @(:exits @*current-room*)) "\n"
       (str-join "\n" (map #(str "There is " % " here.\n")
                           @(:items @*current-room*)))))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
  (dosync
   (let [target-name ((:exits @*current-room*) (keyword direction))
         target (rooms target-name)]
     (if target
       (do
         (move-between-sets *player-name*
                            (:inhabitants @*current-room*)
                            (:inhabitants target))
         (ref-set *current-room* target)
         (look))
       "You can't go that way."))))

(defn grab
  "Pick something up."
  [thing]
  (dosync
   (if (room-contains? @*current-room* thing)
     (do (move-between-sets (keyword thing)
                            (:items @*current-room*)
                            *inventory*)
         (str "You picked up the " thing "."))
     (str "There isn't any " thing " here."))))

(defn discard
  "Put something down that you're carrying."
  [thing]
  (dosync
   (if (carrying? thing)
     (do (move-between-sets (keyword thing)
                            *inventory*
                            (:items @*current-room*))
         (str "You dropped the " thing "."))
     (str "You're not carrying a " thing "."))))

(defn inventory
  "See what you've got."
  []
  (str-join "\n  " (cons "You are carrying:" @*inventory*)))

;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "grab" grab
               "discard" discard
               "inventory" inventory
               "look" look})

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (let [input-words (re-split #"\s+" input)
        command (first input-words)
        args (rest input-words)]
     (apply (commands command) args)))