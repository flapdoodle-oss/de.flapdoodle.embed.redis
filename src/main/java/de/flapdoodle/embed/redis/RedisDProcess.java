/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github, Archimedes Trajano (trajano@github), Christian Bayer (chrbayer84@googlemail.com)
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.process.runtime.Processes;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Net;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.SupportConfig;
import de.flapdoodle.embed.redis.runtime.RedisD;

/**
 *
 */
public class RedisDProcess extends
		AbstractProcess<RedisDConfig, RedisDExecutable, RedisDProcess> {

	private static Logger logger = Logger.getLogger(RedisDProcess.class
			.getName());

	private File dbDir;
	private File dbFile;
	private boolean dbDirIsTemp;
	private boolean dbFileIsTemp;
	boolean stopped = false;
	protected IRuntimeConfig redisCRuntimeConfig;

	public RedisDProcess(Distribution distribution, RedisDConfig config,
			IRuntimeConfig runtimeConfig,
			RedisDExecutable redisdExecutable) throws IOException {
		super(distribution, config, runtimeConfig, redisdExecutable);
	}

	protected Set<String> knownFailureMessages() {
		HashSet<String> ret = new HashSet<String>();
		ret.add("failed errno");
		ret.add("ERROR:");
		return ret;
	}

	@Override
	protected void stopInternal() {

		synchronized (this) {
			if (!stopped) {

				stopped = true;

				logger.info("try to stop redisd");
				if (!sendStopToRedisInstance()) {
					logger.warning("could not stop redisd with command, try next");
					if (!sendKillToProcess()) {
						logger.warning("could not stop redisd, try next");
						if (!sendTermToProcess()) {
							logger.warning("could not stop redisd, try next");
							if (!tryKillToProcess()) {
								logger.warning("could not stop redisd the second time, try one last thing");
							}
						}
					}
				}

				stopProcess();

				deleteTempFiles();

			}
		}
	}

	protected final boolean sendStopToRedisInstance() {
		return shutdownRedis(getConfig());
	}

	public static boolean shutdownRedis(AbstractRedisConfig config) {
		try {
			// ensure that we don't get into a stackoverflow when starting
			// the artifact fails entirely
			if (config.isNested()) {
				logger.log(Level.INFO,
						"Nested stop, won't execute redis process again");
				return false;
			}
			InetAddress host = config.net().getServerAddress();
			int port = config.net().getPort();
			if (!host.isLoopbackAddress()) {
				logger.log(Level.WARNING,
						""
								+ "---------------------------------------\n"
								+ "Your localhost ("
								+ host.getHostAddress()
								+ ") is not a loopback adress\n"
								+ "We can NOT send shutdown to redis, because it is denied from remote."
								+ "---------------------------------------\n");
				return false;
			}
			try {
				Jedis j = new Jedis(host.getHostName(), port);
				String reply = j.shutdown();
				if (StringUtils.isEmpty(reply)) {
					return true;
				} else {
					logger.log(Level.SEVERE,
							String.format(
									"sendShutdown closing %s:%s; Got response from server %s",
									host, port, reply));
					return false;
				}
			} catch (JedisConnectionException e) {
				logger.log(Level.WARNING,
						String.format(
								"sendShutdown closing %s:%s. No Service listening on address.\n%s",
								host, port, e.getMessage()));
				return true;
			}
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "sendStop", e);
		}
		return false;
	}

	public void setRedisCRuntimeConfig(IRuntimeConfig redisCRuntimeConfig) {
		this.redisCRuntimeConfig = redisCRuntimeConfig;
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
			tmpDbFile = new File(config.getStorage().getDatabaseFile());
		} else {
			tmpDbFile = new File("dump.rdb");
			dbFileIsTemp = true;
		}
		this.dbFile = tmpDbFile;
	}

	@Override
	protected List<String> getCommandLine(Distribution distribution,
			RedisDConfig config, IExtractedFileSet exe)
			throws IOException {
		return RedisD.enhanceCommandLinePlattformSpecific(distribution,
				RedisD.getCommandLine(getConfig(), exe, dbDir, dbFile,
						pidFile()));
	}

	protected void deleteTempFiles() {
		// first try to delete db file, mostly it located inside db dir
		if ((dbFile != null) && (dbFileIsTemp)
				&& (!Files.forceDelete(dbFile))) {
			logger.warning("Could not delete temp db file: " + dbFile);
		}
		if ((dbDir != null) && (dbDirIsTemp) && (!Files.forceDelete(dbDir))) {
			logger.warning("Could not delete temp db dir: " + dbDir);
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
		} else {
			// fallback, try to read pid file. will throw IOException if
			// that
			// fails
			setProcessId(getPidFromFile(pidFile()));
		}
	}

	@Override
	protected void cleanupInternal() {
	}

	public static void stopStaleProcess(File pidFile, IVersion version,
			int port) throws IOException {
		// try sending the redis shutdown command via API
		if (shutdownRedis(RedisDConfig.getConfigInstance(version, new Net(
				port)))) {
			return;
		}
		try {
			int pid = getPidFromFile(pidFile);
			Platform platform = Distribution.detectFor(version)
					.getPlatform();
			synchronized (RedisDProcess.class) {
				logger.info("try to stop redisd");

				if (!Processes.killProcess(new SupportConfig(
						Command.RedisD), platform,
						StreamToLineProcessor.wrap(Processors
								.console()), pid)) {
					logger.warning("could not kill redisd, try next");
					if (!Processes.termProcess(new SupportConfig(
							Command.RedisD), platform,
							StreamToLineProcessor.wrap(Processors
									.console()), pid)) {
						logger.warning("could not term redisd, try next");
						if (!Processes
								.tryKillProcess(
										new SupportConfig(
												Command.RedisD),
										platform,
										StreamToLineProcessor
												.wrap(Processors
														.console()),
										pid)) {
							logger.warning("could not stop redisd the third time, try one last thing");
						}
					}
				}
			}
		} catch (IOException e) {
			// will throw if there is no pid file, ignore in this case
		}

	}
}
