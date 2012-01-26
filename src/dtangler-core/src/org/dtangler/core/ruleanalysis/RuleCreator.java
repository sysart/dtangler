// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.exception.DtException;

public class RuleCreator {
	private Map<String, Set<String>> forbiddenDependencies;
	private Map<String, Set<String>> allowedDependencies;
	private Map<String, Group> groups;

	public RuleCreator(Map<String, Set<String>> forbiddenDependencies,
			Map<String, Set<String>> allowedDependencies,
			Map<String, Group> groups) {
		this.forbiddenDependencies = forbiddenDependencies;
		this.allowedDependencies = allowedDependencies;
		this.groups = groups;
	}

	public List<Rule> createRules() {
		List<Rule> rules = new ArrayList<Rule>();

		for (String key : forbiddenDependencies.keySet()) {
			RuleMember member = getRuleMember(key);
			rules.add(new Rule(Rule.Type.cannotDepend, member,
					getMembers(forbiddenDependencies.get(key))));
		}

		for (String key : allowedDependencies.keySet()) {
			RuleMember member = getRuleMember(key);
			rules.add(new Rule(Rule.Type.canDepend, member,
					getMembers(allowedDependencies.get(key))));
		}
		return rules;
	}

	private RuleMember getRuleMember(String name) {
		if (isGroup(name)) {
			return new GroupRuleMember(groups.get(getGroupName(name)));
		}
		return new SingleRuleMember(name);
	}

	private Set<RuleMember> getMembers(Set<String> list) {
		Set<RuleMember> result = new HashSet<RuleMember>();
		for (String name : list) {
			result.add(getRuleMember(name));
		}
		return result;
	}

	private boolean isGroup(String name) {
		if (!name.startsWith(ParserConstants.GROUP_IDENTIFIER)) {
			return false;
		}
		if (!groups.containsKey(getGroupName(name))) {
			throw new DtException("Undefined group " + name);
		}
		return true;
	}

	private String getGroupName(String name) {
		// remove the group identifier
		return name.substring(ParserConstants.GROUP_IDENTIFIER.length());
	}
}
