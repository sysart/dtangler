//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import javax.swing.JLabel;

import org.dtangler.ui.dsm.MockDependableInfo;
import org.junit.Test;

public class DependableInfoRowTableCellRendererTest {

	private JLabel getRenderedComponent(Object value) {
		return (JLabel) new DependableInfoRowTableCellRenderer()
				.getTableCellRendererComponent(null, value, false, false, 0, 0);
	}

	@Test
	public void testRenderDependableInfo() {
		JLabel c = getRenderedComponent(new MockDependableInfo("FooBar", 2, 10,
				false));
		assertEquals(Color.lightGray, c.getBackground());
		assertTrue(c.isOpaque());
		assertEquals("  2 FooBar (10)", c.getText());

		c = getRenderedComponent(new MockDependableInfo("FooBar", 14, 8, true));
		assertEquals(Color.gray, c.getBackground());
		assertTrue(c.isOpaque());
		assertEquals(" 14 FooBar (8)", c.getText());

	}

}
