(ns mire.player
  (:use [clojure.contrib.seq-utils]))

(def *current-room*)
(def *inventory*)
(def *player-name*)

(def prompt "> ")
(def player-streams (ref {}))

(defn carrying?
  [thing]
  (includes? @*inventory* (keyword thing)))