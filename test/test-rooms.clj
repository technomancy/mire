(ns mire.test-player
  (:use [mire player rooms])
  (:use [clojure.contrib test-is seq-utils]))

(deftest test-load-rooms
  (doseq [name [:start :closet :hallway :promenade]]
    (is (contains? rooms name)))
  (is (re-find #"promenade" (:desc (:promenade rooms))))
  (is (= :hallway (:west @(:exits (:promenade rooms)))))
  (is (includes? @(:items (:promenade rooms)) :bunny))
  (is (empty? @(:inhabitants (:promenade rooms)))))

(deftest test-room-contains?
  (let [closet (:closet rooms)]
    (is (not (empty? (filter #(= % :keys) @(:items closet)))))
    (is (room-contains? closet "keys"))
    (is (not (room-contains? closet "monkey")))))

(load-rooms (str (.getParent (java.io.File. *file*)) "/../data/rooms/"))