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

import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Starter;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.RuntimeConfigBuilder;

/**
 *
 */
public class RedisDStarter extends
		Starter<RedisDConfig, RedisDExecutable, RedisDProcess> {

	protected static Logger logger = Logger.getLogger(RedisDStarter.class
			.getName());

	private RedisDStarter(IRuntimeConfig config) {
		super(config);
	}

	public static RedisDStarter getInstance(IRuntimeConfig config) {
		return new RedisDStarter(config);
	}

	public static RedisDStarter getDefaultInstance() {
		return getInstance(new RuntimeConfigBuilder().defaults(
				Command.RedisD).build());
	}

	@Override
	protected RedisDExecutable newExecutable(RedisDConfig mongodConfig,
			Distribution distribution, IRuntimeConfig runtime,
			IExtractedFileSet redisSExe) {
		return new RedisDExecutable(distribution, mongodConfig, runtime,
				redisSExe);
	}
}
