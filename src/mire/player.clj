(ns mire.player
  (:use [mire rooms]))

(def *current-room* (ref (@mire.rooms/*rooms* :start)))
(def *inventory* (ref []))

(defn look []
  (str (:desc @*current-room*) "\n"
       ;; TODO: show items
       "There are exits to the " (keys @(:exits @*current-room*))))

(defn move [direction]
  (let [target (direction @(:exits @*current-room*))]
    (if target
      (dosync (ref-set *current-room* (target @mire.rooms/*rooms*))
              (look))
      "You can't go that way.")))

(defn take-thing [thing]
  (dosync
   (if (room-contains? @*current-room* thing)
     (do (commute *inventory* conj (keyword thing))
         (alter (:items @*current-room*)
                (partial remove #(= % (keyword thing)))))
     (str "There isn't any " thing " here"))))

(defn drop-thing [thing])

(defn inventory []
  (map #(:desc (mire.items/*items* %)) @*inventory*))