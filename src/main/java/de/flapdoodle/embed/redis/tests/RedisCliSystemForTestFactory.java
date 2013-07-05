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
package de.flapdoodle.embed.redis.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.flapdoodle.embed.redis.Command;
import de.flapdoodle.embed.redis.RedisCliExecutable;
import de.flapdoodle.embed.redis.RedisCliProcess;
import de.flapdoodle.embed.redis.RedisCliStarter;
import de.flapdoodle.embed.redis.RedisDExecutable;
import de.flapdoodle.embed.redis.RedisDProcess;
import de.flapdoodle.embed.redis.RedisDStarter;
import de.flapdoodle.embed.redis.config.RedisCliConfig;
import de.flapdoodle.embed.redis.config.RedisDConfig;
import de.flapdoodle.embed.redis.config.RuntimeConfigBuilder;

public class RedisCliSystemForTestFactory {

	private final static Logger logger = Logger
			.getLogger(RedisCliSystemForTestFactory.class.getName());

	private final RedisCliConfig config;
	private final List<RedisDConfig> configServers;

	private RedisCliExecutable redisCliExecutable;
	private RedisCliProcess redisCliProcess;
	private List<RedisDProcess> redisdProcessList;
	private List<RedisDProcess> redisdConfigProcessList;

	public RedisCliSystemForTestFactory(RedisCliConfig config,
			Map<String, List<RedisDConfig>> replicaSets,
			List<RedisDConfig> configServers, String shardDatabase,
			String shardCollection, String shardKey) {
		this.config = config;
		this.configServers = configServers;
	}

	public void start() throws Throwable {
		this.redisdProcessList = new ArrayList<RedisDProcess>();
		this.redisdConfigProcessList = new ArrayList<RedisDProcess>();
		for (RedisDConfig config : configServers) {
			initializeConfigServer(config);
		}
		initializeRedis();
	}

	private void initializeConfigServer(RedisDConfig config) throws Exception {
		RedisDStarter starter = RedisDStarter.getDefaultInstance();
		RedisDExecutable mongodExe = starter.prepare(config);
		RedisDProcess process = mongodExe.start();
		redisdProcessList.add(process);
	}

	private void initializeRedis() throws Exception {
		RedisCliStarter runtime = RedisCliStarter
				.getInstance(new RuntimeConfigBuilder()
						.defaultsWithLogger(Command.RedisC, logger)
						.build());

		redisCliExecutable = runtime.prepare(config);
		redisCliProcess = redisCliExecutable.start();
	}

	public void stop() {
		for (RedisDProcess process : this.redisdProcessList) {
			process.stop();
		}
		for (RedisDProcess process : this.redisdConfigProcessList) {
			process.stop();
		}
		this.redisCliProcess.stop();
	}
}
