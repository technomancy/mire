#!/usr/bin/env clj

(ns mire
  (:use [mire commands rooms])
  (:use [clojure.contrib server-socket duck-streams]))

(def port 3333)
(def prompt "> ")
(def player-name)

(defn- mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)]

    ;; We have to nest this in another binding call instead of using
    ;; the one above so *in* and *out* will be bound to the socket
    (print "\nWhat is your name? ") (flush)
    (binding [player-name (read-line)
              *current-room* (ref (rooms :start))]
      (dosync (alter (:inhabitants @*current-room*) conj player-name))

      (println (look)) (print prompt) (flush)

      (loop [input (read-line)]
        (println (execute input))
        (print prompt)
        (flush)
        (recur (read-line))))))

(def server (create-server port mire-handle-client))