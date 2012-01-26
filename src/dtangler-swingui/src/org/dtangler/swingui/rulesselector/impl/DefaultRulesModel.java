//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.swingui.groupselector.GroupSelector;
import org.dtangler.swingui.rulememberselector.RuleMemberSelector;

public class DefaultRulesModel implements RulesModel {

	private class DefaultRuleModel implements RuleModel {
		private final Map<String, Set<String>> rules = new HashMap();
		private final RuleMemberSelector ruleMemberSelector;

		public DefaultRuleModel(RuleMemberSelector ruleMemberSelector,
				Map<String, Set<String>> previousRules) {
			this.ruleMemberSelector = ruleMemberSelector;
			this.rules.putAll(previousRules);
		}

		public void addRule() {
			String newRule = ruleMemberSelector
					.selectRuleMember(getGroupNames());
			if (newRule != null)
				rules.put(newRule, Collections.EMPTY_SET);
		}

		public List<String> getRules() {
			ArrayList arules = new ArrayList(rules.keySet());
			Collections.sort(arules);
			return arules;
		}

		public void removeRules(List<String> rulesToRemove) {
			for (String rule : rulesToRemove)
				rules.remove(rule);
		}

		public List<String> getRuleItems(List<String> forbiddenDependencyRules) {
			if (forbiddenDependencyRules.size() != 1)
				return Collections.EMPTY_LIST;
			List<String> items = new ArrayList(rules
					.get(forbiddenDependencyRules.get(0)));
			Collections.sort(items);
			return items;
		}

		public void addRuleItem(String ruleToAddTo) {
			String newItem = ruleMemberSelector
					.selectRuleMember(getGroupNames());
			if (newItem == null)
				return;
			Set<String> allItems = new HashSet(rules.get(ruleToAddTo));
			allItems.add(newItem);
			rules.put(ruleToAddTo, allItems);
		}

		public void removeRuleItem(String ruleToRemoveFrom,
				List<String> ruleItems) {
			Set<String> allItems = new HashSet(rules.get(ruleToRemoveFrom));
			allItems.removeAll(ruleItems);
			rules.put(ruleToRemoveFrom, allItems);
		}

		public void updateRuleMemberName(String oldName, String newName) {
			Map<String, Set<String>> newRules = new HashMap();
			for (Entry<String, Set<String>> entry : rules.entrySet()) {
				if (entry.getKey().equals(oldName))
					newRules.put(newName, updateRuleMemberName(oldName,
							newName, entry.getValue()));
				else
					newRules.put(entry.getKey(), updateRuleMemberName(oldName,
							newName, entry.getValue()));
			}

			rules.clear();
			rules.putAll(newRules);
		}

		private Set<String> updateRuleMemberName(String oldName,
				String newName, Set<String> values) {
			Set<String> result = new HashSet(values);
			if (result.contains(oldName)) {
				result.remove(oldName);
				result.add(newName);
			}
			return result;
		}

		public void removeRuleMember(String name) {
			rules.remove(name);
			for (Set<String> ruleItems : rules.values())
				ruleItems.remove(name);

		}
	}

	private final DefaultRuleModel forbiddenDeps;
	private final DefaultRuleModel allowedDeps;

	private final Arguments prevArguments;
	private Arguments arguments;
	private final GroupSelector groupSelector;
	private final Map<String, Group> groups;

	public DefaultRulesModel(RuleMemberSelector ruleMemberSelector,
			Arguments prevArguments, GroupSelector groupSelector) {
		this.prevArguments = prevArguments;
		this.groupSelector = groupSelector;
		forbiddenDeps = new DefaultRuleModel(ruleMemberSelector, prevArguments
				.getForbiddenDependencies());
		allowedDeps = new DefaultRuleModel(ruleMemberSelector, prevArguments
				.getAllowedDependencies());
		this.groups = new HashMap(prevArguments.getGroups());
	}

	public void save() {
		arguments = prevArguments.createDeepCopy();
		arguments.setForbiddenDependencies(forbiddenDeps.rules);
		arguments.setAllowedDependencies(allowedDeps.rules);
		arguments.setGroups(groups);
	}

	public Arguments getArguments() {
		return arguments;
	}

	public List<String> getGroupNames() {
		List<String> groupNames = new ArrayList(groups.keySet());
		Collections.sort(groupNames);
		return groupNames;
	}

	public RuleModel forbiddenDepsModel() {
		return forbiddenDeps;
	}

	public RuleModel allowedDepsModel() {
		return allowedDeps;
	}

	public void newGroup() {
		Group group = groupSelector.createGroup();
		if (group != null)
			groups.put(group.getName(), group);
	}

	public void removeGroups(List<String> groupNames) {
		for (String groupName : groupNames) {
			groups.remove(groupName);
			removeRuleMember("@" + groupName);
		}
	}

	public void editGroup(String groupName) {
		Group editedGroup = groupSelector.editGroup(groups.get(groupName));
		if (editedGroup != null) {
			groups.remove(groupName);
			groups.put(editedGroup.getName(), editedGroup);
			if (!groupName.equals(editedGroup.getName()))
				updateRuleMemberName("@" + groupName, "@"
						+ editedGroup.getName());
		}
	}

	private void updateRuleMemberName(String oldName, String newName) {
		forbiddenDeps.updateRuleMemberName(oldName, newName);
		allowedDeps.updateRuleMemberName(oldName, newName);
	}

	private void removeRuleMember(String name) {
		forbiddenDeps.removeRuleMember(name);
		allowedDeps.removeRuleMember(name);
	}
}
