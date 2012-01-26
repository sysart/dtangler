//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;

public class MockViolation implements Violation {

	private final String name;
	private final Set<Dependable> appliesTo = new HashSet();
	private final Severity severity;

	public MockViolation(String name) {
		this(name, Severity.warning, Collections.EMPTY_SET);
	}

	public MockViolation(String name, Severity severity) {
		this(name, severity, Collections.EMPTY_SET);
	}

	public MockViolation(String name, Severity severity,
			Set<Dependable> appliesTo) {
		this.name = name;
		this.severity = severity;
		this.appliesTo.addAll(appliesTo);
	}

	public String asText() {
		return name;
	}

	public Severity getSeverity() {
		return severity;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MockViolation))
			return false;
		return this.name.equals(((MockViolation) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public boolean appliesTo(Set<Dependable> dependables) {
		for (Dependable dependable : dependables) {
			if (appliesTo.contains(dependable))
				return true;
		}
		return false;
	}

	public Set<Dependable> getMembers() {
		return appliesTo;
	}
}
