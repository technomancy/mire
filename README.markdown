# Mire

It's a nonviolent MUD. (Multi-User Dungeon)

## Usage

You can launch the server either via SLIME by evaling mire.el from
within Emacs (preferred since it gives you access to the REPL) or by
running the included mire.sh shell script.

Dependencies (clojure, clojure-contrib) are included, though you will
need a JVM on your system.

To connect as a player, simply telnet to port 3333, or hit M-x mire
from within Emacs if you have used mire.el.

## Motivation

This code is not that interesting as a game, though I suppose
something fun could be built using it as a base. The primary purpose
of it is as a demonstration of how to build a simple multithreaded app
in Clojure.

Mire is built up step-by-step, where each step introduces one or two
small yet key Clojure principles and builds on the last step. The
steps each exist in separate git branches. To get the most out of
reading Mire, you should start reading in the branch called
[step-01-echo-server](http://github.com/technomancy/mire/tree/01-echo-server)
and continue from there.

While you can learn from Mire on its own, it has been written
specifically for the PeepCode screencast on Clojure, which should be
released early March 2009.

Copyright (c) 2009-2010 Phil Hagelberg
Licensed under the same terms as Clojure.
