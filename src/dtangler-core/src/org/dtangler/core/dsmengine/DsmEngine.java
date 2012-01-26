// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dsmengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;

public class DsmEngine {

	private final DependencyGraph dependencies;

	public DsmEngine(DependencyGraph dependencies) {
		this.dependencies = dependencies;
	}

	public Dsm createDsm() {
		List<Dependable> allItems = new ArrayList(dependencies.getAllItems());
		Collections.sort(allItems, new InstabilityComparator(dependencies));

		List<DsmRow> rows = new ArrayList(allItems.size());
		for (Dependable item : allItems)
			rows.add(new DsmRow(item, createRowCells(item, allItems)));
		return new Dsm(rows);
	}

	private List<DsmCell> createRowCells(Dependable rowItem,
			List<Dependable> allItems) {
		List<DsmCell> cells = new ArrayList();
		for (Dependable colItem : allItems)
			cells.add(createCell(rowItem, colItem));

		return cells;
	}

	private DsmCell createCell(Dependable rowItem, Dependable colItem) {
		int depCount = dependencies.getDependencyWeight(colItem, rowItem);
		return new DsmCell(colItem, rowItem, depCount);
	}
}
