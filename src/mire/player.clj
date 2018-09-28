(ns mire.player)

(def ^:dynamic *current-room*)
(def ^:dynamic *inventory*)
(def ^:dynamic *name*)

(def prompt "> ")
(def streams (ref {}))

(defn carrying? [thing]
  (some #{(keyword thing)} @*inventory*))
