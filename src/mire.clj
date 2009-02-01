#!/usr/bin/env clj

(ns mire
  (:use [mire commands rooms])
  (:use [clojure.contrib server-socket duck-streams]))

(def port 3333)
(def prompt "> ")

(defn- mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)
            *current-room* (:start *rooms*)]
    (println (look))
    (print prompt) (flush)
    (loop [input (read-line)]
      (println (execute input))
      (print prompt)
      (flush)
      (recur (read-line)))))

(def server (create-server port mire-handle-client))