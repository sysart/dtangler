// This product is provided under the terms of EPL (Eclipse Public License)
// version 1.0.
//
// The full license text can be read from:
// http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import org.dtangler.swingui.fileselector.FileSelector;

public class MockFileSelector implements FileSelector {

	private String file;

	public String selectFile(String functionText, String fileSuffix,
			String fileDescription) {
		return file;
	}

	public void setNextFile(String file) {
		this.file = file;
	}

}
