// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.dependencyengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Test;

public class JavaDependencyEngineTest {

	private final String path1 = ClassPathEntryFinder
			.getPathContaining("testdata-cyclic");
	private final String path2 = ClassPathEntryFinder
			.getPathContaining("testdata-good-deps");

	List<String> path = Arrays.asList(path2);

	@Test
	public void testGetJarDependencies() {
		String path = ClassPathEntryFinder.getPathContaining("core")
				+ "/org/dtangler/core/acceptancetests/testdata/jarexample.jar";

		Arguments args = new Arguments();
		args.setInput(Arrays.asList(path));

		JavaDependencyEngine engine = new JavaDependencyEngine();
		Dependencies jarDeps = engine.getDependencies(args);

		Set<Dependable> allItems = jarDeps.getAllItems();
		assertEquals("one jar location, one package, two class files", 4,
				allItems.size());
	}

	@Test
	public void testDefaultScopeDependencies() {
		Arguments defaultArgs = new Arguments();
		defaultArgs.setInput(path);

		testGetPackageDependencies(defaultArgs);
	}

	@Test
	public void testLocationScopeDependencies() {

		Arguments args = new Arguments();
		args.setInput(Arrays.asList(path1, path2));
		args.setScope(JavaScope.locations.getDisplayName());
		JavaDependencyEngine engine = new JavaDependencyEngine();
		DependencyGraph deps = engine.getDependencies(args).getDependencyGraph();
		List<String> items = getItemNames(deps.getAllItems());
		assertTrue(items.containsAll(Arrays.asList(path1, path2)));
	}

	@Test
	public void testPackageScopeDependencies() {
		Arguments packageArgs = new Arguments();
		packageArgs.setInput(path);
		packageArgs.setScope("packages");

		testGetPackageDependencies(packageArgs);
	}

	@Test
	public void testClassScopeDependencies() {
		Arguments classArgs = new Arguments();
		classArgs.setInput(path);
		classArgs.setScope("classes");

		JavaDependencyEngine engine = new JavaDependencyEngine();
		DependencyGraph deps = engine.getDependencies(classArgs)
				.getDependencyGraph();
		List<String> items = getItemNames(deps.getAllItems());

		assertEquals(6, items.size());
		assertTrue(items.containsAll(new HashSet(Arrays.asList(
				"MySecondClient", "MyClient", "MyService", "AnotherClass",
				"MyApi", "YetAnotherClass"))));
	}

	private void testGetPackageDependencies(Arguments args) {
		JavaDependencyEngine engine = new JavaDependencyEngine();
		DependencyGraph deps = engine.getDependencies(args).getDependencyGraph();

		List<String> items = getItemNames(deps.getAllItems());

		assertEquals(3, items.size());
		assertTrue(items.contains("eg.foo.good.deps.client"));
		assertTrue(items.contains("eg.foo.good.deps.impl"));
		assertTrue(items.contains("eg.foo.good.deps.api"));
	}

	private List<String> getItemNames(Set<Dependable> items) {
		List<String> names = new ArrayList();
		for (Dependable dep : items) {
			names.add(dep.getDisplayName());
		}
		return names;
	}
}