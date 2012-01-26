// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

public enum TestScope implements Scope {
	scope1, scope2, scope3;

	public String getDisplayName() {
		return name();
	}

	public int index() {
		return ordinal();
	}
}
