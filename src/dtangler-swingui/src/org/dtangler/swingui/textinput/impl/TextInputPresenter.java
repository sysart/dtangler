//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

class TextInputPresenter {

	private final TextInputView view;
	private final TextInputModel model;

	TextInputPresenter(TextInputView view, TextInputModel model) {
		this.view = view;
		this.model = model;
		view.setTitle(model.getTitle());
		view.setFieldName(model.getFieldName());
	}

	void onOk() {
		if (!canOk())
			return;
		model.setValue(view.getValue());
	}

	boolean canOk() {
		return !view.getValue().equals("");
	}
}