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
  [name contents]
  (dosync (commute *rooms* conj
            {name {:name name :desc (:desc contents)
                   :exits (ref (:exits contents))
                   :items (ref (or (:items contents) []))
                   :inhabitants (ref []) }})))

(defn load-rooms
  "Load room definitions from dir.

Each room should be a map containing a :desc key for a description,
an :exits key with a map of exit directions to room names, and
an :items key with a vector of item names. The :items entry may be
omitted for rooms containing no items. The filename should be the room
name."
  [dir]
  (doseq [file (.listFiles (java.io.File. dir))]
    (make-room (keyword (.getName file)) (read (java.io.PushbackReader.
                                                (java.io.FileReader. file))))))
