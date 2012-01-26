//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.cycleanalysis.DependencyCycle;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.ruleanalysis.Rule;
import org.dtangler.core.ruleanalysis.RuleMember;
import org.dtangler.core.ruleanalysis.RuleViolation;
import org.dtangler.core.ruleanalysis.SingleRuleMember;
import org.junit.Before;
import org.junit.Test;

public class ChildViolationFinderTest {

	private Dependencies dependencies = new Dependencies();
	ChildViolationFinder finder;

	private Dependable package1 = new TestDependable("eg.foo", TestScope.scope2);
	private Dependable package2 = new TestDependable("eg.bar", TestScope.scope2);
	private Dependable package3 = new TestDependable("eg.bay", TestScope.scope2);

	private Dependable fooClass1 = new TestDependable("eg.foo.Class1",
			TestScope.scope3);
	private Dependable fooClass2 = new TestDependable("eg.foo.Class2",
			TestScope.scope3);
	private Dependable barClass1 = new TestDependable("eg.bar.Class1",
			TestScope.scope3);
	private Dependable barClass2 = new TestDependable("eg.bar.Class2",
			TestScope.scope3);
	private Dependable barClass3 = new TestDependable("eg.bar.Class3",
			TestScope.scope3);
	private Dependable bayClass1 = new TestDependable("eg.bay.Class1",
			TestScope.scope3);
	private Dependable bayClass2 = new TestDependable("eg.bay.Class2",
			TestScope.scope3);

	@Before
	public void setUp() {
		dependencies.addChild(package1, fooClass1);
		dependencies.addChild(package1, fooClass2);
		dependencies.addChild(package2, barClass1);
		dependencies.addChild(package2, barClass2);
		dependencies.addChild(package2, barClass3);
		dependencies.addChild(package3, bayClass1);
		dependencies.addChild(package3, bayClass2);

		finder = new ChildViolationFinder(dependencies);
	}

	@Test
	public void testFindCyclesInsideParents() {
		Map<Dependable, Set<Violation>> childViolations = new HashMap();

		childViolations = finder
				.findChildViolationsForParents(createCycleViolationMap());
		assertEquals(1, childViolations.size());
		assertTrue(childViolations.containsKey(package2));
		Set<Violation> violationsForPackage2 = childViolations.get(package2);
		assertEquals(2, violationsForPackage2.size());
		assertTrue(violationsForPackage2.contains(new ChildViolation(package2,
				new DependencyCycle(Arrays.asList(barClass1, barClass2)))));
		assertTrue(violationsForPackage2.contains(new ChildViolation(package2,
				new DependencyCycle(Arrays.asList(barClass2, barClass3)))));
	}

	private Map<Dependency, Set<Violation>> createCycleViolationMap() {
		Map<Dependency, Set<Violation>> violations = new HashMap();
		addDependencyCycle(fooClass1, bayClass1, violations);
		addDependencyCycle(barClass1, barClass2, violations);
		addDependencyCycle(barClass2, barClass3, violations);
		return violations;
	}

	private void addDependencyCycle(Dependable part1, Dependable part2,
			Map<Dependency, Set<Violation>> violations) {
		violations.put(new Dependency(part1, part2), new HashSet(Arrays
				.asList(new DependencyCycle(Arrays.asList(part1, part2)))));
	}

	@Test
	public void testFindRuleViolationsInsideParents() {
		Map<Dependable, Set<Violation>> childViolations = new HashMap();
		finder = new ChildViolationFinder(dependencies);
		childViolations = finder
				.findChildViolationsForParents(createRuleViolationMap());
		assertEquals(2, childViolations.size());
		assertTrue(childViolations.containsKey(package1));
		assertTrue(childViolations.containsKey(package2));

		Set<Violation> violationsForPackage1 = childViolations.get(package1);
		assertEquals(1, violationsForPackage1.size());
		assertTrue(violationsForPackage1.contains(new ChildViolation(package1,
				createRuleViolation(fooClass1, fooClass2))));

		Set<Violation> violationsForPackage2 = childViolations.get(package2);
		assertEquals(2, violationsForPackage2.size());
		assertTrue(violationsForPackage2.contains(new ChildViolation(package2,
				createRuleViolation(barClass1, barClass3))));
		assertTrue(violationsForPackage2.contains(new ChildViolation(package2,
				createRuleViolation(barClass2, barClass3))));
	}

	private RuleViolation createRuleViolation(Dependable dependant,
			Dependable dependee) {
		Dependency dependency = new Dependency(dependant, dependee);
		Rule rule = createRule(dependant, dependee);
		return new RuleViolation(dependency, rule);
	}

	private Map<Dependency, Set<Violation>> createRuleViolationMap() {
		Map<Dependency, Set<Violation>> violations = new HashMap();
		addRuleViolation(fooClass1, fooClass2, violations);
		addRuleViolation(fooClass1, barClass2, violations);
		addRuleViolation(barClass1, barClass3, violations);
		addRuleViolation(barClass2, barClass3, violations);
		return violations;
	}

	private void addRuleViolation(Dependable dependant, Dependable dependee,
			Map<Dependency, Set<Violation>> violations) {
		Dependency dependency = new Dependency(dependant, dependee);
		RuleViolation violation = createRuleViolation(dependant, dependee);
		violations.put(dependency, new HashSet(Arrays.asList(violation)));
	}

	private Rule createRule(Dependable left, Dependable... right) {
		Set<RuleMember> rightSide = new HashSet();
		for (Dependable dep : right) {
			rightSide.add(new SingleRuleMember(dep.getDisplayName()));
		}
		return new Rule(Rule.Type.cannotDepend, new SingleRuleMember(left
				.getDisplayName()), rightSide);
	}
}
