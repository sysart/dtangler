//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.dtangler.swingui.textinput.TextInputSelector;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;

public class TextInputSelectorFeatureTest {

	private MockWindowManager windowManager;
	private TextInputSelector selector;

	@Before
	public void setUp() {
		windowManager = new MockWindowManager();
		selector = new TextInputSelectorImpl(windowManager);
	}

	@Test
	public void testFieldNameAndDialogTitle() {
		selector.selectValue("my field", "my title");
		TextInputViewDriver view = new TextInputViewDriver(windowManager
				.getLastShownView());
		assertEquals("my title", view.title);
		assertEquals("my field", view.fieldName.getText());
	}

	@Test
	public void testOkButtonIsEnabledOnlyWhenValueIsNotEmpty() {
		selector.selectValue("my field", "my title");
		TextInputViewDriver view = new TextInputViewDriver(windowManager
				.getLastShownView());
		assertEquals("", view.value.getText());
		assertFalse(view.okButton.isEnabled().isTrue());

		view.value.setText("a");
		assertTrue(view.okButton.isEnabled().isTrue());
	}

	@Test
	public void testOk() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				TextInputViewDriver view = new TextInputViewDriver(
						windowManager.getLastShownView());
				view.value.setText("myvalue");
				view.okButton.click();
			}

		});

		String result = selector.selectValue("my field", "my title");
		assertEquals("myvalue", result);
		assertNull("window was closed", windowManager.getLastShownView());
	}

	@Test
	public void testCancel() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				TextInputViewDriver view = new TextInputViewDriver(
						windowManager.getLastShownView());
				view.value.setText("myvalue");
				view.cancelButton.click();
			}

		});

		String result = selector.selectValue("my field", "my title");
		assertNull(result);
		assertNull("window was closed", windowManager.getLastShownView());
	}

}
