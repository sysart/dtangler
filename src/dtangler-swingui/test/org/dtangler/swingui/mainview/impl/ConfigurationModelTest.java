// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.mainview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.swingui.windowmanager.MockDialogManager;
import org.dtangler.swingui.windowmanager.DialogManager.DialogResult;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationModelTest {

	private TestableConfigurationModel model;
	private MockDialogManager dialogManager;
	private MockFileSelector fileSelector;

	@Before
	public void setUp() {
		Arguments arguments = new Arguments();
		arguments.setScope("origscope");
		dialogManager = new MockDialogManager();
		fileSelector = new MockFileSelector();
		model = new TestableConfigurationModel(fileSelector, dialogManager,
				arguments);
	}

	@Test
	public void testIsDirty() {
		assertFalse(model.isDirty());
		model.getArguments().setScope("anotherscope");
		assertTrue(model.isDirty());
	}

	@Test
	public void testNewConfiguration() {
		assertTrue(model.newConfiguration());
		assertEquals(new Arguments(), model.getArguments());
		assertFalse(model.isDirty());
	}

	@Test
	public void testNewConfigurationAndCancelWhenDirty() {
		model.getArguments().setScope("myscope");
		dialogManager.setNextResult(DialogResult.cancel);

		assertFalse(model.newConfiguration());
		assertTrue(model.isDirty());
		assertEquals("myscope", model.getArguments().getScope());
	}

	@Test
	public void testNewConfigurationAndDontSaveWhenDirty() {
		model.getArguments().setScope("myscope");
		dialogManager.setNextResult(DialogResult.no);

		assertTrue(model.newConfiguration());
		assertFalse(model.isDirty());
		assertEquals(new Arguments(), model.getArguments());
	}

	@Test
	public void testNewConfigurationAndSaveWhenDirty() {
		model.getArguments().setScope("myscope");

		fileSelector.setNextFile(null);
		dialogManager.setNextResult(DialogResult.yes);

		assertFalse(model.newConfiguration());
		assertTrue(model.isDirty());
		assertEquals("myscope", model.getArguments().getScope());

		fileSelector.setNextFile("myprops");
		dialogManager.setNextResult(DialogResult.yes);

		assertTrue(model.newConfiguration());
		assertFalse(model.isDirty());
		assertEquals(new Arguments(), model.getArguments());
		assertEquals("myprops", model.getLastSavedFile());
		assertEquals("unsaved settings", model.getFileName());
	}

	@Test
	public void testOpenConfiguration() {
		Arguments myArgs = new Arguments();
		myArgs.setScope("myscope");
		fileSelector.setNextFile("myprops");
		model.setArgumentsToOpen(myArgs);
		assertTrue(model.openConfiguration());
		assertSame(myArgs, model.getArguments());
		assertFalse(model.isDirty());
		assertEquals("myprops", model.getLastOpenedFile());
		assertEquals("myprops", model.getFileName());
	}

	@Test
	public void testOpenConfigurationAndCancel() {
		Arguments myArgs = new Arguments();
		myArgs.setScope("myscope");
		fileSelector.setNextFile(null);
		model.setArgumentsToOpen(myArgs);
		assertFalse(model.openConfiguration());
		assertNull(model.getLastOpenedFile());
		assertEquals("unsaved settings", model.getFileName());
	}

	@Test
	public void testOpenConfigurationWhenDirty() {
		model.getArguments().setScope("myscope");

		dialogManager.setNextResult(DialogResult.cancel);
		assertFalse(model.openConfiguration());
		assertTrue(model.isDirty());
		assertNull(model.getLastOpenedFile());
	}

	@Test
	public void testSave() {
		createModelFromConfigFile("originalprops");
		fileSelector.setNextFile("dontUseThisOne");

		model.getArguments().setScope("myscope");
		assertTrue(model.isDirty());

		model.save();
		assertFalse(model.isDirty());
		assertEquals("originalprops", model.getLastSavedFile());
		assertEquals("originalprops", model.getFileName());
	}

	private void createModelFromConfigFile(String fileName) {
		Arguments arguments = new Arguments();
		arguments.setConfigFileName(fileName);
		model = new TestableConfigurationModel(fileSelector, dialogManager,
				arguments);
	}

	@Test
	public void testSaveWhenNoFilename() {
		fileSelector.setNextFile("mypropsfile");
		model.save();
		assertFalse(model.isDirty());
		assertEquals("mypropsfile", model.getLastSavedFile());
		assertEquals("mypropsfile", model.getFileName());
	}

	@Test
	public void testSaveAs() {
		createModelFromConfigFile("originalprops");
		fileSelector.setNextFile("newprops");

		model.getArguments().setScope("myscope");
		assertTrue(model.isDirty());

		model.saveAs();
		assertFalse(model.isDirty());
		assertEquals("newprops", model.getLastSavedFile());
		assertEquals("newprops", model.getFileName());
	}

	@Test
	public void testCancelOnSave() {
		fileSelector.setNextFile(null);
		model.getArguments().setScope("myscope");
		assertTrue(model.isDirty());

		model.save();
		assertTrue(model.isDirty());
		assertNull(model.getLastSavedFile());
		assertEquals("unsaved settings", model.getFileName());
	}

}
