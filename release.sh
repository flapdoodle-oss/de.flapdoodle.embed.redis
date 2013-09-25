#!/bin/sh
mvn -DpreparationGoals=clean release:clean
mvn -Darguments="-Dmaven.test.skip=true" release:prepare
mvn release:perform

