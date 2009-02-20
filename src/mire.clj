#!/usr/bin/env clj

(add-classpath (str "file://" (.getParent (java.io.File. *file*)) "/"))

(ns mire
  (:use [mire commands rooms])
  (:use [clojure.contrib server-socket duck-streams]))

(def port 3333)
(def prompt "> ")

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

(load-rooms (str (.getParent (java.io.File. *file*)) "/../data/rooms/"))
(def server (create-server port mire-handle-client))