//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.dtangler.ui.dsm.DependableInfo;

public class DependableInfoRowTableCellRenderer implements TableCellRenderer {

	private static final JLabel Normal = createLabel(Color.lightGray);
	private static final JLabel Crosshair = createLabel(Color.gray);
	private static final JLabel Selected = createLabel(Color.gray);
	private static final JLabel error_selected = createLabel(Color.red.darker());
	private static final JLabel error_normal = createLabel(Color.red);
	private static final JLabel warning_selected = createLabel(Color.yellow
			.darker());
	private static final JLabel warning_normal = createLabel(Color.yellow);
	private static final char Space = ' ';
	private static final char Open = '(';
	private static final char Close = ')';

	private static JLabel createLabel(Color color) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		label.setBackground(color);
		return label;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		DependableInfo info = (DependableInfo) value;
		JLabel label = getLabel(info);
		label.setText(buildText(info.getIndex(), info.getDisplayName(), info
				.getContentCount()));
		label.setToolTipText(info.getFullyQualifiedName());
		return label;
	}

	private JLabel getLabel(DependableInfo info) {
		if (info.containsErrors())
			return (info.isInCrosshair() || info.isSelected()) ? error_selected
					: error_normal;
		if (info.containsWarnings())
			return (info.isInCrosshair() || info.isSelected()) ? warning_selected
					: warning_normal;
		if (info.isInCrosshair())
			return Crosshair;
		if (info.isSelected())
			return Selected;
		return Normal;
	}

	private String buildText(int index, String name, int contentCount) {
		StringBuilder sb = new StringBuilder(50);
		if (index < 10)
			sb.append(Space);
		if (index < 100)
			sb.append(Space);
		sb.append(index).append(Space);
		sb.append(name).append(Space);
		sb.append(Open).append(contentCount).append(Close);
		return sb.toString();
	}

}
