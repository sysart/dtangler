//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.acceptancetests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.cycleanalysis.CycleValidator;
import org.dtangler.core.cycleanalysis.DependencyCycle;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.junit.Before;
import org.junit.Test;

public class CycleDetectionAcceptanceTest {

	private Dependencies dependencies;
	private Dependable foo = new TestDependable("foo");
	private Dependable bar = new TestDependable("bar");
	private Dependable bay = new TestDependable("bay");
	private Dependable koala = new TestDependable("koala");

	@Before
	public void setUp() {
		dependencies = new Dependencies();
	}

	@Test
	public void testSimpleCycle() {
		dependencies.addDependencies(foo, createMap(bar, koala));
		dependencies.addDependencies(bar, createMap(foo));

		CycleValidator analyzer = new CycleValidator(false);
		analyzer.analyze(dependencies);
		Map<Dependency, Set<Violation>> result = analyzer.getViolations();

		// make sure both cells show the cycle
		assertEquals(1, result.get(
				new Dependency(new TestDependable("bar"), new TestDependable(
						"foo"))).size());
		assertEquals(1, result.get(
				new Dependency(new TestDependable("foo"), new TestDependable(
						"bar"))).size());

		// make sure unrelelated cells do not show the cycle
		assertNull(result.get(new Dependency(new TestDependable("koala"),
				new TestDependable("foo"))));
		assertNull(result.get(new Dependency(new TestDependable("foo"),
				new TestDependable("koala"))));
	}

	@Test
	public void testSimpleCycleContent() {
		dependencies.addDependencies(foo, createMap(bar));
		dependencies.addDependencies(bar, createMap(foo));

		CycleValidator analyzer = new CycleValidator(false);
		analyzer.analyze(dependencies);
		Map<Dependency, Set<Violation>> result = analyzer.getViolations();

		// check cycle content from bar perspective
		DependencyCycle fooCycle = (DependencyCycle) result.get(
				new Dependency(new TestDependable("bar"), new TestDependable(
						"foo"))).iterator().next();
		assertEquals(Arrays.asList("bar", "foo", "bar"), fooCycle
				.getStringElements());

		// check cycle content from foo perspective
		DependencyCycle barCycle = (DependencyCycle) result.get(
				new Dependency(new TestDependable("foo"), new TestDependable(
						"bar"))).iterator().next();
		assertEquals(Arrays.asList("foo", "bar", "foo"), barCycle
				.getStringElements());

		// check that cycles are equal
		assertEquals(fooCycle, barCycle);
	}

	@Test
	public void testLongCycle() {
		dependencies.addDependencies(foo, createMap(bar));
		dependencies.addDependencies(bar, createMap(bay));
		dependencies.addDependencies(bay, createMap(foo));

		CycleValidator analyzer = new CycleValidator(false);
		analyzer.analyze(dependencies);
		Map<Dependency, Set<Violation>> result = analyzer.getViolations();

		// make sure all cycle cells show the cycle
		assertEquals(1, result.get(
				new Dependency(new TestDependable("foo"), new TestDependable(
						"bar"))).size());
		assertEquals(1, result.get(
				new Dependency(new TestDependable("bar"), new TestDependable(
						"bay"))).size());
		assertEquals(1, result.get(
				new Dependency(new TestDependable("bay"), new TestDependable(
						"foo"))).size());

		// these are in the wrong direction --> no cycle
		assertNull(result.get(new Dependency(new TestDependable("bar"),
				new TestDependable("foo"))));
		assertNull(result.get(new Dependency(new TestDependable("bay"),
				new TestDependable("bar"))));
		assertNull(result.get(new Dependency(new TestDependable("foo"),
				new TestDependable("bay"))));
	}

	@Test
	public void testComplicatedCycles() {
		dependencies.addDependencies(foo, createMap(bar, bay));
		dependencies.addDependencies(bar, createMap(bay));
		dependencies.addDependencies(bay, createMap(foo));

		CycleValidator analyzer = new CycleValidator(false);
		analyzer.analyze(dependencies);
		Map<Dependency, Set<Violation>> result = analyzer.getViolations();

		// bar has one cycle through foo and bay
		Dependency dependency = new Dependency(new TestDependable("foo"),
				new TestDependable("bar"));
		Set<Violation> violations = result.get(dependency);
		assertEquals(1, violations.size());
		assertEquals(1, result.get(
				new Dependency(new TestDependable("bar"), new TestDependable(
						"bay"))).size());

		// foo has two cycles through bar and bay and bay directly
		assertEquals(1, violations.size());
		assertEquals(1, result.get(
				new Dependency(new TestDependable("foo"), new TestDependable(
						"bay"))).size());
	}

	protected Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}
}
