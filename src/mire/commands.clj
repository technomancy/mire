(ns mire.commands
  (:use [mire.rooms :only [rooms room-contains?]]
        [mire.player])
  (:use [clojure.string :only [join]]))

(defn- move-between-refs
  "Move one instance of obj between from and to. Must call in a transaction."
  [obj from to]
  (alter from disj obj)
  (alter to conj obj))


(defn- parse-number
  "Reads a number from a string. Returns nil if not a number."
  [s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))

(defn- add-money
  "Add %number% money"
  [number]
  (dosync
    (ref-set *money* (+ @*money* number))))

(defn- reduce-money
  "Reduce %number% money"
  [number]
  (dosync
    (ref-set *money* (- @*money* number))))

(defn- add-money-room
  "Add %number% money in room"
  [number]
  (dosync
    (ref-set (*current-room* :money) (+ @(*current-room* :money) (parse-number number)))))

(defn- add-weapon-room
  "Add %thing% weapon in room"
  [thing]
  (dosync
    (alter (:weapons @*current-room*) conj (keyword thing))
      (str "")))

(defn- add-armor-room
  "Add %thing% armor in room"
  [thing]
  (dosync
    (alter (:armors @*current-room*) conj (keyword thing))
      (str "")))

;; Command functions

(defn look
  "Get a description of the surrounding environs and its contents."
  []
  (str (:desc @*current-room*)
       "\nExits: " (keys @(:exits @*current-room*)) "\n"
       (str "Weapons: ")
       (join ", " (map #(name %)
                        @(*current-room* :weapons)))
       (str "\nArmors: ")
       (join ", " (map #(name %)
                        @(*current-room* :armors)))
       (str "\nMoney in this room: ")
       (str @(*current-room* :money))
       ))

(defn move
  "\"♬ We gotta get out of this place... ♪\" Give a direction."
  [direction]
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
       "You can't go that way."))))

(defn discard
  "Put something down that you're carrying."
  [thing]
  (dosync
    (cond (integer? (parse-number thing))
          (if (>= @*money* (parse-number thing))
            (do
              (reduce-money (parse-number thing))
              (println "You dropped " (parse-number thing) " money")
              (ref-set (*current-room* :money) (+ @(*current-room* :money) (parse-number thing)))
              (str ""))
            (do
              (str "You have " @*money* " money")))
    :else (if (= :money (keyword thing))
        (do
          (println "You dropped " @*money* " money")
          (ref-set (*current-room* :money) @*money*)
          (reduce-money @*money*)
          )
        (cond (= @*weapon* (keyword thing))
          (do
            (ref-set *weapon* "")
            (alter (:weapons @*current-room*) conj (keyword thing))
            (println "You dropped the " (keyword thing))
            (str ""))
        :else (if (= @*armor* (keyword thing))
          (do
            (ref-set *armor* "")
            (alter (:armors @*current-room*) conj (keyword thing))
            (println "You dropped the " (keyword thing))
            (str ""))
          (str "Nothing")))))))

(defn grab
  "Pick something up."
  [thing]
  (dosync
    (cond (integer? (parse-number thing))
          (if (>= @(*current-room* :money) (parse-number thing))
            (do
              (add-money (parse-number thing))
              (println "You picked up " (parse-number thing) " money")
              (ref-set (*current-room* :money) (- @(*current-room* :money) (parse-number thing)))
              (str ""))
            (str "In this room " @(*current-room* :money) " money"))
     :else (if (= :money (keyword thing))
        (do
          (add-money @(*current-room* :money))
          (println "You picked up " @(*current-room* :money) " money")
          (ref-set (*current-room* :money) 0)
          (str ""))
        (cond (not (nil? ((get @(*current-room* :items) :weapons) (keyword thing))))
          (do
            (discard (name @*weapon*))
            (ref-set *weapon* ((get @(*current-room* :items) :weapons) (keyword thing)))
            (alter (:weapons @*current-room*) disj (keyword thing))
            (str "You picked up the " (keyword thing)))
        :else (if (not (nil? ((get @(*current-room* :items) :armors) (keyword thing))))
          (do
            (discard (name @*armor*))
            (ref-set *armor* ((get @(*current-room* :items) :armors) (keyword thing)))
            (alter (:armors @*current-room*) disj (keyword thing))
            (str "You picked up the " (keyword thing)))
          (str "Nothing")))))))


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

(defn help
  "Show available commands and what they do."
  []
  (join "\n" (map #(str (key %) ": " (:doc (meta (val %))))
                      (dissoc (ns-publics 'mire.commands)
                              'execute 'commands))))

(defn stats
  "Show player statistics"
  ([] (str "\nHealth: " (apply str (repeat @*health* "♥ "))
    "\nScore: " @*score*
    "\nStatus: " @*status*
    "\nArmor:"  @*armor*
    "\nWeapon:" @*weapon*
    "\nMoney:" @*money*)
  )
  ([name]
    (if (contains? (disj @(:inhabitants @*current-room*) *player-name*) name)
    (if-let [player (first (filter #(= (:name %) name)
                                 (vals @players-stats)))]
                            (str "\nName:" (:name player)
                              "\nHealth: " (apply str (repeat @(:health player) "♥ "))
                              "\nStatus: " @(:status player)
                              "\nArmor:"  @(:armor player)
                              "\nWeapon:" @(:weapon player)
                            )
    )
    (str ""))
  )
)

(defn players
  "Show players in the room"
  []
    (join "\n" (map stats @(:inhabitants @*current-room*)))
)

(defn hit
  "Hit someone"
  [name]
   (if (contains? (disj @(:inhabitants @*current-room*) *player-name*) name)

    (if-let [player (first (filter #(= (:name %) name)
                                 (vals @players-stats)))]
                           (do (dosync
                                (ref-set (:health player) (- @(:health player) 1))
                                (if (< @(:health player) 1)
                                  (do (ref-set (:status player) "Dead")
                                    (binding [*out* (player-streams (:name player))]
                                                (println "GAME OVER") )
                                  )
                                )

                            ) (str ""))
    )

    (str ""))
)

(defn buy
  "Buy something."
  ([]
   (let [store-things (first (vals @(*current-room* :store)))]
     (if (= (count store-things) 0)
       (str "This room isn't store.")
       (do (println (join "\n" store-things))
           (str "You have " @*money* " coins.")))))
  ([thing]
    (dosync
      (let [ store-type (first (keys @(*current-room* :store)))
             store-things (first (vals @(*current-room* :store)))
             thing-price (get store-things (keyword thing))]
        (if (= (count store-things) 0)
         (str "This room isn't store.")
         (if (contains? store-things (keyword thing))
          (if (>= @*money* thing-price)
               (do
                   (case store-type
                     :weapon (do
                               (discard (name @*weapon*))
                               (ref-set *weapon* (keyword thing)))
                     :armor (do
                              (discard (name @*armor*))
                              (ref-set *armor* (keyword thing))))
                   (reduce-money thing-price)
                   (str "You bought " thing "."))
               (str "You require " (- (get store-things (keyword thing)) @*money*) " more coins."))
          (str "Wrong.")))))))


;; Command data

(def commands {"move" move,
               "north" (fn [] (move :north)),
               "south" (fn [] (move :south)),
               "east" (fn [] (move :east)),
               "west" (fn [] (move :west)),
               "discard" discard
               "grab" grab
               "inventory" inventory
               "detect" detect
               "look" look
               "say" say
               "stats" stats
               "hit" hit
               "players" players
               "help" help
               "buy" buy})

;; Command handling

(defn execute
  "Execute a command that is passed to us."
  [input]
  (try (let [[command & args] (.split input " +")]
         (apply (commands command) args))
       (catch Exception e
         (.printStackTrace e (new java.io.PrintWriter *err*))
         "You can't do that!")))
