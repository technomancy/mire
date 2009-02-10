(ns mire.rooms
  (:use [clojure.contrib str-utils]))

(def rooms
     {:start {:desc "You find yourself in a round room with a pillar in the middle."
              :exits {:north :closet}
              :inhabitants (ref #{})}
      :closet {:desc "You are in a cramped closet."
               :exits {:south :start}
               :inhabitants (ref #{})}})

(def *current-room*)