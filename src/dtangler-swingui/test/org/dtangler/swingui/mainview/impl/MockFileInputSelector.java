//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.fileinput.FileInputSelector;

public class MockFileInputSelector implements FileInputSelector {

	private FileInputSelection lastUsedDefaultInput;
	private FileInputSelection inputToReturn;

	public FileInputSelection selectInput(FileInputSelection defaultInput) {
		this.lastUsedDefaultInput = defaultInput;
		return inputToReturn;
	}

	public void setInputToReturn(FileInputSelection inputToReturn) {
		this.inputToReturn = inputToReturn;
	}

	public FileInputSelection getLastUsedDefaultInput() {
		return lastUsedDefaultInput;
	}

}
