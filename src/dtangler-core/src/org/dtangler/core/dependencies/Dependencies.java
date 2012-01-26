// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dependencies {

	private class ParentInfo {
		private Map<Scope, Set<Dependable>> allParents = new HashMap<Scope, Set<Dependable>>();

		void addParent(Dependable parent) {
			Scope scope = parent.getScope();
			Set<Dependable> parents = this.allParents.get(scope);
			if (parents == null) {
				parents = new HashSet<Dependable>();
				allParents.put(scope, parents);
			}
			parents.add(parent);
		}

		Set<Dependable> getParents(Scope scope) {
			Set<Dependable> parents = this.allParents.get(scope);
			return parents != null ? parents : Collections
					.<Dependable> emptySet();
		}
	}

	private final Map<Scope, Set<Dependable>> allItems = new HashMap<Scope, Set<Dependable>>();
	private final Map<Dependable, Map<Dependable, Integer>> dependencies = new HashMap<Dependable, Map<Dependable, Integer>>();
	private final Map<Dependable, Set<Dependable>> allChilds = new HashMap<Dependable, Set<Dependable>>();
	private final Map<Dependable, ParentInfo> parents = new HashMap<Dependable, ParentInfo>();
	// scopeGraphCache is needed to speed up analysis
	private final Map<Scope, DependencyGraph> scopeGraphCache = new HashMap<Scope, DependencyGraph>();
	private Scope defaultScope;

	public enum DependencyFilter {
		none, itemsContributingToTheParentDependencyWeight;
	}

	public List<Scope> getAvailableScopes() {
		List<Scope> sortedScopes = new ArrayList<Scope>(allItems.keySet());
		Collections.sort(sortedScopes, new ScopeComparator());
		return sortedScopes;
	}

	public Scope getChildScope(Scope scope) {
		List<Scope> scopes = getAvailableScopes();
		int index = scopes.indexOf(scope);
		if (index < 0 || index == scopes.size() - 1)
			return null;
		return scopes.get(index + 1);
	}

	public Scope getParentScope(Scope scope) {
		List<Scope> scopes = getAvailableScopes();
		int index = scopes.indexOf(scope);
		if (index <= 0)
			return null;
		return scopes.get(index - 1);
	}

	public void setDefaultScope(Scope scope) {
		defaultScope = scope;
	}

	public Scope getDefaultScope() {
		if (defaultScope != null)
			return defaultScope;
		List<Scope> scopes = getAvailableScopes();
		if (scopes != null && scopes.size() > 0) {
			for (Scope scope : scopes) {
				if (scope == null)
					continue;
				defaultScope = scope;
				return defaultScope;
			}
		}
		return null;
	}

	public DependencyGraph getDependencyGraph() {
		return getDependencyGraph(getDefaultScope());
	}

	public DependencyGraph getDependencyGraph(Scope scope) {
		DependencyGraph graph = scopeGraphCache.get(scope);
		if (graph == null) {
			graph = createGraph(scope, getItems(scope), null,
					DependencyFilter.none);
			scopeGraphCache.put(scope, graph);
		}
		return graph;
	}

	public DependencyGraph getDependencyGraph(Scope scope,
			Set<Dependable> parents, DependencyFilter dependencyFilter) {
		return createGraph(scope, getItems(scope, parents), parents,
				dependencyFilter);
	}

	private Set<Dependable> getItems(Scope graphScope,
			Set<Dependable> selectedParents) {
		Set<Dependable> items = new HashSet<Dependable>();
		for (Dependable item : getItems(graphScope)) {
			for (Dependable parent : selectedParents) {
				if (getParents(item, parent.getScope()).contains(parent)) {
					items.add(item);
					break;
				}
			}
		}
		return items;
	}

	private DependencyGraph createGraph(Scope graphScope,
			Set<Dependable> items, Set<Dependable> selectedParents,
			DependencyFilter dependencyFilter) {
		DependencyGraph graph = null;
		if (dependencyFilter == DependencyFilter.itemsContributingToTheParentDependencyWeight) {
			graph = new DependencyGraph(graphScope);
		} else {
			graph = new DependencyGraph(graphScope, items);
		}
		for (Dependable item : items) {
			addDependencies(graph, item, graphScope, selectedParents,
					dependencyFilter);
		}
		return graph;
	}

	private void addDependencyToGraph(DependencyGraph graph,
			Dependable dependant, Dependable dependee,
			Set<Dependable> selectedParents, DependencyFilter dependencyFilter) {
		if (dependencyFilter == DependencyFilter.itemsContributingToTheParentDependencyWeight) {
			Scope parentGraphScope = null;
			for (Dependable parent : selectedParents) {
				parentGraphScope = parent.getScope();
				break;
			}
			if (dependant == null || dependee == null
					|| dependant.equals(dependee) || selectedParents == null
					|| parentGraphScope == null)
				return;
			Set<Dependable> parentsOfDependant = getParents(dependant,
					parentGraphScope);
			if (parentsOfDependant == null)
				return;
			for (Dependable parentOfDependee : getParents(dependee,
					parentGraphScope)) {
				if (parentOfDependee != null
						&& selectedParents.contains(parentOfDependee)) {
					for (Dependable parentOfDependant : parentsOfDependant) {
						if (!selectedParents.contains(parentOfDependant))
							continue;
						if (!parentOfDependee.equals(parentOfDependant)) {
							if (!graph.getAllItems().contains(dependant))
								graph.addItem(dependant);
							if (!graph.getAllItems().contains(dependee))
								graph.addItem(dependee);
							graph.addDependency(dependant, dependee);
							return;
						}
					}
				}
			}
		} else {
			graph.addDependency(dependant, dependee);
		}
	}

	private Set<Dependable> getItems(Scope scope) {
		Set<Dependable> result = allItems.get(scope);
		return result != null ? result : Collections.<Dependable> emptySet();
	}

	private void addDependencies(DependencyGraph graph, Dependable item,
			Scope graphScope, Set<Dependable> selectedParents,
			DependencyFilter dependencyFilter) {
		addDirectDependencies(graph, item, graphScope, selectedParents,
				dependencyFilter);
		addChildDependencies(graph, item, item, graphScope, selectedParents,
				dependencyFilter);
	}

	private void addDirectDependencies(DependencyGraph graph, Dependable item,
			Scope graphScope, Set<Dependable> selectedParents,
			DependencyFilter dependencyFilter) {
		Map<Dependable, Integer> directDependenciesMap = dependencies.get(item);
		if (directDependenciesMap != null) {
			Set<Dependable> directDependencies = directDependenciesMap.keySet();
			for (Dependable dep : directDependencies) {
				addDependencies(graph, item, graphScope, directDependenciesMap,
						dep, selectedParents, dependencyFilter);

			}
		}
	}

	private void addDependencies(DependencyGraph graph, Dependable item,
			Scope scope, Map<Dependable, Integer> directDependenciesMap,
			Dependable dep, Set<Dependable> selectedParents,
			DependencyFilter dependencyFilter) {
		if (dep.getScope().equals(scope)) {
			Integer weight = directDependenciesMap.get(dep);

			while (weight > 0) {
				addDependencyToGraph(graph, item, dep, selectedParents,
						dependencyFilter);
				weight--;
			}
		}
	}

	private void addChildDependencies(DependencyGraph graph,
			Dependable dependant, Dependable item, Scope graphScope,
			Set<Dependable> selectedParents, DependencyFilter dependencyFilter) {
		for (Dependable child : getChilds(item)) {
			addChildDependencies(graph, dependant, child, graphScope,
					selectedParents, dependencyFilter);
			Map<Dependable, Integer> childDependenciesMap = dependencies
					.get(child);
			if (childDependenciesMap == null)
				continue;
			Set<Dependable> childDeps = childDependenciesMap.keySet();
			for (Dependable dep : childDeps) {
				for (Dependable parent : getParents(dep, graphScope))
					addDependencyToGraph(graph, dependant, parent,
							selectedParents, dependencyFilter);
			}
		}
	}

	public Set<Dependable> getAllItems() {
		Set<Dependable> items = new HashSet<Dependable>();
		for (Scope scope : getAvailableScopes()) {
			items.addAll(allItems.get(scope));
		}
		return items;
	}

	public Set<Dependable> getParents(Dependable item, Scope scope) {
		ParentInfo parentInfo = parents.get(item);
		return parentInfo != null ? parentInfo.getParents(scope) : Collections
				.<Dependable> emptySet();
	}

	public Scope getParentScope(Dependable item) {
		List<Scope> scopes = getAvailableScopes();
		if (item.getScope().index() > 0) {
			return scopes.get(item.getScope().index() - 1);
		}
		return null;
	}

	public List<Scope> getParentScopes(Dependable item) {
		List<Scope> parentScopes = getAvailableScopes();
		parentScopes = parentScopes.subList(0, item.getScope().index());
		return parentScopes;
	}

	public Set<Dependable> getParentsFromAllScopes(Dependable item) {
		List<Scope> parentScopes = getParentScopes(item);
		Set<Dependable> parents = new HashSet<Dependable>();
		for (Scope scope : parentScopes) {
			parents.addAll(getParents(item, scope));
		}
		return parents;
	}

	public Set<Dependable> getChilds(Dependable item) {
		Set<Dependable> result = allChilds.get(item);
		return result != null ? result : Collections.<Dependable> emptySet();
	}

	public void addDependencies(Dependable dependant,
			Map<? extends Dependable, Integer> newDependees) {
		addItem(dependant);

		for (Dependable dependee : newDependees.keySet()) {
			addItem(dependee);
		}

		Map<Dependable, Integer> dependees = dependencies.get(dependant);
		if (dependees == null) {
			dependees = new HashMap<Dependable, Integer>();
			dependencies.put(dependant, dependees);
		}
		dependees.putAll(newDependees);

		scopeGraphCache.clear();
	}

	public void addChild(Dependable parent, Dependable child) {
		addItem(parent);
		if (parent.equals(child))
			return; // guard for ownership relation on self
		addItem(child);
		Set<Dependable> childs = allChilds.get(parent);
		if (childs == null) {
			childs = new HashSet<Dependable>();
			allChilds.put(parent, childs);
		}
		childs.add(child);
		setParent(parent, child);

		scopeGraphCache.clear();
	}

	private void setParent(Dependable newParent, Dependable child) {
		ParentInfo parentInfo = parents.get(child);
		if (parentInfo == null) {
			parentInfo = new ParentInfo();
			parents.put(child, parentInfo);
		}
		parentInfo.addParent(newParent);
		setParentToChildsOf(child, newParent);
	}

	private void setParentToChildsOf(Dependable item, Dependable newParent) {
		for (Dependable child : getChilds(item)) {
			if (child.equals(newParent))
				continue;// guard for cyclic ownership relations
			setParent(newParent, child);
		}
	}

	private void addItem(Dependable item) {
		Scope scope = item.getScope();
		Set<Dependable> items = allItems.get(scope);
		if (items == null) {
			items = new HashSet<Dependable>();
			allItems.put(scope, items);
		}
		items.add(item);
	}
}
