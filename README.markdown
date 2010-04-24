# Mire

It's a nonviolent MUD. (Multi-User Dungeon)

## Usage

Install [Leiningen](http://github.com/technomancy/leiningen) if you
haven't already:

    $ curl -O ~/bin/lein http://github.com/technomancy/leiningen/raw/stable/bin/lein
    $ chmod 755 bin/lein
    $ lein self-install

Then run "lein deps" inside the Mire directory. Once it finishes, you
should be able to do "lein run" to launch the Mire server. Then
players can connect by telnetting to port 3333.

Copyright (c) 2009-2010 Phil Hagelberg
Licensed under the same terms as Clojure.
