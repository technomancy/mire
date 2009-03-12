#!/bin/sh

java -cp jars/clojure.jar:jars/clojure-contrib.jar:src/ clojure.main -e "(use 'mire.server) (-main)"