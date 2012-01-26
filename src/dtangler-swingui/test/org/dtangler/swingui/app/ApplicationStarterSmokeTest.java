//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.app;

import static org.junit.Assert.assertNotNull;

import org.dtangler.swingui.windowmanager.MockDialogManager;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Test;

public class ApplicationStarterSmokeTest {

	@Test
	public void testStartup() {
		MockWindowManager windowManager = new MockWindowManager();
		MockDialogManager dialogManager = new MockDialogManager();

		new ApplicationStarter(windowManager, dialogManager)
				.start(new String[] { "_build" });
		assertNotNull(windowManager.getLastShownView());
	}
}
