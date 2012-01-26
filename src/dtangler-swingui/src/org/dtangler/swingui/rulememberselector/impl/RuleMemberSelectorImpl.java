// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import java.util.List;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.rulememberselector.RuleMemberSelector;
import org.dtangler.swingui.windowmanager.WindowManager;

public class RuleMemberSelectorImpl implements RuleMemberSelector {

	private final WindowManager windowManager;

	public RuleMemberSelectorImpl(WindowManager windowManager) {
		this.windowManager = windowManager;
	}

	public String selectRuleMember(List<String> groupNames) {
		ActionFactory actionFactory = new ActionFactory();
		SwingRuleMemberView view = new SwingRuleMemberView(actionFactory);
		RuleMemberModel model = new RuleMemberModel(groupNames);
		RuleMemberPresenter presenter = new RuleMemberPresenter(view, model);
		new RuleMemberGlue(actionFactory, presenter, windowManager, view);
		windowManager.showModal(view);
		return model.getValue();
	}

}
