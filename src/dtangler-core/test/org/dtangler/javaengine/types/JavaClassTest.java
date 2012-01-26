// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.dtangler.javaengine.types.JavaClass;
import org.junit.Test;

public class JavaClassTest {

	@Test
	public void testGetName() {
		assertEquals("MyClass", new JavaClass("MyClass").getName());
		assertEquals("MyClass", new JavaClass("my.package.MyClass").getName());
	}

	@Test
	public void testGetPackage() {
		assertEquals("default", new JavaClass("MyClass").getPackage());
		assertEquals("my.package", new JavaClass("my.package.MyClass")
				.getPackage());
	}

	@Test
	public void testEqualsAndHashCode() {
		JavaClass same1 = new JavaClass("eg.foo.MyClass");
		JavaClass same2 = new JavaClass("eg.foo.MyClass");
		JavaClass different1 = new JavaClass("eg.bar.MyClass");

		assertEquals(same1, same2);
		assertEquals(same1.hashCode(), same2.hashCode());

		assertFalse(same1.equals(different1));
		assertFalse(same1.hashCode() == different1.hashCode());

		assertFalse(same1.equals(null));
		assertFalse(same1.equals("foo"));
	}

	@Test
	public void testAddDependencyDoesNotAddTrivialDependencies() {
		JavaClass clazz = new JavaClass("eg.foo.MyClass");
		assertEquals(0, clazz.getDependencies().size());
		clazz.addDependency("java.lang.Object");
		clazz.addDependency("eg.bar.MyClass");
		clazz.addDependency("eg.foo.MyClass");

		assertEquals(1, clazz.getDependencies().size());
		assertTrue(clazz.getDependencies().keySet().contains("eg.bar.MyClass"));
	}

	@Test
	public void testAddMultipleDependenciesBetweenClasses() {
		JavaClass clazz = new JavaClass("eg.foo.MyClass");
		clazz.addDependency("eg.foo.OtherClass");
		clazz.addDependency("eg.foo.OtherClass");
		Map<String, Integer> dependencies = clazz.getDependencies();
		assertEquals(1, dependencies.size());
		for (Integer value : dependencies.values()) {
			assertEquals(Integer.valueOf(2), value);
		}
		clazz.addDependency("eg.foo.YetAnotherClass");
		assertEquals(2, dependencies.size());
	}
}
