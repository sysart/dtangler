//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import java.util.List;

import org.dtangler.swingui.directoryselector.DirectorySelector;

public class MockDirectorySelector implements DirectorySelector {

	private String value;

	public String selectDirectory() {
		return value;
	}

	public String selectDirectory(String dialogTitle,
			String fileTypesDescription, boolean isDirectoryInputAllowed,
			List<String> fileNameExtensions) {
		return value;
	}

	public void setNextValue(String value) {
		this.value = value;
	}

}
