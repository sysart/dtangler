//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.directoryselector.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;

import javax.swing.JFileChooser;

import org.junit.Before;
import org.junit.Test;

public class SwingDirectorySelectorTest {

	private TestableSwingDirectorySelector selector;

	@Before
	public void setUp() {
		selector = new TestableSwingDirectorySelector();
	}

	public void testViewConfiguration() {
		JFileChooser fileChooser = selector.getFileChooser();
		assertEquals("Select directory", fileChooser.getDialogTitle());
		assertEquals("Select", fileChooser.getApproveButtonText());
		assertEquals(JFileChooser.DIRECTORIES_ONLY, fileChooser
				.getFileSelectionMode());
		assertFalse(fileChooser.isAcceptAllFileFilterUsed());
	}

	@Test
	public void testSelectDirectory() {
		File path = new File("/foo");
		selector.getFileChooser().setSelectedFile(path);
		selector.setNextDialogResult(JFileChooser.APPROVE_OPTION);

		assertEquals(path.getAbsolutePath(), selector.selectDirectory());
	}

	@Test
	public void testCancel() {
		String path = File.separator + "foo";
		selector.getFileChooser().setSelectedFile(new File(path));
		selector.setNextDialogResult(JFileChooser.CANCEL_OPTION);

		assertNull(selector.selectDirectory());
	}
}
