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
import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.runtime.Executable;
import de.flapdoodle.embed.redis.config.RedisCliConfig;

/**
 *
 */
public class RedisCliExecutable extends
		Executable<RedisCliConfig, RedisCliProcess> {

	public RedisCliExecutable(Distribution distribution,
			RedisCliConfig redisdConfig, IRuntimeConfig runtimeConfig,
			IExtractedFileSet redisCliExecutable) {
		super(distribution, redisdConfig, runtimeConfig, redisCliExecutable);
	}

	protected static Logger logger = Logger
			.getLogger(RedisCliExecutable.class.getName());

	@Override
	protected RedisCliProcess start(Distribution distribution,
			RedisCliConfig config, IRuntimeConfig runtime)
			throws IOException {
		return new RedisCliProcess(distribution, config, runtime, this);
	}

}
