//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm;

import java.util.List;
import java.util.Set;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dsm.Dsm;

public interface DsmGuiModel {

	public enum DisplayNameFormat {
		full, shortened
	}

	void setDsm(Dsm dsm, AnalysisResult analysisResult);

	int getColumnCount();

	int getRowCount();

	DependableInfo getColumnInfo(int index);

	DependableInfo getRowInfo(int index);

	CellInfo getCellInfo(int row, int col);

	void selectCells(List<Integer> selectedRows, List<Integer> selectedCols);

	List<Integer> getSelectedRows();

	List<Integer> getSelectedCols();

	void clearSelection();

	void addChangeListener(DsmGuiModelChangeListener listener);

	Set<Dependency> getSelectionDependencies();

	Set<Dependable> getSelectionDependables();

	void setDisplayNameFormat(DisplayNameFormat nameFormat);

	DisplayNameFormat getDisplayNameFormat();
}
