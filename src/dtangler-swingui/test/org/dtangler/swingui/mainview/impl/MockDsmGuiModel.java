//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.ui.dsm.CellInfo;
import org.dtangler.ui.dsm.DependableInfo;
import org.dtangler.ui.dsm.DsmGuiModel;
import org.dtangler.ui.dsm.DsmGuiModelChangeListener;

public class MockDsmGuiModel implements DsmGuiModel {

	private DsmGuiModelChangeListener listener;

	public void addChangeListener(DsmGuiModelChangeListener listener) {
		this.listener = listener;
	}

	public void clearSelection() {

	}

	public CellInfo getCellInfo(int row, int col) {
		return null;
	}

	public int getColumnCount() {
		return 0;
	}

	public int getRowCount() {
		return 0;
	}

	public DsmGuiModelChangeListener getListener() {
		return listener;
	}

	public void setDsm(Dsm dsm, AnalysisResult analysisResult) {

	}

	public void selectCells(List<Integer> selectedRows,
			List<Integer> selectedCols) {
	}

	public Set<Dependency> getSelectionDependencies() {
		return Collections.EMPTY_SET;
	}

	public Set<Dependable> getSelectionDependables() {
		return Collections.EMPTY_SET;
	}

	public DependableInfo getColumnInfo(int index) {
		return null;
	}

	public DependableInfo getRowInfo(int index) {
		return null;
	}

	public void setDisplayNameFormat(DisplayNameFormat nameFormat) {
	}

	public DisplayNameFormat getDisplayNameFormat() {
		return null;
	}

	public List<Integer> getSelectedCols() {
		return Collections.emptyList();
	}

	public List<Integer> getSelectedRows() {
		return Collections.emptyList();
	}
}
