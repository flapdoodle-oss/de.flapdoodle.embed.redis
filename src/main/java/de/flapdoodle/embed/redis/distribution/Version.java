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
package de.flapdoodle.embed.redis.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * Redis Version enum
 */
public enum Version implements IVersion {

	/**
	 * old 2.6 release release
	 */
	V2_6_14("2.6.14_5"),

	/**
	 * old 2.8 release
	 */
	V2_8_13("2.8.13_1"),

	/**
	 * new 2.8 release
	 */
	V2_8_19("2.8.19_1"),

	/**
	 * new 3.0 release
	 */
	@Deprecated
	V3_0_04("3.0.0-rc4");
	;

	private final String specificVersion;

	Version(String vName) {
		this.specificVersion = vName;
	}

	@Override
	public String asInDownloadPath() {
		return specificVersion;
	}

	@Override
	public String toString() {
		return "Version{" + specificVersion + '}';
	}

	public static enum Main implements IVersion {
		/**
		 * latest production release
		 */
		V2_8(V2_8_19),

		/**
		 * old production release
		 */
		V2_8_OLD(V2_8_13),

		/**
		 * legacy production release
		 */
		V2_6(V2_6_14),

		PRODUCTION(V2_8),

		@Deprecated
		DEPRECATED(V2_6), ;

		private final IVersion _latest;

		Main(IVersion latest) {
			_latest = latest;
		}

		@Override
		public String asInDownloadPath() {
			return _latest.asInDownloadPath();
		}
	}
}
