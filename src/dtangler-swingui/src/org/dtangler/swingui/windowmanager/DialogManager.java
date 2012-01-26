//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager;

public interface DialogManager {

	public enum DialogResult {
		yes, no, cancel
	}

	DialogResult showYesNoCancelDialog(String message, String title);

}
