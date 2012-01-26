// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.cycleanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class DependencyCycleDuplicateTest {

	@Test
	public void testEqualsAndHashcode3Nodes() {
		DependencyCycle same1 = makeCycle("foo", "bar");
		DependencyCycle same2 = makeCycle("bar", "foo");

		DependencyCycle dif1 = makeCycle("foo", "bar2");
		DependencyCycle dif2 = makeCycle("bar", "foo2");

		assertFalse(same1.equals(null));
		assertFalse(same1.equals(Boolean.TRUE));

		assertEquals(same1, same2);
		assertTrue(same1.hashCode() == same2.hashCode());

		assertFalse(same1.equals(dif1));
		assertFalse(same1.equals(dif2));
	}

	@Test
	public void testEqualsAndHashcode4Nodes() {
		DependencyCycle same1 = makeCycle("foo", "bar", "cuu");
		DependencyCycle same2 = makeCycle("bar", "cuu", "foo");
		DependencyCycle same3 = makeCycle("cuu", "foo", "bar");

		DependencyCycle dif1 = makeCycle("foo", "cuu", "bar");
		DependencyCycle dif2 = makeCycle("bar", "foo", "cuu");

		Set<DependencyCycle> two = makeSet(same1, dif1);
		assertEquals("cycle direction differs - not same cycle.", 2, two.size());

		Set<DependencyCycle> one = makeSet(same1, same2, same3);
		assertEquals("these cycles are the same.", 1, one.size());

		assertEquals(same1, same2);
		assertEquals(same1, same3);
		assertEquals(same2, same2);
		assertFalse(same1.equals(dif1));
		assertFalse(same1.equals(dif2));

		// These are not requirements but we know how our hashcode impl works...
		assertTrue(same1.hashCode() == same2.hashCode());
		assertTrue(same1.hashCode() == same3.hashCode());
		assertTrue(same2.hashCode() == same2.hashCode());
		assertTrue(same1.hashCode() == dif1.hashCode());
		assertTrue(same1.hashCode() == dif2.hashCode());
	}

	private Set<DependencyCycle> makeSet(DependencyCycle... cycles) {
		return new HashSet(Arrays.asList(cycles));
	}

	private DependencyCycle makeCycle(String... items) {
		List<String> list = new ArrayList(Arrays.asList(items));
		list.add(items[0]);
		return new TestDependencyCycle(list);
		// FIXME: this ctor wants 1st and last element to be the same
	}

}
