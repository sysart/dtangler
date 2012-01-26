// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import org.dtangler.core.dependencies.Dependable;

public interface RuleMember {
	String getName();

	boolean appliesTo(Dependable dependable);
}
