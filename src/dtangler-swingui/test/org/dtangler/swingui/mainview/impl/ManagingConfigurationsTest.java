// This product is provided under the terms of EPL (Eclipse Public License)
// version 1.0.
//
// The full license text can be read from:
// http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.GraphicsEnvironment;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.MockDependencyEngine;
import org.dtangler.swingui.dsm.DsmViewFactory;
import org.dtangler.swingui.dsm.impl.DsmViewFactoryImpl;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.windowmanager.MockDialogManager;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.dtangler.swingui.windowmanager.DialogManager.DialogResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ManagingConfigurationsTest {
	/*!!
	 #{set_header 'Managing configurations'}
	 The configurations that you make inside the Gui can be saved to- and loaded from files.
	 
	 The dtangler gui configuration files are compatible with the ones used by the dtangler 
	 command line version.
	*/

	private static boolean isHeadless;
	private MainViewDriver view;
	private MockFileInputSelector fileInputSelector;
	private MockRulesSelector rulesSelector;
	private MockWindowManager windowManager;
	private MockAboutInfoDisplayer aboutInfoDisplayer;
	private Arguments arguments;
	private MockFileSelector fileSelector;
	private MockDialogManager dialogManager;
	private MockDependencyEngine dependencyEngine;
	private DependencyEngineFactory dependencyEngineFactory;
	private List<String> origInput;

	@BeforeClass
	public static void checkHeadless() {
		isHeadless = GraphicsEnvironment.isHeadless();
	}

	private boolean isHeadless() {
		if (isHeadless) {
			String warning = "Warning: cannot run tests from ManagingConfigurationsTest because host is headless";
			System.out.println(warning);
			System.err.println(warning);
		}
		return isHeadless;
	}

	@Before
	public void setUp() {
		rulesSelector = new MockRulesSelector();
		DsmViewFactory dsmViewFactory = new DsmViewFactoryImpl();
		fileInputSelector = new MockFileInputSelector();
		windowManager = new MockWindowManager();
		aboutInfoDisplayer = new MockAboutInfoDisplayer();
		fileSelector = new MockFileSelector();
		dialogManager = new MockDialogManager();
		arguments = new Arguments();
		origInput = Arrays.asList("path1", "path2");
		arguments.setInput(origInput);
		arguments.setConfigFileName("original.properties");
		dependencyEngine = new MockDependencyEngine();
		dependencyEngineFactory = new DependencyEngineFactory(dependencyEngine);
		new MainViewFactoryImpl(dsmViewFactory, fileInputSelector,
				rulesSelector, fileSelector, windowManager, aboutInfoDisplayer,
				dialogManager, dependencyEngineFactory).openMainView(arguments);
		view = new MainViewDriver(windowManager.getLastShownView());
	}

	@Test
	public void currentConfigurationFileName() {
		/*!
		 The name of the currently open configuration file can be seen 
		 in the title bar behind the Dtangler DsmUI title.
		 
		 If the configuration has never been saved, the titlebar will 
		 display **unsaved settings.**
		 
		 If the configuration has been change since it was last saved or 
		 created, a '*' will be displayed in front of the configuration filename.		  
		 */
		assertEquals("DTangler DsmUI - original.properties", view.getTitle());
		view.inputButton.click();
		view.menuBar.getMenu("File").getSubMenu("New").click();
		assertEquals("DTangler DsmUI - unsaved settings", view.getTitle());
		makeConfiguarationDirty();
		assertEquals("DTangler DsmUI - * unsaved settings", view.getTitle());
	}

	@Test
	public void creatingANewConfiguration() {
		/*!
		 A new configuration can be created by selecting **'New'** from 
		 the **'File'** menu or by pressing **Ctrl+N**.<br>
		 This is essentially the same as resetting all configuration parameters 
		 back to their defaults.
		 */
		assertEquals("DTangler DsmUI - original.properties", view.getTitle());
		view.inputButton.click();
		assertEquals(arguments.getInput(), fileInputSelector
				.getLastUsedDefaultInput().getPaths());

		view.menuBar.getMenu("File").getSubMenu("New").click();

		assertEquals("DTangler DsmUI - unsaved settings", view.getTitle());
		view.inputButton.click();
		assertTrue(fileInputSelector.getLastUsedDefaultInput().getPaths()
				.isEmpty());
	}

	@Test
	public void changedConfigurationUponCreatingANewConfiguration() {
		/*!
		 If the current configuration contains changes since the last time it 
		 has been saved, you will be asked whether to save those changes or 
		 not before the new configuration is created. 		  
		 */

		assertEquals("DTangler DsmUI - original.properties", view.getTitle());
		makeConfiguarationDirty();

		assertEquals("DTangler DsmUI - * original.properties", view.getTitle());

		dialogManager.setNextResult(DialogResult.cancel);
		view.menuBar.getMenu("File").getSubMenu("New").click();

		assertEquals("DTangler DsmUI - * original.properties", view.getTitle());

		dialogManager.setNextResult(DialogResult.no);
		view.menuBar.getMenu("File").getSubMenu("New").click();
		assertEquals("DTangler DsmUI - unsaved settings", view.getTitle());
	}

	private void makeConfiguarationDirty() {
		fileInputSelector.setInputToReturn(new FileInputSelection(dependencyEngine.getDependencyEngineId(), Arrays
				.asList("newPath"), Collections.EMPTY_LIST));
		view.inputButton.click();
	}

	@Test
	public void openAnExistingConfigurationFile() throws URISyntaxException {
		/*!
		 An existing configuration can be opened by selecting **'Open'** 
		 from the **'File'** menu or by pressing **Ctrl+O**.<br>
		 */

		String fileName = new File(getClass().getResource("rules.properties")
				.toURI()).getAbsolutePath();

		fileSelector.setNextFile(null);

		view.menuBar.getMenu("File").getSubMenu("Open").click();
		view.rulesButton.click();
		assertTrue(rulesSelector.getPreviousRules().getForbiddenDependencies()
				.isEmpty());
		assertEquals("DTangler DsmUI - original.properties", view.getTitle());

		fileSelector.setNextFile(fileName);

		view.menuBar.getMenu("File").getSubMenu("Open").click();
		view.rulesButton.click();
		Set<String> rule = rulesSelector.getPreviousRules()
				.getForbiddenDependencies().get("foo");
		assertTrue(rule.contains("bar"));
		assertTrue(rule.contains("bay"));

		assertEquals("DTangler DsmUI - " + fileName, view.getTitle());
	}

	@Test
	public void changedConfigurationUponOpeningAnExistingConfigurationFile()
			throws URISyntaxException {
		/*!
		 If the current configuration contains changes since the last time it 
		 has been saved, you will be asked whether to save those changes or 
		 not before another configuration is opened. 		  
		 */
		String fileName = new File(getClass().getResource("rules.properties")
				.toURI()).getAbsolutePath();
		fileSelector.setNextFile(fileName);

		makeConfiguarationDirty();

		assertEquals("DTangler DsmUI - * original.properties", view.getTitle());

		dialogManager.setNextResult(DialogResult.cancel);
		view.menuBar.getMenu("File").getSubMenu("Open").click();

		assertEquals("DTangler DsmUI - * original.properties", view.getTitle());

		dialogManager.setNextResult(DialogResult.no);
		view.menuBar.getMenu("File").getSubMenu("Open").click();

		assertEquals("DTangler DsmUI - " + fileName, view.getTitle());
	}

	@Test
	public void saveConfiguration() {
		/*!
		 At any time you can save the current configuration by selecting 
		 **'Save'** from the **'File'** menu or by pressing **CTRL+S**. 
		 If this is the first time that you save the current configuration, 
		 you will be asked for the filename to save to. 
		 */
		view.menuBar.getMenu("File").getSubMenu("New").click();
		makeConfiguarationDirty();
		String fileName = System.getProperty("java.io.tmpdir") + File.separator
				+ "testsave.properties";
		File file = new File(fileName);
		if (file.exists())
			assertTrue(file.delete());

		fileSelector.setNextFile(fileName);
		view.menuBar.getMenu("File").getSubMenu("Save").click();

		assertTrue(file.exists());

		assertTrue(file.delete());

		view.menuBar.getMenu("File").getSubMenu("Save").click();

		assertTrue(file.exists());
	}

	@Test
	public void saveConfigurationToDifferentFile() {
		/*!
		 At any time you can save the current configuration to a 
		 file of choice by selecting **'Save as...'** from the **'File'** menu 
		 or by pressing **CTRL+SHIFT+S**. You will be asked for 
		 the filename to save to. 
		 */
		String fileName = System.getProperty("java.io.tmpdir") + File.separator
				+ "testsave.properties";
		File file = new File(fileName);
		if (file.exists())
			assertTrue(file.delete());

		fileSelector.setNextFile(fileName);
		view.menuBar.getMenu("File").getSubMenu("Save as").click();

		assertTrue(file.exists());
	}

	@Test
	public void changedConfigurationUponExit() throws URISyntaxException {
		/*!
		 If the current configuration contains changes since the last 
		 time it has been saved, you will be asked whether to
		 save those changes or not before exiting the gui. 		  
		 */

		makeConfiguarationDirty();

		dialogManager.setNextResult(DialogResult.cancel);
		view.menuBar.getMenu("File").getSubMenu("Exit").click();
		assertNotNull(windowManager.getLastShownView());

		dialogManager.setNextResult(DialogResult.no);
		view.menuBar.getMenu("File").getSubMenu("Exit").click();
		assertNull(windowManager.getLastShownView());
	}

	@Test
	public void dragAndDropConfigFileOntoDtangler() throws URISyntaxException {
		/*!
		 You can quickly open a dtangler configuration by just dragging a config file from
		 another application or window onto the dtangler window.
		 
		 Note that you can only drag&drop a single config file, and the extension of that file
		 must be **'.properties'**
		 */

		if (isHeadless())
			return;

		assertEquals("DTangler DsmUI - original.properties", view.getTitle());

		File configFile = new File(getClass().getResource("rules.properties")
				.toURI());

		view.simulateDrop(DataFlavor.javaFileListFlavor, Collections
				.singletonList(configFile));

		assertEquals("DTangler DsmUI - " + configFile.getAbsolutePath(), view
				.getTitle());
	}

	@Test
	public void changedConfigurationUponDragAndDropConfigFileOntoDtangler()
			throws URISyntaxException {
		/*!
		 If the current configuration contains changes since the last time it 
		 has been saved, you will be asked whether to save those changes or 
		 not before another configuration is opened. 		  
		 */

		if (isHeadless())
			return;

		makeConfiguarationDirty();

		assertEquals("DTangler DsmUI - * original.properties", view.getTitle());

		File configFile = new File(getClass().getResource("rules.properties")
				.toURI());

		dialogManager.setNextResult(DialogResult.cancel);
		view.simulateDrop(DataFlavor.javaFileListFlavor, Collections
				.singletonList(configFile));
		assertEquals("DTangler DsmUI - * original.properties", view.getTitle());
		dialogManager.setNextResult(DialogResult.no);
		view.simulateDrop(DataFlavor.javaFileListFlavor, Collections
				.singletonList(configFile));

		assertEquals("DTangler DsmUI - " + configFile.getAbsolutePath(), view
				.getTitle());
	}

	@Test
	public void dragAndDropInputFoldersOntoDtangler() {
		/*!
		 You can quickly add input locations to a configuration by just dragging them from
		 another application or window onto the dtangler window.
		 */

		if (isHeadless())
			return;

		view.inputButton.click();
		assertEquals(origInput, fileInputSelector.getLastUsedDefaultInput()
				.getPaths());

		File folder1 = new File("inputfolder1");
		File folder2 = new File("inputfolder2");
		view.simulateDrop(DataFlavor.javaFileListFlavor, Arrays.asList(folder1,
				folder2));

		List<String> expected = new ArrayList(origInput);
		expected.add(folder1.getAbsolutePath());
		expected.add(folder2.getAbsolutePath());

		view.inputButton.click();
		assertEquals(expected, fileInputSelector.getLastUsedDefaultInput()
				.getPaths());

		File folder3 = new File("inputfolder3");
		view.simulateDrop(DataFlavor.javaFileListFlavor, Collections
				.singletonList(folder3));

		expected.add(folder3.getAbsolutePath());

		view.inputButton.click();
		assertEquals(expected, fileInputSelector.getLastUsedDefaultInput()
				.getPaths());
	}

	@Test
	public void dragAndDropNonFileContentsOntoDtangler() {
		/*!
		Dtangler currently only supports drag&drop for input folder(s) or configuration file
		*/

		if (isHeadless())
			return;

		view.simulateDrop(DataFlavor.stringFlavor, "SomeText");

		assertEquals("DTangler DsmUI - original.properties", view.getTitle());
		view.inputButton.click();
		assertEquals(origInput, fileInputSelector.getLastUsedDefaultInput()
				.getPaths());
	}

}
