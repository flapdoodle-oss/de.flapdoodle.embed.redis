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

import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.store.ArtifactStoreBuilder;
import de.flapdoodle.embed.process.store.IArtifactStore;
import de.flapdoodle.embed.redis.Command;
import de.flapdoodle.embed.redis.PackagePaths;

public class ArtifactStores {

	private ArtifactStores() {
		// no instance
	}

	public static IArtifactStore defaultArtifactStore() {
		return artifactStore(Command.RedisD);
	}

	public static IArtifactStore redisDArtifactStore() {
		return artifactStore(Command.RedisD);
	}

	private static IArtifactStore artifactStore(Command command) {
		return builder(command).build();
	}

	public static ArtifactStoreBuilder builder(Command command) {
		return defaultBuilder().download(
				new DownloadConfigBuilder().defaults()
						.packageResolver(new PackagePaths(command))
						.build());
	}

	public static ArtifactStoreBuilder defaultBuilder() {
		return new ArtifactStoreBuilder().tempDir(
				new PropertyOrPlatformTempDir()).executableNaming(
				new UUIDTempNaming());
	}

}
