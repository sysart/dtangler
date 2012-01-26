// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.dependencies.Dependencies.DependencyFilter;
import org.junit.Test;

public class DependenciesTest {

	private final Dependable A1 = new TestDependable("a1", TestScope.scope1);
	private final Dependable A2 = new TestDependable("a2", TestScope.scope1);
	private final Dependable A3 = new TestDependable("a3", TestScope.scope1);
	private final Dependable A4 = new TestDependable("a4", TestScope.scope1);
	private final Dependable A5 = new TestDependable("a5", TestScope.scope1);
	private final Dependable B1 = new TestDependable("b1", TestScope.scope2);
	private final Dependable B2 = new TestDependable("b2", TestScope.scope2);
	private final Dependable B3 = new TestDependable("b3", TestScope.scope2);
	private final Dependable C1 = new TestDependable("c1", TestScope.scope3);
	private final Dependable C2 = new TestDependable("c2", TestScope.scope3);
	private final Dependable C3 = new TestDependable("c3", TestScope.scope3);

	private final Dependable a1 = new TestDependable("a", TestScope.scope3);
	private final Dependable b1 = new TestDependable("b", TestScope.scope3);
	private final Dependable c1 = new TestDependable("c", TestScope.scope3);
	private final Dependable a2 = new TestDependable("d", TestScope.scope3);
	private final Dependable b2 = new TestDependable("d", TestScope.scope3);

	private Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}

	private Set<Dependable> createSet(Dependable... items) {
		return new HashSet(Arrays.asList(items));
	}

	@Test
	public void testEmptyDependencies() {
		assertTrue(new Dependencies().getAvailableScopes().isEmpty());
	}

	@Test
	public void testGetEmptyGraph() {
		DependencyGraph graph = new Dependencies()
				.getDependencyGraph(TestScope.scope1);
		assertTrue(graph.getAllItems().isEmpty());
	}

	@Test
	public void testGetAvailableScopesByAddingDeps() {
		Dependencies deps = new Dependencies();
		assertTrue(deps.getAvailableScopes().isEmpty());
		deps.addDependencies(B1, createMap(B2));
		assertEquals(Arrays.asList(TestScope.scope2), deps.getAvailableScopes());
		deps.addDependencies(C1, createMap(A2));
		assertEquals(Arrays.asList(TestScope.scope1, TestScope.scope2,
				TestScope.scope3), deps.getAvailableScopes());
	}

	@Test
	public void testGetParentAndChildScopesWithAllScopes() {
		Dependencies deps = new Dependencies();
		deps.addChild(A1, B1);
		deps.addChild(B1, C1);

		assertEquals(TestScope.scope2, deps.getChildScope(TestScope.scope1));
		assertEquals(TestScope.scope3, deps.getChildScope(TestScope.scope2));
		assertNull(deps.getChildScope(TestScope.scope3));

		assertEquals(TestScope.scope2, deps.getParentScope(TestScope.scope3));
		assertEquals(TestScope.scope1, deps.getParentScope(TestScope.scope2));
		assertNull(deps.getParentScope(TestScope.scope1));
	}

	@Test
	public void testGetParentAndChildScopesWithSomeScopes() {
		Dependencies deps = new Dependencies();
		deps.addChild(A1, C1);

		assertEquals(TestScope.scope3, deps.getChildScope(TestScope.scope1));
		assertNull(deps.getChildScope(TestScope.scope2));
		assertNull(deps.getChildScope(TestScope.scope3));

		assertEquals(TestScope.scope1, deps.getParentScope(TestScope.scope3));
		assertNull(deps.getParentScope(TestScope.scope2));
		assertNull(deps.getParentScope(TestScope.scope1));
	}

	@Test
	public void testGetAvailableScopesByAddingParentChildRelations() {
		Dependencies deps = new Dependencies();
		assertTrue(deps.getAvailableScopes().isEmpty());
		deps.addChild(B1, B2);
		assertEquals(Arrays.asList(TestScope.scope2), deps.getAvailableScopes());
		deps.addChild(C1, A2);
		assertEquals(Arrays.asList(TestScope.scope1, TestScope.scope2,
				TestScope.scope3), deps.getAvailableScopes());
	}

	@Test
	public void testSimpleGraphWithOneScope() {
		Dependencies deps = new Dependencies();
		deps.addDependencies(A1, createMap(A2, A3));
		deps.addDependencies(A2, createMap(A3));

		DependencyGraph graph = deps.getDependencyGraph(TestScope.scope1);

		assertEquals(createSet(A1, A2, A3), graph.getAllItems());
		assertEquals(createSet(A2, A3), graph.getDependencies(A1));
		assertEquals(createSet(A3), graph.getDependencies(A2));
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(A3));
	}

	@Test
	public void testTwoScopesGraphWithDerivedDependencies() {
		Dependencies deps = new Dependencies();
		deps.addDependencies(A1, createMap(A2, A3));
		deps.addDependencies(A2, createMap(A3));
		deps.addDependencies(A4, createMap(A3));
		deps.addDependencies(A5, createMap(A1, A4));

		deps.addChild(B1, A1);
		deps.addChild(B1, A2);
		deps.addChild(B2, A3);
		deps.addChild(B3, A4);
		deps.addChild(B3, A5);

		DependencyGraph graph = deps.getDependencyGraph(TestScope.scope2);

		assertEquals(createSet(B1, B2, B3), graph.getAllItems());
		assertEquals(createSet(B2), graph.getDependencies(B1));
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(B2));
		assertEquals(createSet(B1, B2), graph.getDependencies(B3));
	}

	@Test
	public void testTwoScopesGraphWithDirectAndDerivedDependencies() {
		Dependencies deps = new Dependencies();
		deps.addDependencies(A1, createMap(A2));
		deps.addDependencies(B1, createMap(B3));
		deps.addChild(B1, A1);
		deps.addChild(B2, A2);

		DependencyGraph graph = deps.getDependencyGraph(TestScope.scope2);

		assertEquals(createSet(B1, B2, B3), graph.getAllItems());
		assertEquals(createSet(B2, B3), graph.getDependencies(B1));
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(B2));
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(B3));
	}

	@Test
	public void testThreeScopesGraphWithDerivedDependencies() {
		Dependencies deps = new Dependencies();
		deps.addDependencies(A2, createMap(A1));
		deps.addDependencies(B3, createMap(B2));

		deps.addChild(B1, A1);
		deps.addChild(B2, A2);
		deps.addChild(C1, B1);
		deps.addChild(C2, B2);
		deps.addChild(C3, B3);

		DependencyGraph graph = deps.getDependencyGraph(TestScope.scope3);

		assertEquals(createSet(C1, C2, C3), graph.getAllItems());
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(C1));
		assertEquals(createSet(C1), graph.getDependencies(C2));
		assertEquals(createSet(C2), graph.getDependencies(C3));
	}

	@Test
	public void testGetGraphByParentsOneScopeUp() {
		Dependencies deps = new Dependencies();
		deps.addDependencies(A1, createMap(A2, A3, A4));
		deps.addDependencies(A2, createMap(A3, A4));

		deps.addChild(B1, A1);
		deps.addChild(B1, A2);
		deps.addChild(B2, A3);
		deps.addChild(B3, A4);

		DependencyGraph graph = deps.getDependencyGraph(TestScope.scope1,
				new HashSet(Arrays.asList(B1, B2)), DependencyFilter.none);

		assertEquals(createSet(A1, A2, A3), graph.getAllItems());
		assertEquals(createSet(A2, A3), graph.getDependencies(A1));
		assertEquals(createSet(A3), graph.getDependencies(A2));
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(A3));

		DependencyGraph graphFiltered = deps.getDependencyGraph(
				TestScope.scope1, new HashSet(Arrays.asList(B1)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertTrue(graphFiltered.getAllItems().size() == 0);

		graphFiltered = deps.getDependencyGraph(TestScope.scope1, new HashSet(
				Arrays.asList(B1, B2)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertEquals(createSet(A1, A2, A3), graphFiltered.getAllItems());
		assertEquals(createSet(A3), graphFiltered.getDependencies(A1));
		assertEquals(createSet(A3), graphFiltered.getDependencies(A2));

		graphFiltered = deps.getDependencyGraph(TestScope.scope1, new HashSet(
				Arrays.asList(B1, B3)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertEquals(createSet(A1, A2, A4), graphFiltered.getAllItems());
		assertEquals(createSet(A4), graphFiltered.getDependencies(A1));
		assertEquals(createSet(A4), graphFiltered.getDependencies(A2));

		graphFiltered = deps.getDependencyGraph(TestScope.scope1, new HashSet(
				Arrays.asList(B2, B3)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertTrue(graphFiltered.getAllItems().size() == 0);

		graphFiltered = deps.getDependencyGraph(TestScope.scope1, new HashSet(
				Arrays.asList(B1, B2, B3)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertEquals(createSet(A1, A2, A3, A4), graphFiltered.getAllItems());
		assertEquals(createSet(A3, A4), graphFiltered.getDependencies(A1));
		assertEquals(createSet(A3, A4), graphFiltered.getDependencies(A2));
		assertEquals(Collections.EMPTY_SET, graphFiltered.getDependencies(A3));
		assertEquals(Collections.EMPTY_SET, graphFiltered.getDependencies(A4));
	}

	@Test
	public void testGetGraphByParentsTwoScopesUp() {
		Dependencies deps = new Dependencies();
		deps.addDependencies(A1, createMap(A2, A3, A4));
		deps.addDependencies(A2, createMap(A3, A4));

		deps.addChild(B1, A1);
		deps.addChild(B1, A2);
		deps.addChild(B2, A3);
		deps.addChild(B3, A4);
		deps.addChild(C1, B1);
		deps.addChild(C2, B2);
		deps.addChild(C3, B3);

		DependencyGraph graph = deps.getDependencyGraph(TestScope.scope1,
				new HashSet(Arrays.asList(C1, C2)), DependencyFilter.none);

		assertEquals(createSet(A1, A2, A3), graph.getAllItems());
		assertEquals(createSet(A2, A3), graph.getDependencies(A1));
		assertEquals(createSet(A3), graph.getDependencies(A2));
		assertEquals(Collections.EMPTY_SET, graph.getDependencies(A3));

		DependencyGraph graphFiltered = deps.getDependencyGraph(
				TestScope.scope1, new HashSet(Arrays.asList(C1)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertTrue(graphFiltered.getAllItems().size() == 0);

		graphFiltered = deps.getDependencyGraph(
				TestScope.scope1, new HashSet(Arrays.asList(C1, C2)),
				DependencyFilter.itemsContributingToTheParentDependencyWeight);
		assertEquals(createSet(A1, A2, A3), graphFiltered.getAllItems());
		assertEquals(createSet(A3), graphFiltered.getDependencies(A1));
		assertEquals(createSet(A3), graphFiltered.getDependencies(A2));
	}

	@Test
	public void testSetParentToSelfIsIgnored() {
		Dependencies deps = new Dependencies();
		deps.addChild(A1, A1);
		assertEquals(createSet(A1), deps.getDependencyGraph(TestScope.scope1)
				.getAllItems());
		assertEquals(Collections.EMPTY_SET, deps.getChilds(A1));
	}

	@Test
	public void testCyclicParents() {
		Dependencies deps = new Dependencies();
		deps.addChild(B1, A1);
		deps.addChild(C1, B1);
		deps.addChild(A1, C1);
		assertEquals(createSet(C1), deps.getChilds(A1));
		assertEquals(createSet(A1), deps.getChilds(B1));
		assertEquals(createSet(B1), deps.getChilds(C1));
	}

	@Test
	public void testGetParent() {
		Dependencies deps = new Dependencies();
		deps.addChild(A1, B1);
		deps.addChild(A1, B2);
		deps.addChild(A2, B3);

		assertEquals(createSet(A1), deps.getParents(B1, deps.getParentScope(B1
				.getScope())));
		assertEquals(createSet(A1), deps.getParents(B2, deps.getParentScope(B2
				.getScope())));
		assertEquals(createSet(A2), deps.getParents(B3, deps.getParentScope(B3
				.getScope())));
	}

	@Test
	public void testGetDependencyWeight() {
		Dependencies deps = createDependenciesWithWeights();
		assertEquals(2, deps.getDependencyGraph(TestScope.scope3)
				.getDependencyWeight(a1, b1));
		assertEquals(1, deps.getDependencyGraph(TestScope.scope3)
				.getDependencyWeight(b1, a2));
		assertEquals(0, deps.getDependencyGraph(TestScope.scope3)
				.getDependencyWeight(a2, b2));
		assertEquals(24, deps.getDependencyGraph(TestScope.scope3)
				.getDependencyWeight(b2, b1));
		assertEquals(2, deps.getDependencyGraph(TestScope.scope3)
				.getDependencyWeight(c1, a1));

	}

	private Dependencies createDependenciesWithWeights() {
		Dependencies deps = new Dependencies();

		Map<Dependable, Integer> a1b1 = createWeights(b1, 2);
		deps.addDependencies(a1, a1b1);

		Map<Dependable, Integer> b1a2 = createWeights(a2, 1);
		deps.addDependencies(b1, b1a2);

		Map<Dependable, Integer> a2b2 = createWeights(b2, 0);
		deps.addDependencies(a2, a2b2);

		Map<Dependable, Integer> b2b1 = createWeights(b1, 24);
		deps.addDependencies(b2, b2b1);

		Map<Dependable, Integer> c1a1 = createWeights(a1, 2);
		deps.addDependencies(c1, c1a1);

		return deps;
	}

	private Map<Dependable, Integer> createWeights(Dependable to, Integer amount) {
		Map<Dependable, Integer> dependencyWeight = new HashMap();
		dependencyWeight.put(to, amount);
		return dependencyWeight;
	}
}
