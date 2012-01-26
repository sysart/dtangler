// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.analysisresult.Violation.Severity;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.ruleanalysis.RuleViolation;
import org.dtangler.core.testutil.ruleanalysis.MockRule;
import org.junit.Before;
import org.junit.Test;

public class AnalysisResultTest {
	private Dependable d1;
	private Dependable d2;
	private Dependable d3;
	private Dependable d4;
	private Dependable d5;

	private Dependency d1d2;
	private Dependency d1d3;
	private Dependency d1d4;
	private Dependency d1d5;

	Map<Dependency, Set<Violation>> violations;

	@Before
	public void setUp() {
		violations = new HashMap();
		d1 = new TestDependable("d1");
		d2 = new TestDependable("d2");
		d3 = new TestDependable("d3");
		d4 = new TestDependable("d4");
		d5 = new TestDependable("d5");

		d1d2 = new Dependency(d1, d2);
		d1d3 = new Dependency(d1, d3);
		d1d4 = new Dependency(d2, d4);
		d1d5 = new Dependency(d1, d5);
	}

	@Test
	public void testBasicProperties() {
		AnalysisResult result = new AnalysisResult(new HashMap(),
				new HashSet(), true);
		assertTrue(result.isValid());
		assertFalse(result.hasViolations());

		addRuleViolation(d1d2, Arrays.asList(d1d3));
		result = new AnalysisResult(violations, Collections.EMPTY_SET, false);
		assertFalse(result.isValid());
		assertTrue(result.hasViolations());
	}

	@Test
	public void testViolations() {
		addRuleViolation(d1d2, Arrays.asList(d1d3, d1d4));
		addRuleViolation(d1d3, Arrays.asList(d1d2, d1d5));

		AnalysisResult result = new AnalysisResult(violations,
				Collections.EMPTY_SET, true);
		assertTrue(result.hasViolations(d1d2));
		assertTrue(result.hasViolations(d1d3));
		assertFalse(result.hasViolations(d1d4));
		assertFalse(result.hasViolations(d1d5));

		Set<Violation> allViolations = result.getAllViolations();
		assertEquals(4, allViolations.size());
		assertTrue(allViolations.contains(new RuleViolation(d1d2,
				new MockRule())));
		assertTrue(allViolations.contains(new RuleViolation(d1d3,
				new MockRule())));
		assertTrue(allViolations.contains(new RuleViolation(d1d4,
				new MockRule())));
		assertTrue(allViolations.contains(new RuleViolation(d1d5,
				new MockRule())));

		Set<Violation> violationsd1d2 = result.getViolations(d1d2);
		assertEquals(2, violationsd1d2.size());
		assertTrue(violationsd1d2.contains(new RuleViolation(d1d3,
				new MockRule())));
		assertTrue(violationsd1d2.contains(new RuleViolation(d1d4,
				new MockRule())));

		Set<Violation> violationsd1d3 = result.getViolations(d1d3);
		assertEquals(2, violationsd1d3.size());
		assertTrue(violationsd1d3.contains(new RuleViolation(d1d2,
				new MockRule())));
		assertTrue(violationsd1d3.contains(new RuleViolation(d1d5,
				new MockRule())));

		assertEquals(0, result.getViolations(d1d4).size());
		assertEquals(0, result.getViolations(d1d5).size());
	}

	@Test
	public void testGetViolationsForDependables() {
		Dependency d2d3 = new Dependency(d2, d3);

		violations.put(d1d2, createViolationSet(createViolation("violation1",
				d1), createViolation("violation2", d1, d2)));
		violations.put(d2d3, createViolationSet(createViolation("violation3",
				d2, d3)));

		AnalysisResult result = new AnalysisResult(violations,
				Collections.EMPTY_SET, true);

		assertViolationsByName(result.getViolations(new HashSet(Arrays.asList(
				d1, d2, d3, d4, d5))), "violation1", "violation2", "violation3");

		assertViolationsByName(result.getViolations(new HashSet(Arrays
				.asList(d1))), "violation1", "violation2");

		assertViolationsByName(result.getViolations(new HashSet(Arrays
				.asList(d2))), "violation2", "violation3");

		assertViolationsByName(result.getViolations(new HashSet(Arrays
				.asList(d3))), "violation3");
	}

	@Test
	public void testGetChildViolationsForDependables() {

		Set<Violation> childViolations = new HashSet();
		childViolations.addAll(new HashSet(Arrays.asList(new ChildViolation(d1,
				createViolation("violationName", new TestDependable("sub1",
						TestScope.scope2))))));
		AnalysisResult result = new AnalysisResult(violations, childViolations,
				true);
		assertViolationsByName(result.getChildViolations(new HashSet(Arrays
				.asList(d1, d2, d3))), "d1 contains a violation: violationName");
		assertEquals(0, result.getChildViolations(
				new HashSet(Arrays.asList(d2, d3, d4, d5))).size());
	}

	private Violation createViolation(String name, Dependable... appliesTo) {
		return new MockViolation(name, Severity.warning, new HashSet(Arrays
				.asList(appliesTo)));
	}

	private Set<Violation> createViolationSet(Violation... violations) {
		return new HashSet(Arrays.asList(violations));

	}

	private void assertViolationsByName(Set<Violation> actual,
			String... expected) {
		Set<String> expectedNames = new HashSet(Arrays.asList(expected));

		assertEquals(expected.length, actual.size());
		for (Violation v : actual)
			assertTrue(expectedNames.contains(v.asText()));
	}

	@Test
	public void testSeverity() {
		Violation w1 = new MockViolation("w1", Severity.warning);
		Violation w2 = new MockViolation("w2", Severity.warning);
		Violation e1 = new MockViolation("e1", Severity.error);
		Set<Violation> list = new HashSet<Violation>();
		list.addAll(Arrays.asList(w1, w2, e1));

		violations.put(d1d2, list);
		AnalysisResult result = new AnalysisResult(violations,
				Collections.EMPTY_SET, true);

		assertEquals(2, result.getViolations(d1d2, Severity.warning).size());
		assertTrue(result.getViolations(d1d2, Severity.warning).contains(w1));
		assertTrue(result.getViolations(d1d2, Severity.warning).contains(w2));

		assertEquals(1, result.getViolations(d1d2, Severity.error).size());
		assertTrue(result.getViolations(d1d2, Severity.error).contains(e1));
	}

	@Test
	public void testGetChildViolationsBySeverity() {
		Violation w1 = new MockViolation("w1", Severity.warning, Collections
				.singleton(d1));
		Violation w2 = new MockViolation("w2", Severity.warning, new HashSet(
				Arrays.asList(d1, d2)));
		Violation e1 = new MockViolation("e1", Severity.error, Collections
				.singleton(d1));
		Set<Violation> childViolations = new HashSet();
		childViolations.addAll(new HashSet(Arrays.asList(w1, w2, e1)));
		childViolations.add(w2);

		AnalysisResult result = new AnalysisResult(Collections.EMPTY_MAP,
				childViolations, false);

		assertEquals(2, result.getChildViolations(d1, Severity.warning).size());
		assertTrue(result.getChildViolations(d1, Severity.warning).contains(w1));
		assertTrue(result.getChildViolations(d1, Severity.warning).contains(w2));

		assertEquals(1, result.getChildViolations(d1, Severity.error).size());
		assertTrue(result.getChildViolations(d1, Severity.error).contains(e1));

		assertEquals(1, result.getChildViolations(d2, Severity.warning).size());
		assertTrue(result.getChildViolations(d2, Severity.warning).contains(w2));

		assertTrue(result.getChildViolations(d2, Severity.error).isEmpty());
	}

	private void addRuleViolation(Dependency key, List<Dependency> dependencies) {
		Set<Violation> list = new HashSet<Violation>();
		for (Dependency d : dependencies)
			list.add(new RuleViolation(d, new MockRule()));
		violations.put(key, list);
	}

}
