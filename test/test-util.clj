(ns mire.test-util
  (:use [mire util])
  (:use [clojure.contrib test-is seq-utils]))

(deftest test-remove-first
  (def my-vec [1 2 3 2 1])
  (is (= [2 3 2 1]) (remove-first #(= % 1) my-vec))
  (is (= [1 3 2 1]) (remove-first even? my-vec))
  (is (= [1 2 3 2 1]) (remove-first #(> % 3) my-vec)))

(deftest test-move-between-refs
  (def from (ref [1 2 3]))
  (def to (ref [4 5 6]))
  (dosync (move-between-refs 3 from to))
  (is (= [1 2] @from))
  (is (= [4 5 6 3] @to)))

(deftest test-pick-rand
  (def my-vec [1 2 3])
  (is (includes? my-vec (pick-rand my-vec))))

(run-tests)