//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import java.util.List;

import org.dtangler.swingui.actionfactory.ActionKey;

public interface RulesView {

	interface RuleView {
		void setRules(List<String> rules);

		List<String> getSelectedRules();

		void setRuleItems(List<String> ruleItems);

		List<String> getSelectedRuleItems();

	}

	enum Actions implements ActionKey {
		cancel, ok, updateActionStates, addForbiddenRule, removeForbiddenRule, addForbiddenRuleItem, removeForbiddenRuleItems, forbiddenRuleSelectionChanged, addAllowedRule, removeAllowedRule, addAllowedRuleItem, removeAllowedRuleItems, allowedRuleSelectionChanged, newGroup, removeGroups, editGroup
	}

	RuleView forbiddenDeps();

	RuleView allowedDeps();

	void setGroupNames(List<String> groupNames);

	List<String> getSelectedGroups();

}
