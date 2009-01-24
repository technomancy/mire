(ns mire.commands
  (:use [mire player])
  (:use [clojure.contrib str-utils seq-utils]))

(def commands {"move" (fn [dir] (move (keyword dir)))
               "north" (fn [] (move :north))
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
               "go" "move"

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

(def ignored-words ["and" "a" "an" "the" "please"])

(defn parse-input [input]
  ;; TODO: Would be cleaner with something like seq-xor.
  (filter #(not (includes? ignored-words %))
          (re-split #"\s+" input)))

(defn pick-rand [coll]
  ;; TODO: Shouldn't there be something in contrib for this?
  ;; Could be generalized for other seqs.
  (coll (rand-int (count coll))))

(defn execute [input]
  ;; TODO: destructure into words split by space using re-matches
  (let [input-list (parse-input input)
        command (commands (first input-list))
        args (rest input-list)]
    (if command
      (if (string? command)
        (apply (commands command) args) ; look up aliased command
        (apply command args))
      (pick-rand unknown-responses))))
