// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ScopeComparatorTest {

	enum MyScope implements Scope {
		scope1, scope3, scope2, scope4;

		public String getDisplayName() {
			return name();
		}

		public int index() {
			return ordinal();
		}
	}

	@Test
	public void testCompare() {
		List<Scope> scopes = new ArrayList();
		scopes.add(MyScope.scope3);
		scopes.add(MyScope.scope4);
		scopes.add(MyScope.scope2);
		scopes.add(MyScope.scope1);

		Collections.sort(scopes, new ScopeComparator());
		assertSame(MyScope.scope1, scopes.get(0));
		assertSame(MyScope.scope3, scopes.get(1));
		assertSame(MyScope.scope2, scopes.get(2));
		assertSame(MyScope.scope4, scopes.get(3));
	}

}
