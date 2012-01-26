//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager;

public class MockDialogManager implements DialogManager {

	private DialogResult nextResult;

	public DialogResult showYesNoCancelDialog(String message, String title) {
		if (nextResult == null)
			throw new RuntimeException(
					"No behaviour defined for showing dialog");
		DialogResult result = nextResult;
		nextResult = null;
		return result;
	}

	public void setNextResult(DialogResult nextResult) {
		this.nextResult = nextResult;
	}

}
