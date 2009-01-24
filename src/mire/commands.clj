(ns mire.commands
  (:use [mire player rooms util])
  (:use [clojure.contrib str-utils seq-utils]))

;; Command functions

(defn look []
  (let [room @*current-room*]
    (str (:desc room) "\n"
         (look-exits room)
         (look-items room)
         (look-inhabitants room))))

(defn inventory []
  (if (empty? @*inventory*)
    "You are not carrying anything."
    (str-join "\n" (map #(:desc (*items* %)) @*inventory*))))

(defn move [direction]
  (let [target-name (@(:exits @*current-room*) direction)
        target (@*rooms* target-name)]
    (if target-name
      (dosync
       (move-between-refs *name*
                          (:inhabitants @*current-room*)
                          (:inhabitants target))
       (ref-set *current-room* target)
       (look))
      "You can't go that way.")))

(defn grab [thing]
  (dosync
   (if (room-contains? @*current-room* thing)
     (do (move-between-refs (keyword thing)
                            (:items @*current-room*)
                            *inventory*)
         (str "You picked up the " thing "."))
     (str "There isn't any " thing " here."))))

(defn discard [thing]
  (dosync
   (if (inventory-contains? thing)
     (do (move-between-refs (keyword thing)
                            *inventory*
                            (:items @*current-room*))
         (str "You dropped up the " thing "."))
     (str "You don't have a " thing "."))))

(defn say [words]
  ;; TODO: write this
  ;; I guess we need to have the *players* var become a map of player
  ;; names to streams so we can use those streams to send stuff to folks
  )

;; Command data

(def commands {"move" (fn [dir] (move (keyword dir)))
               "north" (fn [] (move :north))
               "south" (fn [] (move :south))
               "east" (fn [] (move :east))
               "west" (fn [] (move :west))

               "look" look
               "inventory" inventory
               "grab" grab
               "discard" discard

               ;; aliases
               "go" move
               "get" grab
               "take" grab
               "drop" discard

               ;; for debugging
               "who" (fn [&rest args] *name*)

               "l" look
               "i" inventory})

(def unknown-responses ["What did you say?"
                        "I don't get it."
                        "Please rephrase that."
                        "Your words confuse me."])

(def ignored-words ["and" "a" "an" "the" "please"])

;; Command handling

(defn parse-input [input]
  "Split input string into words and skip"
  (remove #(includes? ignored-words %) (re-split #"\s+" input)))

(defn execute [input]
  (let [[command & args] (parse-input input)]
    (if command
      (apply (commands command) args)
      (pick-rand unknown-responses))))