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
package de.flapdoodle.embed.redis.tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.redis.Command;
import de.flapdoodle.embed.redis.RedisDExecutable;
import de.flapdoodle.embed.redis.RedisDProcess;
import de.flapdoodle.embed.redis.RedisDStarter;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Net;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Storage;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Timeout;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.redis.distribution.Version;

/**
 * This class encapsulates everything that would be needed to do embedded Redis
 * testing.
 */
public class RedisDForTestsFactory {

	private static Logger logger = Logger
			.getLogger(RedisDForTestsFactory.class.getName());

	public static RedisDForTestsFactory with(final IVersion version)
			throws IOException {
		return new RedisDForTestsFactory(version);
	}

	private final RedisDExecutable redisdExecutable;

	private final RedisDProcess redisdProcess;

	/**
	 * Create the testing utility using the latest production version of
	 * Redis.
	 * 
	 * @throws IOException
	 */
	public RedisDForTestsFactory() throws IOException {
		this(Version.Main.PRODUCTION);
	}

	/**
	 * Create the testing utility using the specified version of MongoDB.
	 * 
	 * @param version
	 *              version of MongoDB.
	 */
	public RedisDForTestsFactory(final IVersion version) throws IOException {

		final RedisDStarter runtime = RedisDStarter
				.getInstance(new RuntimeConfigBuilder()
						.defaultsWithLogger(Command.RedisD, logger)
						.build());
		redisdExecutable = runtime.prepare(newRedisdConfig(version));
		redisdProcess = redisdExecutable.start();

	}

	protected RedisDConfig newRedisdConfig(final IVersion version)
			throws UnknownHostException, IOException {
		return new RedisDConfig(version, new Net(), new Storage(),
				new Timeout());
	}

	/**
	 * Creates a new Redis connection.
	 * 
	 * @throws UnknownHostException
	 */
	public Jedis newJedis() throws UnknownHostException {
		return new Jedis(redisdProcess.getConfig().net().getServerAddress()
				.getCanonicalHostName(), redisdProcess.getConfig()
				.net().getPort());
	}

	/**
	 * Cleans up the resources created by the utility.
	 */
	public void shutdown() {
		redisdProcess.stop();
		redisdExecutable.stop();
	}
}
