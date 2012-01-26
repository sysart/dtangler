//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;

public class TextInputViewDriver {

	public final String title;
	public final TextBox fieldName;
	public final TextBox value;
	public final Button okButton;
	public final Button cancelButton;

	public TextInputViewDriver(SwingView view) {
		this.title = view.getTitle();
		Panel panel = new Panel(view.getViewComponent());
		fieldName = panel.getTextBox("FIELDNAME");
		value = panel.getTextBox("VALUE");
		okButton = panel.getButton("OK");
		cancelButton = panel.getButton("Cancel");
	}

}
