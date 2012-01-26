// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.dtangler.core.exception.DtException;

public class ConfigFileParser {
	Properties properties;
	private String[] allowedKeys;

	public ConfigFileParser(File configFile, String[] allowedKeys) {
		this(openFile(configFile), allowedKeys);
	}

	private static FileInputStream openFile(File configFile) {
		try {
			return new FileInputStream(configFile);
		} catch (FileNotFoundException e) {
			throw new DtException("config file not found: "
					+ configFile.getAbsolutePath(), e);
		}
	}

	public ConfigFileParser(InputStream inputStream, String[] allowedKeys) {
		this.allowedKeys = allowedKeys;
		properties = new Properties();
		try {
			properties.load(convertBackSlashes(inputStream));
		} catch (IOException e) {
			throw new DtException("the config file could not be read", e);
		}
	}

	public Map<String, String> parseValues() {
		Map<String, String> values = new Hashtable<String, String>();

		for (String key : allowedKeys) {
			if (properties.containsKey(key)) {
				values.put(key, (String) properties.get(key));
			}
		}

		return values;
	}

	public InputStream convertBackSlashes(InputStream stream) {
		InputStreamReader reader = new InputStreamReader(stream);
		int slashCount = 0;
		try {
			StringBuilder b = new StringBuilder();
			try {
				while (true) {
					int value = reader.read();
					if (value == -1)
						break;
					char c = (char) value;
					if (slashCount == 1)
						if (slashCount == 1
								&& (!Character.isWhitespace(c) && c != '\\')) {
							b.append('\\');
						}
					b.append(c);
					slashCount = (c == '\\') ? slashCount + 1 : 0;
				}
				byte[] byteArray = b.toString().getBytes("ISO-8859-1");
				return new ByteArrayInputStream(byteArray);
			} catch (IOException e) {
				throw new RuntimeException("Reading stream failed", e);
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) { // dont care
			}
		}
	}
}
