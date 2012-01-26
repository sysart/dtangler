//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.ruleanalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.configuration.Group;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.TestDependable;
import org.junit.Before;
import org.junit.Test;

public class RuleTest {
	Dependable fooA = new TestDependable("eg.foo.a");
	Dependable fooB = new TestDependable("eg.foo.b");
	Dependable barA = new TestDependable("eg.bar.a");
	Dependable barB = new TestDependable("eg.bar.b");
	SingleRuleMember ruleMemberFooA = new SingleRuleMember(fooA
			.getDisplayName());
	SingleRuleMember ruleMemberFooB = new SingleRuleMember(fooB
			.getDisplayName());
	SingleRuleMember ruleMemberBarA = new SingleRuleMember(barA
			.getDisplayName());
	SingleRuleMember ruleMemberBarB = new SingleRuleMember(barB
			.getDisplayName());

	GroupRuleMember groupFoo;
	GroupRuleMember groupBar;

	@Before
	public void setUp() {
		groupFoo = createGroup("Group Foo", "eg.foo.*");
		groupBar = createGroup("Group Bar", "eg.bar.*");
	}

	@Test
	public void testDependable() {
		Rule rule = createRule(Rule.Type.cannotDepend, ruleMemberFooA,
				ruleMemberFooB, ruleMemberBarA);

		assertTrue(rule.appliesToLeftSide(fooA));
		assertFalse(rule.appliesToLeftSide(fooB));
		assertFalse(rule.appliesToLeftSide(barA));
		assertFalse(rule.appliesToLeftSide(barB));

		assertTrue(rule.appliesToRightSide(fooB));
		assertTrue(rule.appliesToRightSide(barA));
		assertFalse(rule.appliesToRightSide(fooA));
		assertFalse(rule.appliesToRightSide(barB));
	}

	@Test
	public void testGroupInLeftSide() {
		Rule rule = createRule(Rule.Type.cannotDepend, groupFoo,
				ruleMemberBarA, ruleMemberBarB);

		assertTrue(rule.appliesToLeftSide(fooA));
		assertTrue(rule.appliesToLeftSide(fooB));

		assertFalse(rule.appliesToLeftSide(barA));
		assertFalse(rule.appliesToLeftSide(barB));
	}

	@Test
	public void testGroupInRightSide() {
		Rule rule = createRule(Rule.Type.cannotDepend, ruleMemberFooA,
				groupBar, ruleMemberFooB);

		assertTrue(rule.appliesToRightSide(barA));
		assertTrue(rule.appliesToRightSide(barB));
		assertTrue(rule.appliesToRightSide(fooB));

		assertFalse(rule.appliesToRightSide(fooA));
	}

	private GroupRuleMember createGroup(String name, String... items) {
		Set<String> itemList = new HashSet();
		for (String item : items)
			itemList.add(item);
		return new GroupRuleMember(new Group(name, itemList));
	}

	private Rule createRule(Rule.Type type, RuleMember left,
			RuleMember... rights) {
		Set<RuleMember> right = new HashSet();
		for (RuleMember rightItem : rights)
			right.add(rightItem);
		return new Rule(type, left, right);
	}
}
