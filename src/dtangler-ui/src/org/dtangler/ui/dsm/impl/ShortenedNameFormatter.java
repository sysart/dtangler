// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.ui.dsm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShortenedNameFormatter implements Formatter {

	private int commonPrefixLength;
	private int commonPostfixLength;

	public ShortenedNameFormatter(Collection<String> allItems) {
		commonPrefixLength = getCommonPrefixLength(allItems);
		commonPostfixLength = getCommonPostfixLength(allItems);
	}

	private int getCommonPrefixLength(Collection<String> allItems) {
		if (allItems.size() < 2)
			return 0;
		int index = 0;
		int lastCommonSeperationPoint = 0;

		for (;;) {
			Character nextChar = null;
			for (String item : allItems) {
				if (item.length() < index + 1)
					return lastCommonSeperationPoint;
				char c = item.charAt(index);
				if (nextChar == null)
					nextChar = c;
				if (nextChar.charValue() != c)
					return lastCommonSeperationPoint;
			}
			if (!Character.isLetterOrDigit(nextChar))
				lastCommonSeperationPoint = index + 1;
			index++;
		}
	}

	private int getCommonPostfixLength(Collection<String> allItems) {
		List<String> inversions = new ArrayList();
		for (String item : allItems)
			inversions.add(inverse(item));
		return getCommonPrefixLength(inversions);
	}

	private String inverse(String item) {
		StringBuilder sb = new StringBuilder();
		for (int i = item.length() - 1; i >= 0; i--)
			sb.append(item.charAt(i));
		return sb.toString();
	}

	public String format(Object item) {
		if (item.equals(""))
			return "";
		String leftTrimmed = item.toString().substring(commonPrefixLength);
		String result = leftTrimmed.substring(0, leftTrimmed.length()
				- commonPostfixLength);
		return result;
	}
}
