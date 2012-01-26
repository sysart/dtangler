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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JList;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dependencyengine.MockDependencyEngine;
import org.dtangler.swingui.dsm.DsmViewFactory;
import org.dtangler.swingui.dsm.impl.DsmViewFactoryImpl;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.windowmanager.MockDialogManager;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.ListBox;

public class MiscFeatureTest {
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
		dependencyEngine = new MockDependencyEngine0();
		dependencyEngineFactory = new DependencyEngineFactory(dependencyEngine);
		new MainViewFactoryImpl(dsmViewFactory, fileInputSelector,
				rulesSelector, fileSelector, windowManager, aboutInfoDisplayer,
				dialogManager, dependencyEngineFactory).openMainView(arguments);
		view = new MainViewDriver(windowManager.getLastShownView());
	}

	private class MockDependencyEngine0 extends MockDependencyEngine {
		public ArgumentsMatch getArgumentsMatchThisEngine(Arguments arguments) {
			for (String path : arguments.getInput()) {
				if (!path.toLowerCase().endsWith(".mock1")
						&& !path.toLowerCase().endsWith(".mock2"))
					return DependencyEngine.ArgumentsMatch.yes;
			}
			return ArgumentsMatch.no;
		}
	}

	private class MockDependencyEngine1 extends MockDependencyEngine {
		public ArgumentsMatch getArgumentsMatchThisEngine(Arguments arguments) {
			for (String path : arguments.getInput()) {
				if (path.toLowerCase().endsWith(".mock1"))
					return DependencyEngine.ArgumentsMatch.yes;
			}
			return ArgumentsMatch.no;
		}
	}

	private class MockDependencyEngine2 extends MockDependencyEngine {
		public ArgumentsMatch getArgumentsMatchThisEngine(Arguments arguments) {
			for (String path : arguments.getInput()) {
				if (path.toLowerCase().endsWith(".mock2"))
					return DependencyEngine.ArgumentsMatch.yes;
			}
			return ArgumentsMatch.no;
		}
	}

	@Test
	public void testGetFileInputSelection() {
		FileInputSelection newInput = new FileInputSelection(dependencyEngine
				.getDependencyEngineId(), Arrays.asList("path3", "path4"),
				Collections.EMPTY_LIST);
		fileInputSelector.setInputToReturn(newInput);
		view.inputButton.click();
		assertEquals(Arrays.asList("path3", "path4"), arguments.getInput());
		assertEquals(Arrays.asList("path1", "path2"), fileInputSelector
				.getLastUsedDefaultInput().getPaths());
		view.inputButton.click();
		assertEquals(newInput.getPaths(), fileInputSelector
				.getLastUsedDefaultInput().getPaths());
	}

	@Test
	public void testGetFileInputSelectionCancelled() {
		fileInputSelector.setInputToReturn(null);
		view.inputButton.click();
		assertEquals(Arrays.asList("path1", "path2"), fileInputSelector
				.getLastUsedDefaultInput().getPaths());
		assertEquals("DTangler DsmUI - original.properties", view.getTitle());
		view.inputButton.click();
		assertEquals(Arrays.asList("path1", "path2"), fileInputSelector
				.getLastUsedDefaultInput().getPaths());
	}

	private Dependencies getMockDependencies(int numberOfDependencies) {
		Dependencies dependencies = new Dependencies();
		for (int i = 1; i <= numberOfDependencies; i++) {
			TestDependable dependant = new TestDependable("dependant" + i);
			TestDependable dependee = new TestDependable("dependee" + i);
			dependencies.addDependencies(dependant, createMap(dependee));
		}
		return dependencies;
	}

	@Test
	public void testDependencyEngineSelection() {
		List<String> originalInput = arguments.getInput();

		MockDependencyEngine1 engine1 = new MockDependencyEngine1();
		Dependencies dependenciesEngine1 = getMockDependencies(10);
		engine1.setDependencies(dependenciesEngine1);
		dependencyEngineFactory.addDependencyEngine("mock1", engine1);

		MockDependencyEngine2 engine2 = new MockDependencyEngine2();
		Dependencies dependenciesEngine2 = getMockDependencies(11);
		engine2.setDependencies(dependenciesEngine2);
		dependencyEngineFactory.addDependencyEngine("mock2", engine2);

		DependencyEngine dependencyEngineMock = null;
		arguments.setInput(Arrays.asList("test.mock1"));
		dependencyEngineMock = dependencyEngineFactory
				.getDependencyEngine(arguments);
		assertEquals(dependencyEngineMock.getClass().getName(), engine1
				.getClass().getName());
		view.refreshBtn.click();
		assertEquals(20, view.dsm.getRowCount());

		arguments.setInput(Arrays.asList("test.mock2"));
		dependencyEngineMock = dependencyEngineFactory
				.getDependencyEngine(arguments);
		assertEquals(dependencyEngineMock.getClass().getName(), engine2
				.getClass().getName());
		view.refreshBtn.click();
		assertEquals(22, view.dsm.getRowCount());

		arguments.setInput(originalInput);
	}

	@Test
	public void testRules() {
		view.rulesButton.click();
		assertTrue(rulesSelector.wereRulesSelected());
	}

	@Test
	public void testSimpleDsmWithoutViolations() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		TestDependable foo = new TestDependable("foo");
		TestDependable bar = new TestDependable("bar");
		TestDependable bay = new TestDependable("bay");
		dependencies.addDependencies(foo, createMap(bar, bay));
		dependencies.addDependencies(bar, createMap(bay));
		view.refreshBtn.click();
		assertEquals(3, view.dsm.getRowCount());
		assertTrue(view.allViolations.contentEquals(new String[] {}).isTrue());
	}

	@Test
	public void testSimpleDsmWitViolation() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		TestDependable foo = new TestDependable("foo");
		TestDependable bar = new TestDependable("bar");
		TestDependable bay = new TestDependable("bay");
		dependencies.addDependencies(foo, createMap(bar, bay));
		dependencies.addDependencies(bar, createMap(foo));
		dependencies.addDependencies(bay, createMap(foo));
		view.refreshBtn.click();
		assertEquals(3, view.dsm.getRowCount());
		assertEquals(2, view.allViolations.getSize());
	}

	@Test
	public void testSimpleDsmWitViolationOutsideScope() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		TestDependable foo = new TestDependable("foo", TestScope.scope1);
		TestDependable bar = new TestDependable("bar", TestScope.scope1);
		TestDependable fooA = new TestDependable("fooA", TestScope.scope2);
		TestDependable fooB = new TestDependable("fooB", TestScope.scope2);
		dependencies.addDependencies(foo, createMap(bar));
		dependencies.addDependencies(bar, createMap(foo));
		dependencies.addDependencies(fooA, createMap(fooB));
		dependencies.addDependencies(fooB, createMap(fooA));
		view.refreshBtn.click();
		assertEquals(2, view.dsm.getRowCount());
		assertEquals(1, view.allViolations.getSize());
	}

	@Test
	public void testShowViolationsOfSelectedCells() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		TestDependable foo = new TestDependable("foo");
		TestDependable bar = new TestDependable("bar");
		TestDependable bay = new TestDependable("bay");

		dependencies.addDependencies(foo, createMap(bar, bay));
		dependencies.addDependencies(bar, createMap(foo));
		dependencies.addDependencies(bay, createMap(foo));

		// increasing depweights
		dependencies.addDependencies(foo, createMap(new TestDependable("x1"),
				new TestDependable("x2"), new TestDependable("x3")));
		dependencies.addDependencies(bar, createMap(new TestDependable("x1")));
		view.refreshBtn.click();
		assertEquals(0, view.cellViolations.getSize());

		view.dsm.selectCell(1, 1);
		assertTrue(view.cellViolations.contentEquals(
				new String[] { "Cycle: foo-->bar-->foo" }).isTrue());

		view.dsm.selectCell(1, 2);
		assertTrue(view.cellViolations.contentEquals(
				new String[] { "Cycle: foo-->bay-->foo" }).isTrue());

		view.dsm.selectBlock(1, 1, 3, 1);
		assertTrue(view.cellViolations.contentEquals(
				new String[] { "Cycle: foo-->bar-->foo",
						"Cycle: foo-->bay-->foo" }).isTrue());

	}

	@Test
	public void testShowChildViolationsOfSelectedRow() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		TestDependable x = new TestDependable("x", TestScope.scope2);
		TestDependable y = new TestDependable("y", TestScope.scope2);
		TestDependable foo = new TestDependable("foo", TestScope.scope1);
		TestDependable bar = new TestDependable("bar", TestScope.scope1);
		dependencies.addDependencies(x, createMap(y));
		dependencies.addDependencies(y, createMap(x));
		dependencies.addChild(foo, x);
		dependencies.addChild(foo, y);
		dependencies.addDependencies(foo, createMap(bar));

		view.refreshBtn.click();
		assertEquals(1, view.allViolations.getSize());
		assertEquals(0, view.cellViolations.getSize());

		view.dsm.selectCell(0, 0);

		assertTrue(getValue(view.cellViolations, 0).startsWith(
				"foo contains a violation: Cycle: "));

		view.dsm.selectCell(1, 0);
		assertTrue(getValue(view.cellViolations, 0).startsWith(
				"foo contains a violation: Cycle: "));

		view.dsm.selectCell(0, 1);
		assertEquals(0, view.cellViolations.getSize());

		view.dsm.selectCell(1, 1);
		assertTrue(getValue(view.cellViolations, 0).startsWith(
				"foo contains a violation: Cycle: "));

		view.dsm.selectBlock(0, 0, 1, 1);
		assertTrue(getValue(view.cellViolations, 0).startsWith(
				"foo contains a violation: Cycle: "));
	}

	// FIXME: move this to driver
	private String getValue(ListBox list, int index) {
		JList jList = (JList) list.getAwtComponent();
		return (String) jList.getModel().getElementAt(index);
	}

	@Test
	public void testScopeSelectionContent() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addChild(new TestDependable("foo", TestScope.scope2),
				new TestDependable("bar", TestScope.scope2));

		view.refreshBtn.click();
		assertTrue(view.scope.contentEquals(new String[] { "scope2" }).isTrue());
		dependencies.addChild(new TestDependable("foo", TestScope.scope3),
				new TestDependable("bar", TestScope.scope1));
		view.refreshBtn.click();
		assertTrue(view.scope.contentEquals(
				new String[] { "scope1", "scope2", "scope3" }).isTrue());
	}

	@Test
	public void testScopeIsSetToDefaultIfNoScopeSelectedUponRefresh() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addChild(new TestDependable("foo", TestScope.scope1),
				new TestDependable("bar", TestScope.scope2));
		dependencyEngine.getDependencies(arguments).setDefaultScope(TestScope.scope2);

		assertTrue(view.scope.selectionEquals(null).isTrue());
		view.refreshBtn.click();
		assertTrue(view.scope.selectionEquals("scope2").isTrue());
	}

	@Test
	public void testScopeRemainsUnchangedIfSetUponRefresh() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addChild(new TestDependable("foo", TestScope.scope1),
				new TestDependable("bar", TestScope.scope2));
		dependencyEngine.getDependencies(arguments).setDefaultScope(TestScope.scope2);
		view.refreshBtn.click();
		view.scope.select("scope1");
		view.refreshBtn.click();
		assertTrue(view.scope.selectionEquals("scope1").isTrue());
	}

	@Test
	public void testScopeIsSetToHighestAvailableScopeWhenDefaultScopeUnavailable() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addChild(new TestDependable("foo", TestScope.scope2),
				new TestDependable("bar", TestScope.scope3));
		dependencyEngine.getDependencies(arguments).setDefaultScope(TestScope.scope1);
		view.refreshBtn.click();
		assertTrue(view.scope.selectionEquals("scope2").isTrue());
	}

	@Test
	public void testChangeScope() {
		TestDependable a1 = new TestDependable("foo1", TestScope.scope2);
		TestDependable a2 = new TestDependable("foo2", TestScope.scope2);
		TestDependable a3 = new TestDependable("foo3", TestScope.scope2);
		TestDependable b1 = new TestDependable("bar1", TestScope.scope1);
		TestDependable b2 = new TestDependable("bar2", TestScope.scope1);

		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addDependencies(a1, createMap(a2, a3));
		dependencies.addDependencies(a2, createMap(a3));
		dependencies.addChild(b1, a1);
		dependencies.addChild(b1, a2);
		dependencies.addChild(b2, a3);

		view.refreshBtn.click();
		assertTrue(view.scope.selectionEquals("scope1").isTrue());
		assertEquals(2, view.dsm.getRowCount());
		System.out.println(view.dsm.getCellText(0, 0));
		assertTrue(view.dsm.getCellText(0, 0).contains("bar"));

		view.scope.select("scope2");
		assertEquals(3, view.dsm.getRowCount());
		assertTrue(view.dsm.getCellText(0, 0).contains("foo"));
	}

	@Test
	public void testAnalysisResultIsUpdatedWhenRefreshing() {
		dependencyEngine.setDependencies(new Dependencies());
		view.refreshBtn.click();
		int violationsBefore = view.allViolations.getSize();

		dependencyEngine.setDependencies(createCyclicDependencies());
		view.refreshBtn.click();
		int violationsAfter = view.allViolations.getSize();

		assertEquals(0, violationsBefore);
		assertEquals(1, violationsAfter);
	}

	@Test
	public void testAnalysisResultIsNotUpdatedWhenNavigating() {

		dependencyEngine.setDependencies(createCyclicDependencies());
		view.refreshBtn.click();
		dependencyEngine.setDependencies(createDirectDependencies());

		view.scope.select("scope3");
		view.dsm.selectBlock(0, 0, 1, 1);
		view.zoomOutButton.click();
		view.dsm.selectBlock(0, 0, 1, 1);
		view.zoomInButton.click();

		assertEquals(1, view.allViolations.getSize());
	}

	private Dependencies createDirectDependencies() {
		TestDependable a1 = new TestDependable("a1", TestScope.scope2);
		TestDependable a2 = new TestDependable("a2", TestScope.scope2);
		TestDependable b1 = new TestDependable("b1", TestScope.scope3);
		TestDependable b2 = new TestDependable("b2", TestScope.scope3);
		Dependencies dependencies = new Dependencies();

		dependencies.addDependencies(a1, createMap(a2));
		dependencies.addChild(a1, b1);
		dependencies.addChild(a2, b2);

		return dependencies;
	}

	private Dependencies createCyclicDependencies() {
		TestDependable a1 = new TestDependable("cycleParent1", TestScope.scope2);
		TestDependable a2 = new TestDependable("cycleParent2", TestScope.scope2);
		TestDependable b1 = new TestDependable("cycle1", TestScope.scope3);
		TestDependable b2 = new TestDependable("cycle2", TestScope.scope3);

		Dependencies dependencies = new Dependencies();

		dependencies.addDependencies(a1, createMap(a2));
		dependencies.addDependencies(a2, createMap(a1));

		dependencies.addDependencies(b1, createMap(b2));
		dependencies.addDependencies(b2, createMap(b1));

		dependencies.addChild(a1, b1);
		dependencies.addChild(a2, b2);

		return dependencies;

	}

	@Test
	public void testZoomInIsDisabledWhenNoDependenciesAreSelected() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addChild(new TestDependable("foo", TestScope.scope1),
				new TestDependable("bar", TestScope.scope2));
		view.refreshBtn.click();
		assertTrue(view.dsm.getPopup() != null);
		assertTrue(view.dsm.getPopup().getSubMenu(SwingMainView.menuItemZoomInNameHeader) != null);
		assertFalse(view.dsm.getPopup().getSubMenu(SwingMainView.menuItemZoomInNameHeader).isEnabled()
				.isTrue());

		view.dsm.selectCell(1, 0);
		assertTrue(view.dsm.getPopup().getSubMenu(SwingMainView.menuItemZoomInNameHeader).isEnabled()
				.isTrue());
	}

	@Test
	public void testZoomAndOutAreDisabledWhenNoNextLevelAvailable() {
		Dependencies dependencies = new Dependencies();
		dependencyEngine.setDependencies(dependencies);
		dependencies.addChild(new TestDependable("foo", TestScope.scope1),
				new TestDependable("bar", TestScope.scope2));
		view.refreshBtn.click();
		view.dsm.selectCell(1, 0);
		assertTrue(view.dsm.getPopup().getSubMenu("Zoom in").isEnabled()
				.isTrue());
		assertFalse(view.dsm.getPopup().getSubMenu("Zoom out").isEnabled()
				.isTrue());
		view.scope.select("scope2");
		view.dsm.selectCell(1, 0);
		assertFalse(view.dsm.getPopup().getSubMenu("Zoom in").isEnabled()
				.isTrue());
		assertTrue(view.dsm.getPopup().getSubMenu("Zoom out").isEnabled()
				.isTrue());
	}

	private Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}

}
