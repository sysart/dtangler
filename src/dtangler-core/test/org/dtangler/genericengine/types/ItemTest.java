package org.dtangler.genericengine.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dtangler.genericengine.types.Item;
import org.junit.Test;

public class ItemTest {

	@Test
	public void testJavaItemLocation() {
		String scope0 = "locations";
		String locationName = "foo.jar";

		Item itemLocation = new Item(scope0, locationName, null);
		assertEquals(0, itemLocation.getScopeIndex());
		assertEquals(scope0, itemLocation.getScope());
		assertNull(itemLocation.getParentDisplaynames());
		assertEquals(locationName, itemLocation.getDisplayname());
		assertNull(itemLocation.getParentFullyqualifiednames());
		assertEquals(Item.encodeValue(locationName), itemLocation
				.getFullyqualifiedname());
	}

	@Test
	public void testJavaItemPackage() {
		String scope1 = "packages";
		String locationName = "foo.jar";
		String packageName = "eg.foo";

		Item itemPackage = new Item(scope1, packageName,
				new String[] { locationName });
		assertEquals(1, itemPackage.getScopeIndex());
		assertEquals(scope1, itemPackage.getScope());
		assertEquals(packageName, itemPackage.getDisplayname());
		assertEquals(Item.encodeValue(locationName) + " "
				+ Item.encodeValue(packageName), itemPackage
				.getFullyqualifiedname());
		assertNotNull(itemPackage.getParentDisplaynames());
		assertNotNull(itemPackage.getParentFullyqualifiednames());
		assertEquals(1, itemPackage.getParentDisplaynames().length);
		for (String parentDisplayName : itemPackage.getParentDisplaynames()) {
			assertNotNull(parentDisplayName);
			assertEquals(parentDisplayName, locationName);
		}
		for (String parentFullyqualifiedName : itemPackage
				.getParentFullyqualifiednames()) {
			assertNotNull(parentFullyqualifiedName);
			assertEquals(parentFullyqualifiedName, Item
					.encodeValue(locationName));
		}
	}

	@Test
	public void testJavaItemClass() {
		String scope2 = "classes";
		String locationName = "foo.jar";
		String packageName = "eg.foo";
		String className = "Foo";
		Item itemClass = new Item(scope2, className, new String[] {
				locationName, packageName });
		assertEquals(2, itemClass.getScopeIndex());
		assertEquals(scope2, itemClass.getScope());
		assertEquals(className, itemClass.getDisplayname());
		assertEquals(Item.encodeValue(locationName) + " "
				+ Item.encodeValue(packageName) + " "
				+ Item.encodeValue(className), itemClass
				.getFullyqualifiedname());
		assertNotNull(itemClass.getParentDisplaynames());
		assertNotNull(itemClass.getParentFullyqualifiednames());
		assertEquals(2, itemClass.getParentDisplaynames().length);
		int parentIndex = 0;
		for (String parentDisplayName : itemClass.getParentDisplaynames()) {
			assertNotNull(parentDisplayName);
			if (parentIndex == 0)
				assertEquals(parentDisplayName, locationName);
			else if (parentIndex == 1)
				assertEquals(parentDisplayName, packageName);
			parentIndex++;
		}
		parentIndex = 0;
		for (String parentFullyqualifiedName : itemClass
				.getParentFullyqualifiednames()) {
			assertNotNull(parentFullyqualifiedName);
			if (parentIndex == 0)
				assertEquals(parentFullyqualifiedName, Item
						.encodeValue(locationName));
			else if (parentIndex == 1)
				assertEquals(parentFullyqualifiedName, Item
						.encodeValue(locationName)
						+ " " + Item.encodeValue(packageName));
			parentIndex++;
		}
	}

	@Test
	public void testEqualsAndHashCode() {
		String[] parents = new String[] { "parent1", "parent2", "parent3" };
		Item item1 = new Item("scope", "item", parents);
		Item item2 = new Item("scope", "item", parents);
		Item itemDifferent = new Item("scope", "item2", parents);

		assertFalse(item1.equals(null));
		assertFalse(item2.equals(null));
		assertFalse(itemDifferent.equals(null));

		assertEquals(item1, item2);
		assertEquals(item1.hashCode(), item2.hashCode());

		assertFalse(item1.equals(itemDifferent));
		assertFalse(item1.hashCode() == itemDifferent.hashCode());

		assertFalse(item1.equals(null));
		assertFalse(item1.equals("something"));
	}

	@Test
	public void testItemEncoding() {
		String[] parents = new String[] { "there are spaces between the words",
				"parenthesis {}", "first : second", "first line\nsecond line" };
		Item item = new Item("scope4", "item", parents);
		assertNotNull(item.getFullyqualifiedname());
		assertEquals(item.getFullyqualifiedname().split("[\\s]").length,
				parents.length + 1);
		String fullyqualifiedName = "";
		for (int i = 0; i < parents.length; i++) {
			assertEquals(Item.decodeValue(Item.encodeValue(parents[i])),
					parents[i]);
			assertFalse(Item.encodeValue(parents[i]).equals(parents[i]));
			assertNotNull(Item.encodeValue(parents[i]));
			fullyqualifiedName += (Item.encodeValue(parents[i]) + " ");
		}
		fullyqualifiedName += "item";
		assertEquals(item.getFullyqualifiedname(), fullyqualifiedName);
	}

	@Test
	public void testAddDependencies() {
		Item item = new Item("packages", "eg.foo",
				new String[] { "foo.jar" });
		Item item2 = new Item("packages", "eg.foo2",
				new String[] { "foo.jar" });
		Item item2duplicate = new Item("packages", "eg.foo2",
				new String[] { "foo.jar" });
		Item item3 = new Item("packages", "eg.foo3",
				new String[] { "foo.jar" });
		assertEquals(0, item.getDependencies().size());
		item.addDependency(item2);
		assertEquals(1, item.getDependencies().size());
		assertTrue(item.getDependencies().keySet().contains(item2));
		item.addDependency(item2duplicate);
		assertEquals(1, item.getDependencies().size());
		item.addDependency(item3);
		assertEquals(2, item.getDependencies().size());
	}

}