de.flapdoodle.embed.redis
=========================

redis starter

TODO
- support for redis 2.4? - currently unsupported because it does not print out its PID and does 
  not take any arguments (like --port). You need a config file for adjusting the port with 2.4.
  The newer 2.6 versions support both.
- externalize binary server string to properties file
- support more options of redis like bind or slaveof