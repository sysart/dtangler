// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyGraph {

	private final Scope scope;
	private final Map<Dependable, Set<Dependable>> dependants = new HashMap<Dependable, Set<Dependable>>();
	private final Map<Dependable, Set<Dependable>> dependencies = new HashMap<Dependable, Set<Dependable>>();
	private final Map<Dependency, Integer> dependencyWeights = new HashMap<Dependency, Integer>();
	private final Set<Dependable> allItems = new HashSet<Dependable>();

	public DependencyGraph(Scope scope) {
		this.scope = scope;
	}

	public DependencyGraph(Scope scope, Collection<Dependable> items) {
		this.scope = scope;
		this.allItems.addAll(items);
	}

	public void addItem(Dependable item) {
		this.allItems.add(item);
	}

	public void addDependency(Dependable dependant, Dependable dependee) {
		if (!isValid(dependant) || !isValid(dependee))
			return;
		if (dependant.equals(dependee))
			return;
		getDependencies(dependant).add(dependee);
		getDependants(dependee).add(dependant);
		addDependencyWeight(dependant, dependee);
	}

	private boolean isValid(Dependable dependant) {
		return dependant != null && allItems.contains(dependant);
	}

	private void addDependencyWeight(Dependable dependant, Dependable dependee) {
		Dependency dependency = new Dependency(dependant, dependee);
		Integer weight = dependencyWeights.get(dependency);
		if (weight == null)
			weight = 1;
		else
			weight++;
		dependencyWeights.put(dependency, weight);
	}

	public Set<Dependable> getAllItems() {
		return allItems;
	}

	public Set<Dependable> getDependants(Dependable dependant) {
		Set<Dependable> classes = dependants.get(dependant);
		if (classes == null) {
			classes = new HashSet<Dependable>();
			dependants.put(dependant, classes);
		}
		return classes;
	}

	public Set<Dependable> getDependencies(Dependable dependee) {
		Set<Dependable> classes = dependencies.get(dependee);
		if (classes == null) {
			classes = new HashSet<Dependable>();
			dependencies.put(dependee, classes);
		}
		return classes;
	}

	public float getInstability(Dependable item) {
		float ce = getDependencyCount(item);
		float ca = getDependantCount(item);
		float f = (ce / (ce + ca));
		return f;
	}

	private int getDependencyCount(Dependable item) {
		return getDependencies(item).size();
	}

	private int getDependantCount(Dependable item) {
		return getDependants(item).size();
	}

	public int getDependencyWeight(Dependable dependant, Dependable dependee) {
		return getDependencyWeight(new Dependency(dependant, dependee));
	}

	public int getOutgoingDependenciesWeight(Dependable item) {
		int sum = 0;
		for (Dependable dependee : getDependencies(item)) {
			sum += getDependencyWeight(item, dependee);
		}
		return sum;
	}

	private int getDependencyWeight(Dependency dependency) {
		Integer weight = dependencyWeights.get(dependency);
		return weight != null ? weight : 0;
	}

	public Dependable getItemByName(String name) {
		for (Dependable dependable : getAllItems()) {
			if (dependable.getDisplayName().equals(name))
				return dependable;
		}
		return null;
	}

	public Scope getScope() {
		return scope;
	}
}
