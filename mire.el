;; For launching Mire from SLIME

(setq mire-dir (file-name-directory
                (or (buffer-file-name) load-file-name))
      swank-clojure-jar-path (concat mire-dir "jars/clojure.jar")
      swank-clojure-extra-classpaths (list (concat mire-dir "jars/clojure-contrib.jar")
                                           (concat mire-dir "src")))

(slime)

(find-file (concat mire-dir "src/mire.clj"))

(defun mire ()
  (interactive)
  (if (get-buffer "*mire*")
      (switch-to-buffer "*mire*")
    (telnet "localhost" 3333)
    (rename-buffer "*mire*")))

(global-set-key (kbd "C-c m") 'mire)
    