(ns mire.test-player
  (:use [mire player rooms])
  (:use [clojure.contrib test-is]))

(defn init []
  (dosync
   (ref-set *current-room* (@mire.rooms/*rooms* :closet))
   (ref-set *inventory* [])
   (make-room :closet
           "You are in a cramped closet."
           {:south :start}
           [:keys])))

(deftest test-room-contains?
  (init)
  (is (filter #(= % :keys) @(@*current-room* :items)))
  (is (room-contains? @*current-room* "keys"))
  (is (not (room-contains? @*current-room* "monkey"))))

(deftest test-take
  (init)
  (is (not (= "There isn't any keys here"
              (take-thing "keys"))))
  (is (= @*inventory* [:keys]))
  (is (empty? @(@*current-room* :items))))

(run-tests)
