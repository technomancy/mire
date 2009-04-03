(ns mire.test.util
  (:use [mire util] :reload-all)
  (:use [clojure.contrib test-is seq-utils]))

(deftest test-move-between-refs
  (let [from (ref #{1 2 3})
        to (ref #{4 5 6})]
    (dosync (move-between-refs 3 from to))
    (is (= #{1 2} @from))
    (is (= #{4 5 6 3} @to))))
