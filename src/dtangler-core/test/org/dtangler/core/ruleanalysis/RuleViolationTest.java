// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.testutil.ruleanalysis.MockRule;
import org.junit.Test;

public class RuleViolationTest {

	@Test
	public void testGetRightRuleViolationText() {

		TestDependable packageA = new TestDependable("a");
		TestDependable packageB = new TestDependable("b");
		Dependency packageDependency = new Dependency(packageA, packageB);

		TestDependable classA = new TestDependable("a.A");
		TestDependable classB = new TestDependable("b.B");
		Dependency classDependency = new Dependency(classA, classB);

		Rule rule = new Rule(Rule.Type.cannotDepend, new SingleRuleMember("a"),
				createSet(new SingleRuleMember("b")));

		Violation packageViolation = new RuleViolation(packageDependency, rule);
		Violation classViolation = new RuleViolation(classDependency, rule);

		String violationText = "Rule violation: a cannot depend on b";

		assertEquals(violationText, packageViolation.asText());
		assertEquals(violationText, classViolation.asText());
	}

	@Test
	public void testAppliesTo() {
		TestDependable depA = new TestDependable("a");
		TestDependable depB = new TestDependable("b");
		TestDependable depC = new TestDependable("c");

		Violation v = new RuleViolation(new Dependency(depA, depB),
				new MockRule());

		assertTrue(v.appliesTo(new HashSet(Arrays.asList(depA, depB, depC))));
		assertTrue(v.appliesTo(new HashSet(Arrays.asList(depA, depB))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depA))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depB))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depC))));
	}

	protected Set<RuleMember> createSet(RuleMember... items) {
		return new HashSet(Arrays.asList(items));
	}
}
