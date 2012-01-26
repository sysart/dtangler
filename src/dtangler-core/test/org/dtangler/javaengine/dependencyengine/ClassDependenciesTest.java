// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.dependencyengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.javaengine.dependencyengine.ClassDependencies;
import org.dtangler.javaengine.types.JavaClass;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Before;
import org.junit.Test;

public class ClassDependenciesTest {

	private Set<JavaClass> classes;
	private JavaClass foo;
	private JavaClass fooImpl;
	private JavaClass someUtil;
	private JavaClass anotherUtil;

	@Before
	public void setUpClasses() {
		foo = createClass("eg.foo.Foo", new String[] {});
		fooImpl = createClass("eg.fooimpl.FooImpl", "eg.foo.Foo", "eg.foo.Foo",
				"eg.util.SomeUtil");
		someUtil = createClass("eg.util.SomeUtil", "eg.util.AnotherUtil");
		anotherUtil = createClass("eg.util.AnotherUtil", new String[] {});

		classes = new HashSet();
		classes.add(foo);
		classes.add(fooImpl);
		classes.add(someUtil);
		classes.add(anotherUtil);
	}

	@Test
	public void testGetClassDependencies() {
		ClassDependencies engine = new ClassDependencies(classes);

		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.classes);
		assertTrue(deps.getDependencies(foo.toDependable()).isEmpty());
		assertEquals(2, deps.getDependencies(fooImpl.toDependable()).size());
		assertTrue(deps.getDependencies(fooImpl.toDependable()).contains(
				foo.toDependable()));
		assertTrue(deps.getDependencies(fooImpl.toDependable()).contains(
				someUtil.toDependable()));
		assertEquals(1, deps.getDependencies(someUtil.toDependable()).size());
		assertTrue(deps.getDependencies(someUtil.toDependable()).contains(
				anotherUtil.toDependable()));
		assertTrue(deps.getDependencies(anotherUtil.toDependable()).isEmpty());
	}

	@Test
	public void testInnerclassDependant() {
		String innerClass = "eg.foo.Foo$Inner";
		JavaClass innerFoo = createClass(innerClass, "eg.foo.Foo",
				"eg.util.AnotherUtil");
		foo.addDependency(innerClass);
		JavaClass usesInner = createClass("eg.bar.needsInner", innerClass);

		classes.add(innerFoo);
		classes.add(usesInner);

		ClassDependencies engine = new ClassDependencies(classes);
		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.classes);

		assertTrue(deps.getDependencies(usesInner.toDependable()).contains(
				foo.toDependable()));
		assertEquals(1, usesInner.toDependable().getContentCount());
	}

	@Test
	public void testProjectInnerClassToParent() {
		String innerClass = "eg.foo.Foo$Inner";
		JavaClass innerFoo = createClass(innerClass, "eg.foo.Foo",
				"eg.util.AnotherUtil");
		foo.addDependency(innerClass);
		JavaClass usesInner = createClass("eg.bar.needsInner", innerClass);

		classes.add(innerFoo);
		classes.add(usesInner);

		ClassDependencies engine = new ClassDependencies(classes);
		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.classes);
		assertEquals(5, deps.getAllItems().size());
		assertEquals(1, deps.getDependencies(foo.toDependable()).size());
		assertTrue(deps.getDependencies(foo.toDependable()).contains(
				anotherUtil.toDependable()));
		assertEquals(2, foo.toDependable().getContentCount());
	}

	@Test
	public void testInnerClassDependsOnInnerClass() {
		String innerClass1 = "eg.foo.Foo$Inner1";
		String innerClass2 = "eg.foo.Foo$Inner2";
		JavaClass innerFoo1 = createClass(innerClass1, "eg.foo.Foo",
				innerClass2);
		JavaClass innerFoo2 = createClass(innerClass1, "eg.foo.Foo",
				"eg.util.AnotherUtil");
		foo.addDependency(innerClass1);
		foo.addDependency(innerClass2);

		classes.add(innerFoo1);
		classes.add(innerFoo2);

		ClassDependencies engine = new ClassDependencies(classes);
		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.classes);

		assertEquals(4, deps.getAllItems().size());
		assertEquals(0, deps.getDependencies(foo.toDependable()).size());
		assertEquals(2, foo.toDependable().getContentCount());
	}

	@Test
	public void testProjectInnerInnerClassToParent() {
		String innerClass = "eg.foo.Foo$Inner";
		String innerInnerClass = "eg.foo.Foo$Inner$Inner";
		JavaClass innerFoo = createClass(innerClass, "eg.foo.Foo",
				innerInnerClass);
		JavaClass innerInnerFoo = createClass(innerInnerClass, "eg.foo.Foo",
				innerClass);
		foo.addDependency(innerClass);
		foo.addDependency(innerInnerClass);
		classes.add(innerFoo);
		classes.add(innerInnerFoo);

		ClassDependencies engine = new ClassDependencies(classes);
		DependencyGraph deps = engine.getDependencies().getDependencyGraph(
				JavaScope.classes);
		assertEquals(4, deps.getAllItems().size());
		assertEquals(3, foo.toDependable().getContentCount());
	}

	private JavaClass createClass(String className, String... dependencies) {
		JavaClass clazz = new JavaClass(className);
		for (String dependency : dependencies)
			clazz.addDependency(dependency);
		return clazz;
	}
}