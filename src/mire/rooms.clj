(ns mire.rooms
  (:use [clojure.contrib str-utils duck-streams]))

(def rooms (ref {}))

(defn load-room [file]
  ;; TODO: this pushbackreader business is lame.
  (let [room (read (java.io.PushbackReader. (reader file)))]
    (dosync (commute rooms conj
                     {(keyword (.getName file))
                      {:desc (:desc room)
                       :exits (ref (:exits room))
                       :inhabitants (ref #{})}}))))

(defn load-rooms [dir]
  (map load-room (.listFiles (java.io.File. dir))))

(def *current-room*)
(def player-name)