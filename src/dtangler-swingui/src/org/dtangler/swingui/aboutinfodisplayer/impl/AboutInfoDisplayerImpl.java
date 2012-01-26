//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.aboutinfodisplayer.impl;

import org.dtangler.swingui.aboutinfodisplayer.AboutInfoDisplayer;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.windowmanager.WindowManager;

public class AboutInfoDisplayerImpl implements AboutInfoDisplayer {

	private final WindowManager windowManager;

	public AboutInfoDisplayerImpl(WindowManager windowManager) {
		this.windowManager = windowManager;
	}

	public void displayAboutInfo() {
		ActionFactory actionFactory = new ActionFactory();
		SwingAboutInfoView view = new SwingAboutInfoView(actionFactory);
		new AboutInfoGlue(actionFactory, windowManager, view);
		windowManager.showModal(view);
	}

}
