#!/bin/sh
mvn release:clean
#mvn -DpreparationGoals=clean release:prepare
mvn -Darguments="-Dmaven.test.skip=true" release:prepare
mvn -Darguments="-Dmaven.test.skip=true" release:perform

