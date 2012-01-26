//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;

public class ChildViolation implements Violation {

	private final Dependable parent;
	private final Violation violation;

	public ChildViolation(Dependable parent, Violation violation) {
		this.parent = parent;
		this.violation = violation;
	}

	public boolean appliesTo(Set<Dependable> dependables) {
		return dependables.contains(parent);
	}

	public String asText() {
		return parent.getDisplayName() + " contains a violation: "
				+ violation.asText();
	}

	public Severity getSeverity() {
		return violation.getSeverity();
	}

	public Set<Dependable> getMembers() {
		return violation.getMembers();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChildViolation))
			return false;
		ChildViolation other = (ChildViolation) obj;
		return parent.equals(other.parent) && violation.equals(other.violation);
	}

	@Override
	public int hashCode() {
		return 21 * parent.hashCode() * violation.hashCode();
	}

	@Override
	public String toString() {
		return asText();
	}
}
