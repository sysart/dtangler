//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.testutil.cyclic.part1;

import org.dtangler.core.testutil.cyclic.part2.CyclicB;

public class CyclicA {

	private CyclicB dependency;

	public CyclicA() {
		dependency.toString();
	}
}