// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

public class TestDependable extends Dependable {

	public TestDependable(String name) {
		this(name, TestScope.scope1);
	}

	public TestDependable(String name, Scope scope) {
		super(scope, name, name, 0);
	}

}
