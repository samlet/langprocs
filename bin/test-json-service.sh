#!/bin/bash
export MAVEN_OPTS="-Xmx14000m"
mvn exec:java -Dexec.mainClass="com.samlet.langprocs.actors.JacksonExampleTest"
