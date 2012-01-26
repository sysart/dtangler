// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DependencyGraphTest {

	private static final Dependable XOO = new TestDependable("boo");
	private static final Dependable BAY = new TestDependable("bay");
	private static final Dependable BAR = new TestDependable("bar");
	private static final Dependable FOO = new TestDependable("foo");
	private static final Dependable XYZ = new TestDependable("xyz");
	private DependencyGraph deps;

	@Before
	public void setUp() {
		deps = new DependencyGraph(null, new HashSet(Arrays.asList(FOO, BAR,
				BAY, XOO, XYZ)));
		deps.addDependency(FOO, BAR);
		deps.addDependency(FOO, BAY);
		deps.addDependency(FOO, BAY);
		deps.addDependency(XOO, FOO);
		deps.addDependency(XOO, BAY);
		deps.addDependency(BAY, BAR);
	}

	@Test
	public void testGetDependants() {
		assertResult(deps.getDependants(FOO), XOO);
		assertResult(deps.getDependants(BAR), FOO, BAY);
		assertResult(deps.getDependants(BAY), FOO, XOO);
		assertResult(deps.getDependants(XOO), new Dependable[] {});
	}

	@Test
	public void testGetDependencies() {
		assertResult(deps.getDependencies(FOO), BAR, BAY);
		assertResult(deps.getDependencies(BAR), new Dependable[] {});
		assertResult(deps.getDependencies(BAY), BAR);
		assertResult(deps.getDependencies(XOO), FOO, BAY);
	}

	@Test
	public void testDependencyWeight() {
		assertEquals(1, deps.getDependencyWeight(FOO, BAR));
		assertEquals(2, deps.getDependencyWeight(FOO, BAY));
		assertEquals(0, deps.getDependencyWeight(FOO, XOO));
	}

	@Test
	public void testInstability() {
		assertInstability(2, 1, deps.getInstability(FOO));
		assertInstability(2, 0, deps.getInstability(XOO));
		assertInstability(0, 1, deps.getInstability(BAR));
		assertInstability(0, 0, deps.getInstability(XYZ));
		assertInstability(1, 2, deps.getInstability(BAY));
	}

	@Test
	public void testDependencyWeightDoesNotAffectInstability() {
		assertInstability(2, 1, deps.getInstability(FOO));
		deps.addDependency(FOO, BAR);
		assertInstability(2, 1, deps.getInstability(FOO));
		deps.addDependency(FOO, BAR);
		assertInstability(2, 1, deps.getInstability(FOO));
		deps.addDependency(XOO, FOO);
		assertInstability(2, 1, deps.getInstability(FOO));
	}

	private void assertInstability(float dependencies, float dependants,
			float actual) {
		float expected = dependencies / (dependants + dependencies);
		// Workaround for incorrect double assertEquals in some JUnit4 versions
		assertTrue(Double.valueOf(expected).equals(Double.valueOf(actual)));
	}

	@Test
	public void testDependencyOnSelfIsIgnored() {
		deps.addDependency(XYZ, XYZ);
		assertTrue(deps.getDependants(XYZ).isEmpty());
		assertTrue(deps.getDependencies(XYZ).isEmpty());
	}

	@Test
	public void testOuterDependency() {
		deps.addDependency(XYZ, new TestDependable("outer"));
		assertTrue(deps.getDependencies(XYZ).isEmpty());
	}

	@Test
	public void testOuterDependant() {
		deps.addDependency(new TestDependable("outer"), XYZ);
		assertTrue(deps.getDependants(XYZ).isEmpty());
	}

	private void assertResult(Set<Dependable> actual, Dependable... expected) {
		assertEquals(actual.size(), expected.length);
		for (Object item : expected)
			assertTrue(actual.contains(item));
	}
}
