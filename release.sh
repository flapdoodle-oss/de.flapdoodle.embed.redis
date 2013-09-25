#!/bin/sh
mvn -Dmaven.test.skip=true release:clean
mvn -Dmaven.test.skip=true release:prepare
mvn -Dmaven.test.skip=true release:perform

