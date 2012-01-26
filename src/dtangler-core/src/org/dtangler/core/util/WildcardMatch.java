// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.util;

import java.util.regex.Pattern;

public class WildcardMatch {
	String regex;

	public WildcardMatch(String mask) {
		super();
		this.regex = createRegex(mask);
	}

	public boolean isMatch(String value) {
		if (!regex.contains("*"))
			return false;
		return value.matches(regex);
	}

	private String createRegex(String mask) {
		int pos = 0;
		int nextPos = mask.indexOf("*", pos);

		StringBuilder sb = new StringBuilder();
		while (nextPos >= 0) {
			sb.append(Pattern.quote(mask.substring(pos, nextPos)));
			sb.append(".*");
			pos = nextPos + 1;
			nextPos = pos < mask.length() ? mask.indexOf("*", pos) : -1;
		}
		sb.append(Pattern.quote(mask.substring(pos, mask.length())));
		return sb.toString();
	}
}
