// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.ruleanalysis;

import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.ruleanalysis.Rule;
import org.dtangler.core.ruleanalysis.RuleViolation;

public class MockRuleViolation extends RuleViolation {
	public MockRuleViolation(String dependant, String dependee, Rule rule) {
		super(new Dependency(new TestDependable(dependant), new TestDependable(
				dependee)), rule);
	}
}
