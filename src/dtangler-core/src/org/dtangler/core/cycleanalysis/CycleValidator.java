// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.cycleanalysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.analysis.DependencyAnalyzer;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.DependencyPath;
import org.dtangler.core.dependencies.Scope;

public class CycleValidator extends DependencyAnalyzer {

	private final Set<Dependency> processedItems = new HashSet();
	private final boolean cyclesAllowed;
	private int stepCount = 0;

	public CycleValidator(boolean cyclesAllowed) {
		this.cyclesAllowed = cyclesAllowed;
	}

	private void findCycles(Dependable item, DependencyGraph dependencies) {
		Set<Dependable> deps = dependencies.getDependencies(item);

		for (Dependable dep : deps)
			findCycles(item, createPath(new DependencyPath(Collections
					.singletonList(item)), dep), dep, dependencies);
	}

	private DependencyPath createPath(DependencyPath prevPath,
			Dependable nextItem) {
		DependencyPath path = new DependencyPath(prevPath.getItems());
		path.addItem(nextItem);
		return path;
	}

	private void findCycles(Dependable item, DependencyPath path,
			Dependable dep, DependencyGraph dependencies) {
		Set<Dependable> childDeps = dependencies.getDependencies(dep);

		Dependency dependency = path.getDependencyByDependee(dep);
		if (processedItems.contains(dependency))
			return;

		stepCount++;
		for (Dependable childDep : childDeps) {
			if (path.contains(childDep)) {
				addCycle(childDep, path);
			} else {
				findCycles(item, createPath(path, childDep), childDep,
						dependencies);
			}
		}
		processedItems.add(dependency);
	}

	private void addCycle(Dependable item, DependencyPath path) {
		List<Dependable> pathItems = path.getItems();
		List<Dependable> cycleMembers = pathItems.subList(pathItems
				.indexOf(item), pathItems.size());
		for (int i = 0; i < cycleMembers.size(); i++) {
			Dependable dependant = cycleMembers.get(i);
			Dependable dependee = getDependee(i, cycleMembers);
			Dependency dependency = new Dependency(dependant, dependee);
			DependencyCycle cycle = createCycle(dependant, cycleMembers);
			addViolation(dependency, cycle);
		}
	}

	private Dependable getDependee(int index, List<Dependable> cycleMembers) {
		if (index >= cycleMembers.size() - 1)
			return cycleMembers.get(0);
		return cycleMembers.get(index + 1);
	}

	private DependencyCycle createCycle(Dependable cycleMember,
			List<Dependable> cycleMembers) {
		List<Dependable> cycle = new ArrayList();
		cycle.add(cycleMember);
		int index = cycleMembers.indexOf(cycleMember);
		int count = cycleMembers.size();
		if (index < cycleMembers.size() - 1)
			cycle.addAll(cycleMembers.subList(index + 1, count));
		if (index > 0)
			cycle.addAll(cycleMembers.subList(0, index));
		cycle.add(cycleMember);

		return new DependencyCycle(cycle);
	}

	// for test purposes
	int stepCount() {
		return stepCount;
	}

	@Override
	public void doAnalyze(Dependencies dependencies) {
		for (Scope scope : dependencies.getAvailableScopes()) {
			analyze(dependencies.getDependencyGraph(scope));
		}
	}

	private void analyze(DependencyGraph dependencies) {
		processedItems.clear();
		Set<Dependable> items = dependencies.getAllItems();
		for (Dependable dep : items) {
			findCycles(dep, dependencies);
		}
	}

	public boolean isValidResult() {
		if (cyclesAllowed)
			return true;
		return getViolations().isEmpty();
	}
}
