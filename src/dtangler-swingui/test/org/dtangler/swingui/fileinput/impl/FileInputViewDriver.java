//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;

public class FileInputViewDriver {

	public final ComboBox engineCombo;
	public final ListBox paths;
	public final ListBox masks;
	public final Button addPathButton;
	public final Button removePathButton;
	public final Button addMaskButton;
	public final Button removeMaskButton;
	public final Button okButton;
	public final Button cancelButton;

	public FileInputViewDriver(SwingView view) {
		Panel panel = new Panel(view.getViewComponent());
		engineCombo = panel.getComboBox("engineCombo");
		paths = panel.getListBox("PATHS");
		masks = panel.getListBox("MASKS");
		addPathButton = panel.getButton("addPath");
		removePathButton = panel.getButton("removePath");
		addMaskButton = panel.getButton("addMask");
		removeMaskButton = panel.getButton("removeMask");
		okButton = panel.getButton("OK");
		cancelButton = panel.getButton("Cancel");
	}
}
