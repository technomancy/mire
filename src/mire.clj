#!/usr/bin/env clj

(ns mire
  (:use [clojure.contrib server-socket duck-streams]))

(def port (* 3 1111))

(defn mire-handle-client [in out]
  (binding [*in* (reader in)
            *out* (writer out)]
    (loop []
      (println (read-line))
      (recur))))

(def server (create-server port mire-handle-client))
