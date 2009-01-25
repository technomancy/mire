(ns mire.test-player
  (:use [mire player rooms])
  (:use [clojure.contrib test-is seq-utils]))

(defn init []
  (dosync (ref-set *rooms* {}))
  (load-rooms "../data/rooms/"))

(deftest test-load-rooms
  (init)
  (doseq [name [:start :closet :hallway :promenade]]
    (is (contains? @*rooms* name)))
  (is (re-find #"promenade" (:desc (:promenade @*rooms*))))
  (is (= :hallway (:west @(:exits (:promenade @*rooms*)))))
  (is (includes? @(:items (:promenade @*rooms*)) :bunny))
  (is (empty? @(:inhabitants (:promenade @*rooms*)))))

(run-tests)