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
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.Executable;
import de.flapdoodle.embed.process.runtime.IStopable;
import de.flapdoodle.embed.redis.config.AbstractRedisConfig;
import de.flapdoodle.embed.redis.runtime.RedisC;

public abstract class AbstractRedisProcess<T extends AbstractRedisConfig, E extends Executable<T, P>, P extends IStopable>
		extends AbstractProcess<T, E, P> {

	private static Logger logger = Logger
			.getLogger(AbstractRedisProcess.class.getName());

	boolean stopped = false;

	public AbstractRedisProcess(Distribution distribution, T config,
			IRuntimeConfig runtimeConfig, E executable)
			throws IOException {
		super(distribution, config, runtimeConfig, executable);
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

	protected void deleteTempFiles() {

	}

	protected final boolean sendStopToRedisInstance() {
		try {
			boolean result = RedisC.sendShutdown(getConfig().version(),
					getConfig().net().getServerAddress(), getConfig()
							.net().getPort(), getConfig()
							.isNested());
			return result;
		} catch (UnknownHostException e) {
			logger.log(Level.SEVERE, "sendStop", e);
		}
		return false;
	}
}
