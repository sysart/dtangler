// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

import org.dtangler.core.configuration.Group;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.groupselector.GroupSelector;
import org.dtangler.swingui.textinput.TextInputSelector;
import org.dtangler.swingui.windowmanager.WindowManager;

public class GroupSelectorImpl implements GroupSelector {

	private final WindowManager windowManager;
	private final TextInputSelector textInputSelector;

	public GroupSelectorImpl(WindowManager windowManager,
			TextInputSelector textInputSelector) {
		this.windowManager = windowManager;
		this.textInputSelector = textInputSelector;
	}

	public Group createGroup() {
		return createOrEditGroup(null);
	}

	public Group editGroup(Group group) {
		return createOrEditGroup(group);
	}

	private Group createOrEditGroup(Group group) {
		ActionFactory actionFactory = new ActionFactory();
		SwingGroupView view = new SwingGroupView(actionFactory);
		GroupModel model = new GroupModel(textInputSelector, group);
		GroupPresenter presenter = new GroupPresenter(view, model);
		new GroupGlue(actionFactory, windowManager, view, presenter);
		windowManager.showModal(view);
		return model.getGroup();
	}

}
