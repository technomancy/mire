#!/usr/bin/env clj

(ns mire
  (:use [mire commands player rooms items])
  (:use [clojure.contrib duck-streams server-socket])
  (:import [java.io InputStreamReader OutputStreamWriter]
           [clojure.lang LineNumberingPushbackReader]))

(def *port* 3333)

(defn- mire-handle-client [ins outs]
  (binding [*in* (LineNumberingPushbackReader. (InputStreamReader. ins))
            *out* (OutputStreamWriter. outs)
            *name* (read-name)
            *inventory* (ref [])
            *current-room* (ref (@mire.rooms/*rooms* :start))]
    (welcome-player)
    (try
     (loop [input (read-line)]
       (when input
         (println (execute input))
         (print prompt) (flush)
         (recur (read-line))))
     (finally
      (cleanup-player)))))

(try
 (def *server* (create-server *port* mire-handle-client))
 (catch java.net.BindException _
   (close-server *server*) ;; TODO: do this only if *server* is bound
   (def *server* (create-server *port* mire-handle-client))))