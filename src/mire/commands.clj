(ns mire.commands
  (:use [mire rooms util])
  (:use [clojure.contrib str-utils seq-utils]))

;; Command functions

(defn look "Get a description of the surrounding environs and its contents."
  []
  (str (:desc *current-room*)
       "\nExits: " (keys (:exits room))
       ".\n"))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
  (let [target-name ((:exits *current-room*) (keyword direction))
        target (rooms target-name)]
    (if target-name
      (do (set! *current-room* target)
          (look))
      "You can't go that way.")))

;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "look" look})

(def unknown-responses ["What you say?" "Speak up!" "I don't get it."
                        "Please rephrase that." "Your words confuse me."])

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (let [input-words (re-split #"\s+" input)
        command (first input-words)
        args (rest input-words)]
    (try
     (apply (commands command) args)
     (catch java.lang.NullPointerException _
       (pick-rand unknown-responses))
     (catch java.lang.IllegalArgumentException _
       "You can't do that."))))