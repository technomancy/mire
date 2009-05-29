#!/bin/bash

cd `dirname $0`

# Replace ~/src/clojure(-contrib)? with your clojure install location

rm -rf classes/*
unzip -u ../clojure/clojure.jar -d target/dependency
unzip -u ../clojure-contrib/clojure-contrib.jar -d target/dependency/
rm -rf classes/META-INF

java -cp src/:target/classes/:target/dependency -Dclojure.compile.path=target/classes \
     clojure.main -e "(compile 'mire.server)"

mkdir -p target/jar
cp -r target/dependency/* target/jar
cp -r target/classes/* target/jar
jar cf target/mire.jar -C target/jar .

echo "Created target/mire.jar. Use \"java -jar target/mire.jar\" to launch."