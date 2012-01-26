//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager.impl;

import javax.swing.JFrame;

import org.dtangler.swingui.windowmanager.WindowInteractionProvider;

public class WindowInteractionProviderJFrameImpl implements
		WindowInteractionProvider {

	private final JFrame frame;

	public WindowInteractionProviderJFrameImpl(JFrame frame) {
		this.frame = frame;
	}

	public void updateTitle(String newTitle) {
		frame.setTitle(newTitle);
	}
}
