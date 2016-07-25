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
package de.flapdoodle.embed.redis.config;

import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.io.progress.Slf4jProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.redis.Command;

import java.util.logging.Level;

public class RuntimeConfigBuilder extends
		de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

	@Deprecated
	public RuntimeConfigBuilder defaultsWithLogger(Command command, java.util.logging.Logger logger) {
		defaults(command);
		processOutput().overwriteDefault(RedisDProcessOutputConfig.getInstance(command, logger));

		IDownloadConfig downloadConfig = new DownloadConfigBuilder()
				.defaultsForCommand(command)
				.progressListener(new LoggingProgressListener(logger, Level.FINE))
				.build();

		artifactStore().overwriteDefault(storeBuilder().defaults(command).download(downloadConfig).build());
		return this;
	}

	public RuntimeConfigBuilder defaultsWithLogger(Command command, org.slf4j.Logger logger) {
		defaults(command);
		processOutput().overwriteDefault(RedisDProcessOutputConfig.getInstance(command, logger));

		IDownloadConfig downloadConfig = new DownloadConfigBuilder()
				.defaultsForCommand(command)
				.progressListener(new Slf4jProgressListener(logger))
				.build();

		artifactStore().overwriteDefault(storeBuilder().defaults(command).download(downloadConfig).build());
		return this;
	}

	public RuntimeConfigBuilder defaults(Command command) {
		processOutput().setDefault(RedisDProcessOutputConfig.getDefaultInstance(command));
		commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
		artifactStore().setDefault(storeBuilder().defaults(command).build());
		return this;
	}

	public RuntimeConfigBuilder defaultsCli(Command command) {
		processOutput().setDefault(RedisDProcessOutputConfig.getDefaultInstance(command));
		commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop());
		artifactStore().setDefault(storeBuilder().defaults(command).build());
		daemonProcess().setDefault(false);
		return this;
	}

	private ExtractedArtifactStoreBuilder storeBuilder() {
		return new ExtractedArtifactStoreBuilder();
	}

}
