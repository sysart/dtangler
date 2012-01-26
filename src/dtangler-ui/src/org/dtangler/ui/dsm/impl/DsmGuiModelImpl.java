//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation.Severity;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.ui.dsm.CellInfo;
import org.dtangler.ui.dsm.DependableInfo;
import org.dtangler.ui.dsm.DsmGuiModel;
import org.dtangler.ui.dsm.DsmGuiModelChangeListener;
import org.dtangler.ui.dsm.NullCellInfo;

public class DsmGuiModelImpl implements DsmGuiModel, ColumnAndRowSelectionModel {

	private final List<DsmGuiModelChangeListener> listeners = new ArrayList();
	private Formatter displayNameFormatter;
	private DisplayNameFormat displayNameFormat = DisplayNameFormat.full;
	private Dsm dsm;
	private AnalysisResult analysisResult;
	private List<Integer> selectedRows = Collections.EMPTY_LIST;
	private List<Integer> selectedCols = Collections.EMPTY_LIST;
	private int crossHairRow = -1;
	private int crossHairCol = -1;

	private final Map<Integer, Map<Integer, CellInfo>> cellInfoCache = new HashMap();

	public CellInfo getCellInfo(final int row, final int col) {
		Map<Integer, CellInfo> rowCache = cellInfoCache.get(row);
		if (rowCache == null) {
			rowCache = new HashMap<Integer, CellInfo>();
			cellInfoCache.put(row, rowCache);
		}

		CellInfo cellInfo = rowCache.get(col);
		if (cellInfo == null) {
			cellInfo = createCellInfo(row, col);
			rowCache.put(col, cellInfo);
		}
		return cellInfo;
	}

	private CellInfo createCellInfo(final int row, final int col) {
		DsmCell dsmCell = getDsmCell(row, col);

		if (!dsmCell.isValid())
			return NullCellInfo.instance;

		Dependency dependency = dsm.getRows().get(row).getCells().get(col)
				.getDependency();

		return new CellInfoImpl(dsmCell, new AnalysisResultInfoImpl(
				hasViolations(dependency, Severity.error), hasViolations(
						dependency, Severity.warning)), row, col, this);
	}

	private boolean hasViolations(Dependency dependency, Severity severity) {
		return !analysisResult.getViolations(dependency, severity).isEmpty();
	}

	private boolean containsViolations(Dependable dependable, Severity severity) {
		return !analysisResult.getChildViolations(dependable, severity)
				.isEmpty();
	}

	public boolean isRowOrColumnInCrossHair(final int row, final int col) {
		if (multiCellSelectionExists())
			return false;
		return crossHairRow == row || crossHairCol == col;
	}

	public int getColumnCount() {
		return getRowCount();
	}

	public DependableInfo getColumnInfo(int index) {
		return createDependableInfo(index, false);
	}

	public DependableInfo getRowInfo(int index) {
		boolean isSelected = selectedRows.contains(index)
				&& selectedCols.isEmpty();

		return createDependableInfo(index, isSelected);
	}

	private DependableInfo createDependableInfo(int index, boolean isSelected) {
		boolean isInCrossHair = isInCrosshair(index);
		DsmRow dsmRow = dsm.getRows().get(index);
		return new DependableInfoImpl(dsmRow, index + 1, isInCrossHair,
				isSelected, containsViolations(dsmRow.getDependee(),
						Severity.error), containsViolations(dsmRow
						.getDependee(), Severity.warning),
				getDisplayNameFormatter());
	}

	private boolean isInCrosshair(final int index) {
		if (multiCellSelectionExists())
			return false;
		return crossHairCol == index || crossHairRow == index;
	}

	private boolean multiCellSelectionExists() {
		return selectedCols.size() != 1 || selectedRows.size() != 1;
	}

	private DsmCell getDsmCell(int row, int col) {
		return dsm.getRows().get(row).getCells().get(col);
	}

	public int getRowCount() {
		if (dsm == null)
			return 0;
		return dsm.getRows().size();
	}

	public void selectCells(List<Integer> rows, List<Integer> cols) {
		this.selectedRows = rows;
		this.selectedCols = cols;
		if (!rows.isEmpty())
			this.crossHairRow = rows.get(0).intValue();
		else
			this.crossHairRow = -1;
		if (!cols.isEmpty())
			this.crossHairCol = cols.get(0).intValue();
		else
			this.crossHairCol = -1;
		fireDsmGuiModelChanged();
	}

	public List<Integer> getSelectedRows() {
		return this.selectedRows;
	}

	public List<Integer> getSelectedCols() {
		return this.selectedCols;
	}

	private void fireDsmGuiModelChanged() {
		for (DsmGuiModelChangeListener listener : listeners)
			listener.dsmGuiModelChanged();
	}

	// TODO efficiency problems with this
	private void fireDsmDataChanged() {
		for (DsmGuiModelChangeListener listener : listeners)
			listener.dsmDataChanged();
	}

	public void addChangeListener(DsmGuiModelChangeListener listener) {
		listeners.add(listener);
	}

	public void clearSelection() {
		selectCells(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	public void setDsm(Dsm dsm, AnalysisResult analysisResult) {
		this.analysisResult = analysisResult;
		clearSelection();
		cellInfoCache.clear();
		this.dsm = dsm;
		displayNameFormatter = null;
		fireDsmDataChanged();
	}

	public Set<Dependency> getSelectionDependencies() {
		Set<Dependency> result = new HashSet();

		for (Integer col : selectedCols)
			for (Integer row : selectedRows)
				result.add(getDsmCell(row, col).getDependency());
		return result;
	}

	public Set<Dependable> getSelectionDependables() {
		if (selectedRows.isEmpty())
			return Collections.EMPTY_SET;
		Set<Dependency> selectionDependencies = getSelectionDependencies();
		Set<Dependable> result = new HashSet();
		if (selectionDependencies.isEmpty()) {
			for (int row : selectedRows)
				result.add(dsm.getRows().get(row).getDependee());
		} else {
			for (Dependency dependency : selectionDependencies) {
				result.add(dependency.getDependant());
				result.add(dependency.getDependee());
			}
		}
		return result;
	}

	public boolean isSelected(int row, int col) {
		return selectedRows.contains(row) && selectedCols.contains(col);
	}

	public void setDisplayNameFormat(DisplayNameFormat nameFormat) {
		if (nameFormat.equals(this.displayNameFormat))
			return;
		displayNameFormat = nameFormat;
		displayNameFormatter = null;
		fireDsmGuiModelChanged();
	}

	private Formatter getDisplayNameFormatter() {
		if (displayNameFormatter == null) {
			if (displayNameFormat.equals(DisplayNameFormat.shortened))
				displayNameFormatter = new ShortenedNameFormatter(getRowNames());
			else
				displayNameFormatter = new PassThroughFormatter();

		}
		return displayNameFormatter;
	}

	private List<String> getRowNames() {
		List<String> names = new ArrayList();
		for (DsmRow row : dsm.getRows())
			names.add(row.getDependee().getDisplayName());
		return names;
	}

	public DisplayNameFormat getDisplayNameFormat() {
		return displayNameFormat;
	}
}
