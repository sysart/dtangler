// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.cycleanalysis;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.TestDependable;

public class TestDependencyCycle extends DependencyCycle {

	public TestDependencyCycle(List<String> names) {
		super(toDependables(names));
	}

	public static List<Dependable> toDependables(List<String> strings) {
		List<Dependable> cycle = new ArrayList<Dependable>();
		for (String string : strings) {
			cycle.add(new TestDependable(string));
		}
		return cycle;
	}

}
