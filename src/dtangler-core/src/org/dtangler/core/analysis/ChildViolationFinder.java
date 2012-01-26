//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;

public class ChildViolationFinder {

	private final Dependencies dependencies;

	public ChildViolationFinder(Dependencies dependencies) {
		this.dependencies = dependencies;
	}

	public Map<Dependable, Set<Violation>> findChildViolationsForParents(
			Map<Dependency, Set<Violation>> violationMap) {
		Set<Violation> allViolations = new HashSet();
		for (Set<Violation> violations : violationMap.values())
			allViolations.addAll(violations);

		Map<Dependable, Set<Violation>> result = new HashMap();
		for (Violation v : allViolations) {
			if (violationMembersHaveSingleParent(v)) {
				addChildViolations(result, v);
			}
		}
		return result;
	}

	private void addChildViolations(Map<Dependable, Set<Violation>> result,
			Violation violation) {
		Map<Dependable, Set<Violation>> newViolations = createChildViolationsForParents(
				violation, violation.getMembers());
		for (Dependable dep : newViolations.keySet()) {
			Set<Violation> oldViolations = result.get(dep);
			if (oldViolations == null) {
				oldViolations = new HashSet();
			}
			oldViolations.addAll(newViolations.get(dep));
			result.put(dep, oldViolations);
		}
	}

	private boolean violationMembersHaveSingleParent(Violation violation) {
		Dependable firstParent = null;
		for (Dependable member : violation.getMembers()) {
			Dependable parent = getParent(member);
			if (parent == null)
				return false;
			if (firstParent == null)
				firstParent = parent;
			else if (!firstParent.equals(parent))
				return false;
		}
		return true;
	}

	private Dependable getParent(Dependable dependable) {
		Set<Dependable> parents = getParents(dependable);
		if (parents.size() != 1)
			// Handling multiparent relationships requires new story
			return null;
		return parents.iterator().next();
	}

	private Set<Dependable> getParents(Dependable dependable) {
		return dependencies.getParents(dependable, dependencies
				.getParentScope(dependable.getScope()));
	}

	private Map<Dependable, Set<Violation>> createChildViolationsForParents(
			Violation v, Set<Dependable> items) {
		if (items.isEmpty())
			return Collections.EMPTY_MAP;
		Set<Dependable> parents = new HashSet();
		for (Dependable item : items)
			parents.addAll(getParents(item));

		Map<Dependable, Set<Violation>> result = new HashMap();
		for (Dependable parent : parents)
			createChildViolation(parent, v, result);
		result.putAll(createChildViolationsForParents(v, parents));
		return result;
	}

	private void createChildViolation(Dependable parent, Violation v,
			Map<Dependable, Set<Violation>> violationMap) {
		Set<Violation> violations = violationMap.get(parent);
		if (violations == null) {
			violations = new HashSet();
			violationMap.put(parent, violations);
		}
		violations.add(createChildViolation(parent, v));
	}

	private Violation createChildViolation(final Dependable parent,
			final Violation v) {
		return new ChildViolation(parent, v);
	}
}
