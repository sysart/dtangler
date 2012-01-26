// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dsm;

import java.util.List;

import org.dtangler.core.dependencies.Dependable;

public class DsmRow {

	private final List<DsmCell> cells;
	private final Dependable dependee;

	public DsmRow(Dependable dependee, List<DsmCell> cells) {
		this.dependee = dependee;
		this.cells = cells;
	}

	public List<DsmCell> getCells() {
		return cells;
	}

	public Dependable getDependee() {
		return dependee;
	}
}
