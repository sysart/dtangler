// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;

public class RuleFilter {
	private List<Rule> rules;

	public RuleFilter(List<Rule> rules) {
		this.rules = rules;
	}

	public List<Rule> getRulesForDependant(Dependable dependant) {
		List<Rule> rulesFound = new ArrayList();
		for (Rule rule : rules) {
			if (rule.appliesToLeftSide(dependant)) {
				rulesFound.add(rule);
			}
		}
		return rulesFound;
	}

	public List<Rule> getParentRulesForDependant(Dependable dependant,
			Dependencies dependencies) {
		List<Rule> rules = new ArrayList();
		Set<Dependable> parents = dependencies
				.getParentsFromAllScopes(dependant);
		for (Dependable parent : parents) {
			rules.addAll(getRulesForDependant(parent));
		}
		return rules;
	}

	public boolean isDependencyAllowedByRule(Dependable dependant,
			Dependable dependee) {
		for (Rule rule : rules) {
			if (!rule.getType().equals(Rule.Type.canDepend))
				continue;
			if (rule.appliesToLeftSide(dependant)
					&& rule.appliesToRightSide(dependee)) {
				return true;
			}
		}
		return false;
	}
}
