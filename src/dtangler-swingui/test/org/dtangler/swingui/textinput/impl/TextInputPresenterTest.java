//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.textinput.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TextInputPresenterTest {

	private MockTextInputView view;
	private TextInputModel model;
	private TextInputPresenter presenter;

	@Before
	public void setUp() {
		view = new MockTextInputView();
		model = new TextInputModel("myField", "myTitle");
		presenter = new TextInputPresenter(view, model);
	}

	@Test
	public void testViewConfiguration() {
		assertEquals("myField", view.getFieldName());
		assertEquals("myTitle", view.getTitle());
	}

	@Test
	public void testCanOk() {
		view.setValue("");
		assertFalse(presenter.canOk());

		view.setValue("a");
		assertTrue(presenter.canOk());
	}

	@Test
	public void testOnOk() {
		assertNull(model.getValue());

		view.setValue("myValue");
		presenter.onOk();

		assertEquals("myValue", model.getValue());
	}
}
