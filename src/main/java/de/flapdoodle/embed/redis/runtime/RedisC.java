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

import org.apache.commons.lang3.StringUtils;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.redis.RedisCliStarter;
import de.flapdoodle.embed.redis.config.RedisCliConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

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
		return sendShutdown(redisVersion, hostname, port, isNested,
				RedisCliStarter.getDefaultInstance());
	}

	public static boolean sendShutdown(IVersion redisVersion,
			InetAddress hostname, int port, boolean isNested,
			IRuntimeConfig runtimeConfig) {
		return sendShutdown(redisVersion, hostname, port, isNested,
				RedisCliStarter.getInstance(runtimeConfig));
	}

	private static boolean sendShutdown(IVersion redisVersion,
			InetAddress hostname, int port, boolean isNested,
			RedisCliStarter runtime) {
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
			try {
				Jedis j = new Jedis(hostname.getHostName(), port);
				String reply = j.shutdown();
				if (StringUtils.isEmpty(reply)) {
					return true;
				} else {
					logger.log(Level.SEVERE,
							String.format(
									"sendShutdown closing %s:%s; Got response from server %s",
									hostname, port, reply));
					return false;
				}
			} catch (JedisConnectionException e) {
				logger.log(Level.WARNING,
						String.format(
								"sendShutdown closing %s:%s. No Service listening on address",
								hostname, port), e);
				return true;
			}
		} catch (Exception iox) {
			logger.log(Level.SEVERE, String.format(
					"sendShutdown closing %s:%s", hostname, port),
					iox);
			iox.printStackTrace();
			return false;
		}
	}

}
