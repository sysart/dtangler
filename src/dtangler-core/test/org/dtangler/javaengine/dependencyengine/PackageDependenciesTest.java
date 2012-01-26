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
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.javaengine.dependencyengine.ClassDependencies;
import org.dtangler.javaengine.types.JavaClass;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Before;
import org.junit.Test;

public class PackageDependenciesTest {

	private Set<JavaClass> classes;
	private ClassDependencies engine;
	private JavaClass foo;
	private JavaClass fooImpl;
	private JavaClass someUtil;
	private JavaClass anotherUtil;

	@Before
	public void setUp() {
		classes = new HashSet();
		foo = createClass("eg.foo.Foo", new String[] {});

		fooImpl = createClass("eg.fooimpl.FooImpl", "eg.foo.Foo",
				"eg.util.SomeUtil");

		someUtil = createClass("eg.util.SomeUtil", "eg.util.AnotherUtil");
		anotherUtil = createClass("eg.util.AnotherUtil", new String[] {});
		classes.add(foo);
		classes.add(fooImpl);
		classes.add(someUtil);
		classes.add(anotherUtil);
		engine = new ClassDependencies(classes);
	}

	@Test
	public void testGetPackageDependencies() {
		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.packages);

		Dependable fooPkg = engine.getPackageByName("eg.foo");
		Dependable fooImplPkg = engine.getPackageByName("eg.fooimpl");
		Dependable utilPkg = engine.getPackageByName("eg.util");

		assertTrue(deps.getDependencies(fooPkg).isEmpty());
		assertEquals(2, deps.getDependencies(fooImplPkg).size());
		assertTrue(deps.getDependencies(fooImplPkg).contains(fooPkg));
		assertTrue(deps.getDependencies(fooImplPkg).contains(utilPkg));
		assertTrue(deps.getDependencies(utilPkg).isEmpty());
	}

	@Test
	public void testGetPackageContentCount() {
		assertEquals(1, engine.getPackageByName("eg.foo").getContentCount());
		assertEquals(1, engine.getPackageByName("eg.fooimpl").getContentCount());
		assertEquals(2, engine.getPackageByName("eg.util").getContentCount());
	}

	private JavaClass createClass(String className, String... dependencies) {
		JavaClass clazz = new JavaClass(className);
		for (String dependency : dependencies)
			clazz.addDependency(dependency);
		return clazz;
	}

}
