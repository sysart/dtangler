//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.ui.dsm.DsmGuiModel.DisplayNameFormat;
import org.junit.Before;
import org.junit.Test;

public class DsmGuiModelImplTest {

	private static final Dependable foo = new TestDependable("eg.foo");
	private static final Dependable bar = new TestDependable("eg.bar");
	private static final Dependable bay = new TestDependable("eg.bay");

	private DsmGuiModelImpl model;
	private AnalysisResult analysisResult;

	@Before
	public void setUp() {
		Dsm dsm = create3x3Dsm();
		model = new DsmGuiModelImpl();
		analysisResult = new AnalysisResult(Collections.EMPTY_MAP,
				Collections.EMPTY_SET, true);
		model.setDsm(dsm, analysisResult);
	}

	private Dsm create3x3Dsm() {
		return new Dsm(Arrays.asList(createRow(foo, foo, bar, bay), createRow(
				bar, foo, bar, bay), createRow(bay, foo, bar, bay)));
	}

	private DsmRow createRow(Dependable rowItem, Dependable... colItems) {
		List<DsmCell> cells = new ArrayList();
		for (Dependable colItem : colItems)
			cells.add(new DsmCell(colItem, rowItem, 1));
		return new DsmRow(rowItem, cells);
	}

	private Set createSet(Object... dependables) {
		return new HashSet(Arrays.asList(dependables));
	}

	@Test
	public void testGetColumnAndRowCount() {
		assertEquals(3, model.getColumnCount());
		assertEquals(3, model.getRowCount());
	}

	@Test
	public void testSetDsm() {
		model.selectCells(Collections.singletonList(1), Collections
				.singletonList(1));
		MockModelChangeListener listener = new MockModelChangeListener();
		model.addChangeListener(listener);

		model.setDsm(new Dsm(Collections.EMPTY_LIST), new AnalysisResult(
				Collections.EMPTY_MAP, Collections.EMPTY_SET, true));

		assertEquals(0, model.getRowCount());
		assertFalse(model.isRowOrColumnInCrossHair(1, 1));
		assertEquals(1, listener.timesDataChangedCalled);
	}

	@Test
	public void testCrossHair() {
		model.selectCells(Collections.singletonList(1), Collections
				.singletonList(1));

		assertTrue(model.getCellInfo(1, 0).isInCrossHair());
		assertTrue(model.getCellInfo(1, 2).isInCrossHair());

		assertTrue(model.getCellInfo(0, 1).isInCrossHair());
		assertTrue(model.getCellInfo(2, 1).isInCrossHair());

		assertFalse(model.getCellInfo(0, 2).isInCrossHair());
		assertFalse(model.getCellInfo(2, 0).isInCrossHair());

		model.selectCells(Collections.singletonList(2), Collections
				.singletonList(0));

		assertTrue(model.getCellInfo(2, 0).isInCrossHair());
		assertTrue(model.getCellInfo(2, 1).isInCrossHair());

		assertTrue(model.getCellInfo(1, 0).isInCrossHair());

		assertFalse(model.getCellInfo(0, 1).isInCrossHair());
		assertFalse(model.getCellInfo(0, 2).isInCrossHair());
		assertFalse(model.getCellInfo(1, 2).isInCrossHair());
	}

	@Test
	public void testCrossHairIsDisabledOnMultiCellSelection() {
		model.selectCells(Arrays.asList(1), Arrays.asList(1));
		assertTrue(model.getCellInfo(1, 0).isInCrossHair());
		model.selectCells(Arrays.asList(1, 2), Arrays.asList(1));
		assertFalse(model.getCellInfo(1, 0).isInCrossHair());
		model.selectCells(Arrays.asList(1), Arrays.asList(1, 2));
		assertFalse(model.getCellInfo(1, 0).isInCrossHair());
	}

	@Test
	public void testNoSelection() {
		model.selectCells(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
		assertTrue(model.getSelectionDependables().isEmpty());
		assertTrue(model.getSelectionDependencies().isEmpty());
		model.selectCells(Arrays.asList(1), Collections.EMPTY_LIST);
	}

	@Test
	public void testSingleRowSelection() {
		model.selectCells(Arrays.asList(1), Collections.EMPTY_LIST);
		assertEquals(createSet(bar), model.getSelectionDependables());
		assertTrue(model.getSelectionDependencies().isEmpty());
	}

	@Test
	public void testMultipleRowSelection() {
		model.selectCells(Arrays.asList(0, 2), Collections.EMPTY_LIST);
		assertEquals(createSet(foo, bay), model.getSelectionDependables());
		assertTrue(model.getSelectionDependencies().isEmpty());
	}

	@Test
	public void testSingleCellSelection() {
		model.selectCells(Arrays.asList(1), Arrays.asList(2));
		assertEquals(createSet(bar, bay), model.getSelectionDependables());
		assertEquals(createSet(new Dependency(bay, bar)), model
				.getSelectionDependencies());
	}

	@Test
	public void testInvalidCellSelection() {
		model.selectCells(Arrays.asList(1), Arrays.asList(1));
		assertEquals(createSet(bar), model.getSelectionDependables());
		// Maybe dependencies should be empty in this case?
		assertEquals(createSet(new Dependency(bar, bar)), model
				.getSelectionDependencies());
	}

	@Test
	public void testMultipleCellSelection() {
		model.selectCells(Arrays.asList(0, 1), Arrays.asList(0, 1));
		assertEquals(createSet(foo, bar), model.getSelectionDependables());
		assertEquals(createSet(new Dependency(foo, foo), new Dependency(bar,
				foo), new Dependency(foo, bar), new Dependency(bar, bar)),
				model.getSelectionDependencies());
	}

	@Test
	public void testSelectionChangeFiresEvent() {
		MockModelChangeListener listener = new MockModelChangeListener();
		model.addChangeListener(listener);

		assertEquals(0, listener.timesGuiModelChangedCalled);
		model.selectCells(Arrays.asList(0, 1), Arrays.asList(0, 1));
		assertEquals(1, listener.timesGuiModelChangedCalled);
		model.selectCells(Arrays.asList(0, 1), Arrays.asList(1, 1));
		assertEquals(2, listener.timesGuiModelChangedCalled);
	}

	@Test
	public void testDisplayNameModeChangeFiresEvent() {
		MockModelChangeListener listener = new MockModelChangeListener();
		model.addChangeListener(listener);

		assertEquals(0, listener.timesGuiModelChangedCalled);
		model.setDisplayNameFormat(DisplayNameFormat.shortened);
		assertEquals(1, listener.timesGuiModelChangedCalled);
		model.setDisplayNameFormat(DisplayNameFormat.shortened);
		assertEquals(1, listener.timesGuiModelChangedCalled);
		model.setDisplayNameFormat(DisplayNameFormat.full);
		assertEquals(2, listener.timesGuiModelChangedCalled);
	}

	@Test
	public void testDisplayNameModeChange() {
		assertEquals("default should be full", "eg.foo", model.getRowInfo(0)
				.getDisplayName());
		model.setDisplayNameFormat(DisplayNameFormat.shortened);
		assertEquals("foo", model.getRowInfo(0).getDisplayName());
	}

	@Test
	public void testNameFormatterIsUpdatedUponDsmChange() {
		Dependable d1 = new TestDependable("eg.foo.abc.xxx");
		Dependable d2 = new TestDependable("eg.foo.def.xxx");

		model.setDisplayNameFormat(DisplayNameFormat.shortened);

		assertEquals("foo", model.getRowInfo(0).getDisplayName());

		model.setDsm(new Dsm(Arrays.asList(createRow(d1, d1, d2), createRow(d2,
				d1, d2))), analysisResult);

		assertEquals("abc", model.getRowInfo(0).getDisplayName());

	}
}
