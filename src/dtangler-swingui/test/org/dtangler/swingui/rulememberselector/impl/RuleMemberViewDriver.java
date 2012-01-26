// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.Panel;
import org.uispec4j.RadioButton;
import org.uispec4j.TextBox;

public class RuleMemberViewDriver {

	public final ComboBox groups;
	public final RadioButton groupRadio;
	public final TextBox literal;
	public final RadioButton literalRadio;
	public final Button okButton;
	public final Button cancelButton;

	public RuleMemberViewDriver(SwingView view) {
		Panel panel = new Panel(view.getViewComponent());
		groups = panel.getComboBox();
		groupRadio = panel.getRadioButton("Group");
		literal = panel.getTextBox("literal");
		literalRadio = panel.getRadioButton("Item by name");
		okButton = panel.getButton("Ok");
		cancelButton = panel.getButton("Cancel");
	}

}
