//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.testutil.cyclic.part2;

import org.dtangler.core.testutil.cyclic.part1.CyclicA;

public class CyclicB {

	private CyclicA dependency;

	public CyclicB() {
		dependency.toString();
	}
}