(ns mire.items)

(def *items* {:keys {:desc "some keys"}
              :bunny {:desc "a bunny"}})

(defn find-item [name]
  (*items* name))