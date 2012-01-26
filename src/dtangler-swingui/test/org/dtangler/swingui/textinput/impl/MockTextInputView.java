//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

public class MockTextInputView implements TextInputView {

	private String title;
	private String fieldName;
	private String value;

	public String getValue() {
		return value;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;

	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getTitle() {
		return title;
	}

}
