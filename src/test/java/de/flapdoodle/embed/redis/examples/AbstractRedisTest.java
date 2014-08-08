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
package de.flapdoodle.embed.redis.examples;

import junit.framework.TestCase;
import redis.clients.jedis.Jedis;
import de.flapdoodle.embed.redis.RedisDExecutable;
import de.flapdoodle.embed.redis.RedisDProcess;
import de.flapdoodle.embed.redis.RedisDStarter;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.distribution.Version;

// ->
public abstract class AbstractRedisTest extends TestCase {

	private RedisDExecutable _redisdExe;
	private RedisDProcess _redisd;

	private Jedis _jedis;

	@Override
	protected void setUp() throws Exception {

		RedisDStarter runtime = RedisDStarter.getDefaultInstance();
		_redisdExe = runtime.prepare(new RedisDConfig(
				Version.Main.PRODUCTION, 12345));
		_redisd = _redisdExe.start();

		super.setUp();

		_jedis = new Jedis("localhost", 12345);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		_redisd.stop();
		_redisdExe.stop();
	}

	public Jedis getJedis() {
		return _jedis;
	}

}
// <-
