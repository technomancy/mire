(ns mire.player
  (:use [mire rooms])
  (:use [clojure.contrib seq-utils str-utils]))

(def *players* (ref {}))
(def prompt "> ")

(declare *current-room* *inventory* *name*)

(defn look-exits [room]
  (str "There are exits to the "
       (str-join " and " (map name (keys @(:exits @*current-room*))))
       ".\n"))

(defn look-items [room]
  (str-join "\n" (map #(str "There is " (name %) " here.\n") @(:items room))))

(defn look-inhabitants [room]
  (str-join "\n" (map #(if (not (= % *name*)) (str % " is here."))
                      @(:inhabitants room))))

(defn inventory-contains? [thing]
  (includes? @*inventory* (keyword thing)))

(defn read-name []
  (print "\nWhat is your name? ") (flush)
  (loop [name (read-line)]
    (if (not (@*players* name))
      name
      (do (print "\nThat name is taken; please choose another: ")
          (flush)
          (recur (read-line))))))