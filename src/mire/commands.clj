(ns mire.commands
  (:use [mire.rooms :only [rooms room-contains?]]
        [mire.player])
  (:use [clojure.string :only [join]]))

(defn- move-between-refs
  "Move one instance of obj between from and to. Must call in a transaction."
  [obj from to]
  (alter from disj obj)
  (alter to conj obj))



;; Command functions

(defn look
  "Get a description of the surrounding environs and its contents."
  []
  (str (:desc @*current-room*)
       "\nMessage: " (:message @*current-room*)
       "\nExits: " (keys @(:exits @*current-room*)) "\n"
       (join "\n" (map #(str "There is " % " here.\n")
                           @(:items @*current-room*)))))

(defn move
  "\"♬ wtf! ♪\" Give a direction."
  [direction]
  (if (@*inventory* :keys)
     (dosync
   (let [target-name ((:exits @*current-room*) (keyword direction))
         target (@rooms target-name)]
     (if target
       (do
         (move-between-refs *player-name*
                            (:inhabitants @*current-room*)
                            (:inhabitants target))
         (ref-set *current-room* target)
         (look))
       "You can't go that way.")))
  (dosync
   (let [target-name ((:exits @*current-room*) (keyword direction))
         target (@rooms target-name)]
     (if (="open"((:status @*current-room*) (keyword direction)))
     (if target
       (do
         (move-between-refs *player-name*
                            (:inhabitants @*current-room*)
                            (:inhabitants target))
         (ref-set *current-room* target)
         (look))
       "You can't go that way.")
       "This direction is block, U need keys!"
       )))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;@(target :exits)

(defn check-state
  "You can close doors/directions."
  [direction]
  (dosync
   (let [target-name ((:status @*current-room*) (keyword direction))
        target-status (@*current-room* :status) ]
        (do
        (if (= "open" (@target-status (keyword direction))) (str "Aaaaaaand Open") (str "CLOSED"))
          ))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(assoc @target-status (keyword direction) "close")
 (defn change
  "You can close doors/directions, if you only have keys!"
  [direction]
   (if (@*inventory* :keys)
  (dosync
   (let [target-name ((:status @*current-room*) (keyword direction))
        target-status (@*current-room* :status) ]
        (do
        (if (= "open" (@target-status (keyword direction))) (alter target-status conj [(keyword direction) "close"])
          (alter target-status conj [(keyword direction) "open"]))
     )))"Maybe you forgot your keys somewhere?"))

(defn teleport
  "If you have the teleport-panel, you can teleport to any room."
  [room]
  (if (@*inventory* :teleport-panel)
  (dosync
   (let [target-name (keyword room)
         target (@rooms target-name)]
     (if target
       (do
         (move-between-refs *player-name*
                            (:inhabitants @*current-room*)
                            (:inhabitants target))
         (ref-set *current-room* target)
         (look))
       "This room doesn't exist.")))
  "You need to be carrying the teleport-panel for that."))

(defn grab
  "Pick something up."
  [thing]
  (dosync
   (if (room-contains? @*current-room* thing)
     (do (move-between-refs (keyword thing)
                            (:items @*current-room*)
                            *inventory*)
         (str "You picked up the " thing "."))
     (str "There isn't any " thing " here."))))

(defn discard
  "Put something down that you're carrying."
  [thing]
  (dosync
   (if (carrying? thing)
     (do (move-between-refs (keyword thing)
                            *inventory*
                            (:items @*current-room*))
         (str "You dropped the " thing "."))
     (str "You're not carrying a " thing "."))))

(defn message
  "Left a message in room"
  [& line]
  ( let [message1 (join " " line)]
  (dosync
    (alter *current-room* conj [:message message1])
    (str "You left a message: " message1))))
;  [& line]
 ; (dosync
  ; (do (set-message (keyword line)
   ;      (:message @*current-room*))
    ;     (str "You left the message " line "."))
     ;))

(defn inventory
  "See what you've got."
  []
  (str "You are carrying:\n"
       (join "\n" (seq @*inventory*))))

(defn detect
  "If you have the detector, you can see which room an item is in."
  [item]
  (if (@*inventory* :detector)
    (if-let [room (first (filter #((:items %) (keyword item))
                                 (vals @rooms)))]
      (str item " is in " (:name room))
      (str item " is not in any room."))
    "You need to be carrying the detector for that."))

(defn say
  "Say something out loud so everyone in the room can hear."
  [& words]
  (let [message (join " " words)]
    (doseq [inhabitant (disj @(:inhabitants @*current-room*) *player-name*)]
      (binding [*out* (player-streams inhabitant)]
        (println message)
        (println prompt)))
    (str "You said " message)))


(defn show-users-list
	"Display name for each user being on the server"
	[]
		(println "The names of all the players being on the server now:")
		(println "----------------------------------------------------")
		(doseq [player @player-streams]
			(println "\t" (first player)))
		(println "----------------------------------------------------"))

(defn help
  "Show available commands and what they do."
  []
  (join "\n" (map #(str (key %) ": " (:doc (meta (val %))))
                      (dissoc (ns-publics 'mire.commands)
                              'execute 'commands))))


(defn show-name
  []
  "See what is your name."

  (str *player-name*))

(defn change-name
   [& line]
  (let [line1 (join " " line)]
  (dosync
    (set! *player-name* line1)
    (str "Your name now is: " line1))))

(defn who-is-in-the-room
  []
  (doseq [player @player-streams]
      (println "\t" (get player 0))
  ))

;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "teleport" teleport,
              ; "closet" (fn [] (teleport :closet)),
              ; "hallway" (fn [] (teleport :hallway)),
              ; "promenade" (fn [] (teleport :promenade)),
              ; "start" (fn [] (teleport :start)),
               "grab" grab
               "discard" discard
               "inventory" inventory
               "detect" detect
               "look" look
               "say" say
               "help" help
               "message" message
                "show-name" show-name
               "change-name" change-name
			   "show-users-list" show-users-list
               "check" check-state
               "change" change
               "whos-here" who-is-in-the-room})

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (try (let [[command & args] (.split input " +")]
         (apply (commands command) args))
       (catch Exception e
         (.printStackTrace e (new java.io.PrintWriter *err*))
         "You can't do that!")))
