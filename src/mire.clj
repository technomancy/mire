(ns mire
  (:use [mire player rooms items]
        [clojure.contrib duck-streams]))

(def prompt "> ")

(def port 3333)

(def commands {"north" (fn [] (move :north))
               "south" (fn [] (move :south))
               "east" (fn [] (move :east))
               "west" (fn [] (move :west))

               "look" look
               ;; "inventory" inventory
               "take" take-thing
               "drop" drop-thing

               ;; String values are aliases.
               "get" "take"
               "n" "north"
               "s" "south"
               "e" "east"
               "w" "west"
               "l" "look"
               "i" "inventory"})

(def unknown-responses ["What did you say?"
                        "I don't get it."
                        "Please rephrase that."
                        "Your words confuse me."])

(defn execute [input]
  (let [command (commands input)]
    (if command
      ;; TODO: support aliased commands (ones that are strings)
      ;; TODO: pass args to command
      (command)
      (unknown-responses (rand-int (count unknown-responses))))))

(defn repl [in out]
  (binding [*out* (java.io.OutputStreamWriter. out)
            *in* (reader in)]
    (print prompt) (flush)
    (try (io! (loop [input (read-line)]
                (println (execute input))
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
;; (if (and (find-var 'mire/server)
;;          (.isClosed mire/server))
;;   (.close mire/server))

(def server (create-server
             (fn [socket] (.start (Thread.
                                  #(repl (.getInputStream socket)
                                         (.getOutputStream socket))))) port))

;;; (telnet "localhost" 3333