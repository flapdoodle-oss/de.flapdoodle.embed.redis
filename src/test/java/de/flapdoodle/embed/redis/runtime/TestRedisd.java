/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.redis.runtime;

import junit.framework.TestCase;

//CHECKSTYLE:OFF
public class TestRedisd extends TestCase {

	public void testGetPID() {
		String consoleOutput = "[29559] 09 Jul 10:53:34.606 # Warning: no config file specified, using the default config. In order to specify a config file use ./redis-server /path/to/redis.conf\n"
				+ "[29559] 09 Jul 10:53:34.607 # Unable to set the max number of files limit to 10032 (Operation not permitted), setting the max clients configuration to 3984.\n"
				+ "                _._                                                  \n"
				+ "           _.-``__ ''-._                                             \n"
				+ "      _.-``    `.  `_.  ''-._           Redis 2.6.14 (d33934a4/1) 64 bit\n"
				+ "  .-`` .-```.  ```\\/    _.,_ ''-._                                   \n"
				+ " (    '      ,       .-`  | `,    )     Running in stand alone mode\n"
				+ " |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6379\n"
				+ " |    `-._   `._    /     _.-'    |     PID: 29559\n"
				+ "  `-._    `-._  `-./  _.-'    _.-'                                   \n"
				+ " |`-._`-._    `-.__.-'    _.-'_.-'|                                  \n"
				+ " |    `-._`-._        _.-'_.-'    |           http://redis.io        \n"
				+ "  `-._    `-._`-.__.-'_.-'    _.-'                                   \n"
				+ " |`-._`-._    `-.__.-'    _.-'_.-'|                                  \n"
				+ " |    `-._`-._        _.-'_.-'    |                                  \n"
				+ "  `-._    `-._`-.__.-'_.-'    _.-'                                   \n"
				+ "      `-._    `-.__.-'    _.-'                                       \n"
				+ "          `-._        _.-'                                           \n"
				+ "              `-.__.-'                                               \n"
				+ "              \n"
				+ "[29559] 09 Jul 10:53:34.608 # Server started, Redis version 2.6.14\n"
				+ "[29559] 09 Jul 10:53:34.608 # WARNING overcommit_memory is set to 0! Background save may fail under low memory condition. To fix this issue add 'vm.overcommit_memory = 1' to /etc/sysctl.conf and then reboot or run the command 'sysctl vm.overcommit_memory=1' for this to take effect.\n"
				+ "[29559] 09 Jul 10:53:34.608 * The server is now ready to accept connections on port 6379";

		assertEquals("PID", 29559,
				RedisD.getRedisdProcessId(consoleOutput, -1));
	}
}
