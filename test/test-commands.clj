(ns mire.test-commands
  (:use [mire commands player rooms] :reload-all)
  (:use [clojure.contrib test-is seq-utils]))

(def rooms-dir (str (.getParent (java.io.File. *file*)) "/../data/rooms/"))

(defmacro def-command-test [name & body]
  `(deftest ~name
     (load-rooms rooms-dir)
     (binding [*current-room* (ref (:start rooms))
               *inventory* (ref #{})
               *player-name* "Tester"]
       ~@body)))

(def-command-test test-execute
  (is (= "You can't do that!" (execute "drop a can of beans into the fridge")))
  (is (re-find #"closet" (execute "north")))
  (is (= @*current-room* (:closet rooms))))

(def-command-test test-move
  (is (re-find #"hallway" (execute "south")))
  (is (re-find #"promenade" (move "east")))
  (is (re-find #"can't go that way" (move "south"))))

(def-command-test test-look
  (binding [*current-room* (ref (:closet rooms))]
    (doseq [look-for [#"closet" #"keys" #"south"]]
    (is (re-find look-for (look))))))

(def-command-test test-inventory
  (binding [*inventory* (ref [:keys :bunny])]
    (is (re-find #"bunny" (inventory)))
    (is (re-find #"keys" (inventory)))))

(def-command-test test-grab
  (binding [*current-room* (ref (:closet rooms))]
    (is (not (= "There isn't any keys here"
                (grab "keys"))))
    (is (carrying? :keys))
    (is (empty? @(@*current-room* :items)))))

(def-command-test test-discard
  (binding [*inventory* (ref #{:bunny})]
    (is (re-find #"dropped" (discard "bunny")))
    (is (not (carrying? "bunny")))
    (is (mire.rooms/room-contains? @*current-room* "bunny"))))
