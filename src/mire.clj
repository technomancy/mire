#!/usr/bin/env clj

(ns mire
  (:use [mire commands])
  (:use [clojure.contrib server-socket duck-streams]))

(def port 3333)
(def prompt "> ")

(defn- mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)]
    (print prompt) (flush)
    (loop [input (read-line)]
      (println (execute input))
      (print prompt)
      (flush)
      (recur (read-line)))))

(def server (create-server port mire-handle-client))