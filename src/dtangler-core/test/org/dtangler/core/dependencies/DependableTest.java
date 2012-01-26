// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DependableTest {

	@Test
	public void testEqualsAndHashCode() {
		Dependable same1 = new Dependable(TestScope.scope1, "foo", "a", 1);
		Dependable same2 = new Dependable(TestScope.scope1, "foo", "a", 1);
		Dependable different1 = new Dependable(TestScope.scope1, "bar", "a", 1);
		Dependable different2 = new Dependable(TestScope.scope2, "foo", "a", 1);

		assertEquals(same1, same2);
		assertEquals(same1.hashCode(), same2.hashCode());

		assertFalse(same1.equals(different1));
		assertFalse(same1.hashCode() == different1.hashCode());

		assertFalse(same1.equals(different2));
		assertFalse(same1.hashCode() == different2.hashCode());

		assertFalse(same1.equals(null));
		assertFalse(same1.equals(Boolean.TRUE));
	}

}
