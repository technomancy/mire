#!/usr/bin/env clj

;; (add-classpath (str "file://" (.getParent (java.io.File. *file*)) "/"))

(ns mire
  (:use [mire commands player rooms])
  (:use [clojure.contrib server-socket])
  (:import [java.io InputStreamReader OutputStreamWriter]
           [clojure.lang LineNumberingPushbackReader]))

(def port 3333)

(defn cleanup []
  ;; This is factored out into its own function because doseq calls
  ;; are currently not allowed in finally blocks. (clojure bug?)
  (doseq [thing @*inventory*] (discard thing))
  (dosync (commute (:inhabitants @*current-room*)
                   (partial remove #(= % *name*)))))

(defn- mire-handle-client [ins outs]
  (binding [*in* (LineNumberingPushbackReader. (InputStreamReader. ins))
            *out* (OutputStreamWriter. outs)]
    ;; bindings doesn't work sequentially, so we need to nest them
    ;; otherwise the call to read-name uses the old value of *in*/*out*
    (binding [*name* (read-name)
              *inventory* (ref [])
              *current-room* (ref (@mire.rooms/*rooms* :start))]
      (welcome-player)
      (try
       (loop [input (read-line)]
         (when input
           (println (execute input))
           (print prompt) (flush)
           (recur (read-line))))
       (finally (cleanup))))))

(def *server* (create-server port mire-handle-client))