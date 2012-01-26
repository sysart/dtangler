//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.actionfactory;

import java.awt.event.ActionListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DocumentChangeActionAdapter implements DocumentListener {
	private final ActionListener listener;

	public DocumentChangeActionAdapter(ActionListener listener) {
		this.listener = listener;
	}

	public void changedUpdate(DocumentEvent e) {
		listener.actionPerformed(null);
	}

	public void insertUpdate(DocumentEvent e) {
		listener.actionPerformed(null);
	}

	public void removeUpdate(DocumentEvent e) {
		listener.actionPerformed(null);
	}

}
