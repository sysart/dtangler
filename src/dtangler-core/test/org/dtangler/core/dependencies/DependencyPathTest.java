// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

public class DependencyPathTest {

	@Test
	public void testGetDependencyByDependant() {
		Dependable foo = new TestDependable("foo");
		Dependable bar = new TestDependable("bar");
		Dependable bay = new TestDependable("bay");
		DependencyPath path = new DependencyPath(Arrays.asList(foo, bar, bay));

		assertEquals(new Dependency(foo, bar), path
				.getDependencyByDependant(foo));
		assertEquals(new Dependency(bar, bay), path
				.getDependencyByDependant(bar));
		assertNull(path.getDependencyByDependant(bay));
	}

	@Test
	public void testGetDependencyByDependeet() {
		Dependable foo = new TestDependable("foo");
		Dependable bar = new TestDependable("bar");
		Dependable bay = new TestDependable("bay");
		DependencyPath path = new DependencyPath(Arrays.asList(foo, bar, bay));

		assertNull(path.getDependencyByDependee(foo));
		assertEquals(new Dependency(foo, bar), path
				.getDependencyByDependee(bar));
		assertEquals(new Dependency(bar, bay), path
				.getDependencyByDependee(bay));
	}
}
