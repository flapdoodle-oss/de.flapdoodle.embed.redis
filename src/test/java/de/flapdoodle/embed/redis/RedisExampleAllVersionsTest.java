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
package de.flapdoodle.embed.redis;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.distribution.Version;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.assertEquals;

/**
 * Test whether a race condition occurs between setup and tear down of setting
 * up and closing a redis process.
 * <p/>
 * This test will run a long time based on the download process for all redis
 * versions.
 *
 * @author m.joehren
 */
@RunWith(value = Parameterized.class)
public class RedisExampleAllVersionsTest {
	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> result = new ArrayList<Object[]>();
		for (IVersion version : Versions
				.testableVersions(Version.Main.class)) {
			// TODO currently, only 2.6 versions are supported because
			// they
			// print out a PID and allow using --port argument. 2.4
			// versions
			// don't.
			// if (!version.equals(Version.Main.V2_4)) {
			result.add(new Object[] { version });
			// }
		}
		return result;
	}

	private static final int PORT = 12345;
	private final IVersion redisVersion;
	private RedisDExecutable redisdExe;
	private RedisDProcess redisd;
	private Jedis jedis;

	public RedisExampleAllVersionsTest(IVersion v) {
		this.redisVersion = v;
	}

	@Before
	public void setUp() throws Exception {

		RedisDStarter runtime = RedisDStarter.getDefaultInstance();
		redisdExe = runtime.prepare(new RedisDConfig(this.redisVersion,
				PORT));
		redisd = redisdExe.start();

		// Connecting to Redis on localhost
		jedis = new Jedis("localhost", PORT);
	}

	@After
	public void tearDown() throws Exception {

		redisd.stop();
		redisdExe.stop();
	}

	public Jedis getRedis() {
		return jedis;
	}

	@Test
	public void testInsert1() {
		// adding a new key
		jedis.set("key", "value");
		// getting the key value
		assertEquals("value", jedis.get("key"));
	}

}
