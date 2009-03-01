(ns mire.util)

(defn remove-from-set
  "Return a set of coll with all instances of obj removed."
  [coll obj]
  (set (remove #(= % obj) coll)))

(defn move-between-sets
  "Move one instance of obj between from and to. Must be called in a transaction."
  [obj from to]
  (commute from remove-from-set obj)
  (commute to conj obj))