#!/usr/bin/env bash

#SBE_JAR=/Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar

#java -jar -Dsbe.target.language=java -Dsbe.output.dir=../java -Dsbe.generate.ir=true /Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar schema.xml

#java -jar -Dsbe.target.language=java -Dsbe.output.dir=../java /Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar schema.sbeir




#java -jar -Dsbe.ir.filename=schema.sbeir -Dsbe.output.dir=../ir /Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar schema.xml

#     -Dsbe.generate.stubs=false \
#     -Dsbe.ir.filename=com.aksharp.baseline.sbeir \

java -Dsbe.output.dir=../java  \
     -Dsbe.generate.ir=true \
     -Dsbe.target.namespace=io.github.aksharp.sbe.poc \
     -jar /Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar \
     schema.xml




#java -Dsbe.output.dir=../OUT -Dsbe.generate.ir=true -Dsbe.generate.stubs=false -jar /Users/alex/.ivy2/cache/uk.co.real-logic/sbe-all/jars/sbe-all-1.8.1.jar schema.xml