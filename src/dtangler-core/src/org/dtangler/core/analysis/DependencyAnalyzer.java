//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;

public abstract class DependencyAnalyzer {

	private final Map<Dependency, Set<Violation>> violations = new HashMap();
	private final Map<Dependable, Set<Violation>> childViolations = new HashMap();

	public abstract void doAnalyze(Dependencies dependencies);

	public final void analyze(Dependencies dependencies) {
		violations.clear();
		childViolations.clear();

		doAnalyze(dependencies);

		childViolations.putAll(new ChildViolationFinder(dependencies)
				.findChildViolationsForParents(violations));
	}

	public final Map<Dependency, Set<Violation>> getViolations() {
		return violations;
	}

	public Set<Violation> getChildViolations() {
		Set<Violation> childViolations = new HashSet();
		for (Set<Violation> v : this.childViolations.values()) {
			childViolations.addAll(v);
		}
		return childViolations;
	}

	abstract public boolean isValidResult();

	protected void addViolation(Dependency dependency, Violation violation) {
		Set<Violation> violationSet = violations.get(dependency);
		if (violationSet == null) {
			violationSet = new HashSet();
			violations.put(dependency, violationSet);
		}
		violationSet.add(violation);
	}

	protected void addViolations(Dependency dependency,
			Collection<Violation> violations) {
		for (Violation v : violations)
			addViolation(dependency, v);
	}

}
