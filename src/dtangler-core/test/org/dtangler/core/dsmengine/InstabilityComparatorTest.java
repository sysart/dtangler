// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dsmengine;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.TestDependable;
import org.junit.Test;

public class InstabilityComparatorTest {

	private static final Dependable XOO = new TestDependable("xoo");
	private static final Dependable BAY = new TestDependable("bay");
	private static final Dependable BAR = new TestDependable("bar");
	private static final Dependable FOO = new TestDependable("foo");
	private static final Dependable XYZ = new TestDependable("xyz");

	@Test
	public void testSortOrder() {
		DependencyGraph deps = new DependencyGraph(null, new HashSet(Arrays
				.asList(FOO, BAR, BAY, XOO)));
		deps.addDependency(FOO, BAR);
		deps.addDependency(FOO, BAY);
		deps.addDependency(XOO, FOO);
		deps.addDependency(XOO, BAR);

		List<Dependable> items = new ArrayList(Arrays
				.asList(FOO, BAR, BAY, XOO));
		Collections.sort(items, new InstabilityComparator(deps));
		assertEquals(XOO, items.get(0)); // ce = 2, ca = 1
		assertEquals(FOO, items.get(1)); // ce = 2 ca = 0
		assertEquals(BAR, items.get(2)); // ce = 0 ca = 2
		assertEquals(BAY, items.get(3)); // ce = 0 ca = 1
	}

	@Test
	public void testSortOrderForEqualWeight() {
		DependencyGraph deps = createDepsWithEqualInstability();
		List<Dependable> items = new ArrayList(Arrays.asList(FOO, BAR, BAY,
				XOO, XYZ));
		Collections.sort(items, new InstabilityComparator(deps));

		// Instability = 0 for both
		assertEquals(BAR, items.get(3)); // R comes before Y
		assertEquals(BAY, items.get(4));
	}

	@Test
	public void testSortOrderForEqualInstability() {
		DependencyGraph deps = createDepsWithEqualInstability();
		List<Dependable> items = new ArrayList(Arrays.asList(FOO, BAR, BAY,
				XOO, XYZ));
		Collections.sort(items, new InstabilityComparator(deps));

		assertEquals(XYZ, items.get(0)); // I = 1
		assertEquals(XOO, items.get(1)); // I = 0.66, ce weight = 3
		assertEquals(FOO, items.get(2)); // I = 0.66, ce weight = 2
	}

	private DependencyGraph createDepsWithEqualInstability() {
		DependencyGraph deps = new DependencyGraph(null, new HashSet(Arrays
				.asList(FOO, BAR, BAY, XOO, XYZ)));
		deps.addDependency(FOO, BAR);
		deps.addDependency(FOO, BAY);
		deps.addDependency(XOO, FOO);
		deps.addDependency(XOO, BAR);
		deps.addDependency(XOO, BAR);
		deps.addDependency(XYZ, XOO);
		return deps;
	}
}
