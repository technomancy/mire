(ns mire.util)

(defn remove-from
  "Return coll with all instances of obj removed."
  [coll obj]
  (remove #(= % obj) coll))

(defn move-between-sets
  "Move one instance of obj between from and to. Must be called in a transaction."
  [obj from to]
  (commute from remove-from obj)
  (commute to conj obj))