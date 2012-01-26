//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

public interface DsmView {

	void setPopupMenuForHeaderCells(JPopupMenu popupMenu);

	void setPopupMenuForDataCells(JPopupMenu popupMenu);

	void refreshPopupMenu();

	JComponent getJComponent();
	
	void addSelectionListener(ListSelectionListener listener);

	void setTableModel(TableModel tableModel);

	int[] getSelectedRows();

	int[] getSelectedColumns();

	int getRowCount();

	void refreshTableStructure();

	void refresh();

}
