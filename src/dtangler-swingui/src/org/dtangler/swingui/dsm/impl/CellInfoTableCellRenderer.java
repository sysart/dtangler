//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import org.dtangler.ui.dsm.CellInfo;
import org.dtangler.ui.dsm.NullCellInfo;

public class CellInfoTableCellRenderer implements TableCellRenderer {

	static final Color NORMAL_SELECTION_BG = new Color(161, 202, 110);
	static final Color ERROR_BG = Color.red;
	static final Color WARNING_BG = Color.yellow;
	static final Color ERROR_SELECTION_BG = Color.red.darker();
	static final Color WARNING_SELECTION_BG = Color.yellow.darker();
	static final Color CELL_SELECTION_BG = new Color(203, 229, 173);

	private static final JLabel invalid = createLabel(Color.gray.darker(),
			true, null);
	private static final JLabel invalid_selected = createLabel(Color.gray
			.darker(), true, new LineBorder(Color.lightGray));

	private static final JLabel normal = createLabel(Color.white, false, null);
	private static final JLabel normal_selected = createLabel(
			CELL_SELECTION_BG, true, new LineBorder(Color.gray));
	private static final JLabel normal_crosshair = createLabel(
			NORMAL_SELECTION_BG, true, null);
	private static final JLabel error = createLabel(ERROR_BG, true, null);
	private static final JLabel error_selected = createLabel(
			ERROR_SELECTION_BG, true, new LineBorder(Color.gray));
	private static final JLabel error_crosshair = createLabel(
			ERROR_SELECTION_BG, true, null);
	private static final JLabel warning = createLabel(WARNING_BG, true, null);
	private static final JLabel warning_selected = createLabel(
			WARNING_SELECTION_BG, true, new LineBorder(Color.gray));
	private static final JLabel warning_crosshair = createLabel(
			WARNING_SELECTION_BG, true, null);
	private static final String Empty = "";

	private static JLabel createLabel(Color background, boolean opaque,
			Border border) {
		JLabel label = new JLabel();
		label.setBackground(background);
		label.setOpaque(opaque);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(border);
		return label;
	}

	private static JLabel getLabel(boolean isSelected, boolean isInCrossHair,
			boolean hasErrors, boolean hasWarnings) {
		if (hasErrors) {
			if (isSelected)
				return error_selected;
			if (isInCrossHair)
				return error_crosshair;
			return error;
		}
		if (hasWarnings) {
			if (isSelected)
				return warning_selected;
			if (isInCrossHair)
				return warning_crosshair;
			return warning;
		}

		if (isSelected)
			return normal_selected;
		if (isInCrossHair)
			return normal_crosshair;
		return normal;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		CellInfo cellInfo = (CellInfo) value;

		if (cellInfo == NullCellInfo.instance)
			return isSelected ? invalid_selected : invalid;

		JLabel l = getLabel(cellInfo.isSelected(), cellInfo.isInCrossHair(),
				cellInfo.getAnalysisResultInfo().hasErrors(), cellInfo
						.getAnalysisResultInfo().hasWarnings());

		l.setText(getText(cellInfo));
		return l;
	}

	private String getText(CellInfo cellInfo) {
		int dependencyWeight = cellInfo.getDependencyWeight();
		if (dependencyWeight == 0)
			return Empty;
		return Integer.toString(dependencyWeight);
	}
}
