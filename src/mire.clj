#!/usr/bin/env clj

(ns mire
  (:use [clojure.contrib server-socket])
  (:import [java.io InputStreamReader OutputStreamWriter]
           [clojure.lang LineNumberingPushbackReader]))

(def port 3333)

(defn- mire-handle-client [ins outs]
  (binding [*in* (LineNumberingPushbackReader. (InputStreamReader. ins))
            *out* (OutputStreamWriter. outs)]
    (loop [input (read-line)]
      (println input)
      (recur (read-line)))))

(defonce *server* (create-server port mire-handle-client))