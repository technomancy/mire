(ns mire.player)

(def *current-room*)
(def *inventory*)
(def *player-name*)

(def prompt "> ")
(def player-streams (ref {}))

(defn carrying?
  [thing]
  (some #{(keyword thing)} @*inventory*))
