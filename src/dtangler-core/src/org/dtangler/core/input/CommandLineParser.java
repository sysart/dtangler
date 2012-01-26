// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CommandLineParser {
	private String[] allowedKeys;

	public CommandLineParser(String[] allowed) {
		this.allowedKeys = allowed;
	}

	public Map<String, String> parseValues(String[] args) {
		List<String> groupedArgs = groupArguments(args);
		Map<String, String> result = new Hashtable<String, String>();

		for (String line : groupedArgs) {
			for (String possibleKey : allowedKeys) {
				String value = getValueForKey(line, possibleKey);
				if (value != null) {
					result.put(possibleKey, value);
				}
			}
		}
		return result;
	}

	private List<String> groupArguments(String[] args) {
		List<String> groupedArgs = new ArrayList<String>();
		groupedArgs.addAll(Arrays.asList(args));

		for (int i = groupedArgs.size() - 1; i > 0; i--) {
			boolean keyFound = false;
			for (String possibleKey : allowedKeys) {
				if (groupedArgs.get(i).startsWith(getKeyString(possibleKey))) {
					keyFound = true;
				}
			}
			if (!keyFound) {
				groupedArgs.set(i - 1, groupedArgs.get(i - 1) + " "
						+ groupedArgs.get(i));
				groupedArgs.remove(i);
			}
		}
		return groupedArgs;
	}

	private String getValueForKey(String arg, String key) {
		String keyString = getKeyString(key);

		if (arg.startsWith(keyString)) {
			String value = arg.substring(keyString.length());
			return value;
		}
		return null;
	}

	public static final String keyStart = "-";
	public static final String keyEnd = "=";

	public static String getKeyString(String key) {
		return keyStart + key + keyEnd;
	}
}