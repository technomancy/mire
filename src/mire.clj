(ns mire
  (:use [mire commands player rooms items])
  (:use [clojure.contrib duck-streams server-socket])
  (:import [java.io InputStreamReader OutputStream OutputStreamWriter PrintWriter]
           [clojure.lang LineNumberingPushbackReader]))

(def *port* 3333)

;;; TODO: could the binding form get pushed up into create-server?
(defn- mire-loop [ins outs]
  (binding [*in* (LineNumberingPushbackReader. (InputStreamReader. ins))
            *out* (OutputStreamWriter. outs)
            *err* (PrintWriter. #^OutputStream outs true)]
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
       (finally
        (cleanup-player))))))

(try
 (def *server* (create-server *port* mire-loop))
 (catch java.net.BindException _
   (close-server *server*)
   (def *server* (create-server *port* mire-loop))))