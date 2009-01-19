(ns mire.rooms)

(def *rooms* (ref {}))

(defn room-contains? [room thing-name]
  (not (empty?
        (filter #(= % (keyword thing-name))
                @(room :items)))))

(defn make-room
  ([name desc exits items]
     (dosync
      (commute *rooms* conj
               {name {:name name :desc desc :exits (ref exits)
                      :items (ref items) :inhabitants (ref []) }})))
  ([name desc exits]
     (make-room name desc exits [])))

(make-room :start
           "You find yourself in a round room with a pillar in the middle."
           {:north :closet
            :south :hallway})

(make-room :closet
           "You are in a cramped closet."
           {:south :start}
           [:keys])

(make-room :hallway
           "You are in a long, low-lit hallway that turns to the east."
           {:north :start
            :east :promenade})

(make-room :promenade
           "The promenade stretches out before you."
           {:west :hallway}
           [:bunny])