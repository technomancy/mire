(ns mire.rooms)

(declare rooms)

(defn load-room [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc room)
            :exits (ref (:exits room))
            :items (ref (or (:items room) #{}))
            :inhabitants (ref #{})}})))

(defn load-rooms [dir]
  "Given a dir, return a map with an entry corresponding to each file
in it. Files should be maps containing room data."
  (reduce load-room {} (.listFiles (java.io.File. dir))))

(defn set-rooms
  "Set mire.rooms/rooms to a map of rooms corresponding to each file
  in dir. This function should be used only once at mire startup, so
  having a def inside the function body should be OK. Defaults to
  looking in data/rooms/."
  ([dir]
     (def rooms (load-rooms dir)))
  ([] (set-rooms "data/rooms/")))

(defn room-contains?
  [room thing]
  (@(:items room) (keyword thing)))
