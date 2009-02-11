(ns mire.util)

(defn pick-rand
  "Return a random element of vect."
  [vect]
  (vect (rand-int (count vect))))

(defn remove-from
  "Return coll with all instances of obj removed."
  [coll obj]
  (remove #(= % obj) coll))