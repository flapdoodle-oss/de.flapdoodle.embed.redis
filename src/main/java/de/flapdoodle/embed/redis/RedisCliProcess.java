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
import java.util.List;
import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.redis.config.RedisCliConfig;
import de.flapdoodle.embed.redis.config.RedisCliSupportConfig;
import de.flapdoodle.embed.redis.runtime.RedisC;

/**
 *
 */
public class RedisCliProcess
		extends
		AbstractRedisProcess<RedisCliConfig, RedisCliExecutable, RedisCliProcess> {

	private static Logger logger = Logger.getLogger(RedisCliProcess.class
			.getName());

	public RedisCliProcess(Distribution distribution, RedisCliConfig config,
			IRuntimeConfig runtimeConfig,
			RedisCliExecutable rediscExecutable) throws IOException {
		super(distribution, config, runtimeConfig, rediscExecutable);
	}

	@Override
	protected ISupportConfig supportConfig() {
		return RedisCliSupportConfig.getInstance();
	}

	@Override
	protected List<String> getCommandLine(Distribution distribution,
			RedisCliConfig config, IExtractedFileSet exe)
			throws IOException {
		return RedisC.getCommandLine(getConfig(), exe);
	}

	@Override
	protected final void onAfterProcessStart(ProcessControl process,
			IRuntimeConfig runtimeConfig) throws IOException {
		ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
		LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor("",
				knownFailureMessages(),
				StreamToLineProcessor.wrap(outputConfig.getOutput()));
		Processors.connect(process.getReader(), logWatch);
		Processors.connect(process.getError(),
				StreamToLineProcessor.wrap(outputConfig.getError()));
		logWatch.waitForResult(getConfig().timeout().getStartupTimeout());
	}

	@Override
	protected void cleanupInternal() {
	}
}
