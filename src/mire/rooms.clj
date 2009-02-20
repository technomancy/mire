(ns mire.rooms)

(def rooms)

(defn load-room [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(keyword (.getName file))
           {:desc (:desc room)
            :exits (ref (:exits room))
            :inhabitants (ref #{})}})))

(defn load-rooms [dir]
  (def rooms (reduce load-room {}
                     (.listFiles (java.io.File. dir)))))

(def *current-room*)
(def player-name)