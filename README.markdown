# Mire

It's a MUD.

You can either launch via SLIME by evaling mire.el from within Emacs
(preferred since it gives you access to the REPL) or by the included
mire.sh script.

Dependencies (clojure, clojure-contrib) are included, though you will
need a JVM on your system.

To connect, simply telnet to port 3333, or hit M-x mire from within
Emacs if you have used mire.el.

## TODO

* Read room definitions (and other config?) from files
* Combat?
* Player interaction?
* Get this working with (hashdot)[http://hashdot.sourceforge.net].
