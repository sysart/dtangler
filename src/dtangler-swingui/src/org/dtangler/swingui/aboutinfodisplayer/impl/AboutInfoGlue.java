//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.aboutinfodisplayer.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dtangler.swingui.aboutinfodisplayer.impl.AboutInfoView.Actions;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.windowmanager.SwingView;
import org.dtangler.swingui.windowmanager.WindowManager;

public class AboutInfoGlue {

	public AboutInfoGlue(ActionFactory actionFactory,
			final WindowManager windowManager, final SwingView view) {

		actionFactory.setImplementation(Actions.close, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				windowManager.close(view);
			}
		});
	}

}
