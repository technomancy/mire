;; For launching Mire from SLIME

(setq mire-dir (file-name-directory
                (or (buffer-file-name) load-file-name))
      swank-clojure-jar-path (concat mire-dir "jars/clojure.jar")
      swank-clojure-extra-classpaths (list (concat mire-dir "jars/clojure-contrib.jar")
                                           mire-dir))

(slime)

(find-file (concat mire-dir "src/mire.clj"))