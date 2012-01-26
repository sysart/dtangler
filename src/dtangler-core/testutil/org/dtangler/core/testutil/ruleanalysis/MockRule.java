// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.ruleanalysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.configuration.Group;
import org.dtangler.core.ruleanalysis.GroupRuleMember;
import org.dtangler.core.ruleanalysis.Rule;
import org.dtangler.core.ruleanalysis.RuleMember;
import org.dtangler.core.ruleanalysis.SingleRuleMember;

public class MockRule extends Rule {

	public MockRule() {
		super(Rule.Type.cannotDepend, new SingleRuleMember("TestRuleMember"),
				new HashSet(Arrays.asList(new SingleRuleMember(
						"TestRuleMember2"))));
	}

	public MockRule(Type type, RuleMember leftSide, Set<RuleMember> rightSide) {
		super(type, leftSide, rightSide);
	}

	public MockRule(Type type, Group leftSide, Group rightside) {
		super(type, new GroupRuleMember(leftSide), new HashSet(Arrays
				.asList(new GroupRuleMember(rightside))));
	}

	public MockRule(Type type, Group leftSide, String rightSide) {
		super(type, new GroupRuleMember(leftSide), new HashSet(Arrays
				.asList(new SingleRuleMember(rightSide))));
	}

	public MockRule(Type type, String leftSide, Group rightSide) {
		super(type, new SingleRuleMember(leftSide), new HashSet(Arrays
				.asList(new GroupRuleMember(rightSide))));
	}
}