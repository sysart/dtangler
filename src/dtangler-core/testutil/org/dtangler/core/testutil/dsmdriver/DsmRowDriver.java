// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.dsmdriver;

import java.util.Map;

import org.dtangler.core.dsm.DsmRow;

public class DsmRowDriver {

	private final DsmRow dsmRow;
	private final Map<String, Integer> itemIndices;

	public DsmRowDriver(DsmRow dsmRow, Map<String, Integer> itemIndices) {
		this.dsmRow = dsmRow;
		this.itemIndices = itemIndices;
	}

	public DsmCellDriver col(String name) {
		Integer index = itemIndices.get(name);
		if (index == null)
			throw new RuntimeException("column not found by name:" + name);

		return new DsmCellDriver(dsmRow.getCells().get(index));
	}

}
