(ns mire.util)

(defn pick-rand
  "Return a random element of vect."
  [vect]
  (vect (rand-int (count vect))))
