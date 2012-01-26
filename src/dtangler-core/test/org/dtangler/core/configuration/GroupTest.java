//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.ruleanalysis.GroupRuleMember;
import org.junit.Test;

public class GroupTest {
	@Test
	public void testEquals() {
		Group a = createGroup("A", "1", "2");
		Group b = createGroup("A", "1", "2");
		Group c = createGroup("A", "2", "2");

		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertFalse(a.equals(c));
		assertFalse(b.equals(c));
	}

	@Test
	public void testGroupsWithExcludedItems() {

		Set<String> containedItems = new HashSet();
		containedItems.add("org.*");

		Set<String> excludedItems = new HashSet();
		excludedItems.add("org.public");

		Set<String> excludedItems2 = new HashSet();
		excludedItems2.add("org.public2");

		Group a = new Group("A", containedItems, excludedItems);
		Group b = new Group("A", containedItems, excludedItems);
		Group c = new Group("A", containedItems, excludedItems2);

		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		assertFalse(a.equals(c));
		assertFalse(c.equals(b));
	}

	@Test
	public void testEqualsOthers() {
		Group a = createGroup("A", "1", "2");
		assertFalse(a.equals(new Object()));
		assertFalse(a.equals(null));
		assertFalse(a.equals(new GroupRuleMember(a)));
	}

	protected Group createGroup(String name, String... items) {
		Set<String> groupItems = new HashSet();
		for (String item : items)
			groupItems.add(item);
		return new Group(name, groupItems);
	}
}
