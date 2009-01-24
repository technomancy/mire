(ns mire.rooms
  (:use [clojure.contrib seq-utils]))

(def *rooms* (ref {}))

;; A single item can be instantiated many places. This map simply
;; provides the descriptions and other properties for items.
(def *items* {:keys {:desc "some keys"}
              :bunny {:desc "a bunny"}})

(defn room-contains? [room thing]
  (includes? @(room :items) (keyword thing)))

(defn make-room
  ([name desc exits items]
     (dosync
      (commute *rooms* conj
               {name {:name name :desc desc :exits (ref exits)
                      :items (ref items) :inhabitants (ref []) }})))
  ([name desc exits]
     (make-room name desc exits [])))

;; TODO: load room (and maybe item) data from files.

(make-room :start
           "You find yourself in a round room with a pillar in the middle."
           {:north :closet
            :south :hallway})

(make-room :closet
           "You are in a cramped closet."
           {:south :start}
           [:keys])

(make-room :hallway
           "You are in a long, low-lit hallway that turns to the east."
           {:north :start
            :east :promenade})

(make-room :promenade
           "The promenade stretches out before you."
           {:west :hallway}
           [:bunny])