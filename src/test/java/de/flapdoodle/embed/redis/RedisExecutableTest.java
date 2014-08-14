/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github, Archimedes Trajano (trajano@github), Christian Bayer (chrbayer84@googlemail.com)
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
package de.flapdoodle.embed.redis;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.redis.distribution.Version;
import junit.framework.TestCase;
import redis.clients.jedis.Jedis;

/**
 * Integration test for starting and stopping MongodExecutable
 *
 * @author m.joehren
 */
// CHECKSTYLE:OFF
public class RedisExecutableTest extends TestCase {

	private static final Logger _logger = Logger
			.getLogger(RedisExecutableTest.class.getName());

	@Test
	public void testStartStopTenTimesWithNewRedisExecutable()
			throws IOException {
		boolean useRedis = true;
		int loops = 10;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(
				Command.RedisD).build();

		for (int i = 0; i < loops; i++) {
			RedisDConfig redisdConfig = new RedisDConfig(
					Version.Main.PRODUCTION, 12345);
			_logger.info("Loop: " + i);
			RedisDExecutable redisdExe = RedisDStarter.getInstance(
					runtimeConfig).prepare(redisdConfig);
			try {
				RedisDProcess redisd = redisdExe.start();

				if (useRedis) {
					Jedis jedis = new Jedis("localhost", 12345);
					// adding a new key
					jedis.set("key", "value");
					// getting the key value
					assertEquals("value", jedis.get("key"));
				}

				redisd.stop();
			} finally {
				redisdExe.stop();
			}
		}

	}

	@Test
	public void testStartRedisdOnNonFreePort() throws IOException,
			InterruptedException {

		RedisDConfig rediddConfig = new RedisDConfig(
				Version.Main.PRODUCTION, 12346);

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(
				Command.RedisD).build();

		RedisDExecutable redisdExe = RedisDStarter.getInstance(
				runtimeConfig).prepare(rediddConfig);
		RedisDProcess redisd = redisdExe.start();

		boolean innerRedisCouldNotStart = false;
		{
			Thread.sleep(500);

			RedisDExecutable innerExe = RedisDStarter.getInstance(
					runtimeConfig).prepare(rediddConfig);
			RedisDProcess innerRedisd = innerExe.start();
			assertFalse(innerRedisd.isProcessRunning());
			innerExe.stop();
		}

		redisd.stop();
		redisdExe.stop();
	}

	@Test
	public void testIsRunning() throws InterruptedException, IOException {
		RedisDConfig redisdConfig = new RedisDConfig(
				Version.Main.PRODUCTION, 12345);

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(
				Command.RedisD).build();

		RedisDExecutable redisdExe = RedisDStarter.getInstance(
				runtimeConfig).prepare(redisdConfig);
		try {
			RedisDProcess redisd = redisdExe.start();

			assertTrue(redisd.isProcessRunning());

			redisd.stop();
		} finally {
			redisdExe.stop();
		}
	}
}
