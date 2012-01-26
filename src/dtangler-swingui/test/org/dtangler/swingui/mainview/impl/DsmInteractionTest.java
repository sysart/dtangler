// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.mainview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.MockDependencyEngine;
import org.dtangler.swingui.dsm.DsmViewFactory;
import org.dtangler.swingui.dsm.impl.DsmViewFactoryImpl;
import org.dtangler.swingui.windowmanager.MockDialogManager;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;

public class DsmInteractionTest {
	/*!!
	 #{set_header 'DSM interaction'}
	*/

	private MainViewDriver view;
	private MockFileInputSelector fileInputSelector;
	private MockRulesSelector rulesSelector;
	private MockWindowManager windowManager;
	private MockAboutInfoDisplayer aboutInfoDisplayer;
	private Arguments arguments;
	private MockFileSelector fileSelector;
	private MockDialogManager dialogManager;
	private MockDependencyEngine dependencyEngine;

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
		arguments.setInput(Arrays.asList("path1", "path2"));
		arguments.setConfigFileName("original.properties");
		dependencyEngine = new MockDependencyEngine();
		DependencyEngineFactory dependencyEngineFactory = new DependencyEngineFactory(dependencyEngine);
		new MainViewFactoryImpl(dsmViewFactory, fileInputSelector,
				rulesSelector, fileSelector, windowManager, aboutInfoDisplayer,
				dialogManager, dependencyEngineFactory).openMainView(arguments);
		view = new MainViewDriver(windowManager.getLastShownView());
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

	@Test
	public void zoomIn() {
		/*! 
		 If the DSM that you are currently viewing, contains dependencies on a deeper scope (level),
		 then it is possible to 'zoom in' onto specific parts of the level below the current one, by
		 selecting those items from the DSM.
		 
		 After making a selection, Zooming in can be done by pressing the **'zoom in'** button or by selecting
		 **Zoom in onto selection** from the **View** menu. You can also press the **'+'** key to 'zoom in' or
		 right-click the mouse and select the **Zoom in (Show dependencies)** or **Zoom in (Show contents)** from
		 the pop-up menu.
		 */

		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addDependencies(new TestDependable("foo"), Collections
				.singletonMap(new TestDependable("bar"), 1));
		view.refreshBtn.click();

		view.dsm.selectCell(1, 1);
		assertFalse(view.zoomInButton.isEnabled().isTrue());
		assertFalse(view.menuBar.getMenu("View").getSubMenu(
				"Zoom in onto selection").isEnabled().isTrue());
	}

	@Test
	public void zoomInOntoSpecificItems() {
		/*!
		 To zoom in onto specific items in the DSM, select those items from the left-side name column of the DSM.		 
		 Multiple items can be selected. If you select
		 only one cell, you can double-click it to zoom in. With multiple cells, you can select
		 **'Zoom in (Show contents)'** from the pop-up menu.

		 Dtangler will show all childs ot the selected items and all dependencies between them.
		 */
		dependencyEngine.setDependencies(createChildDependencies());
		view.refreshBtn.click();

		assertFalse(view.zoomInButton.isEnabled().isTrue());
		view.dsm.selectBlock(1, 0, 2, 0);
		view.zoomInButton.click();

		assertEquals(2, view.dsm.getRowCount());
		assertEquals("  1 bar.BarImpl (0)", view.dsm.getCellText(0, 0));
		assertEquals("  2 bay.BayImpl (0)", view.dsm.getCellText(1, 0));
	}

	@Test
	public void zoomInOntoDependencies() {
		/*!
		 To zoom in onto dependencies in the DSM, select the cells of those dependencies from the DSM.
		 Either single cells or an area of cells can be selected. If you select
		 only one cell, you can double-click to zoom in. With multiple cells, you can select
		 **'Zoom in (Show dependencies)'** from the pop-up menu.

		 Dtangler will show only those dependencies and only those childs
		 of the selected items that contribute to the dependency weight number at the scope level of the selected items.
		 */

		dependencyEngine.setDependencies(createChildDependencies());
		view.refreshBtn.click();

		assertFalse(view.zoomInButton.isEnabled().isTrue());
		view.dsm.selectCell(1, 1);
		view.menuBar.getMenu("View").getSubMenu("Zoom in onto selection")
				.click();

		assertEquals(2, view.dsm.getRowCount());
		assertEquals("  1 foo.FooImpl (0)", view.dsm.getCellText(0, 0));
		assertEquals("  2 bar.BarImpl (0)", view.dsm.getCellText(1, 0));
	}

	@Test
	public void zoomOut() {
		/*!
		 After zooming in, you can return to the previous view of the DSM by zooming out. It returns to the scope above
		 the current scope and remembers your previous selections.
		 If you select the scope above the current scope from the **'Scope'** combobox, your previous selections will be
		 cleared and all the items and all the dependencies at the selected scope level are shown.

		 You can 'Zoom out' by pressing the **'Zoom out'** button, by selecting **'Zoom out'** from the **'View'** menu or
		 by pressing the '-' key.
		 */
		dependencyEngine.setDependencies(createChildDependencies());
		dependencyEngine.getDependencies(arguments).setDefaultScope(TestScope.scope2);
		view.refreshBtn.click();

		assertEquals("  1 foo.FooImpl (0)", view.dsm.getCellText(0, 0));
		view.zoomOutButton.click();
		assertEquals("  1 foo (0)", view.dsm.getCellText(0, 0));

	}

	@Test
	public void showingShortenedItemNames() {
		/*!
		By default, the dsm shows the full name of each item.
		
		In many domains and scopes, however, all items inside the DSM tend to differ from eachother 
		ony at the start or end of the name.
		
		To make the DSM more readible, it is possible to  hide parts of the name that *all* items have
		in common. Only whole words and numbers are hidden from the start and/or end of the item's name.
		
		This can be done by clicking the **'show shortened names'** button on the **toolbar** or selecting 
		**'show shortened names'** from the **'View'** menu. 
		*/

		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		Dependable d1 = new TestDependable("eg.fooa.abc.x");
		Dependable d2 = new TestDependable("eg.fooa.def.x");
		dependencies.addDependencies(d1, Collections.singletonMap(d2, 1));
		view.refreshBtn.click();

		assertEquals("  1 eg.fooa.abc.x (0)", view.dsm.getCellText(0, 0));
		assertFalse(view.showShortNamesButton.isSelected().isTrue());

		view.showShortNamesButton.click();

		assertEquals("  1 abc (0)", view.dsm.getCellText(0, 0));
		assertTrue(view.showShortNamesButton.isSelected().isTrue());

		view.menuBar.getMenu("View").getSubMenu("show shortened names").click();
		assertEquals("  1 eg.fooa.abc.x (0)", view.dsm.getCellText(0, 0));
	}

}
