#!/bin/bash

echo $JAVA_HOME

JAR_FILE=target/jatalog-0.9-SNAPSHOT-mainClass.jar

${JAVA_HOME}/bin/java -jar ${JAR_FILE} $@
