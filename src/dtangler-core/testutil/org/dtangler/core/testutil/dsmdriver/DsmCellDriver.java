// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.dsmdriver;

import org.dtangler.core.dsm.DsmCell;

public class DsmCellDriver {

	private final DsmCell dsmCell;

	public DsmCellDriver(DsmCell dsmCell) {
		this.dsmCell = dsmCell;
	}

	public int getWeight() {
		return dsmCell.getDependencyWeight();
	}

}
