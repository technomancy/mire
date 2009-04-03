#!/bin/sh

CLASSPATH=jars/clojure.jar:jars/clojure-contrib.jar:src/

java -cp $CLASSPATH clojure.main src/mire/test/all.clj
