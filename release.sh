#!/bin/sh
mvn release:clean -DskipTests=true
mvn release:prepare -DskipTests=true
mvn release:perform -DskipTests=true

