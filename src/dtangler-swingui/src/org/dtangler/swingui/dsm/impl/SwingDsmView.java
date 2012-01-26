//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import org.dtangler.swingui.dsm.DsmView;
import org.dtangler.ui.dsm.CellInfo;
import org.dtangler.ui.dsm.DependableInfo;

public class SwingDsmView extends JTable implements DsmView {

	JPopupMenu popupMenuForHeaderCells;
	JPopupMenu popupMenuForDataCells;

	public SwingDsmView() {
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);
		setCellSelectionEnabled(true);
		setDefaultRenderer(CellInfo.class, new CellInfoTableCellRenderer());
		setDefaultRenderer(DependableInfo.class,
				new DependableInfoRowTableCellRenderer());
		getTableHeader().setDefaultRenderer(
				new DependableInfoColumnTableCellRenderer());
	}

	public JComponent getJComponent() {
		return this;
	}

	public void setPopupMenuForHeaderCells(JPopupMenu popupMenu) {
		this.popupMenuForHeaderCells = popupMenu;
	}

	public void setPopupMenuForDataCells(JPopupMenu popupMenu) {
		this.popupMenuForDataCells = popupMenu;
	}

	private void setDefaultMenu() {
		if (popupMenuForHeaderCells != null)
			this.setComponentPopupMenu(popupMenuForHeaderCells);
	}

	public void refreshPopupMenu() {
		int[] selectedColumns = getSelectedColumns();
		int[] selectedRows = getSelectedRows();
		if (selectedColumns == null)
			return;
		for (int col : selectedColumns) {
			if (col == 0) {
				this.setComponentPopupMenu(popupMenuForHeaderCells);
				break;
			} else {
				if (selectedColumns.length == 1 && selectedRows != null
						&& selectedRows.length == 1) {
					if (col - 1 == selectedRows[0]) {
						this.setComponentPopupMenu(popupMenuForHeaderCells);
						break;
					}
				}
				this.setComponentPopupMenu(popupMenuForDataCells);
				break;
			}
		}
		if (this.getComponentPopupMenu() == null)
			setDefaultMenu();
	}

	public void setTableModel(TableModel model) {
		super.setModel(model);
		refreshTableStructure();
	}

	private void setColumnWidths() {
		getColumnModel().getColumn(0).setPreferredWidth(300);
		for (int i = 1; i < getColumnModel().getColumnCount(); i++)
			getColumnModel().getColumn(i).setPreferredWidth(35);
	}

	public void addSelectionListener(ListSelectionListener listener) {
		getSelectionModel().addListSelectionListener(listener);
		getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	public void refreshTableStructure() {
		setColumnWidths();
	}

	public void refresh() {
		getTableHeader().repaint();
		repaint();
	}

}
