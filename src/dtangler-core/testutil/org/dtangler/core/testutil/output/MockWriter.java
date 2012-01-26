// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.output;

import org.dtangler.core.textui.Writer;

public class MockWriter implements Writer {
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private StringBuilder output = new StringBuilder();

	public void print(String s) {
		output.append(s);
	}

	public void println(String s) {
		output.append(s);
		output.append(LINE_SEPARATOR);
	}

	public String getOutput() {
		return output.toString();
	}
}