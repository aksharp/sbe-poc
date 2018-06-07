#!/usr/bin/env bash

# path to sbe-all-1.8.1.jar likely to be different for you
java -Dsbe.output.dir=../java  \
     -Dsbe.generate.ir=true \
     -Dsbe.target.namespace=io.github.aksharp.sbe.poc \
     -jar /Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar \
     schema.xml

# Unfortunately no command to set schema location, hence moving it back to resources here
mv ../java/schema.sbeir .