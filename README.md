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