// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.types;

import org.dtangler.core.dependencies.Scope;

public enum JavaScope implements Scope {
	locations, packages, classes;

	public String getDisplayName() {
		return name();
	}

	public int index() {
		return ordinal();
	}
}