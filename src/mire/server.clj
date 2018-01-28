(ns mire.server
  (:use [mire.player]
        [mire.commands :only [discard look execute]]
        [mire.rooms :only [add-rooms rooms]])
  (:use [clojure.java.io :only [reader writer]]
        [server.socket :only [create-server]]))

(defn- cleanup []
  "Drop all inventory and remove player from room and player list."
  (dosync
   (doseq [item @*inventory*]
     (discard item))
   (commute player-streams dissoc *player-name*)
   (commute (:inhabitants @*current-room*)
            disj *player-name*)))

(defn- get-unique-player-name [name]
  (if (@player-streams name)
    (do (print "That name is in use; try again: ")
        (flush)
        (recur (read-line)))
    name))

(defn- mire-handle-client [in out]
	(binding [*in* (reader in) *out* (writer out) *err* (writer System/err)]
	    ;; We have to nest this in another binding call instead of using
	    ;; the one above so *in* and *out* will be bound to the socket
	    (print "What is your name?\n") (flush)
	    (binding [*player-name* (get-unique-player-name (read-line))
	              *current-room* (ref (@rooms :start))
	              *inventory* (ref #{})
	              *score* (ref 0)
	              *health* (ref 5)
	              *status* (ref "Alive")
	              *money* (ref 5)
	              *weapon* (ref " ")
	              *armor*  (ref " ")
	              *last-message* (ref (System/currentTimeMillis))]

		(dosync
			(commute (:inhabitants @*current-room*) conj *player-name*)
			(commute player-streams assoc *player-name* *out*)
			(commute players-stats conj {
				(keyword *player-name*) { 
					:name *player-name* 
					:health *health* 
					:status *status* 
					:armor *armor* 
					:weapon *weapon*
                                        :money *money*
					}
				})
		)

	  	(println (look)) (print prompt) (flush)

		(try 
			(loop [input (read-line)]
		    	(when (and input  (= @*status* "Alive"))
			     	(cond (> (- (System/currentTimeMillis) 1000) @*last-message*)
			     		(do
			     			(println (execute input))
			       	    	(.flush *err*)
			       		)
			       		:else 
			       			(println "You are so fast! Maybe, you are a bot?")
			       	)

			     	(dosync (ref-set *last-message* (System/currentTimeMillis)))	
			       	(print prompt) (flush)
			     	(recur (read-line))
			    )
			)

			(finally (cleanup)))
		)
	)
)


(defn -main
  ([port dir]
     (add-rooms dir)
     (defonce server (create-server (Integer. port) mire-handle-client))
     (println "Launching Mire server on port" port))
  ([port] (-main port "resources/rooms"))
  ([] (-main 3333)))
