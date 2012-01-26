//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager.impl;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class ErrorDisplayComponent {

	private final JTextArea detailsField;
	private final JComponent viewComponent;

	ErrorDisplayComponent(Throwable t) {
		detailsField = createDetailsField();
		viewComponent = createViewComponent(detailsField);
		setDetails(t);
	}

	private JTextArea createDetailsField() {
		JTextArea area = new JTextArea();
		area.setName("detailsField");
		area.setEditable(false);
		return area;
	}

	private JComponent createViewComponent(JTextArea detailsField) {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:250dlu:grow", "p,8dlu,p,p,p,8dlu,p,fill:50dlu:grow"));

		builder.append("An internal error has occured.");
		builder.nextRow();
		builder
				.append("<html>If you like, you can report the error to <b>contact@dtangler.org</b>");
		builder
				.append("Reporting the error to us will help us improve this tool\n");
		builder.append("Thank you and sorry for the inconvenience.");
		builder.nextRow();
		builder.appendSeparator("Error details");
		builder.append(new JScrollPane(detailsField));
		return builder.getPanel();
	}

	private void setDetails(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(getMessage(t));
		sb.append("\nat:\n");
		for (StackTraceElement item : t.getStackTrace()) {
			sb.append(item);
			sb.append("\n");
		}
		detailsField.setText(sb.toString());
		detailsField.setCaretPosition(0);
	}

	private String getMessage(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.getClass().getSimpleName());
		if (t.getMessage() != null && !"".equals(t.getMessage())
				&& !t.getMessage().equals("null"))
			sb.append("\nmessage: " + t.getMessage());
		return sb.toString();
	}

	public JComponent getViewComponent() {
		return viewComponent;
	}
}
