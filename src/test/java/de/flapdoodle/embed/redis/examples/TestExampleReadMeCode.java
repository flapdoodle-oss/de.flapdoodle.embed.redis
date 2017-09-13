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
package de.flapdoodle.embed.redis.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import redis.clients.jedis.Jedis;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.redis.Command;
import de.flapdoodle.embed.redis.RedisDExecutable;
import de.flapdoodle.embed.redis.RedisDProcess;
import de.flapdoodle.embed.redis.RedisDStarter;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Net;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Storage;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig.Timeout;
import de.flapdoodle.embed.redis.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.redis.config.DownloadConfigBuilder;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.redis.distribution.Version;
import de.flapdoodle.embed.redis.tests.RedisDForTestsFactory;

public class TestExampleReadMeCode extends TestCase {

	// ### Usage
	public void testStandard() throws UnknownHostException, IOException {
		// ->
		int port = Network.getFreeServerPort();
		RedisDConfig redisdConfig = new RedisDConfig(
				Version.Main.PRODUCTION, port);

		RedisDStarter runtime = RedisDStarter.getDefaultInstance();

		RedisDExecutable redisdExecutable = null;
		try {
			redisdExecutable = runtime.prepare(redisdConfig);
			RedisDProcess redisd = redisdExecutable.start();

			Jedis jedis = new Jedis("localhost", port);
			// adding a new key
			jedis.set("key", "value");
			// getting the key value
			assertEquals("value", jedis.get("key"));

		} finally {
			if (redisdExecutable != null)
				redisdExecutable.stop();
		}
		// <-
	}

	// ### Usage - custom mongod filename
	public void testCustomRedisdFilename() throws UnknownHostException,
			IOException {
		// ->
		int port = Network.getFreeServerPort();;
		RedisDConfig redisdConfig = new RedisDConfig(
				Version.Main.PRODUCTION, port);

		Command command = Command.RedisD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(command)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(command))
								.executableNaming(
										new UserTempNaming()))
				.build();

		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);

		RedisDExecutable redisdExecutable = null;
		try {
			redisdExecutable = runtime.prepare(redisdConfig);
			redisdExecutable.start();

			Jedis jedis = new Jedis("localhost", port);
			// adding a new key
			jedis.set("key", "value");
			// getting the key value
			assertEquals("value", jedis.get("key"));

		} finally {
			if (redisdExecutable != null)
				redisdExecutable.stop();
		}
		// <-
	}

	// ### Unit Tests
	public void testUnitTests() {
		// @include AbstractRedisTest.java
		Class<?> see = AbstractRedisTest.class;
	}

	// #### ... with some more help
	public void testRedisdForTests() throws IOException {
		// ->
		// ...
		RedisDForTestsFactory factory = null;
		try {
			factory = RedisDForTestsFactory.with(Version.Main.PRODUCTION);

			Jedis jedis = factory.newJedis();
			// adding a new key
			jedis.set("key", "value");
			// getting the key value
			assertEquals("value", jedis.get("key"));

		} finally {
			if (factory != null)
				factory.shutdown();
		}
		// ...
		// <-
	}

	// ### Customize Download URL
	public void testCustomizeDownloadURL() {
		// ->
		// ...
		Command command = Command.RedisD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(command)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(
												command)
										.downloadPath(
												"http://my.custom.download.domain/")))
				.build();
		// ...
		// <-
	}

	// ### Customize Artifact Storage
	public void testCustomizeArtifactStorage() throws IOException {

		int port = Network.getFreeServerPort();
		RedisDConfig redisdConfig = new RedisDConfig(
				Version.Main.PRODUCTION, port);

		// ->
		// ...
		IDirectory artifactStorePath = new FixedPath(
				System.getProperty("user.home")
						+ "/.embeddedRedisdbCustomPath");
		ITempNaming executableNaming = new UUIDTempNaming();

		Command command = Command.RedisD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(command)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(
												command)
										.artifactStorePath(
												artifactStorePath))
								.executableNaming(
										executableNaming))
				.build();

		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);
		RedisDExecutable redisdExe = runtime.prepare(redisdConfig);
		// ...
		// <-
		RedisDProcess redisd = redisdExe.start();

		redisd.stop();
		redisdExe.stop();
	}

	// ### Usage - custom redisd process output
	// #### ... to console with line prefix
	public void testCustomOutputToConsolePrefix() {
		// ->
		// ...
		ProcessOutput processOutput = new ProcessOutput(
				Processors.namedConsole("[redisd>]"),
				Processors.namedConsole("[REDISD>]"),
				Processors.namedConsole("[console>]"));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.RedisD).processOutput(processOutput)
				.build();

		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to file
	public void testCustomOutputToFile() throws FileNotFoundException,
			IOException {
		// ->
		// ...
		IStreamProcessor redisdOutput = Processors.named(
				"[redisd>]",
				new FileStreamProcessor(File.createTempFile("redisd",
						"log")));
		IStreamProcessor redisdError = new FileStreamProcessor(
				File.createTempFile("redisd-error", "log"));
		IStreamProcessor commandsOutput = Processors
				.namedConsole("[console>]");

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.RedisD)
				.processOutput(
						new ProcessOutput(redisdOutput,
								redisdError, commandsOutput))
				.build();

		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	/*
	 * Ist fürs Readme, deshalb nicht statisch und public
	 */
	// ->

	// ...
	public class FileStreamProcessor implements IStreamProcessor {

		private final FileOutputStream outputStream;

		public FileStreamProcessor(File file) throws FileNotFoundException {
			outputStream = new FileOutputStream(file);
		}

		@Override
		public void process(String block) {
			try {
				outputStream.write(block.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProcessed() {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// ...
	// <-

	// #### ... to java logging
	public void testCustomOutputToLogging() throws FileNotFoundException,
			IOException {
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		ProcessOutput processOutput = new ProcessOutput(Processors.logTo(
				logger, Level.INFO), Processors.logTo(logger,
				Level.SEVERE), Processors.named("[console>]",
				Processors.logTo(logger, Level.FINE)));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.RedisD, logger)
				.processOutput(processOutput)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(Command.RedisD)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(
												Command.RedisD)
										.progressListener(
												new LoggingProgressListener(
														logger,
														Level.FINE))))
				.build();

		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to default java logging (the easy way)
	public void testDefaultOutputToLogging() throws FileNotFoundException,
			IOException {
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.RedisD, logger).build();

		RedisDStarter runtime = RedisDStarter.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// ### Custom Version
	public void testCustomVersion() throws UnknownHostException, IOException {
		// ->
		// ...
		int port = Network.getFreeServerPort();
		RedisDConfig redisdConfig = new RedisDConfig(new GenericVersion(
				"3.2.1"), port);

		RedisDStarter runtime = RedisDStarter.getDefaultInstance();
		RedisDProcess redisd = null;

		RedisDExecutable redisdExecutable = null;
		try {
			redisdExecutable = runtime.prepare(redisdConfig);
			redisd = redisdExecutable.start();

			// <-
			Jedis jedis = new Jedis("localhost", port);
			// adding a new key
			jedis.set("key", "value");
			// getting the key value
			assertEquals("value", jedis.get("key"));
			// ->
			// ...

		} finally {
			if (redisd != null) {
				redisd.stop();
			}
			if (redisdExecutable != null)
				redisdExecutable.stop();
		}
		// ...
		// <-

	}

	// ### Main Versions
	public void testMainVersions() throws UnknownHostException, IOException {
		// ->
		IVersion version = Version.V2_6_14;
		// uses latest supported 2.2.x Version
		version = Version.Main.V2_6;
		// uses latest supported production version
		version = Version.Main.PRODUCTION;
		// uses latest supported development version
		version = Version.Main.DEPRECATED;
		// <-
	}

	// ### Use Free Server Port
	/*
	 * // -> Warning: maybe not as stable, as expected. // <-
	 */
	// #### ... by hand
	public void testFreeServerPort() throws UnknownHostException, IOException {
		// ->
		// ...
		int port = Network.getFreeServerPort();
		// ...
		// <-
	}

	// #### ... automagic
	public void testFreeServerPortAuto() throws UnknownHostException,
			IOException {
		// ->
		// ...
		RedisDConfig redisdConfig = new RedisDConfig(
				Version.Main.PRODUCTION);

		RedisDStarter runtime = RedisDStarter.getDefaultInstance();

		RedisDExecutable redisdExecutable = null;
		RedisDProcess redisd = null;
		try {
			redisdExecutable = runtime.prepare(redisdConfig);
			redisd = redisdExecutable.start();

			Jedis jedis = new Jedis("localhost", redisd.getConfig().net()
					.getPort());
			// adding a new key
			jedis.set("key", "value");
			// getting the key value
			assertEquals("value", jedis.get("key"));
			// ->
			// ...

		} finally {
			if (redisd != null) {
				redisd.stop();
			}
			if (redisdExecutable != null)
				redisdExecutable.stop();
		}
		// ...
		// <-
	}

	// ### ... custom timeouts
	public void testCustomTimeouts() throws UnknownHostException, IOException {
		// ->
		// ...
		RedisDConfig redisdConfig = new RedisDConfig(
				Version.Main.PRODUCTION, new Net(), new Storage(),
				new Timeout(30000));
		// ...
		// <-
	}

	// ### Command Line Post Processing
	public void testCommandLinePostProcessing() {

		// ->
		// ...
		ICommandLinePostProcessor postProcessor = // ...
		// <-
		new ICommandLinePostProcessor() {
			@Override
			public List<String> process(Distribution distribution,
					List<String> args) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		// ->

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.RedisD)
				.commandLinePostProcessor(postProcessor).build();
		// ...
		// <-
	}

}
