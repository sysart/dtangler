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
import javax.swing.table.TableCellRenderer;

import org.dtangler.ui.dsm.DependableInfo;

public class DependableInfoColumnTableCellRenderer implements TableCellRenderer {

	private final static JLabel Empty = new JLabel();
	private final static JLabel Selected = createLabel(Color.gray);
	private final static JLabel Background1 = createLabel(Color.lightGray);
	private final static JLabel Background2 = createLabel(new Color(204, 204,
			204));

	private static JLabel createLabel(Color color) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(color);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 0)
			return Empty;

		DependableInfo info = (DependableInfo) table.getValueAt(column - 1, 0);
		JLabel label = getLabel(info.isInCrosshair(), column);
		label.setText(Integer.toString(info.getIndex()));
		label.setToolTipText(info.getDisplayName());
		return label;
	}

	private JLabel getLabel(boolean selected, int column) {
		if (selected)
			return Selected;
		return column % 2 == 0 ? Background1 : Background2;
	}
}
