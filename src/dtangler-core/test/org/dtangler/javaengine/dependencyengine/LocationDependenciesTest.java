// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.dependencyengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.javaengine.dependencyengine.ClassDependencies;
import org.dtangler.javaengine.types.JavaClass;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Before;
import org.junit.Test;

public class LocationDependenciesTest {
	private Set<JavaClass> classes;
	private ClassDependencies engine;
	private JavaClass foo;
	private JavaClass fooImpl;
	private JavaClass someUtil;
	private JavaClass anotherUtil;

	@Before
	public void setUp() {
		classes = new HashSet();
		foo = createClass("eg.foo.Foo", "test/foo", new String[] {});

		fooImpl = createClass("eg.fooimpl.FooImpl", "test/foo", "eg.foo.Foo",
				"eg.util.SomeUtil");

		someUtil = createClass("eg.util.SomeUtil", "test/util",
				"eg.util.AnotherUtil");
		anotherUtil = createClass("eg.util.AnotherUtil", "test/util",
				new String[] {});
		classes.add(foo);
		classes.add(fooImpl);
		classes.add(someUtil);
		classes.add(anotherUtil);
		engine = new ClassDependencies(classes);

	}

	@Test
	public void testGetLocationDependencies() {
		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.locations);
		Dependable fooLocation = engine.getLocationByName("test/foo");
		Dependable utilLocation = engine.getLocationByName("test/util");

		assertTrue(deps.getDependencies(utilLocation).isEmpty());
		assertEquals(1, deps.getDependencies(fooLocation).size());
		assertTrue(deps.getDependencies(fooLocation).contains(utilLocation));
	}

	@Test
	public void testLocationContentCount() {
		Dependencies dependencies = engine.getDependencies();
		DependencyGraph deps = dependencies
				.getDependencyGraph(JavaScope.locations);

		for (Dependable location : deps.getAllItems()) {
			int expected = dependencies.getChilds(location).size();
			assertEquals(expected, location.getContentCount());
		}
	}

	private JavaClass createClass(String className, String location,
			String... dependencies) {
		JavaClass clazz = new JavaClass(className);
		clazz.setLocation(location);
		for (String dependency : dependencies)
			clazz.addDependency(dependency);
		return clazz;
	}

}
