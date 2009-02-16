(ns mire.player
  (:use clojure.contrib.seq-utils))

(def *current-room*)
(def *inventory*)
(def *player-name*)

(defn carrying?
  [thing]
  (includes? @*inventory* (keyword thing)))