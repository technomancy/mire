(ns mire.commands
  (:use [clojure.contrib str-utils]))

(defn current-time []
     (str "It is now "(java.util.Date.)))

(def commands {"time" current-time,
               "look" (fn [] "You see an empty room, waiting to be filled.")})

(defn execute
  "Execute a command that is passed to us."
  [input]
  (let [input-words (re-split #"\s+" input)
        command (first input-words)
        args (rest input-words)]
    (apply (commands command) args)))