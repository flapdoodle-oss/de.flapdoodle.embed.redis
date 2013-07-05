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
package de.flapdoodle.embed.redis.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import de.flapdoodle.embed.redis.distribution.Version;

public class RedisdForTestsFactoryTest {
	private static RedisDForTestsFactory testsFactory;

	@BeforeClass
	public static void setRedis() throws IOException {
		testsFactory = RedisDForTestsFactory.with(Version.Main.DEVELOPMENT);
	}

	@AfterClass
	public static void tearDownJedis() throws Exception {
		testsFactory.shutdown();
	}

	private Jedis jedis;

	@Before
	public void setUpJedis() throws Exception {
		// create database
		jedis = testsFactory.newJedis();
	}

	public void testRedisInstanceCreated() {
		assertNotNull(jedis);
	}

	/**
	 * This tests based on an imported JSON data file.
	 */
	@Test
	public void testImport() throws Exception {
		// perform operations
		// adding a new key
		jedis.set("key", CharStreams.toString(new InputStreamReader(Thread
				.currentThread().getContextClassLoader()
				.getResourceAsStream("sample.json"), Charsets.UTF_8)));
		// getting the key value
		assertEquals(CharStreams.toString(new InputStreamReader(Thread
				.currentThread().getContextClassLoader()
				.getResourceAsStream("sample.json"), Charsets.UTF_8)),
				jedis.get("key"));
	}

	/**
	 * This is an example based on
	 * http://www.mongodb.org/display/DOCS/Java+Tutorial to see if things
	 * work.
	 */
	@Test
	public void testSample() throws Exception {

		// perform operations
		jedis.set("key", "value");
		// getting the key value
		assertEquals("value", jedis.get("key"));
	}
}
