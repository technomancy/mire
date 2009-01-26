(ns mire.test-commands
  (:use [mire commands player])
  (:use [clojure.contrib test-is seq-utils]))

(defn init []
  (dosync (ref-set mire.rooms/*rooms* {}))
  (mire.rooms/load-rooms (str (.getParent (java.io.File. *file*)) "/../data/rooms/")))

(deftest test-execute
  (init)
  (binding [*current-room* (ref (:start @mire.rooms/*rooms*))
            *inventory* (ref [])
            *name* "Tester"]
    (is (includes? unknown-responses (execute "do something funky")))
    ;; too many args
    (is (= "You can't do that." (execute "drop a can of beans into the fridge")))
    (is (re-find #"closet" (execute "north")))
    (is (= @*current-room* (:closet @mire.rooms/*rooms*)))))

(deftest test-grab
  (init)
  (binding [*current-room* (ref (:closet @mire.rooms/*rooms*))
            *inventory* (ref [])]
    (is (not (= "There isn't any keys here"
                (grab "keys"))))
    (is (inventory-contains? :keys))
    (is (empty? @(@*current-room* :items)))))

(run-tests)