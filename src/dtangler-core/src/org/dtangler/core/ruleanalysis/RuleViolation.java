//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.ruleanalysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;

public class RuleViolation implements Violation {

	private final Dependency dependency;
	private final Rule violatedRule;

	public RuleViolation(Dependency dependency, Rule rule) {
		this.dependency = dependency;
		this.violatedRule = rule;
	}

	public String asText() {
		return "Rule violation: " + violatedRule;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RuleViolation))
			return false;
		RuleViolation other = (RuleViolation) obj;
		return dependency.equals(other.dependency)
				&& violatedRule.equals(other.violatedRule);
	}

	@Override
	public int hashCode() {
		return 21 * dependency.hashCode() * violatedRule.hashCode();
	}

	@Override
	public String toString() {
		return asText();
	}

	public Severity getSeverity() {
		return Severity.warning;
	}

	public boolean appliesTo(Set<Dependable> dependables) {
		return dependables.contains(dependency.getDependant())
				&& dependables.contains(dependency.getDependee());
	}

	public Set<Dependable> getMembers() {
		return new HashSet(Arrays.asList(dependency.getDependant(), dependency
				.getDependee()));
	}
}
