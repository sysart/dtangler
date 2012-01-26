// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.cycleanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.TestDependable;
import org.junit.Test;

public class DependencyCycleTest {

	@Test
	public void testEqualsAndHashCode() {
		DependencyCycle same1 = new TestDependencyCycle(Arrays.asList("foo",
				"bar", "foo"));
		DependencyCycle same2 = new TestDependencyCycle(Arrays.asList("foo",
				"bar", "foo"));
		DependencyCycle different = new TestDependencyCycle(Arrays.asList(
				"bay", "bar", "bay"));

		assertEquals(same1, same2);
		assertEquals(same1.hashCode(), same2.hashCode());

		assertFalse(same1.equals(different));
		assertFalse(same1.equals(null));
		assertFalse(same1.equals(Boolean.TRUE));
	}

	@Test
	public void testAppliesTo() {
		Dependable depA = new TestDependable("a");
		Dependable depB = new TestDependable("b");
		Dependable depC = new TestDependable("c");

		Violation v = new DependencyCycle(Arrays.asList(depA, depB));

		assertTrue(v.appliesTo(new HashSet(Arrays.asList(depA, depB, depC))));
		assertTrue(v.appliesTo(new HashSet(Arrays.asList(depA, depB))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depA, depC))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depA))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depB))));
		assertFalse(v.appliesTo(new HashSet(Arrays.asList(depC))));
	}
}
