//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.MockDependencyEngine;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.textinput.MockTextInputSelector;
import org.junit.Before;
import org.junit.Test;

public class DefaultFileInputSelectorModelTest {

	private MockDirectorySelector directorySelector;
	private MockTextInputSelector textInputSelector;
	private FileInputSelection defaultInput;
	private DefaultFileInputSelectorModel model;

	@Before
	public void setUp() {
		directorySelector = new MockDirectorySelector();
		textInputSelector = new MockTextInputSelector();
		DependencyEngine dependencyEngine = new MockDependencyEngine();
		DependencyEngineFactory dependencyEngineFactory = new DependencyEngineFactory(dependencyEngine);
		defaultInput = new FileInputSelection(dependencyEngine.getDependencyEngineId(), Arrays.asList("path1", "path2"),
				Arrays.asList("mask1", "mask2"));
		model = new DefaultFileInputSelectorModel(directorySelector,
				textInputSelector, dependencyEngineFactory, defaultInput);
	}

	@Test
	public void textAddAndRemovePaths() {
		directorySelector.setNextValue("foo");
		model.addPath();
		assertEquals(Arrays.asList("path1", "path2", "foo"), model.getPaths());

		directorySelector.setNextValue("foo");
		model.addPath();
		assertEquals(Arrays.asList("path1", "path2", "foo"), model.getPaths());

		model.removePaths(Arrays.asList("foo", "path1"));
		assertEquals(Arrays.asList("path2"), model.getPaths());
	}

	@Test
	public void textAddAndRemoveMasks() {
		textInputSelector.setNextValue("foo");
		model.addMask();
		assertEquals(Arrays.asList("mask1", "mask2", "foo"), model.getMasks());

		textInputSelector.setNextValue("foo");
		model.addMask();
		assertEquals(Arrays.asList("mask1", "mask2", "foo"), model.getMasks());

		model.removeMasks(Arrays.asList("foo", "mask1"));
		assertEquals(Arrays.asList("mask2"), model.getMasks());
	}

	@Test
	public void textApplySelection() {
		directorySelector.setNextValue("foo");
		model.addPath();

		textInputSelector.setNextValue("bar");
		model.addMask();

		assertNull(model.getAppliedInputSelection());
		model.applySelection();

		FileInputSelection result = model.getAppliedInputSelection();
		assertNotNull(result);

		assertEquals(Arrays.asList("path1", "path2", "foo"), model.getPaths());
		assertEquals(Arrays.asList("mask1", "mask2", "bar"), model.getMasks());
	}

	@Test
	public void testIsValidInputSelection() {
		assertTrue(model.isValidInputSelection());
		model.removeMasks(model.getMasks());
		assertTrue(model.isValidInputSelection());

		model.removePaths(model.getPaths());
		assertFalse(model.isValidInputSelection());
	}

	@Test
	public void testDefaultValues() {
		assertEquals(Arrays.asList("path1", "path2"), model.getPaths());
		assertEquals(Arrays.asList("mask1", "mask2"), model.getMasks());
	}

}
