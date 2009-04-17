#!/usr/bin/env clj

(ns mire.server
  (:use [mire commands rooms player])
  (:use [clojure.contrib server-socket duck-streams])
  (:gen-class))

(def prompt "> ")

(defn cleanup []
  "Drop all inventory and remove player from room and player list."
  (dosync
   (doseq [item @*inventory*]
     (discard item))
   (commute (:inhabitants @*current-room*)
            disj *player-name*)))

(defn- mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)]

    ;; We have to nest this in another binding call instead of using
    ;; the one above so *in* and *out* will be bound to the socket
    (print "\nWhat is your name? ") (flush)
    (binding [*player-name* (read-line)
              *current-room* (ref (rooms :start))
              *inventory* (ref #{})]
      (dosync (commute (:inhabitants @*current-room*) conj *player-name*))

      (println (look)) (print prompt) (flush)

      (try (loop [input (read-line)]
             (when input
               (println (execute input))
               (print prompt) (flush)
               (recur (read-line))))
           (finally (cleanup))))))

(defn -main
  ([rooms-dir port]
     (set-rooms rooms-dir)
     (defonce server (create-server (Integer. port) mire-handle-client))
     (println "Launching Mire server on port" port))
  ([rooms-dir] (-main rooms-dir 3333))
  ([] (-main "data/rooms")))