//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import org.dtangler.core.dependencies.Dependencies;

public class MockAnalyzer extends DependencyAnalyzer {

	private boolean isValid;

	public MockAnalyzer() {
		this(true);
	}

	public MockAnalyzer(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isValidResult() {
		return isValid;
	}

	@Override
	public void doAnalyze(Dependencies dependencies) {

	}

}
