//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import org.dtangler.swingui.aboutinfodisplayer.AboutInfoDisplayer;

public class MockAboutInfoDisplayer implements AboutInfoDisplayer {

	private boolean infoDisplayed;

	public boolean wasAboutInfoDisplayed() {
		return infoDisplayed;
	}

	public void displayAboutInfo() {
		infoDisplayed = true;
	}

}
