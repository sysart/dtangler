//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import javax.swing.table.AbstractTableModel;

import org.dtangler.swingui.dsm.SwingDsm;
import org.dtangler.ui.dsm.CellInfo;
import org.dtangler.ui.dsm.DependableInfo;
import org.dtangler.ui.dsm.DsmGuiModel;

public class DsmTableModelAdapter extends AbstractTableModel {

	private final DsmGuiModel model;

	public DsmTableModelAdapter(DsmGuiModel model) {
		this.model = model;
	}

	public String getColumnName(int column) {
		SwingDsm.class.toString();
		if (column == 0)
			return null;
		return "" + model.getColumnInfo(column - 1).getIndex();
	}

	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return DependableInfo.class;
		return CellInfo.class;
	}

	public int getColumnCount() {
		return model.getColumnCount() + 1;
	}

	public int getRowCount() {
		return model.getRowCount();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return model.getRowInfo(rowIndex);

		return model.getCellInfo(rowIndex, columnIndex - 1);
	}
}
