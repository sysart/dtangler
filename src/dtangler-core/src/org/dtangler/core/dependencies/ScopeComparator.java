// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import java.util.Comparator;

public class ScopeComparator implements Comparator<Scope> {

	public int compare(Scope s1, Scope s2) {
		return s1.index() - s2.index();
	}

}
