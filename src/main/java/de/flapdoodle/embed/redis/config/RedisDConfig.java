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
public class RedisDConfig extends AbstractRedisConfig {

	private final Storage storage;

	public RedisDConfig(IVersion version) throws UnknownHostException,
			IOException {
		this(version, new Net(), new Storage(), new Timeout());
	}

	public RedisDConfig(IVersion version, int port) {
		this(version, new Net(port), new Storage(), new Timeout());
	}

	/*
	 * Preferred constructor to redis config server
	 */
	public static RedisDConfig getConfigInstance(IVersion version, Net network) {
		return new RedisDConfig(version, network, new Storage(),
				new Timeout());
	}

	public RedisDConfig(IVersion version, Net network, Storage storage,
			Timeout timeout) {
		super(version, network, timeout);
		this.storage = storage;
	}

	public Storage getStorage() {
		return storage;
	}
}
