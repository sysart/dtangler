package org.dtangler.genericengine.dependencyengine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.genericengine.dependenciesstreamparser.ItemDependenciesStreamParser;
import org.dtangler.genericengine.dependencyengine.ItemDependencyBuilder;
import org.dtangler.genericengine.types.Item;
import org.dtangler.genericengine.types.ItemScope;
import org.dtangler.genericengine.types.ValidScopes;
import org.junit.Test;

public class ItemDependencyBuilderTest {

	@Test
	public void testBuilder() {
		Set<Item> items = new HashSet<Item>(Arrays.asList(new Item("Homer"),
				new Item("Pizza"), new Item("Pepperoni"), new Item("Cheese"),
				new Item("Beer")));
		assertEquals(5, items.size());
		for (Item item : items) {
			assertEquals(0, item.getDependencies().size());
			if (item.getDisplayname().equals("Homer")) {
				item.addDependency(new Item("Pizza"));
				item.addDependency(new Item("Beer"));
				assertEquals(2, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Pizza"),
								new Item("Beer")))));
			} else if (item.getDisplayname().equals("Pizza")) {
				assertEquals(0, item.getDependencies().size());
				item.addDependency(new Item("Pepperoni"));
				item.addDependency(new Item("Cheese"));
				assertEquals(2, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Pepperoni"),
								new Item("Cheese")))));
			} else {
				assertEquals(0, item.getDependencies().size());
			}
		}
		assertEquals(5, items.size());
		Dependencies dependencies = ItemDependencyBuilder.getInstance().build(
				new ValidScopes(), items);
		assertEquals(5, dependencies.getAllItems().size());
		DependencyGraph dependencyGraph = dependencies
				.getDependencyGraph(new ItemScope("", 0));
		assertEquals(5, dependencyGraph.getAllItems().size());
	}

	@Test
	public void testScopes() {
		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/genericengine/dependencyengine/testdata/testParsing4.dt";
		ItemDependenciesStreamParser parser = new ItemDependenciesStreamParser();
		ValidScopes validScopes = new ValidScopes();
		Set<Item> items = parser.parse(validScopes, new File(dtPath),
				"UTF-8");
		assertEquals(3, items.size());
		assertEquals(2, validScopes.getNumberOfScopes());
		assertTrue("organ".equals(validScopes.getDefaultScope().getDisplayName()));
		assertEquals(1, validScopes.getDefaultScope().index());
		List<String> listNames = getItemNames(items);
		assertTrue(listNames.containsAll(new HashSet<String>(Arrays.asList(
				"stomach", "eye", "homer"))));
		assertEquals(items.size(), listNames.size());
		Dependencies dependencies = ItemDependencyBuilder.getInstance().build(
				validScopes, items);
		dependencies.setDefaultScope(validScopes.getDefaultScope());
		assertEquals(listNames.size(), dependencies.getAllItems().size());
		assertEquals(validScopes.getNumberOfScopes(), dependencies.getAvailableScopes().size());
		assertTrue(validScopes.getDefaultScope().getDisplayName().equals(dependencies.getDefaultScope().getDisplayName()));
		assertTrue(dependencies.getAvailableScopes().containsAll(
				new ArrayList<ItemScope>(Arrays.asList(
						new ItemScope("person", 0), new ItemScope("organ", 1)))));
	}

	@Test
	public void testJavaFullyqualifiedNames() {
		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/genericengine/dependencyengine/testdata/testJavafullyqualifiednames.dt";
		ItemDependenciesStreamParser parser = new ItemDependenciesStreamParser();
		ValidScopes validScopes = new ValidScopes();
		Set<Item> items = parser.parse(validScopes, new File(dtPath),
				"UTF-8");
		assertEquals(5, items.size());
		List<String> listNames = getItemNames(items);
		assertTrue(listNames.containsAll(new HashSet<String>(Arrays.asList(
				"foo.jar", "foo.jar/eg.process", "foo.jar/eg.filters",
				"foo.jar/eg.filters.InFilter", "foo.jar/eg.process.Process"))));
		for (Item item : items) {
			if (item.getDisplayname().equals("foo.jar")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals("foo.jar/eg.process")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals("foo.jar/eg.filters")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals(
					"foo.jar/eg.filters.InFilter")) {
				assertEquals(1, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("class",
								"foo.jar/eg.process.Process", new String[] {
										"foo.jar", "foo.jar/eg.process" })))));
			} else if (item.getDisplayname().equals(
					"foo.jar/eg.process.Process")) {
				assertEquals(0, item.getDependencies().size());
			} else {
				assertTrue(false);
			}
		}

		Dependencies dependencies = ItemDependencyBuilder.getInstance().build(
				validScopes, items);
		assertEquals(5, dependencies.getAllItems().size());
		assertEquals(3, dependencies.getAvailableScopes().size());

		DependencyGraph dependencyGraphScopeLocation = dependencies
				.getDependencyGraph(new ItemScope("location", 0));
		assertEquals(1, dependencyGraphScopeLocation.getAllItems().size());
		assertNotNull(dependencyGraphScopeLocation.getItemByName("foo.jar"));
		for (Dependable dependable : dependencyGraphScopeLocation.getAllItems()) {
			assertNotNull(dependable);
			assertEquals("foo.jar", dependable.getFullyQualifiedName());
			assertTrue(dependencyGraphScopeLocation.getDependants(dependable)
					.isEmpty());
			assertTrue(dependencyGraphScopeLocation.getDependencies(dependable)
					.isEmpty());
		}

		DependencyGraph dependencyGraphScopePackage = dependencies
				.getDependencyGraph(new ItemScope("package", 1));
		assertEquals(2, dependencyGraphScopePackage.getAllItems().size());
		assertNotNull(dependencyGraphScopePackage
				.getItemByName("foo.jar/eg.process"));
		assertNotNull(dependencyGraphScopePackage
				.getItemByName("foo.jar/eg.filters"));
		for (Dependable dependable : dependencyGraphScopePackage.getAllItems()) {
			assertNotNull(dependable);
			if (dependable.getDisplayName().equals("foo.jar/eg.process")) {
				assertEquals(1, dependencyGraphScopePackage.getDependants(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScopePackage.getDependants(dependable))
						.containsAll(Arrays.asList("foo.jar/eg.filters")));
				assertEquals(0, dependencyGraphScopePackage.getDependencies(
						dependable).size());
			} else if (dependable.getDisplayName().equals("foo.jar/eg.filters")) {
				assertEquals(0, dependencyGraphScopePackage.getDependants(
						dependable).size());
				assertEquals(1, dependencyGraphScopePackage.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScopePackage.getDependencies(dependable))
						.containsAll(Arrays.asList("foo.jar/eg.process")));
			} else {
				assertTrue(false);
			}
		}

		DependencyGraph dependencyGraphScopeClass = dependencies
				.getDependencyGraph(new ItemScope("class", 2));
		assertEquals(2, dependencyGraphScopeClass.getAllItems().size());
		assertNotNull(dependencyGraphScopeClass
				.getItemByName("foo.jar/eg.filters.InFilter"));
		assertNotNull(dependencyGraphScopeClass
				.getItemByName("foo.jar/eg.process.Process"));
		for (Dependable dependable : dependencyGraphScopeClass.getAllItems()) {
			assertNotNull(dependable);
			if (dependable.getDisplayName().equals(
					"foo.jar/eg.filters.InFilter")) {
				assertEquals(0, dependencyGraphScopeClass.getDependants(
						dependable).size());
				assertEquals(1, dependencyGraphScopeClass.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScopeClass.getDependencies(dependable))
						.containsAll(
								Arrays.asList("foo.jar/eg.process.Process")));
			} else if (dependable.getDisplayName().equals(
					"foo.jar/eg.process.Process")) {
				assertEquals(1, dependencyGraphScopeClass.getDependants(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScopeClass.getDependants(dependable))
						.containsAll(
								Arrays.asList("foo.jar/eg.filters.InFilter")));
				assertEquals(0, dependencyGraphScopeClass.getDependencies(
						dependable).size());
			} else {
				assertTrue(false);
			}
		}

	}

	@Test
	public void testScope2Builder() {
		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/genericengine/dependencyengine/testdata/testParsing2.dt";
		testABCBasics(dtPath);
	}

	@Test
	public void testScope2DependencyWeightsBuilder() {
		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/genericengine/dependencyengine/testdata/testParsing3.dt";
		Dependencies dependencies = testABCBasics(dtPath);
		DependencyGraph dependencyGraphScope1 = dependencies
				.getDependencyGraph(new ItemScope("1", 1));
		for (Dependable dependable : dependencyGraphScope1.getAllItems()) {
			assertNotNull(dependable);
			if (dependable.getDisplayName().equals("B")) {
				for (Dependable dependant : dependencyGraphScope1
						.getDependants(dependable)) {
					if (dependant.getDisplayName().equals("D")) {
						assertEquals(6, dependencyGraphScope1
								.getDependencyWeight(dependant, dependable));
					} else {
						assertTrue(false);
					}
				}
			} else if (dependable.getDisplayName().equals("C")) {
			} else if (dependable.getDisplayName().equals("D")) {
				for (Dependable dependee : dependencyGraphScope1
						.getDependencies(dependable)) {
					if (dependee.getDisplayName().equals("B")) {
						assertEquals(6, dependencyGraphScope1
								.getDependencyWeight(dependable, dependee));
					} else {
						assertTrue(false);
					}
				}
			} else {
				assertTrue(false);
			}
		}

		DependencyGraph dependencyGraphScope2 = dependencies
				.getDependencyGraph(new ItemScope("2", 2));
		for (Dependable dependable : dependencyGraphScope2.getAllItems()) {
			assertNotNull(dependable);
			if (dependable.getDisplayName().equals("E")) {
				for (Dependable dependant : dependencyGraphScope2
						.getDependants(dependable)) {
					if (dependant.getDisplayName().equals("I")) {
						assertEquals(1, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else {
						assertTrue(false);
					}
				}
				for (Dependable dependee : dependencyGraphScope2
						.getDependencies(dependable)) {
					if (dependee.getDisplayName().equals("F")) {
						assertEquals(7, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else {
						assertTrue(false);
					}
				}
			} else if (dependable.getDisplayName().equals("F")) {
				for (Dependable dependant : dependencyGraphScope2
						.getDependants(dependable)) {
					if (dependant.getDisplayName().equals("E")) {
						assertEquals(7, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else if (dependant.getDisplayName().equals("I")) {
						assertEquals(3, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else if (dependant.getDisplayName().equals("G")) {
						assertEquals(2, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else {
						assertTrue(false);
					}
				}
			} else if (dependable.getDisplayName().equals("G")) {
				for (Dependable dependant : dependencyGraphScope2
						.getDependants(dependable)) {
					if (dependant.getDisplayName().equals("H")) {
						assertEquals(10, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else if (dependant.getDisplayName().equals("I")) {
						assertEquals(2, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else {
						assertTrue(false);
					}
				}
				for (Dependable dependee : dependencyGraphScope2
						.getDependencies(dependable)) {
					if (dependee.getDisplayName().equals("F")) {
						assertEquals(2, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else {
						assertTrue(false);
					}
				}
			} else if (dependable.getDisplayName().equals("H")) {
				for (Dependable dependant : dependencyGraphScope2
						.getDependants(dependable)) {
					if (dependant.getDisplayName().equals("I")) {
						assertEquals(4, dependencyGraphScope2
								.getDependencyWeight(dependant, dependable));
					} else {
						assertTrue(false);
					}
				}
				for (Dependable dependee : dependencyGraphScope2
						.getDependencies(dependable)) {
					if (dependee.getDisplayName().equals("G")) {
						assertEquals(10, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else {
						assertTrue(false);
					}
				}
			} else if (dependable.getDisplayName().equals("I")) {
				for (Dependable dependee : dependencyGraphScope2
						.getDependencies(dependable)) {
					if (dependee.getDisplayName().equals("E")) {
						assertEquals(1, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else if (dependee.getDisplayName().equals("F")) {
						assertEquals(3, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else if (dependee.getDisplayName().equals("G")) {
						assertEquals(2, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else if (dependee.getDisplayName().equals("H")) {
						assertEquals(4, dependencyGraphScope2
								.getDependencyWeight(dependable, dependee));
					} else {
						assertTrue(false);
					}
				}
			} else {
				assertTrue(false);
			}
		}

	}

	private Dependencies testABCBasics(String fileName) {
		ItemDependenciesStreamParser parser = new ItemDependenciesStreamParser();
		ValidScopes validScopes = new ValidScopes();
		Set<Item> items = parser.parse(validScopes, new File(fileName),
				"UTF-8");
		assertEquals(9, items.size());
		List<String> listNames = getItemNames(items);
		assertTrue(listNames.containsAll(new HashSet<String>(Arrays.asList("A",
				"B", "C", "D", "E", "F", "G", "H", "I"))));
		for (Item item : items) {
			if (item.getDisplayname().equals("A")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals("B")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals("C")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals("D")) {
				assertEquals(1, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("1", "B",
								new String[] { "A" })))));
			} else if (item.getDisplayname().equals("E")) {
				assertEquals(1, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("2", "F",
								new String[] { "A", "B" })))));
			} else if (item.getDisplayname().equals("F")) {
				assertEquals(0, item.getDependencies().size());
			} else if (item.getDisplayname().equals("G")) {
				assertEquals(1, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("2", "F",
								new String[] { "A", "B" })))));
			} else if (item.getDisplayname().equals("H")) {
				assertEquals(1, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("2", "G",
								new String[] { "A", "B" })))));
			} else if (item.getDisplayname().equals("I")) {
				assertEquals(4, item.getDependencies().size());
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("2", "H",
								new String[] { "A", "D" }), new Item("2", "E",
								new String[] { "A", "B" }), new Item("2", "F",
								new String[] { "A", "B" }), new Item("2", "G",
								new String[] { "A", "B" })))));
			}
		}

		Dependencies dependencies = ItemDependencyBuilder.getInstance().build(
				validScopes, items);
		assertEquals(9, dependencies.getAllItems().size());
		assertEquals(3, dependencies.getAvailableScopes().size());

		DependencyGraph dependencyGraphScope0 = dependencies
				.getDependencyGraph(new ItemScope("0", 0));
		assertEquals(1, dependencyGraphScope0.getAllItems().size());
		assertNotNull(dependencyGraphScope0.getItemByName("A"));
		for (Dependable dependable : dependencyGraphScope0.getAllItems()) {
			assertNotNull(dependable);
			assertEquals("A", dependable.getFullyQualifiedName());
			assertTrue(dependencyGraphScope0.getDependants(dependable)
					.isEmpty());
			assertTrue(dependencyGraphScope0.getDependencies(dependable)
					.isEmpty());
		}

		DependencyGraph dependencyGraphScope1 = dependencies
				.getDependencyGraph(new ItemScope("1", 1));
		assertEquals(3, dependencyGraphScope1.getAllItems().size());
		assertNotNull(dependencyGraphScope1.getItemByName("B"));
		assertNotNull(dependencyGraphScope1.getItemByName("C"));
		assertNotNull(dependencyGraphScope1.getItemByName("D"));
		for (Dependable dependable : dependencyGraphScope1.getAllItems()) {
			assertNotNull(dependable);
			if (dependable.getDisplayName().equals("B")) {
				assertEquals(1, dependencyGraphScope1.getDependants(dependable)
						.size());
				assertTrue(getDependableNames(
						dependencyGraphScope1.getDependants(dependable))
						.containsAll(Arrays.asList("D")));
				assertEquals(0, dependencyGraphScope1.getDependencies(
						dependable).size());
			} else if (dependable.getDisplayName().equals("C")) {
				assertEquals(0, dependencyGraphScope1.getDependants(dependable)
						.size());
				assertEquals(0, dependencyGraphScope1.getDependencies(
						dependable).size());
			} else if (dependable.getDisplayName().equals("D")) {
				assertEquals(0, dependencyGraphScope1.getDependants(dependable)
						.size());
				assertEquals(1, dependencyGraphScope1.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScope1.getDependencies(dependable))
						.containsAll(Arrays.asList("B")));
			} else {
				assertTrue(false);
			}
		}

		DependencyGraph dependencyGraphScope2 = dependencies
				.getDependencyGraph(new ItemScope("2", 2));
		assertEquals(5, dependencyGraphScope2.getAllItems().size());
		assertNotNull(dependencyGraphScope2.getItemByName("E"));
		assertNotNull(dependencyGraphScope2.getItemByName("F"));
		assertNotNull(dependencyGraphScope2.getItemByName("G"));
		assertNotNull(dependencyGraphScope2.getItemByName("H"));
		assertNotNull(dependencyGraphScope2.getItemByName("I"));
		for (Dependable dependable : dependencyGraphScope2.getAllItems()) {
			assertNotNull(dependable);
			if (dependable.getDisplayName().equals("E")) {
				assertEquals(1, dependencyGraphScope2.getDependants(dependable)
						.size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependants(dependable))
						.containsAll(Arrays.asList("I")));
				assertEquals(1, dependencyGraphScope2.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependencies(dependable))
						.containsAll(Arrays.asList("F")));
			} else if (dependable.getDisplayName().equals("F")) {
				assertEquals(3, dependencyGraphScope2.getDependants(dependable)
						.size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependants(dependable))
						.containsAll(Arrays.asList("E", "I", "G")));
				assertEquals(0, dependencyGraphScope2.getDependencies(
						dependable).size());
			} else if (dependable.getDisplayName().equals("G")) {
				assertEquals(2, dependencyGraphScope2.getDependants(dependable)
						.size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependants(dependable))
						.containsAll(Arrays.asList("H", "I")));
				assertEquals(1, dependencyGraphScope2.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependencies(dependable))
						.containsAll(Arrays.asList("F")));
			} else if (dependable.getDisplayName().equals("H")) {
				assertEquals(1, dependencyGraphScope2.getDependants(dependable)
						.size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependants(dependable))
						.containsAll(Arrays.asList("I")));
				assertEquals(1, dependencyGraphScope2.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependencies(dependable))
						.containsAll(Arrays.asList("G")));
			} else if (dependable.getDisplayName().equals("I")) {
				assertEquals(0, dependencyGraphScope2.getDependants(dependable)
						.size());
				assertEquals(4, dependencyGraphScope2.getDependencies(
						dependable).size());
				assertTrue(getDependableNames(
						dependencyGraphScope2.getDependencies(dependable))
						.containsAll(Arrays.asList("E", "F", "G", "H")));
			} else {
				assertTrue(false);
			}
		}

		return dependencies;
	}

	private List<String> getDependableNames(Set<Dependable> items) {
		List<String> names = new ArrayList<String>();
		for (Dependable dep : items) {
			names.add(dep.getDisplayName());
		}
		return names;
	}

	private List<String> getItemNames(Set<Item> items) {
		List<String> names = new ArrayList<String>();
		for (Item item : items) {
			names.add(item.getDisplayname());
		}
		return names;
	}

}