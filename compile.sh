#!/bin/bash

cd `dirname $0`

# Replace ~/src/clojure(-contrib)? with your clojure install location

rm -rf classes/*
unzip -u ~/src/clojure/clojure.jar -d classes/
unzip -u ~/src/clojure-contrib/clojure-contrib.jar -d classes
rm -rf classes/META-INF

java -cp jars/clojure.jar:jars/clojure-contrib.jar:src/:classes/ \
     clojure.main -e "(compile 'mire.server)"

jar cmf Manifest.txt mire.jar -C classes .

echo "\nCreated mire.jar. Use \"java -jar mire.jar\" to launch."