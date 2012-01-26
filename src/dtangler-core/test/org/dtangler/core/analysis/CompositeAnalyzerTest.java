//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.ruleanalysis.Rule;
import org.dtangler.core.ruleanalysis.RuleMember;
import org.dtangler.core.ruleanalysis.RuleViolation;
import org.dtangler.core.ruleanalysis.SingleRuleMember;
import org.junit.Test;

public class CompositeAnalyzerTest {

	@Test
	public void testAllViolationsAreCollected() {
		CompositeAnalyzer analyzer = new CompositeAnalyzer();
		MockAnalyzer analyzer1 = new MockAnalyzer();
		Dependency dependency1 = new Dependency(new TestDependable("foo"),
				new TestDependable("bar"));
		Dependency dependency2 = new Dependency(new TestDependable("foo"),
				new TestDependable("bay"));
		Dependencies dependencies = new Dependencies();

		Violation violation1 = new MockViolation("v1");
		Violation violation2 = new MockViolation("v2");
		Violation violation3 = new MockViolation("v3");

		analyzer1.addViolation(dependency1, violation1);
		MockAnalyzer analyzer2 = new MockAnalyzer();
		analyzer2.addViolation(dependency1, violation2);
		analyzer2.addViolation(dependency2, violation3);

		analyzer.add(analyzer1);
		analyzer.add(analyzer2);

		analyzer.analyze(dependencies);

		Map<Dependency, Set<Violation>> violations = analyzer.getViolations();
		assertEquals(2, violations.size());
		assertTrue(violations.keySet().contains(dependency1));
		assertTrue(violations.keySet().contains(dependency2));

		assertEquals(2, violations.get(dependency1).size());
		assertTrue(violations.get(dependency1).contains(violation1));
		assertTrue(violations.get(dependency1).contains(violation2));

		assertEquals(1, violations.get(dependency2).size());
		assertTrue(violations.get(dependency2).contains(violation3));
	}

	@Test
	public void testIsInvalidIfOneResultIsInvalid() {
		CompositeAnalyzer analyzer = new CompositeAnalyzer();
		analyzer.add(new MockAnalyzer(true));
		analyzer.add(new MockAnalyzer(true));
		Dependencies dependencies = new Dependencies();

		analyzer.analyze(dependencies);
		assertTrue(analyzer.isValidResult());

		analyzer.add(new MockAnalyzer(false));
		analyzer.analyze(dependencies);
		assertFalse(analyzer.isValidResult());
	}

	@Test
	public void testAddMultipleViolationsToSameDependency() {
		Dependency dependency = new Dependency(new TestDependable("Foo"),
				new TestDependable("Bar"));
		Rule packageRule = new Rule(Rule.Type.cannotDepend,
				new SingleRuleMember("package1.Foo"),
				createSet(new SingleRuleMember("package2.Bar")));
		Rule classRule = new Rule(Rule.Type.cannotDepend, new SingleRuleMember(
				"Foo"), createSet(new SingleRuleMember("Bar")));
		Violation violation1 = new RuleViolation(dependency, packageRule);
		Violation violation2 = new RuleViolation(dependency, classRule);

		DependencyAnalyzer analyzer = new MockAnalyzer();
		analyzer.addViolation(dependency, violation1);
		analyzer.addViolation(dependency, violation2);

		Map<Dependency, Set<Violation>> allViolations = analyzer
				.getViolations();
		Set<Violation> values = new HashSet(allViolations.get(dependency));

		assertEquals(2, values.size());
		assertTrue(values.contains(violation1));
		assertTrue(values.contains(violation2));
	}

	private Set<RuleMember> createSet(SingleRuleMember... items) {
		return new HashSet(Arrays.asList(items));
	}
}
