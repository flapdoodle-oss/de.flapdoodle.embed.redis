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
import java.net.UnknownHostException;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 *
 */
public class RedisCliConfig extends AbstractRedisConfig {

	@Override
	public String toString() {
		return "RedisCliConfig [shutdown=" + shutdown + ", network="
				+ network + ", timeout=" + timeout + ", nested="
				+ nested + ", version=" + version + "]";
	}

	private final boolean shutdown;

	public RedisCliConfig(IVersion version) throws UnknownHostException,
			IOException {
		this(version, new Net(), new Timeout());
	}

	public RedisCliConfig(IVersion version, Net network, Timeout timeout) {
		this(version, network, timeout, false);
	}

	public RedisCliConfig(IVersion version, Net network, Timeout timeout,
			boolean shutdown) {
		super(version, network, timeout);
		this.shutdown = shutdown;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public static RedisCliConfig getConfigInstance(IVersion version,
			Net network) {
		return new RedisCliConfig(version, network, new Timeout());
	}
}