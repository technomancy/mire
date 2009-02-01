(ns mire.rooms
  (:use [clojure.contrib str-utils]))

(declare *current-room*)

(def *rooms*
     {:start {:desc "You find yourself in a round room with a pillar in the middle."
              :exits {:north :closet}}
      :closet {:desc "You are in a cramped closet."
                :exits {:south :start}}})

(defn look-exits [room]
  (str "There are exits to the "
       (str-join " and " (map name (keys (:exits room))))
       ".\n"))
