#!/usr/bin/env clj

(ns mire.server
  (:use [mire commands rooms player])
  (:use [clojure.contrib server-socket duck-streams])
  (:gen-class))

(defn cleanup []
  "Drop all inventory and remove player from room and player list."
  (dosync
   (doseq [item @*inventory*]
     (discard item))
   (commute player-streams dissoc *player-name*)
   (commute (:inhabitants @*current-room*)
            disj *player-name*)))

(defn get-unique-player-name [name]
  (if (@player-streams name)
    (do (print "That name is in use; try again: ")
        (flush)
        (recur (read-line)))
    name))

(defn- mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)]

    ;; We have to nest this in another binding call instead of using
    ;; the one above so *in* and *out* will be bound to the socket
    (print "\nWhat is your name? ") (flush)
    (binding [*player-name* nil
              *current-room* (ref (rooms :start))
              *inventory* (ref #{})]
      (dosync
       (set! *player-name* (get-unique-player-name (read-line)))
       (commute (:inhabitants @*current-room*) conj *player-name*)
       (commute player-streams assoc *player-name* *out*))

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