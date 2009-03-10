(ns mire.rooms)

(def rooms)
(def default-rooms-dir
     (str (.getParent (java.io.File. *file*)) "/../../data/rooms/"))

(defn load-room [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc room)
            :exits (ref (:exits room))
            :items (ref (or (:items room) #{}))
            :inhabitants (ref #{})}})))

(defn load-rooms
  ([dir]
     (def rooms (reduce load-room {}
                        (.listFiles (java.io.File. dir)))))
  ([] (load-rooms default-rooms-dir)))

(defn room-contains?
  [room thing]
  (@(:items room) (keyword thing)))