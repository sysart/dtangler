// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.cycleanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.junit.Before;
import org.junit.Test;

public class CycleValidatorTest {

	private static final Dependable C1 = new TestDependable("c1");
	private static final Dependable C2 = new TestDependable("c2");
	private static final Dependable L1 = new TestDependable("l1");
	private static final Dependable L2 = new TestDependable("l2");
	private static final Dependable L3 = new TestDependable("l3");
	private static final Dependable INNOCENT = new TestDependable("not me");

	private CycleValidator cycleFinder;

	@Before
	public void setUp() {
		Dependencies dependencies = new Dependencies();
		dependencies.addDependencies(INNOCENT, createMap(C1));
		dependencies.addDependencies(C1, createMap(C2));
		dependencies.addDependencies(C2, createMap(C1));

		dependencies.addDependencies(INNOCENT, createMap(L1));
		dependencies.addDependencies(L1, createMap(L2));
		dependencies.addDependencies(L2, createMap(L3));
		dependencies.addDependencies(L3, createMap(L1));

		cycleFinder = new CycleValidator(false);
		cycleFinder.analyze(dependencies);
	}

	// TODO split
	@Test
	public void testGetCycles() {
		assertNull(cycleFinder.getViolations()
				.get(new Dependency(INNOCENT, C1)));

		Set<Violation> c1cycles = cycleFinder.getViolations().get(
				new Dependency(C1, C2));
		assertEquals(1, c1cycles.size());
		DependencyCycle cycle = (DependencyCycle) c1cycles.iterator().next();
		assertEquals(3, cycle.getStringElements().size());
		assertCycle(cycle, C1, C2, C1);

		Set<Violation> l2cycles = cycleFinder.getViolations().get(
				new Dependency(L2, L3));
		assertEquals(1, l2cycles.size());
		cycle = (DependencyCycle) l2cycles.iterator().next();
		assertCycle(cycle, L2, L3, L1, L2);
	}

	void assertCycle(DependencyCycle actual, Dependable... expected) {
		assertEquals(expected.length, actual.getElements().size());
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual.getElements().get(i));
	}

	protected Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}
}
