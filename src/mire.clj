#!/usr/bin/env clj
;; Set the classpath using Hashdot: (http://hashdot.sf.net)
;;.java.class.path += /home/phil/src/mire/src/

(add-classpath (str "file://" (.getParent (java.io.File. *file*)) "/"))

(ns mire
  (:use [mire commands player rooms])
  (:use [clojure.contrib server-socket duck-streams]))

(def port 3333)

(defn welcome []
  (dosync (commute *players* conj {*name* *out*})
          (commute (:inhabitants @*current-room*) conj *name*))
  (println "Welcome to Mire, " *name* "\n")
  (println (look))
  (print prompt) (flush))

(defn cleanup []
  "Drop all inventory and remove player from room and player list."
  (doseq [thing @*inventory*] (discard thing))
  (dosync (commute *players* dissoc *name*)
          (commute (:inhabitants @*current-room*)
                   (partial remove #(= % *name*)))))

(defn- mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)]
    ;; bindings doesn't work sequentially, so we need to nest them
    ;; otherwise the call to read-name uses the old value of *in*/*out*
    (binding [*name* (read-name)
              *inventory* (ref [])
              *current-room* (ref (@mire.rooms/*rooms* :start))]
      (welcome)
      (try
       (loop [input (read-line)]
         (when input
           (println (execute input))
           (print prompt) (flush)
           (recur (read-line))))
       (finally (cleanup))))))

(load-rooms (str (.getParent (java.io.File. *file*)) "/../data/rooms/"))
(defonce server (create-server port mire-handle-client))