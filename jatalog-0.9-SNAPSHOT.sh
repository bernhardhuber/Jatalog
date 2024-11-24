#!/bin/bash

echo $JAVA_HOME

${JAVA_HOME}/bin/java -jar target/jatalog-0.9-SNAPSHOT.jar $@
