// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import java.io.File;
import java.util.Map;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;

public class ArgumentBuilder {
	public Arguments build(String[] cmdLineArgs) {
		Map<String, String> values = new CommandLineParser(
				ParserConstants.VALID_KEYS).parseValues(cmdLineArgs);
		ArgumentParser parser = new ArgumentParser();

		// if config file was given, read it first
		if (values.containsKey(ParserConstants.CONFIG_FILE_KEY)) {
			File configFile = new File(values
					.get(ParserConstants.CONFIG_FILE_KEY));
			Map<String, String> configFileValues = new ConfigFileParser(
					configFile, ParserConstants.VALID_KEYS).parseValues();
			parser.parseArguments(configFileValues);
		}
		return parser.parseArguments(values);
	}
}
