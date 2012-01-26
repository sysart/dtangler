//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import java.util.List;
import java.util.Set;

import org.dtangler.core.analysis.DependencyAnalyzer;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;

public class ForbiddenDependencyFinder extends DependencyAnalyzer {
	private final RuleFilter ruleFilter;

	public ForbiddenDependencyFinder(List<Rule> rules) {
		this.ruleFilter = new RuleFilter(rules);
	}

	public boolean isValidResult() {
		return getViolations().isEmpty();
	}

	@Override
	public final void doAnalyze(Dependencies dependencies) {
		Set<Dependable> items = dependencies.getAllItems();
		for (Dependable dep : items) {
			Set<Dependable> dependees = dependencies.getDependencyGraph(
					dep.getScope()).getDependencies(dep);
			analyze(dep, dependees, dependencies);
		}
	}

	protected void analyze(Dependable dependant, Set<Dependable> dependees,
			Dependencies dependencies) {
		List<Rule> rules = ruleFilter.getRulesForDependant(dependant);
		rules.addAll(ruleFilter.getParentRulesForDependant(dependant,
				dependencies));

		if (rules.size() == 0)
			return;

		for (Rule rule : rules) {
			checkRule(dependant, dependees, rule, dependencies);
		}
	}

	private void checkRule(Dependable dependant, Set<Dependable> dependees,
			Rule rule, Dependencies dependencies) {
		for (Dependable dependee : dependees) {
			Set<Dependable> parents = dependencies
					.getParentsFromAllScopes(dependee);

			if (!isRuleApplicable(rule, dependee, parents))
				continue;

			// check for an allowing rule that overrides the forbidden rule
			if (ruleFilter.isDependencyAllowedByRule(dependant, dependee))
				continue;

			addViolation(dependant, dependee, rule);
		}
	}

	private boolean isRuleApplicable(Rule rule, Dependable dependee,
			Set<Dependable> parents) {
		if (rule.getType().equals(Rule.Type.canDepend))
			return false;
		if (rule.appliesToRightSide(dependee))
			return true;
		for (Dependable parent : parents) {
			if (rule.appliesToRightSide(parent))
				return true;
		}
		return false;
	}

	private void addViolation(Dependable dependant, Dependable dependee,
			Rule violatedRule) {
		Dependency dependency = new Dependency(dependant, dependee);
		addViolation(dependency, new RuleViolation(dependency, violatedRule));
	}
}