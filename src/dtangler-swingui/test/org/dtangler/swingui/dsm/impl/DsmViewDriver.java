//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.uispec4j.MenuItem;
import org.uispec4j.Table;

public class DsmViewDriver {

	private Table table;

	public DsmViewDriver(JComponent component) {
		this(new Table((JTable) component));
	}

	public DsmViewDriver(Table table) {
		this.table = table;
	}

	public int getColumCount() {
		return table.getColumnCount();
	}

	public int getRowCount() {
		return table.getRowCount();
	}

	public String getCellText(int row, int col) {
		return (String) table.getContentAt(row, col);
	}

	public void selectCell(int col, int row) {
		table.selectCell(row, col);
	}

	public void selectBlock(int top, int left, int bottom, int right) {
		table.selectBlock(top, left, bottom, right);
	}

	public MenuItem getPopup() {
		return new MenuItem(getJTable().getComponentPopupMenu());
	}

	private JTable getJTable() {
		return (JTable) table.getAwtComponent();
	}

}
