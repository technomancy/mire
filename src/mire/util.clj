(ns mire.util)

(defn remove-from
  "Return coll with all instances of obj removed."
  [coll obj]
  (remove #(= % obj) coll))