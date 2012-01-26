//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import java.util.List;

public interface RulesModel {

	public interface RuleModel {

		void addRule();

		List<String> getRules();

		void removeRules(List<String> rulesToRemove);

		List<String> getRuleItems(List<String> rules);

		void addRuleItem(String ruleToAddTo);

		void removeRuleItem(String ruleToRemoveFrom, List<String> ruleItems);

	}

	void save();

	List<String> getGroupNames();

	RuleModel forbiddenDepsModel();

	RuleModel allowedDepsModel();

	void newGroup();

	void removeGroups(List<String> groupNames);

	void editGroup(String groupName);

}
