// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dsmengine;

import java.util.Comparator;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;

public class InstabilityComparator implements Comparator<Dependable> {

	private final DependencyGraph dependencies;

	public InstabilityComparator(DependencyGraph dependencies) {
		this.dependencies = dependencies;
	}

	public int compare(Dependable o1, Dependable o2) {
		int result = compareInstability(o1, o2);
		if (result == 0)
			result = compareDependencyWeight(o1, o2);
		if (result == 0)
			return o1.getFullyQualifiedName().compareTo(
					o2.getFullyQualifiedName());
		return result;
	}

	private int compareInstability(Dependable o1, Dependable o2) {
		float o1Instability = dependencies.getInstability(o1);
		float o2Instability = dependencies.getInstability(o2);
		if (o1Instability > o2Instability)
			return -1;
		if (o1Instability < o2Instability)
			return 1;
		return 0;
	}

	private int compareDependencyWeight(Dependable o1, Dependable o2) {
		int o1CeSum = dependencies.getOutgoingDependenciesWeight(o1);
		int o2CeSum = dependencies.getOutgoingDependenciesWeight(o2);
		if (o1CeSum > o2CeSum)
			return -1;
		if (o1CeSum < o2CeSum)
			return 1;
		return 0;
	}

}
