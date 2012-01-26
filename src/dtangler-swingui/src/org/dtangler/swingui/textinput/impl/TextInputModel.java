//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

class TextInputModel {

	private final String fieldName;
	private final String dialogTitle;
	private String value;

	TextInputModel(String fieldName, String title) {
		this.fieldName = fieldName;
		this.dialogTitle = title;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getTitle() {
		return dialogTitle;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}