//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.groupselector.GroupSelector;
import org.dtangler.swingui.rulememberselector.RuleMemberSelector;
import org.dtangler.swingui.rulesselector.RulesSelector;
import org.dtangler.swingui.windowmanager.WindowManager;

public class RulesSelectorImpl implements RulesSelector {

	private final WindowManager windowManager;
	private final RuleMemberSelector ruleMemberSelector;
	private final GroupSelector groupSelector;

	public RulesSelectorImpl(RuleMemberSelector ruleMemberSelector,
			WindowManager windowManager, GroupSelector groupSelector) {
		this.ruleMemberSelector = ruleMemberSelector;
		this.windowManager = windowManager;
		this.groupSelector = groupSelector;
	}

	public Arguments selectRules(Arguments previousArguments) {
		ActionFactory actionFactory = new ActionFactory();
		SwingRulesView view = new SwingRulesView(actionFactory);
		DefaultRulesModel model = new DefaultRulesModel(ruleMemberSelector,
				previousArguments, groupSelector);
		RulesPresenter presenter = new RulesPresenter(view, model);
		new RulesGlue(actionFactory, presenter, windowManager, view);
		windowManager.showModal(view);
		return model.getArguments();
	}

}
