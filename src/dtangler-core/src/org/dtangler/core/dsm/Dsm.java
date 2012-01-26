// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dsm;

import java.util.List;

public class Dsm {

	private final List<DsmRow> rows;

	public Dsm(List<DsmRow> rows) {
		this.rows = rows;
	}

	public List<DsmRow> getRows() {
		return rows;
	}

}
