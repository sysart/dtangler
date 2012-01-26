//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput;

public interface FileInputSelector {

	/**
	 * @param defaultInput -
	 *            the default values used in the UI, or null if none
	 * @return inputSelection upon successful selection, null upon cancel
	 */
	FileInputSelection selectInput(FileInputSelection defaultInput);
}
