//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import javax.swing.JLabel;

import org.dtangler.ui.dsm.MockCellInfo;
import org.dtangler.ui.dsm.NullCellInfo;
import org.junit.Test;

public class CellInfoTableCellRendererTest {

	private JLabel getRenderedComponent(Object value) {
		return (JLabel) new CellInfoTableCellRenderer()
				.getTableCellRendererComponent(null, value, false, false, 0, 0);
	}

	@Test
	public void testRenderNullCellInfo() {
		JLabel c = getRenderedComponent(NullCellInfo.instance);
		assertTrue(c.isOpaque());
		assertEquals(Color.gray.darker(), c.getBackground());
		assertEquals("", c.getText());
	}

	@Test
	public void testRenderSelectedCellInfo() {
		JLabel c = getRenderedComponent(new MockCellInfo(3, true, false));
		assertTrue(c.isOpaque());
		assertEquals(CellInfoTableCellRenderer.NORMAL_SELECTION_BG, c
				.getBackground());
		assertEquals("3", c.getText());
	}

	@Test
	public void testRenderNonSelectedCellInfo() {
		JLabel c = getRenderedComponent(new MockCellInfo(5, false, false));
		assertFalse(c.isOpaque());
		assertEquals("5", c.getText());
	}

	@Test
	public void testRenderSelectedCellInfoWithCycles() {
		JLabel c = getRenderedComponent(new MockCellInfo(3, true, true));
		assertTrue(c.isOpaque());
		assertEquals(CellInfoTableCellRenderer.ERROR_SELECTION_BG, c
				.getBackground());
		assertEquals("3", c.getText());
	}

	@Test
	public void testRenderNonSelectedCellInfoWithCycles() {
		JLabel c = getRenderedComponent(new MockCellInfo(3, false, true));
		assertTrue(c.isOpaque());
		assertEquals(Color.red, c.getBackground());
		assertEquals("3", c.getText());
	}

}
