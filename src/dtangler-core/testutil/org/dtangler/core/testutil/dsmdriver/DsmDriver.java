// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.testutil.dsmdriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;

public class DsmDriver {

	private final Dsm dsm;
	private final Map<String, Integer> itemIndices = new HashMap();

	public DsmDriver(Dsm dsm) {
		this.dsm = dsm;
		int i = 0;
		for (DsmRow row : dsm.getRows())
			itemIndices.put(row.getDependee().getDisplayName(), i++);
	}

	public DsmRowDriver row(String name) {
		Integer index = itemIndices.get(name);
		if (index == null)
			throw new RuntimeException("row not found by name:" + name);

		return new DsmRowDriver(dsm.getRows().get(index), itemIndices);

	}

	public List<String> getRowNames() {
		List<String> names = new ArrayList();
		for (DsmRow row : dsm.getRows())
			names.add(row.getDependee().getDisplayName());
		return names;
	}

}
