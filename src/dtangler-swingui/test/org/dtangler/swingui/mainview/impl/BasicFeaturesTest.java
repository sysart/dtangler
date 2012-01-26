// This product is provided under the terms of EPL (Eclipse Public License)
// version 1.0.
//
// The full license text can be read from:
// http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.MockDependencyEngine;
import org.dtangler.swingui.dsm.impl.DsmViewFactoryImpl;
import org.dtangler.swingui.testutil.SnapShotTaker;
import org.dtangler.swingui.windowmanager.MockDialogManager;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;

public class BasicFeaturesTest {
	/*!!
	 #{set_header 'Basic features'}
	 */

	private MainViewDriver view;
	private MockWindowManager windowManager;
	private MockAboutInfoDisplayer aboutInfoDisplayer;
	private MockDependencyEngine dependencyEngine;
	private MockFileInputSelector fileInputSelector;
	private Arguments arguments;

	@Before
	public void setUp() {
		windowManager = new MockWindowManager();
		aboutInfoDisplayer = new MockAboutInfoDisplayer();
		fileInputSelector = new MockFileInputSelector();
		dependencyEngine = new MockDependencyEngine();
		DependencyEngineFactory dependencyEngineFactory = new DependencyEngineFactory(dependencyEngine);
		MainViewFactoryImpl mainViewFactory = new MainViewFactoryImpl(
				new DsmViewFactoryImpl(), fileInputSelector,
				new MockRulesSelector(), new MockFileSelector(), windowManager,
				aboutInfoDisplayer, new MockDialogManager(), dependencyEngineFactory);
		arguments = new Arguments();
		mainViewFactory.openMainView(arguments);
		view = new MainViewDriver(windowManager.getLastShownView());
	}

	@Test
	public void screenShot() {
		/*!
		 #{main}
		 */
		dependencyEngine.setDependencies(createChildDependencies());
		view.refreshBtn.click();
		view.dsm.selectCell(2, 1);

		SnapShotTaker.snap("main", view.getView());
	}

	private Dependencies createChildDependencies() {
		Dependencies dependencies = new Dependencies();
		Dependable d1 = new TestDependable("foo", TestScope.scope1);
		Dependable d2 = new TestDependable("bar", TestScope.scope1);
		Dependable d3 = new TestDependable("bay", TestScope.scope1);
		Dependable dc1 = new TestDependable("foo.FooImpl", TestScope.scope2);
		Dependable dc2 = new TestDependable("bar.BarImpl", TestScope.scope2);
		Dependable dc3 = new TestDependable("bay.BayImpl", TestScope.scope2);
		dependencies.addDependencies(dc1, Collections.singletonMap(dc2, 1));
		dependencies.addDependencies(dc2, Collections.singletonMap(dc3, 2));
		dependencies.addChild(d1, dc1);
		dependencies.addChild(d2, dc2);
		dependencies.addChild(d3, dc3);
		return dependencies;
	}

	private boolean getLastUsedDefaultInputPath(List<String> paths) {
		return fileInputSelector.getLastUsedDefaultInput() != null &&
				fileInputSelector.getLastUsedDefaultInput().getPaths() != null &&
				fileInputSelector.getLastUsedDefaultInput().getPaths().equals(paths);
	}

	@Test
	public void selectTheDependencyEngineAndTheInputDataForTheDependencyStructureMatrix() {
		/*!
		 The dependency engine and the data to be analyzed can be selected by clicking on the **'Input...'** -button or 
		 by selecting **'Dependency Input...'** from the **'Model'** menu.
		 
		 Dtangler supports multiple dependency engines and is easily extendable by third-party plug-in engines. All the
		 plug-ins are automatically registered to Dtangler and will be shown in the Dependency engines combo box.
		 
		 The current version of the Dtangler comes with two plug-ins: java and generic engine.
		 
		 The input can be a single or a group of files or directories. With java dependency engine, the input
		 files are of type .class and .jar. With generic dependency engine, the input files are of type .dt.
		 */
		arguments.setInput(Arrays.asList("mypath1"));
		assertFalse(getLastUsedDefaultInputPath(Arrays.asList("mypath1")));
		view.menuBar.getMenu("Model").getSubMenu("Dependency Input...").click();
		assertTrue(getLastUsedDefaultInputPath(Arrays.asList("mypath1")));
	}

	@Test
	public void viewInformationAboutDtangler() {
		/*!
		 Additional information and the version number of dtangler can be viewed by selecting 
		 **'About'** from the **'Help'** menu.
		 */
		view.menuBar.getMenu("Help").getSubMenu("About").click();
		assertTrue(aboutInfoDisplayer.wasAboutInfoDisplayed());
	}

	@Test
	public void exitTheGui() {
		/*!
		 The dtangler GUI can be closed by selecting **'Exit'** from the **'File'** menu.
		 */
		view.menuBar.getMenu("File").getSubMenu("exit").click();
		assertNull(windowManager.getLastShownView());
	}

}
