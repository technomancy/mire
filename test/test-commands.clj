(ns mire.test-commands
  (:use [mire commands player])
  (:use [clojure.contrib test-is seq-utils]))

(defn init []
  (dosync (ref-set mire.rooms/*rooms* {}))
  (mire.rooms/load-rooms (str (.getParent (java.io.File. *file*)) "/../data/rooms/")))

(deftest test-execute
  (init)
  ;; TODO: DRY up this binding etc.
  (binding [*current-room* (ref (:start @mire.rooms/*rooms*))
            *inventory* (ref [])
            *name* "Tester"]
    (is (includes? unknown-responses (execute "do something funky")))
    ;; too many args
    (is (= "You can't do that." (execute "drop a can of beans into the fridge")))
    (is (re-find #"closet" (execute "north")))
    (is (= @*current-room* (:closet @mire.rooms/*rooms*)))))

(deftest test-look
  (init)
  (binding [*current-room* (ref (:closet @mire.rooms/*rooms*))
            *inventory* (ref [])
            *name* "Tester"]
    (doseq [look-for [#"closet" #"keys" #"south"]]
      (is (re-find look-for (look))))))

(deftest test-inventory
  (init)
  (binding [*current-room* (ref (:closet @mire.rooms/*rooms*))
            *inventory* (ref [:keys :bunny])
            *name* "Tester"]
    (is (re-find #"bunny" (inventory)))
    (is (re-find #"keys" (inventory)))))

(deftest test-move
  (init)
  (binding [*current-room* (ref (:start @mire.rooms/*rooms*))
            *inventory* (ref [])
            *name* "Tester"]
    (is (re-find #"hallway" (execute "south")))
    (is (re-find #"promenade" (move "east")))
    (is (re-find #"can't go that way" (move "south")))))

(deftest test-grab
  (init)
  (binding [*current-room* (ref (:closet @mire.rooms/*rooms*))
            *inventory* (ref [])]
    (is (not (= "There isn't any keys here"
                (grab "keys"))))
    (is (inventory-contains? :keys))
    (is (empty? @(@*current-room* :items)))))

(deftest test-discard
  (init)
  (binding [*current-room* (ref (:closet @mire.rooms/*rooms*))
            *inventory* (ref [:bunny])]
    (is (re-find #"dropped" (discard "bunny")))
    (is (not (inventory-contains? "bunny")))
    (is (mire.rooms/room-contains? @*current-room* "bunny"))))

(deftest test-say
  ;; Crap; this one is going to be hard to test!
  )

(run-tests)