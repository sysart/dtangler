// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.cycleanalysis;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.TestDependable;
import org.junit.Test;

public class CycleFinderPerformanceTest {

	@Test
	public void testFoo() {
		Dependencies dependencies = new Dependencies();

		Dependable a1 = new TestDependable("a1");
		TestDependable a11 = new TestDependable("a1.1");
		TestDependable a111 = new TestDependable("a1.1.1");
		TestDependable a1111 = new TestDependable("a1.1.1.1");
		TestDependable a1112 = new TestDependable("a1.1.1.2");
		TestDependable a1113 = new TestDependable("a1.1.1.3");
		TestDependable a1114 = new TestDependable("a1.1.1.4");
		TestDependable a1115 = new TestDependable("a1.1.1.5");

		Dependable a2 = new TestDependable("a2");
		TestDependable a21 = new TestDependable("a2.1");
		TestDependable a211 = new TestDependable("a2.1.1");
		TestDependable a2111 = new TestDependable("a2.1.1.1");
		TestDependable a2112 = new TestDependable("a2.1.1.2");
		TestDependable a2113 = new TestDependable("a2.1.1.3");
		TestDependable a2114 = new TestDependable("a2.1.1.4");
		TestDependable a2115 = new TestDependable("a2.1.1.5");

		Dependable a3 = new TestDependable("a3");
		TestDependable a31 = new TestDependable("a3.1");
		TestDependable a311 = new TestDependable("a3.1.1");
		TestDependable a3111 = new TestDependable("a3.1.1.1");
		TestDependable a3112 = new TestDependable("a3.1.1.2");
		TestDependable a3113 = new TestDependable("a3.1.1.3");
		TestDependable a3114 = new TestDependable("a3.1.1.4");
		TestDependable a3115 = new TestDependable("a3.1.1.5");

		Dependable a4 = new TestDependable("a4");
		TestDependable a41 = new TestDependable("a4.1");
		TestDependable a411 = new TestDependable("a4.1.1");
		TestDependable a4111 = new TestDependable("a4.1.1.1");
		TestDependable a4112 = new TestDependable("a4.1.1.2");
		TestDependable a4113 = new TestDependable("a4.1.1.3");
		TestDependable a4114 = new TestDependable("a4.1.1.4");
		TestDependable a4115 = new TestDependable("a4.1.1.5");

		dependencies.addDependencies(a1, createMap(a11));
		dependencies.addDependencies(a11, createMap(a111));
		dependencies.addDependencies(a111, createMap(a1111, a1112, a1113,
				a1114, a1115));

		dependencies.addDependencies(a2, createMap(a21));
		dependencies.addDependencies(a21, createMap(a211));
		dependencies.addDependencies(a211, createMap(a2111, a2112, a2113,
				a2114, a2115));

		dependencies.addDependencies(a3, createMap(a31));
		dependencies.addDependencies(a31, createMap(a311));
		dependencies.addDependencies(a311, createMap(a3111, a3112, a3113,
				a3114, a3115));

		dependencies.addDependencies(a4, createMap(a41));
		dependencies.addDependencies(a41, createMap(a411));
		dependencies.addDependencies(a411, createMap(a4111, a4112, a4113,
				a4114, a4115));

		Dependable b = new TestDependable("b");
		Dependable b1 = new TestDependable("b1");
		Dependable b2 = new TestDependable("b2");
		Dependable b3 = new TestDependable("b3");
		Dependable b4 = new TestDependable("b4");
		Dependable x = new TestDependable("x");
		Dependable y = new TestDependable("y");
		Dependable z = new TestDependable("z");

		dependencies.addDependencies(b, createMap(b1, b2, b3, b4));
		dependencies.addDependencies(x, createMap(b));
		dependencies.addDependencies(y, createMap(b));
		dependencies.addDependencies(z, createMap(b));

		CycleValidator f = new CycleValidator(false);
		f.analyze(dependencies);
		f.getViolations();
		assertEquals(35, f.stepCount()); // was 203
	}

	protected Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}
}
