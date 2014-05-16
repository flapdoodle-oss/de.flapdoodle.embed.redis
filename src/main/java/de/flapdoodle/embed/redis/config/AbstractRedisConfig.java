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
package de.flapdoodle.embed.redis.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.redis.Command;

public abstract class AbstractRedisConfig extends ExecutableProcessConfig {

	protected final Net network;
	protected final Timeout timeout;
	protected boolean nested = false;

	public AbstractRedisConfig(IVersion version, Net networt, Timeout timeout) {
		super(version, new SupportConfig(Command.RedisD));
		this.network = networt;
		this.timeout = timeout;
	}

	public boolean isNested() {
		return nested;
	}

	public void nested() {
		nested = true;
	}

	public Net net() {
		return network;
	}

	public Timeout timeout() {
		return timeout;
	}

	public static class Storage {

		private final String databaseDir;
		private final String databaseFile;
		private final String pidFile;

		public Storage() {
			this(null, null, null);
		}

		public Storage(String databaseDir, String databaseFile,
				String pidFile) {
			this.databaseDir = databaseDir;
			this.databaseFile = databaseFile;
			this.pidFile = pidFile;
		}

		public String getDatabaseDir() {
			return databaseDir;
		}

		public String getDatabaseFile() {
			return databaseFile;
		}

		public String getPidFile() {
			return pidFile;
		}

	}

	public static class Net {

		private final int port;

		public Net() throws UnknownHostException, IOException {
			this(Network.getFreeServerPort());
		}

		public Net(int port) {
			this.port = port;
		}

		public int getPort() {
			return port;
		}

		public InetAddress getServerAddress() throws UnknownHostException {
			return Network.getLocalHost();
		}
	}

	public static class Timeout {

		private final long startupTimeout;

		public Timeout() {
			this(2000);
		}

		public Timeout(long startupTimeout) {
			this.startupTimeout = startupTimeout;
		}

		public long getStartupTimeout() {
			return startupTimeout;
		}
	}

}
