(ns mire.player
  (:use [mire items rooms])
  (:use [clojure.contrib seq-utils str-utils]))

(def prompt "> ")

(def *current-room*)
(def *inventory*)
(def *name*)

(defn look-exits [room]
  ;; TODO: need to un-intern the exit names here
  (str "There are exits to the "
       (str-join " and " (map name (keys @(:exits @*current-room*))))
       ".\n"))

(defn look-items [room]
  (str-join "\n" (map #(str "There is " (name %) " here.") @(:items room))))

(defn look-inhabitants [room]
  (str-join "\n" (map #(if (not (= % *name*)) (str % " is here."))
                      @(:inhabitants room))))

(defn look []
  (let [room @*current-room*]
    (str (:desc room) "\n"
         (look-exits room)
         (look-items room)
         (look-inhabitants room))))

(defn move [direction]
  (let [target-name (@(:exits @*current-room*) direction)
        target (@*rooms* target-name)]
    (if target-name
      (dosync
       (commute (:inhabitants target) conj *name*)
       (alter (:inhabitants @*current-room*)
              (partial remove #(= % *name*)))
       (ref-set *current-room* target)
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

(defn init-player []
  (def *current-room* (ref (@mire.rooms/*rooms* :start)))
  (def *inventory* (ref []))
  (print "\nWhat is your name? ") (flush)
  ;; TODO: ensure name is unique
  (def *name* (read-line))
  (println "Welcome to Mire, " *name* "\n")
  (println (look))
  (print prompt) (flush))

(defn cleanup-player
  "Drop a player's inventory and purge him from his room's inhabitants list."
  []
  (dosync
   (doall (map #(drop-thing (name %)) @*inventory*))
   (alter (:inhabitants @*current-room*)
          (partial remove #(= % *name*)))))