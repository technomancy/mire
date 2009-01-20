(ns mire.commands
  (:use [mire player])
  (:use [clojure.contrib str-utils]))

(def commands {"north" (fn [] (move :north))
               "south" (fn [] (move :south))
               "east" (fn [] (move :east))
               "west" (fn [] (move :west))

               "look" look
               "inventory" inventory
               "take" take-thing
               "drop" drop-thing

               ;; String values are aliases.
               "get" "take"
               "discard" "drop"

               ;; for debugging
               "who" (fn [&rest args] *name*)

               "n" "north"
               "s" "south"
               "e" "east"
               "w" "west"
               "l" "look"
               "i" "inventory"})

(def unknown-responses ["What did you say?"
                        "I don't get it."
                        "Please rephrase that."
                        "Your words confuse me."])

(defn parse-input [input]
  ;; TODO: remove no-op words like "the" "and" etc.
  (re-split #"\s+" input))

(defn execute [input]
  ;; TODO: destructure into words split by space using re-matches
  (let [input-list (parse-input input)
        command (commands (first input-list))
        args (rest input-list)]
    (if command
      (if (string? command)
        (apply (commands command) args) ; look up aliased command
        (apply command args))
      ;; should be some kind of "pick-rand" function for this, no?
      (unknown-responses (rand-int (count unknown-responses))))))
