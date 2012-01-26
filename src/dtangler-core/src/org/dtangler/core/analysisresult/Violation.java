//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysisresult;

import java.util.Set;

import org.dtangler.core.dependencies.Dependable;

public interface Violation {

	public enum Severity {
		error, warning
	}

	/** returns a user-friendly text description of the violation */
	String asText();

	Severity getSeverity();

	boolean appliesTo(Set<Dependable> dependables);

	Set<Dependable> getMembers();

}
