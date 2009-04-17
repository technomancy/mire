(ns mire.test.rooms
  (:use [mire player rooms] :reload-all)
  (:use [clojure.contrib test-is seq-utils]))

(deftest test-set-rooms
  (let [rooms (load-rooms "data/rooms/")]
    (doseq [name [:start :closet :hallway :promenade]]
      (is (contains? rooms name)))
    (is (re-find #"promenade" (:desc (:promenade rooms))))
    (is (= :hallway (:west @(:exits (:promenade rooms)))))
    (is (includes? @(:items (:promenade rooms)) :bunny))
    (is (empty? @(:inhabitants (:promenade rooms))))))

(deftest test-room-contains?
  (let [rooms (load-rooms "data/rooms/")
        closet (:closet rooms)]
    (is (not (empty? (filter #(= % :keys) @(:items closet)))))
    (is (room-contains? closet "keys"))
    (is (not (room-contains? closet "monkey")))))
