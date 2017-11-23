(ns mire.player)

(def ^:dynamic *current-room*)
(def ^:dynamic *inventory*)
(def ^:dynamic *player-name*)
(def ^:dynamic *health*)
(def ^:dynamic *score*)
(def ^:dynamic *status*)
(def ^:dynamic *money*)
(def ^:dynamic *armor*)
(def ^:dynamic *weapon*)
(def ^:dynamic *last-message*)

(def prompt "> ")
(def player-streams (ref {}))
(def players-stats (ref {}))

(defn carrying?
  [thing]
  (some #{(keyword thing)} @*inventory*))
