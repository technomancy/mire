(ns mire.commands
  (:use [mire rooms])
  (:use [clojure.contrib str-utils seq-utils]))

;; Command functions

(defn look "Get a description of the surrounding environs and its contents."
  []
  (str (:desc @*current-room*)
       "\nExits: " (keys (:exits @*current-room*))
       ".\n"))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
  (let [target-name ((:exits @*current-room*) (keyword direction))
        target (rooms target-name)]
    (if target
      (dosync (alter (:inhabitants @*current-room*) disj player-name)
              (alter (:inhabitants target) conj player-name)
              (ref-set *current-room* target)
              (look))
      "You can't go that way.")))

;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "look" look})

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (let [input-words (re-split #"\s+" input)
        command (first input-words)
        args (rest input-words)]
    (apply (commands command) args)))