//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput;

public class MockTextInputSelector implements TextInputSelector {

	private String value;

	public String selectValue(String fieldName, String dialogTitle) {
		return value;
	}

	public void setNextValue(String value) {
		this.value = value;
	}

}
