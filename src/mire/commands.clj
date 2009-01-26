(ns mire.commands
  (:use [mire player rooms util])
  (:use [clojure.contrib str-utils seq-utils]))

(declare commands)

;; Command functions

(defn look "Get a description of the surrounding environs and its contents."
  []
  (let [room @*current-room*]
    (str (:desc room) "\n"
         (look-exits room)
         (look-items room)
         (look-inhabitants room))))

(defn inventory
  "What are we carrying? Let's take a look."
  []
  (if (empty? @*inventory*)
    "You are not carrying anything."
    (str-join "\n" (map #(:desc (*items* %)) @*inventory*))))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
  (let [target-name (@(:exits @*current-room*) (keyword direction))
        target (@*rooms* target-name)]
    (if target-name
      (dosync
       (move-between-refs *name*
                          (:inhabitants @*current-room*)
                          (:inhabitants target))
       (ref-set *current-room* target)
       (look))
      "You can't go that way.")))

(defn grab
  "Pick something up."
  [thing]
  (dosync
   (if (room-contains? @*current-room* thing)
     (do (move-between-refs (keyword thing)
                            (:items @*current-room*)
                            *inventory*)
         (str "You picked up the " thing "."))
     (str "There isn't any " thing " here."))))

(defn discard
  "Put something down."
  [thing]
  (dosync
   (if (inventory-contains? thing)
     (do (move-between-refs (keyword thing)
                            *inventory*
                            (:items @*current-room*))
         (str "You dropped up the " thing "."))
     (str "You don't have a " thing "."))))

(defn say
  "Speak some words so that they can be heard by everyone within earshot."
  [& words]
  (let [string (str "\"" (str-join " " words) "\"")]
    (doseq [player @(:inhabitants @*current-room*)]
      (if (not (= player *name*))
        (binding [*out* (*players* player)]
          (println *name* "says:" string)
          (print prompt) (flush))))
    (str "You say: " string)))

(defn help
  "Print an explaination of available commands."
  []
  ;; TODO: remove non-commands from output
  (str-join "\n" (map #(str (key %) ": " (:doc (meta (val %))))
                      (ns-publics 'mire.commands))))

;; Command data

(def commands {"move" move
               "north" (fn [] (move :north))
               "south" (fn [] (move :south))
               "east" (fn [] (move :east))
               "west" (fn [] (move :west))

               "look" look
               "inventory" inventory
               "grab" grab
               "discard" discard
               "say" say
               "help" help

               ;; aliases
               "speak" say
               "go" move
               "get" grab
               "take" grab
               "drop" discard

               ;; for debugging
               "who" (fn [& args] *name*)

               "l" look
               "i" inventory})

(def unknown-responses ["What you say?" "Speak up!" "I don't get it."
                        "Please rephrase that." "Your words confuse me."])

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (let [[command & args] (re-split #"\s+" input)]
    (try
     (apply (commands (.toLowerCase command)) args)
     (catch java.lang.NullPointerException _
       (pick-rand unknown-responses))
     (catch java.lang.IllegalArgumentException _
       "You can't do that."))))