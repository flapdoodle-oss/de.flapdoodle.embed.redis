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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.redis.RedisCliExecutable;
import de.flapdoodle.embed.redis.RedisCliStarter;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Net;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Timeout;
import de.flapdoodle.embed.redis.config.RedisCliConfig;

/**
 *
 */
public class RedisC {

	private static Logger logger = Logger.getLogger(RedisC.class.getName());

	public static final int WAITING_TIME_SHUTDOWN_IN_MS = 500;

	public static List<String> getCommandLine(RedisCliConfig config,
			IExtractedFileSet rediscExecutable)
			throws UnknownHostException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(rediscExecutable.executable()
				.getAbsolutePath(), "-p", "" + config.net().getPort()));

		if (config.isShutdown()) {
			ret.add("SHUTDOWN");
		}
		return ret;
	}

	public static boolean sendShutdown(IVersion redisVersion,
			InetAddress hostname, int port, boolean isNested) {
		// ensure that we don't get into a stackoverflow when starting the
		// artifact fails entirely
		if (isNested) {
			logger.log(Level.INFO,
					"Nested stop, won't execute redis process again");
			return false;
		}
		if (!hostname.isLoopbackAddress()) {
			logger.log(Level.WARNING,
					""
							+ "---------------------------------------\n"
							+ "Your localhost ("
							+ hostname.getHostAddress()
							+ ") is not a loopback adress\n"
							+ "We can NOT send shutdown to redis, because it is denied from remote."
							+ "---------------------------------------\n");
			return false;
		}

		try {
			RedisCliStarter runtime = RedisCliStarter
					.getDefaultInstance();
			RedisCliConfig redisCliConfig = new RedisCliConfig(
					redisVersion, new Net(port), new Timeout(
							WAITING_TIME_SHUTDOWN_IN_MS), true);
			// set nested
			redisCliConfig.nested();
			RedisCliExecutable rediscliExe = runtime
					.prepare(redisCliConfig);
			rediscliExe.start();
			return true;
		} catch (Exception iox) {
			logger.log(Level.SEVERE, String.format(
					"sendShutdown closing %s:%s", hostname, port),
					iox);
			iox.printStackTrace();
			return false;
		}
	}
}
