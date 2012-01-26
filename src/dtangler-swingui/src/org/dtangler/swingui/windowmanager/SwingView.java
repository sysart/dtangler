//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

public interface SwingView {

	Dimension getPreferredSize();

	JComponent getViewComponent();

	JMenuBar getMenuBar();

	String getTitle();

	void setWindowInteractionProvider(
			WindowInteractionProvider windowInteractionProvider);

	Component getFirstComponentToFocus();

}
