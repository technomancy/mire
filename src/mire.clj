(ns mire
  (:require [mire commands])
  (:use [mire player rooms items])
  (:use [clojure.contrib duck-streams]))

(def port 3333)

(defn repl [in out]
  (binding [*out* (java.io.OutputStreamWriter. out)
            *in* (reader in)]
    (init-game)
    (try (io! (loop [input (read-line)]
                (println (mire.commands/execute input))
                (print prompt) (flush)
                (recur (read-line))))
         (catch Exception e (prn e)))))

;; TODO: Update this to use server-sockets in clojure.contrib

(defn create-server
  "creates and returns a server socket on port, will pass the client
  socket to accepter on connection"
  [accepter port]
  (let [ss (java.net.ServerSocket. port)]
    (.start (Thread. #(accepter (.accept ss))))
    ss))

;; restart if applicable
(if (and (find-var 'mire/server)
         (not (.isClosed mire/server)))
  (.close mire/server))

(def server (create-server
             (fn [socket] (.start (Thread.
                                  #(repl (.getInputStream socket)
                                         (.getOutputStream socket))))) port))
