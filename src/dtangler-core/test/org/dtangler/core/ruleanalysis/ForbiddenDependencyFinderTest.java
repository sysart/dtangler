// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.configuration.Group;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.ruleanalysis.Rule.Type;
import org.junit.Test;

public class ForbiddenDependencyFinderTest {
	private static final String item1Name = "item1";
	private static final String item2Name = "item2";
	private static final String item3Name = "item3";
	private static final String item4Name = "item4";

	private Dependable item1 = new TestDependable(item1Name);
	private Dependable item2 = new TestDependable(item2Name);
	private Dependable item3 = new TestDependable(item3Name);
	private Dependable item4 = new TestDependable(item4Name);

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

	@Test
	public void testNoRules() {

		Dependencies dependencies = new Dependencies();
		dependencies.addDependencies(item1, createMap(item2, item3));
		dependencies.addDependencies(item3, createMap(item4));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Collections.EMPTY_LIST);

		analyzer.analyze(dependencies);
		Map<Dependency, Set<Violation>> result = analyzer.getViolations();

		assertTrue(result.isEmpty());

	}

	private Set<Violation> getAllViolations(
			Map<Dependency, Set<Violation>> violations) {
		Set<Violation> result = new HashSet();
		for (Set<Violation> subSet : violations.values())
			result.addAll(subSet);
		return result;

	}

	@Test
	public void testSpecificItemRule() {
		Dependencies dependencies = new Dependencies();
		dependencies.addDependencies(item1, createMap(item2, item3));
		dependencies.addDependencies(item3, createMap(item4));

		Dependable item1Dep = new TestDependable(item1Name);
		Dependable item2Dep = new TestDependable(item2Name);

		Rule rule = createRule(Type.cannotDepend, new SingleRuleMember(item1Dep
				.getDisplayName()), new SingleRuleMember(item2Dep
				.getDisplayName()));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(1, result.size());
		assertTrue(result.contains(new RuleViolation(new Dependency(item1Dep,
				item2Dep), rule)));
	}

	@Test
	public void testMultipleViolationsForSameDependency() {
		Dependencies dependencies = new Dependencies();
		dependencies.addDependencies(item1, createMap(item2));

		Dependable item1Dep = new TestDependable(item1Name);
		Dependable item2Dep = new TestDependable(item2Name);

		Rule rule1 = createRule(Type.cannotDepend, new SingleRuleMember(
				item1Dep.getDisplayName()), new GroupRuleMember(createGroup(
				"Group1", item2Name, item3Name, item4Name)));
		Rule rule2 = createRule(Type.cannotDepend, new SingleRuleMember(
				item1Dep.getDisplayName()), new SingleRuleMember(item2Dep
				.getDisplayName()));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule1, rule2));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(2, result.size());
		assertTrue(result.contains(new RuleViolation(new Dependency(item1Dep,
				item2Dep), rule1)));
		assertTrue(result.contains(new RuleViolation(new Dependency(item1Dep,
				item2Dep), rule2)));
	}

	@Test
	public void testAllowedDependencyOverWritesForbiddenDependency() {
		Dependencies dependencies = new Dependencies();
		dependencies.addDependencies(item1, createMap(item2, item3, item4));

		Dependable item1Dep = new TestDependable(item1Name);

		Rule rule1 = createRule(Type.cannotDepend, new SingleRuleMember(
				item1Dep.getDisplayName()), new GroupRuleMember(createGroup(
				"x", item2Name, item3Name, item4Name)));
		Rule rule2 = createRule(Type.canDepend, new SingleRuleMember(item1Dep
				.getDisplayName()), new GroupRuleMember(createGroup("x",
				item3Name)));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule1, rule2));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(2, result.size());
		assertTrue(result.contains(new RuleViolation(new Dependency(item1Dep,
				new TestDependable(item2Name)), rule1)));
		assertTrue(result.contains(new RuleViolation(new Dependency(item1Dep,
				new TestDependable(item4Name)), rule1)));
	}

	@Test
	public void testRightSideWildCard() {
		Dependencies dependencies = new Dependencies();
		Dependable foo = new TestDependable("eg.foo");
		Dependable bar1 = new TestDependable("eg.bar.c1");
		Dependable bar2 = new TestDependable("eg.bar.c2");
		dependencies.addDependencies(foo, createMap(bar1, bar2));

		Dependable fooDep = new TestDependable("eg.foo");
		Rule rule = createRule(Type.cannotDepend, new SingleRuleMember(fooDep
				.getDisplayName()), new GroupRuleMember(createGroup("bar",
				"eg.bar.*")));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(2, result.size());
		assertTrue(result.contains(new RuleViolation(new Dependency(fooDep,
				new TestDependable("eg.bar.c1")), rule)));
		assertTrue(result.contains(new RuleViolation(new Dependency(fooDep,
				new TestDependable("eg.bar.c2")), rule)));
	}

	@Test
	public void testGroupOnBothSides() {
		Dependencies dependencies = new Dependencies();
		Dependable foo = new TestDependable("foo");
		Dependable bar = new TestDependable("bar");
		dependencies.addDependencies(foo, createMap(item1, item2, item3));
		dependencies.addDependencies(bar, createMap(item2));

		Rule rule = createRule(Type.cannotDepend, new GroupRuleMember(
				createGroup("Group1", "foo", "bar")), new GroupRuleMember(
				createGroup("Group2", "item1", "item2")));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		Dependable fooDep = new TestDependable("foo");
		Dependable barDep = new TestDependable("bar");

		assertEquals(3, result.size());
		assertTrue(result.contains(new RuleViolation(new Dependency(fooDep,
				new TestDependable("item1")), rule)));
		assertTrue(result.contains(new RuleViolation(new Dependency(fooDep,
				new TestDependable("item2")), rule)));
		assertTrue(result.contains(new RuleViolation(new Dependency(barDep,
				new TestDependable("item2")), rule)));
	}

	@Test
	public void testGroupWithWildCardsOnBothSides() {
		Dependencies dependencies = new Dependencies();
		Dependable foo1 = new TestDependable("foo.c1");
		Dependable foo2 = new TestDependable("foo.c2");
		Dependable bar1 = new TestDependable("bar.c1");
		dependencies.addDependencies(foo1, createMap(item1, item2));
		dependencies.addDependencies(foo2, createMap(item2));
		dependencies.addDependencies(bar1, createMap(item2));

		Rule rule = createRule(Type.cannotDepend, new GroupRuleMember(
				createGroup("foo", "foo*")), new GroupRuleMember(createGroup(
				"item", "item*")));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		Dependable foo1Dep = new TestDependable("foo.c1");
		Dependable foo2Dep = new TestDependable("foo.c2");

		assertEquals(3, result.size());
		assertTrue(result.contains(new RuleViolation(new Dependency(foo1Dep,
				new TestDependable("item1")), rule)));
		assertTrue(result.contains(new RuleViolation(new Dependency(foo1Dep,
				new TestDependable("item2")), rule)));
		assertTrue(result.contains(new RuleViolation(new Dependency(foo2Dep,
				new TestDependable("item2")), rule)));
	}

	@Test
	public void testDenyAll() {
		Dependencies dependencies = new Dependencies();
		Dependable foo = new TestDependable("foo");
		Dependable bar = new TestDependable("bar");
		Dependable bay = new TestDependable("bay");
		dependencies.addDependencies(foo, createMap(item1, item2));
		dependencies.addDependencies(bar, createMap(item1, item2));
		dependencies.addDependencies(bay, createMap(item1, item2));

		Rule rule = createRule(Type.cannotDepend, new GroupRuleMember(
				createGroup("all", "*")), new GroupRuleMember(createGroup(
				"all", "*")));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(6, result.size());
	}

	@Test
	public void testDenyAllAndAllowSome() {
		Dependencies dependencies = new Dependencies();
		Dependable foo = new TestDependable("foo");
		Dependable bar = new TestDependable("bar");
		Dependable bay = new TestDependable("bay");
		dependencies.addDependencies(foo, createMap(item1, item2));
		dependencies.addDependencies(bar, createMap(item1, item2));
		dependencies.addDependencies(bay, createMap(item1, item2));

		Rule rule1 = createRule(Type.cannotDepend, new GroupRuleMember(
				createGroup("all", "*")), new GroupRuleMember(createGroup(
				"all", "*")));

		Rule rule2 = createRule(Type.canDepend, new GroupRuleMember(
				createGroup("bar", "bar")), new GroupRuleMember(createGroup(
				"all", "*")));

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule1, rule2));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(4, result.size());
	}

	private Dependencies createDependencies() {
		Dependencies dependencies = new Dependencies();
		dependencies.addChild(package1, fooClass1);
		dependencies.addChild(package1, fooClass2);
		dependencies.addChild(package2, barClass1);
		dependencies.addChild(package2, barClass2);
		dependencies.addChild(package2, barClass3);
		dependencies.addChild(package3, bayClass1);
		dependencies.addChild(package3, bayClass2);

		return dependencies;
	}

	@Test
	public void testPackageRuleAffectsClassScope() {
		Dependencies dependencies = createDependencies();
		dependencies.addDependencies(package1, createMap(package2));
		dependencies.addDependencies(fooClass1, createMap(barClass1));

		Rule packageLevelRule = createRule(Rule.Type.cannotDepend,
				new SingleRuleMember("eg.foo"), new SingleRuleMember("eg.bar"));
		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(packageLevelRule));
		analyzer.analyze(dependencies);
		Set<Violation> results = getAllViolations(analyzer.getViolations());
		assertEquals(2, results.size());
		assertTrue(results.contains(new RuleViolation(new Dependency(package1,
				package2), packageLevelRule)));
		assertTrue(results.contains(new RuleViolation(new Dependency(fooClass1,
				barClass1), packageLevelRule)));
	}

	@Test
	public void testAllowedDependencyOverwritesForbiddenDependencyWithGroups() {
		Dependencies dependencies = createDependencies();

		dependencies.addDependencies(package1, createMap(package2, package3));
		dependencies.addDependencies(package2, createMap(package3));
		dependencies
				.addDependencies(fooClass1, createMap(barClass1, bayClass2));
		dependencies.addDependencies(barClass1, createMap(barClass2));
		dependencies.addDependencies(barClass3, createMap(bayClass2));

		GroupRuleMember groupAll = new GroupRuleMember(createGroup("all", "*"));
		GroupRuleMember groupPart = new GroupRuleMember(createGroup("part",
				"*eg.ba*"));

		Rule rule1 = createRule(Type.cannotDepend, groupAll, groupPart);
		Rule rule2 = createRule(Type.canDepend, groupPart, groupPart);

		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(rule1, rule2));
		analyzer.analyze(dependencies);
		Set<Violation> result = getAllViolations(analyzer.getViolations());

		assertEquals(4, result.size());
	}

	@Test
	public void testFindOnlyForbiddenDependencies() {
		Dependencies dependencies = createDependencies();
		setDependencies(dependencies);

		Rule packageLevelRule = createRule(Rule.Type.cannotDepend,
				new SingleRuleMember("eg.foo"), new SingleRuleMember("eg.bar"));
		Rule allowedRule = createRule(Rule.Type.canDepend,
				new SingleRuleMember("eg.foo.Class1"), new SingleRuleMember(
						"eg.bar.Class1"));
		ForbiddenDependencyFinder analyzer = new ForbiddenDependencyFinder(
				Arrays.asList(packageLevelRule, allowedRule));

		analyzer.analyze(dependencies);
		Set<Violation> results = getAllViolations(analyzer.getViolations());

		assertEquals(2, results.size());
		assertTrue(results.contains(new RuleViolation(new Dependency(package1,
				package2), packageLevelRule)));
		assertTrue(results.contains(new RuleViolation(new Dependency(fooClass2,
				barClass1), packageLevelRule)));
	}

	private void setDependencies(Dependencies dependencies) {
		dependencies.addDependencies(package1, createMap(package2));
		dependencies
				.addDependencies(fooClass1, createMap(fooClass2, barClass1));
		dependencies.addDependencies(fooClass2, createMap(barClass1));
		dependencies.addDependencies(barClass2, createMap(barClass1));
	}

	private Group createGroup(String name, String... items) {
		return new Group(name, new HashSet(Arrays.asList(items)));
	}

	private Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}

	private Rule createRule(Type type, RuleMember leftSide,
			RuleMember... rightSideMembers) {
		return new Rule(type, leftSide, new HashSet(Arrays
				.asList(rightSideMembers)));
	}
}
