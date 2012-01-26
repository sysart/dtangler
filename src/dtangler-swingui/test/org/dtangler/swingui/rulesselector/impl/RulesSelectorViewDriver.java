//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;

public class RulesSelectorViewDriver {

	public final RuleSelectorViewDriver forbiddenDeps;
	public final RuleSelectorViewDriver allowedDeps;
	public final Button okButton;
	public final Button cancelButton;
	public final ListBox groups;
	public final Button newGroupButton;
	public final Button removeGroupButton;
	public final Button editGroupButton;

	public RulesSelectorViewDriver(SwingView view) {
		Panel panel = new Panel(view.getViewComponent());
		okButton = panel.getButton("ok");
		cancelButton = panel.getButton("cancel");
		newGroupButton = panel.getButton("newGroup");
		editGroupButton = panel.getButton("editGroup");
		removeGroupButton = panel.getButton("removeGroup");
		groups = panel.getListBox("groups");
		allowedDeps = new RuleSelectorViewDriver(panel.getPanel("allowed"),
				"Allowed");
		forbiddenDeps = new RuleSelectorViewDriver(panel.getPanel("forbidden"),
				"Forbidden");
	}

}
