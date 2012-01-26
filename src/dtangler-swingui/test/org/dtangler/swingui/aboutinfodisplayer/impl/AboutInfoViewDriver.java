//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.aboutinfodisplayer.impl;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.Panel;

public class AboutInfoViewDriver {

	public final Button okButton;
	private final JPanel viewComponent;

	public AboutInfoViewDriver(SwingView view) {
		viewComponent = (JPanel) view.getViewComponent();
		Panel panel = new Panel(view.getViewComponent());
		okButton = panel.getButton("Ok");
	}

	public void pressKey(KeyStroke keyStroke) {
		Object actionKey = viewComponent.getInputMap(
				JComponent.WHEN_IN_FOCUSED_WINDOW).get(keyStroke);
		viewComponent.getActionMap().get(actionKey).actionPerformed(null);
	}
}
