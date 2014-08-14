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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.store.IDownloadPath;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.UserHome;
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;
import de.flapdoodle.embed.redis.Command;
import de.flapdoodle.embed.redis.PackagePaths;

public class DownloadConfigBuilder extends
		de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {
	protected static Logger logger = Logger
			.getLogger(DownloadConfigBuilder.class.getName());

	public DownloadConfigBuilder packageResolverForCommand(Command command) {
		packageResolver(new PackagePaths(command));
		return this;
	}

	public DownloadConfigBuilder defaultsForCommand(Command command) {
		return defaults().packageResolverForCommand(command);
	}

	public DownloadConfigBuilder defaults() {
		fileNaming().setDefault(new UUIDTempNaming());
		downloadPath().setDefault(new RedisDownloadPath());
		progressListener()
				.setDefault(new StandardConsoleProgressListener());
		artifactStorePath().setDefault(new UserHome(".embedredis"));
		downloadPrefix().setDefault(
				new DownloadPrefix("embedredis-download"));
		userAgent().setDefault(
				new UserAgent(
						"Mozilla/5.0 (compatible; Embedded Redis; +https://github.com/flapdoodle-oss/de.flapdoodle.embed.redis)"));
		return this;
	}

	private static class RedisDownloadPath implements IDownloadPath {

		@Override
		public String getPath(Distribution distribution) {
			String redisDownloadUrl = null;
			try {
				Properties properties = new Properties();
				properties.load(new FileInputStream(new File(
						"server.properties")));
				redisDownloadUrl = properties
						.getProperty("redis.download.url");
			} catch (FileNotFoundException e) {
				logger.severe("Couldn't find server.properties in working directory.");
				// wrap and re-throw
				// TODO should probably let the method throw
				throw new RuntimeException(e);
			} catch (IOException e) {
				logger.severe("Couldn't load server.properties from working directory.");
				// wrap and re-throw
				// TODO should probably let the method throw
				throw new RuntimeException(e);
			}
			if (redisDownloadUrl == null) {
				throw new IllegalArgumentException(
						"Please specify a property 'redis.download.url'"
								+ " with the redis distribution download URL.");
			}
			return redisDownloadUrl;
		}
	}
}
