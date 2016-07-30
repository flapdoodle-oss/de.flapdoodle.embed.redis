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

import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.store.FileSet;
import de.flapdoodle.embed.process.config.store.FileType;
import de.flapdoodle.embed.process.config.store.IPackageResolver;
import de.flapdoodle.embed.process.distribution.ArchiveType;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 *
 */
public class Paths implements IPackageResolver {

	protected static Logger logger = Logger.getLogger(Paths.class
			.getName());
	private final Command command;

	public Paths(Command command) {
		this.command = command;
	}

	// CHECKSTYLE:OFF
	@Override
	public FileSet getFileSet(Distribution distribution) {
		String redisdPattern;
		switch (distribution.getPlatform()) {
		case Linux:
		case OS_X:
			redisdPattern = command.commandName();
			break;
		case Windows:
			redisdPattern = command.commandName() + ".exe";
			break;
		default:
			throw new IllegalArgumentException("Unknown Platform "
					+ distribution.getPlatform());
		}
		return FileSet.builder()
				.addEntry(FileType.Executable, redisdPattern).build();
	}

	@Override
	public ArchiveType getArchiveType(Distribution distribution) {
		ArchiveType archiveType;
		switch (distribution.getPlatform()) {
		case Linux:
		case OS_X:
			archiveType = ArchiveType.TGZ;
			break;
		case Windows:
			archiveType = ArchiveType.ZIP;
			break;
		default:
			throw new IllegalArgumentException("Unknown Platform "
					+ distribution.getPlatform());
		}
		return archiveType;
	}

	@Override
	public String getPath(Distribution distribution) {
		String sversion = getVersionPart(distribution.getVersion());

		ArchiveType archiveType = getArchiveType(distribution);
		String sarchiveType;
		switch (archiveType) {
		case TGZ:
			sarchiveType = "tar.gz";
			break;
		case ZIP:
			sarchiveType = "zip";
			break;
		default:
			throw new IllegalArgumentException("Unknown ArchiveType "
					+ archiveType);
		}

		String splatform;
		switch (distribution.getPlatform()) {
		case Linux:
			splatform = "linux";
			break;
		case Windows:
			splatform = "windows";
			break;
		case OS_X:
			splatform = "macos";
			break;
		default:
			throw new IllegalArgumentException("Unknown Platform "
					+ distribution.getPlatform());
		}

		return "/" + sversion + "/redis-" + splatform + "-" + sversion + "." + sarchiveType;
	}

	protected static String getVersionPart(IVersion version) {
		return version.asInDownloadPath();
	}
}
