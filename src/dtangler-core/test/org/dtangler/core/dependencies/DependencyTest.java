// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DependencyTest {

	@Test
	public void testEqualsAndHashCode() {
		Dependency same1 = new Dependency(new TestDependable("foo"),
				new TestDependable("bar"));
		Dependency same2 = new Dependency(new TestDependable("foo"),
				new TestDependable("bar"));

		Dependency different1 = new Dependency(new TestDependable("foo"),
				new TestDependable("bay"));

		assertEquals(same1, same2);
		assertEquals(same1.hashCode(), same2.hashCode());

		assertFalse(same1.equals(different1));
		assertFalse(same1.hashCode() == different1.hashCode());

		assertFalse(same1.equals(null));
		assertFalse(same1.equals("foo"));
	}

}
