//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dtangler.swingui.dsm.DsmView;
import org.dtangler.ui.dsm.DsmGuiModel;
import org.dtangler.ui.dsm.DsmGuiModelChangeListener;

public class DsmPresenter implements ListSelectionListener,
		DsmGuiModelChangeListener {

	private final DsmTableModelAdapter tableModel;
	private final DsmGuiModel model;
	private final DsmView view;

	DsmPresenter(DsmView view, DsmGuiModel model) {
		this.view = view;
		this.model = model;
		tableModel = new DsmTableModelAdapter(model);
		view.setTableModel(tableModel);
		view.addSelectionListener(this);
		model.addChangeListener(this);
	}

	public void dsmGuiModelChanged() {
		view.refresh();
	}

	public void dsmDataChanged() {
		tableModel.fireTableStructureChanged();
		view.refreshTableStructure();
	}

	public void valueChanged(ListSelectionEvent e) {
		model.selectCells(normalizeIndices(view.getSelectedRows(), 0),
				normalizeIndices(view.getSelectedColumns(), -1));
		view.refreshPopupMenu();
	}

	private List<Integer> normalizeIndices(int[] selectedColumns, int delta) {
		List<Integer> result = new ArrayList();
		for (int col : selectedColumns) {
			int index = col + delta;
			if (index >= 0)
				result.add(index);
		}
		return result;
	}

}
