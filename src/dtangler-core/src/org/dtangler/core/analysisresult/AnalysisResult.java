//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysisresult;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation.Severity;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;

public class AnalysisResult {

	private final Map<Dependency, Set<Violation>> violations = new HashMap();
	private final Set<Violation> ownViolations = new HashSet();
	private final Set<Violation> childViolations = new HashSet();
	private final Set<Violation> allViolations = new HashSet();
	private final boolean isValid;

	public AnalysisResult(Map<Dependency, Set<Violation>> violations,
			Set<Violation> childViolations, boolean isValid) {
		this.violations.putAll(violations);
		this.childViolations.addAll(childViolations);
		this.isValid = isValid;
		for (Set<Violation> violationSet : this.violations.values())
			ownViolations.addAll(violationSet);
		allViolations.addAll(ownViolations);
		allViolations.addAll(childViolations);
	}

	public boolean isValid() {
		return isValid;
	}

	public Set<Violation> getAllViolations() {
		return allViolations;
	}

	public Set<Violation> getViolations(Dependency dependency) {
		Set<Violation> result = this.violations.get(dependency);
		if (result == null)
			return Collections.EMPTY_SET;
		return result;
	}

	public Set<Violation> getViolations(Dependency dependency, Severity severity) {
		Set<Violation> violations = new HashSet();
		for (Violation v : getViolations(dependency))
			if (v.getSeverity().equals(severity))
				violations.add(v);
		return violations;
	}

	public boolean hasViolations(Dependency dependency) {
		return !getViolations(dependency).isEmpty();
	}

	public boolean hasViolations() {
		return !getAllViolations().isEmpty();
	}

	public Set<Violation> getAllChildViolations() {
		return childViolations;
	}

	public Set<Violation> getChildViolations(Set<Dependable> dependables) {
		Set<Violation> result = new HashSet();
		for (Violation violation : childViolations) {
			if (violation.appliesTo(dependables)) {
				result.add(violation);
			}
		}
		return result;
	}

	public Set<Violation> getChildViolations(Dependable dependable,
			Severity severity) {
		Set<Violation> unfilteredResult = getChildViolations(Collections
				.singleton(dependable));
		Set<Violation> result = new HashSet();
		for (Violation v : unfilteredResult)
			if (v.getSeverity().equals(severity))
				result.add(v);
		return result;
	}

	public Set<Violation> getViolations(Set<Dependable> dependables) {
		Set<Violation> result = new HashSet();
		for (Violation violation : getAllViolations()) {
			if (violation.appliesTo(dependables)) {
				result.add(violation);
			}
		}
		return result;
	}
}
