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
package de.flapdoodle.embed.redis.runtime;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.redis.config.RedisDConfig;

/**
 *
 */
public class RedisD {

	protected static Logger logger = Logger.getLogger(RedisD.class.getName());

	public static int getRedisdProcessId(String output, int defaultValue) {
		Pattern pattern = Pattern.compile("PID: ([1234567890]+)",
				Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(output);
		if (matcher.find()) {
			String value = matcher.group(1);
			return Integer.valueOf(value);
		}
		return defaultValue;
	}

	public static List<String> getCommandLine(RedisDConfig config,
			IExtractedFileSet redisdExecutable, File dbDir, File dbFile,
			File pidFile) throws UnknownHostException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(redisdExecutable.executable()
				.getAbsolutePath(),//
				"--port", "" + config.net().getPort(), //
				"--dir", dbDir.getAbsolutePath(),//
				// daemonize doesn't work with the output processor,
				// when
				// daemonized, redis does not give any output..
				// "--daemonize", "yes",//
				"--pidfile", pidFile.getAbsolutePath(),//
				"--dbfilename", dbFile.getName()));

		return ret;
	}

	public static List<String> enhanceCommandLinePlattformSpecific(
			Distribution distribution, List<String> commands) {
		// do nothing
		return commands;
	}

}
