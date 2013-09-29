#!/bin/sh
mvn -Pskip-test-in-release release:clean
#mvn -DpreparationGoals=clean release:prepare
#mvn -Darguments="-Dmaven.test.skip=true" release:perform
mvn -Pskip-test-in-release release:prepare
mvn -Pskip-test-in-release release:perform

