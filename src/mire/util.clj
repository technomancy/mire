(ns mire.util
  (:use [clojure.contrib seq-utils]))

(defn remove-first [pred coll]
  "Returns a lazy seq of the items in coll excluding the first for
  which (pred item) returns false. pred must be free of side-effects."
  (when (seq coll)
      (if (pred (first coll))
        (rest coll)
        (lazy-cons (first coll) (remove pred (rest coll))))))

(defn move-between-refs [obj from to]
  "Move one instance of obj between ref1 and ref2"
  (commute from (partial remove-first #(= % obj)))
  (commute to conj obj))

(defn pick-rand [coll]
  (coll (rand-int (count coll))))
