//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.MockDependencyEngine;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.fileinput.FileInputSelector;
import org.dtangler.swingui.textinput.MockTextInputSelector;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;

public class FileInputSelectorFeatureTest {

	private MockDirectorySelector directorySelector;
	private MockTextInputSelector textInputSelector;
	private MockWindowManager windowManager;
	private FileInputSelector selector;
	private FileInputSelection defaultInput;
	private DependencyEngine dependencyEngine;
	
	@Before
	public void setUp() {
		directorySelector = new MockDirectorySelector();
		textInputSelector = new MockTextInputSelector();
		windowManager = new MockWindowManager();
		dependencyEngine = new MockDependencyEngine();
		dependencyEngine.setDependencyEngineId("defaultDependencyEngine");
		DependencyEngine anotherDependencyEngine = new MockDependencyEngine();
		anotherDependencyEngine.setDependencyEngineId("anotherDependencyEngine");
		DependencyEngineFactory dependencyEngineFactory = new DependencyEngineFactory(dependencyEngine, anotherDependencyEngine);
		selector = new FileInputSelectorImpl(directorySelector,
				textInputSelector, windowManager, dependencyEngineFactory);
		defaultInput = new FileInputSelection(dependencyEngine.getDependencyEngineId(), Arrays.asList("mypath1",
				"mypath1/mypath2"), Arrays.asList("*foo*", "*bar*"));
	}

	@Test
	public void testDefaultValues() {
		selector.selectInput(defaultInput);
		FileInputViewDriver view = new FileInputViewDriver(windowManager
				.getLastShownView());
		assertTrue(view.engineCombo.contentEquals(new String[] {"defaultDependencyEngine", "anotherDependencyEngine"}).isTrue());
		assertTrue(view.paths.contentEquals(
				new String[] { "mypath1", "mypath1/mypath2" }).isTrue());
		assertTrue(view.masks.contentEquals(new String[] { "*foo*", "*bar*" })
				.isTrue());
	}

	@Test
	public void testAddAndRemovePaths() {
		selector.selectInput(defaultInput);
		FileInputViewDriver view = new FileInputViewDriver(windowManager
				.getLastShownView());

		directorySelector.setNextValue("mypath3");
		view.addPathButton.click();

		assertTrue(view.paths.contentEquals(
				new String[] { "mypath1", "mypath1/mypath2", "mypath3" })
				.isTrue());

		view.paths.selectIndices(new int[] { 0, 2 });
		view.removePathButton.click();

		assertTrue(view.paths.contentEquals(new String[] { "mypath1/mypath2" })
				.isTrue());

		directorySelector.setNextValue(null);
		assertTrue(view.paths.contentEquals(new String[] { "mypath1/mypath2" })
				.isTrue());

		directorySelector.setNextValue("mypath1/mypath2");
		assertTrue(view.paths.contentEquals(new String[] { "mypath1/mypath2" })
				.isTrue());
	}

	@Test
	public void testAddAndRemoveMasks() {
		selector.selectInput(defaultInput);
		FileInputViewDriver view = new FileInputViewDriver(windowManager
				.getLastShownView());

		textInputSelector.setNextValue("*bay*");
		view.addMaskButton.click();

		assertTrue(view.masks.contentEquals(
				new String[] { "*foo*", "*bar*", "*bay*" }).isTrue());

		view.masks.selectIndices(new int[] { 0, 2 });
		view.removeMaskButton.click();

		assertTrue(view.masks.contentEquals(new String[] { "*bar*" }).isTrue());

		textInputSelector.setNextValue(null);
		assertTrue(view.masks.contentEquals(new String[] { "*bar*" }).isTrue());
		textInputSelector.setNextValue("*bar*");
		assertTrue(view.masks.contentEquals(new String[] { "*bar*" }).isTrue());
	}

	@Test
	public void testRemoveMaskIsEnabledOnlyWhenAtleastOnePathSelected() {
		selector.selectInput(defaultInput);
		FileInputViewDriver view = new FileInputViewDriver(windowManager
				.getLastShownView());
		assertFalse(view.removePathButton.isEnabled().isTrue());
		view.paths.selectIndex(0);
		assertTrue(view.removePathButton.isEnabled().isTrue());
	}

	@Test
	public void testRemoveMaskIsEnabledOnlyWhenAtleastOneMaskSelected() {
		selector.selectInput(defaultInput);
		FileInputViewDriver view = new FileInputViewDriver(windowManager
				.getLastShownView());
		assertFalse(view.removeMaskButton.isEnabled().isTrue());
		view.masks.selectIndex(0);
		assertTrue(view.removeMaskButton.isEnabled().isTrue());
	}

	@Test
	public void testOkIsEnabledOnlyWhenAtleastOnePathSelected() {
		selector.selectInput(defaultInput);
		FileInputViewDriver view = new FileInputViewDriver(windowManager
				.getLastShownView());
		assertTrue(view.okButton.isEnabled().isTrue());

		view.paths.selectIndices(new int[] { 0, 1 });
		view.removePathButton.click();

		assertFalse(view.okButton.isEnabled().isTrue());

		directorySelector.setNextValue("some value");
		view.addPathButton.click();

		view.masks.selectIndices(new int[] { 0, 1 });
		view.removeMaskButton.click();

		assertTrue(view.okButton.isEnabled().isTrue());
	}

	@Test
	public void testOk() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				FileInputViewDriver view = new FileInputViewDriver(
						windowManager.getLastShownView());

				view.engineCombo.select("anotherDependencyEngine");
				view.masks.selectIndices(new int[] { 0, 1 });

				directorySelector.setNextValue("some path");
				view.addPathButton.click();

				textInputSelector.setNextValue("some mask");
				view.addMaskButton.click();

				view.okButton.click();
			}
		});
		FileInputSelection result = selector.selectInput(defaultInput);
		assertEquals(Arrays.asList("mypath1", "mypath1/mypath2", "some path"),
				result.getPaths());
		assertEquals(Arrays.asList("*foo*", "*bar*", "some mask"), result
				.getIgnoredFileMasks());
		assertTrue("anotherDependencyEngine".equals(result.getEngine()));
		assertNull("window was closed", windowManager.getLastShownView());
	}

	@Test
	public void testCancel() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				FileInputViewDriver view = new FileInputViewDriver(
						windowManager.getLastShownView());

				view.masks.selectIndices(new int[] { 0, 1 });

				directorySelector.setNextValue("some path");
				view.addPathButton.click();

				textInputSelector.setNextValue("some mask");
				view.addMaskButton.click();

				view.cancelButton.click();
			}
		});
		FileInputSelection result = selector.selectInput(defaultInput);
		assertNull(result);
		assertNull("window was closed", windowManager.getLastShownView());
	}

}
