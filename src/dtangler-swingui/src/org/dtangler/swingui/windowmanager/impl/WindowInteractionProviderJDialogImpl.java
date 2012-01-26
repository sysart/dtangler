//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager.impl;

import javax.swing.JDialog;

import org.dtangler.swingui.windowmanager.WindowInteractionProvider;

public class WindowInteractionProviderJDialogImpl implements
		WindowInteractionProvider {

	private final JDialog dlg;

	public WindowInteractionProviderJDialogImpl(JDialog dlg) {
		this.dlg = dlg;
	}

	public void updateTitle(String newTitle) {
		dlg.setTitle(newTitle);
	}

}
