// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.dependenciesbuilder;

import java.util.HashMap;
import java.util.Map;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;

public class DependenciesBuilder {
	private final Dependencies deps = new Dependencies();

	public Dependencies getDependencies() {
		return deps;
	}

	public Dependencies addDependencies(DependencyGraph dependencies) {
		for (Dependable dependant : dependencies.getAllItems()) {
			for (Dependable dependee : dependencies.getDependencies(dependant)) {
				deps.addDependencies(dependant, createMap(dependee));
			}
		}
		return deps;
	}

	protected Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}
}
