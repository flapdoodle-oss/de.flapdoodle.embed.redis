# Organisation Flapdoodle OSS
[![Build Status](https://travis-ci.org/flapdoodle-oss/de.flapdoodle.embed.redis.svg?branch=master)](https://travis-ci.org/flapdoodle-oss/de.flapdoodle.embed.redis)

We are now a github organisation. You are invited to participate. :)

de.flapdoodle.embed.redis
=========================

redis starter

You need to create a file 'server.properties' in the working directory.
This file must contain a value for the redis download server that should be used.
Currently, there is no server that offers binaries for redis on Linux or MacOS. There is
one for Windows binaries, but this is a hack, not a production ready project. We use redis
by creating tarballs for the operating system and version we want to use and putting them
on a shared http server. (Nexus 3rd party repo in our case.)

TODO
- support for redis 2.4? - currently unsupported because it does not print out its PID and does 
  not take any arguments (like --port). You need a config file for adjusting the port with 2.4.
  The newer 2.6 versions support both.
- support more options of redis like bind or slaveof

## Dependencies

### Build on top of

- Embed Process Util [de.flapdoodle.embed.process](https://github.com/flapdoodle-oss/de.flapdoodle.embed.process)

## Howto

### Maven

Stable (Maven Central Repository, Released: 10.12.2015 - wait 24hrs for [maven central](http://repo1.maven.org/maven2/de/flapdoodle/embed/de.flapdoodle.embed.redis/maven-metadata.xml))

	<dependency>
		<groupId>de.flapdoodle.embed</groupId>
		<artifactId>de.flapdoodle.embed.redis</artifactId>
		<version>1.11.3</version>
	</dependency>

Snapshots (Repository http://oss.sonatype.org/content/repositories/snapshots)

	<dependency>
		<groupId>de.flapdoodle.embed</groupId>
		<artifactId>de.flapdoodle.embed.redis</artifactId>
		<version>1.11.4-SNAPSHOT</version>
	</dependency>

