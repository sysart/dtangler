//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.textinput.TextInputSelector;
import org.dtangler.swingui.windowmanager.WindowManager;

public class TextInputSelectorImpl implements TextInputSelector {

	private final WindowManager windowManager;

	public TextInputSelectorImpl(WindowManager windowManager) {
		this.windowManager = windowManager;
	}

	public String selectValue(String fieldName, String dialogTitle) {
		ActionFactory actionFactory = new ActionFactory();
		SwingTextInputView view = new SwingTextInputView(actionFactory);
		TextInputModel model = new TextInputModel(fieldName, dialogTitle);
		TextInputPresenter presenter = new TextInputPresenter(view, model);
		new TextInputGlue(actionFactory, presenter, view, windowManager);
		windowManager.showModal(view);
		return model.getValue();
	}

}
