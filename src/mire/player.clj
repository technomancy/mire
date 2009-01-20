(ns mire.player
  (:use [mire items rooms])
  (:use [clojure.contrib seq-utils str-utils]))

(def prompt "> ")
(def *current-room*)
(def *inventory*)

(defn look-exits [room]
  ;; TODO: need to un-intern the exit names here
  (str "There are exits to the " (keys @(:exits @*current-room*)) "\n"))

(defn look-items [room]
  (str-join "\n" (map #(str "There is " % " here.") @(:items room))))

(defn look-inhabitants [room] "")

(defn look []
  (let [room @*current-room*]
    (str (:desc room) "\n"
         (look-exits room)
         (look-items room)
         (look-inhabitants room))))

(defn move [direction]
  (let [target (direction @(:exits @*current-room*))]
    (if target
      (dosync (ref-set *current-room* (target @mire.rooms/*rooms*))
              (look))
      "You can't go that way.")))

(defn inventory-contains? [thing]
  (not (empty?
        (filter #(= % (keyword thing))
                @*inventory*))))

(defn take-thing [thing]
  (dosync
   (if (room-contains? @*current-room* thing)
     (do (commute *inventory* conj (keyword thing))
         (alter (:items @*current-room*)
                (partial remove #(= % (keyword thing))))
         (str "You picked up the " thing "."))
     (str "There isn't any " thing " here."))))

(defn drop-thing [thing]
  (dosync
   (if (inventory-contains? thing)
     (do (commute (:items @*current-room*) conj (keyword thing))
         (alter *inventory*
                (partial remove #(= % (keyword thing))))
         (str "You dropped up the " thing "."))
     (str "You don't have a " thing "."))))

(defn inventory []
  (str-join "\n" (map #(:desc (mire.items/*items* %)) @*inventory*)))

(defn init-game []
  (print prompt) (flush)
  (def *current-room* (ref (@mire.rooms/*rooms* :start)))
  (def *inventory* (ref [])))