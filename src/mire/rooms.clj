(ns mire.rooms)

(def rooms (ref {}))

(defn load-room [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc room)
            :exits (ref (:exits room))
            :items (ref (or (:items room) #{}))
            :store (ref (or (:store room) #{}))
            :weapons (ref (or (:weapons room) #{})) ; Artur
            :armors (ref (or (:armors room) #{}))   ; Artur
            :inhabitants (ref #{})}})))

(defn load-rooms
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing room data."
  [rooms dir]
  (dosync
   (reduce load-room rooms
           (.listFiles (java.io.File. dir)))))

(defn add-rooms
  "Look through all the files in a dir for files describing rooms and add
  them to the mire.rooms/rooms map."
  [dir]
  (dosync
   (alter rooms load-rooms dir)))

(defn room-contains?
  [room thing]
  (@(:items room) (keyword thing)))

; Artur
(defn room-contains-weapon?
  [room thing]
  (@(:weapons room) (keyword thing)))

; Artur
(defn room-contains-armor?
  [room thing]
  (@(:armors room) (keyword thing)))
