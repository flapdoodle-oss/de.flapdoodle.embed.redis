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
package de.flapdoodle.embed.redis.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * Redis Version enum
 */
public enum Version implements IVersion {

	/**
	 * new production release
	 */
	V2_4_18("2.4.18_1"),

	/**
	 * new developement releases
	 */
	@Deprecated
	V2_6_10("2.6.10"),
	/**
	 * newest developement release
	 */
	V2_6_14("2.6.14_5"), ;

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
		 * current production release
		 */
		V2_4(V2_4_18),
		/**
		 * development release
		 */
		V2_6(V2_6_14),

		PRODUCTION(V2_4), DEVELOPMENT(V2_6), ;

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
