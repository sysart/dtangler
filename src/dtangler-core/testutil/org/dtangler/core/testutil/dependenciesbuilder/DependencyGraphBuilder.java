// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.dependenciesbuilder;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.TestDependable;

public class DependencyGraphBuilder {

	public class DependantBuilder {

		private final String dependant;

		public DependantBuilder(String name) {
			this.dependant = name;
			DependencyGraphBuilder.this.deps.addItem(new TestDependable(name));
		}

		/**
		 * Adds dependency from this item to the <i>dependee</i> and returns
		 * DependencyBuilder for possible further dependency building
		 */
		public DependencyBuilder dependsOn(String dependee) {
			return dependsOn(dependee, 1);
		}

		/**
		 * Adds dependency from this item to the <i>dependee</i> with a
		 * dependency weight of <i>times</i> and returns DependencyBuilder for
		 * possible further dependency building
		 */
		public DependencyBuilder dependsOn(String dependee, int times) {
			return new DependencyBuilder(dependant, dependee, times);
		}
	}

	public class DependencyBuilder {

		private final Dependable dependant;
		private final Dependable dependee;

		public DependencyBuilder(String dependantName, String dependeeName,
				int times) {
			this.dependant = new TestDependable(dependantName);
			this.dependee = new TestDependable(dependeeName);
			DependencyGraphBuilder.this.deps.addItem(dependee);
			for (int i = 0; i < times; i++)
				DependencyGraphBuilder.this.deps.addDependency(dependant,
						dependee);
		}

		/**
		 * Adds dependency from this item to the dependee by <i>name</i> and
		 * returns DependencyBuilder for adding more dependencies from <b>the
		 * dependee</b> to other items
		 */
		public DependencyBuilder dependsOn(String dependee) {
			return dependsOn(dependee, 1);
		}

		/**
		 * Adds dependency from this item to the dependee by <i>name</i> with a
		 * dependency weight of <i>times</i> and returns DependencyBuilder for
		 * adding more dependencies from <b>the dependee</b> to other items
		 */
		public DependencyBuilder dependsOn(String name, int times) {
			return new DependantBuilder(dependee.getDisplayName()).dependsOn(
					name, times);
		}

		/**
		 * Adds dependency from this item to the dependee by <i>name</i> and
		 * returns DependencyBuilder for adding more dependencies from <b>this
		 * item</b> to other items
		 */
		public DependencyBuilder and(String name) {
			return and(name, 1);
		}

		/**
		 * Adds dependency from this item to the dependee by <i>name</i> with a
		 * dependency weight of <i>times</i> and returns DependencyBuilder for
		 * adding more dependencies from <b>this item</b> to other items
		 */
		public DependencyBuilder and(String name, int times) {
			return new DependencyBuilder(dependant.getDisplayName(), name,
					times);
		}
	}

	final DependencyGraph deps = new DependencyGraph(null);

	public DependencyGraph getDependencies() {
		return deps;
	}

	/**
	 * Adds the item to the dependencies allItems list and returns
	 * DependantBuilder for possible Dependency adding
	 */
	public DependantBuilder add(String name) {
		return new DependantBuilder(name);
	}

	/**
	 * Adds the dependency dependant-->dependee but only adds dependee to the
	 * dependencies allItems list
	 */
	public void addOuterDependant(String dependantName, String dependeeName) {
		addOuterDependency(dependantName, dependeeName, dependantName);
	}

	/**
	 * Adds the dependency dependant-->dependee but only adds dependant to the
	 * dependencies allItems list
	 */
	public void addOuterDependee(String dependantName, String dependeeName) {
		addOuterDependency(dependantName, dependeeName, dependeeName);
	}

	private void addOuterDependency(String dependantName, String dependeeName,
			String outer) {
		Dependable dependant = new TestDependable(dependantName);
		Dependable dependee = new TestDependable(dependeeName);

		Dependable inner = dependantName.equals(outer) ? dependee : dependant;
		deps.addItem(inner);
		deps.addDependency(dependant, dependee);
	}

}
