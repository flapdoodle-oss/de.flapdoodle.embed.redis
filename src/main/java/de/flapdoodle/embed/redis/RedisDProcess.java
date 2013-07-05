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

import java.io.File;
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
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.SupportConfig;
import de.flapdoodle.embed.redis.runtime.RedisD;

/**
 *
 */
public class RedisDProcess extends
		AbstractRedisProcess<RedisDConfig, RedisDExecutable, RedisDProcess> {

	private static Logger logger = Logger.getLogger(RedisDProcess.class
			.getName());

	private File dbDir;
	private File dbFile;
	private boolean dbDirIsTemp;
	private boolean dbFileIsTemp;

	public RedisDProcess(Distribution distribution, RedisDConfig config,
			IRuntimeConfig runtimeConfig,
			RedisDExecutable redisdExecutable) throws IOException {
		super(distribution, config, runtimeConfig, redisdExecutable);

	}

	@Override
	protected void onBeforeProcess(IRuntimeConfig runtimeConfig)
			throws IOException {
		super.onBeforeProcess(runtimeConfig);

		RedisDConfig config = getConfig();

		File tmpDbDir;
		if (config.getStorage().getDatabaseDir() != null) {
			tmpDbDir = Files.createOrCheckDir(config.getStorage()
					.getDatabaseDir());
		} else {
			tmpDbDir = Files.createTempDir(
					PropertyOrPlatformTempDir.defaultInstance(),
					"embedredis-db");
			dbDirIsTemp = true;
		}
		this.dbDir = tmpDbDir;
		// db file
		File tmpDbFile;
		if (config.getStorage().getDatabaseFile() != null) {
			tmpDbFile = new File(dbDir, config.getStorage()
					.getDatabaseFile());
		} else {
			tmpDbFile = new File(PropertyOrPlatformTempDir
					.defaultInstance().asFile(), "dump.rdb");
			dbFileIsTemp = true;
		}
		this.dbFile = tmpDbFile;

		File tmpPidFile;
		if (config.getStorage().getPidFile() != null) {
			tmpPidFile = new File(pidFile, config.getStorage()
					.getPidFile());
		} else {
			tmpPidFile = new File(dbDir, "redisd.pid");
		}
		this.pidFile = tmpPidFile;
	}

	@Override
	protected ISupportConfig supportConfig() {
		return new SupportConfig(Command.RedisD);
	}

	@Override
	protected List<String> getCommandLine(Distribution distribution,
			RedisDConfig config, IExtractedFileSet exe)
			throws IOException {
		return RedisD.enhanceCommandLinePlattformSpecific(distribution,
				RedisD.getCommandLine(getConfig(), exe, dbDir, dbFile,
						pidFile));
	}

	@Override
	protected void deleteTempFiles() {
		super.deleteTempFiles();

		if ((dbDir != null) && (dbDirIsTemp) && (!Files.forceDelete(dbDir))) {
			logger.warning("Could not delete temp db dir: " + dbDir);
		}
		if ((dbFile != null) && (dbFileIsTemp)
				&& (!Files.forceDelete(dbFile))) {
			logger.warning("Could not delete temp db file: " + dbFile);
		}
		if ((pidFile != null) && (!Files.forceDelete(pidFile))) {
			logger.warning("Could not delete temp pid file: " + pidFile);
		}
	}

	@Override
	protected final void onAfterProcessStart(ProcessControl process,
			IRuntimeConfig runtimeConfig) throws IOException {
		ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
		LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
				"The server is now ready to accept connections on port",
				knownFailureMessages(), StreamToLineProcessor
						.wrap(outputConfig.getOutput()));
		Processors.connect(process.getReader(), logWatch);
		Processors.connect(process.getError(),
				StreamToLineProcessor.wrap(outputConfig.getError()));
		logWatch.waitForResult(getConfig().timeout().getStartupTimeout());
		int redisdProcessId = RedisD.getRedisdProcessId(
				logWatch.getOutput(), -1);
		if (logWatch.isInitWithSuccess() && redisdProcessId != -1) {
			setProcessId(redisdProcessId);
			// write pid to file
			forceWritePidFile(process.getPid());
		} else {
			// fallback, try to read pid file. will throw IOException if
			// that
			// fails
			setProcessId(getPidFromFile(pidFile));
		}
	}

	@Override
	protected void cleanupInternal() {
	}
}
