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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.redis.distribution.Version;
import junit.framework.TestCase;
import redis.clients.jedis.Jedis;

// CHECKSTYLE:OFF
public class RedisRuntimeTest extends TestCase {

	public void testNothing() {

	}

	public void testDistributions() throws IOException {
		RuntimeConfigBuilder defaultBuilder = new RuntimeConfigBuilder()
				.defaults(Command.RedisD);

		IRuntimeConfig config = defaultBuilder.build();

		for (Platform platform : Platform.values()) {
			if (platform == Platform.Solaris) {
				continue;
			}
			for (IVersion version : Versions
					.testableVersions(Version.Main.class)) {
				int numberChecked = 0;
				for (BitSize bitsize : BitSize.values()) {
					// there is no osx 32bit version for v2.2.1
					boolean skip = (platform == Platform.Windows && bitsize == BitSize.B64)
							|| (platform != Platform.Windows && bitsize == BitSize.B32);
					if (!skip)
						if (!shipThisVersion(platform, version,
								bitsize)) {
							numberChecked++;
							check(config, new Distribution(
									version, platform,
									bitsize));
						}
				}
				assertTrue(numberChecked > 0);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean shipThisVersion(Platform platform, IVersion version,
			BitSize bitsize) {
		// there is no osx 32bit version for v2.2.1 and above
		String currentVersion = version.asInDownloadPath();
		if ((platform == Platform.OS_X) && (bitsize == BitSize.B32)) {
			if (currentVersion.equals(Version.V2_8_3.asInDownloadPath()))
				return true;
			if (currentVersion.equals(Version.V2_6_14.asInDownloadPath()))
				return true;
			if (currentVersion.equals(Version.Main.PRODUCTION
					.asInDownloadPath()))
				return true;
			if (currentVersion.equals(Version.Main.PRODUCTION
					.asInDownloadPath()))
				return true;
		}
		return false;
	}

	private void check(IRuntimeConfig runtime, Distribution distribution)
			throws IOException {
		assertTrue("Check",
				runtime.getArtifactStore().checkDistribution(
						distribution));
		IExtractedFileSet files = runtime.getArtifactStore()
				.extractFileSet(distribution);
		assertNotNull("Extracted", files.executable());
		assertTrue("Delete", files.executable().delete());
	}

	public void testCheck() throws IOException, InterruptedException {

		Timer timer = new Timer();

		int port = 12345;
		RedisDProcess redisdProcess = null;
		RedisDExecutable redisd = null;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(
				Command.RedisD).build();
		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);

		timer.check("After Runtime");

		try {
			redisd = runtime.prepare(new RedisDConfig(
					Version.Main.PRODUCTION, port));
			timer.check("After redisd");
			assertNotNull("redisd", redisd);
			redisdProcess = redisd.start();
			timer.check("After redisdProcess");

			Jedis jedis = new Jedis("localhost", 12345);
			timer.check("After jedisd");
			// adding a new key
			jedis.set("key", "value");
			timer.check("After jedis store");
			// getting the key value
			assertEquals("value", jedis.get("key"));
			timer.check("After jedis get");
		} finally {
			if (redisdProcess != null)
				redisdProcess.stop();
			timer.check("After redisdProcess stop");
			if (redisd != null)
				redisd.stop();
			timer.check("After redisd stop");
		}
		timer.log();
	}

	static class Timer {

		long _start = System.currentTimeMillis();
		long _last = _start;

		List<String> _log = new ArrayList<String>();

		void check(String label) {
			long current = System.currentTimeMillis();
			long diff = current - _last;
			_last = current;

			_log.add(label + ": " + diff + "ms");
		}

		void log() {
			for (String line : _log) {
				System.out.println(line);
			}
		}
	}

}
