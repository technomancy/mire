(ns mire.items)

;; A single item can be instantiated many places. This map simply
;; provides the descriptions and other properties for items.
(def *items* {:keys {:desc "some keys"}
              :bunny {:desc "a bunny"}})

(defn find-item [name]
  (*items* name))