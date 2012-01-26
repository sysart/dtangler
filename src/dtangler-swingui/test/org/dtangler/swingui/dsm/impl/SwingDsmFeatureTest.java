//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.testutil.dependenciesbuilder.DependencyGraphBuilder;
import org.dtangler.swingui.dsm.SwingDsm;
import org.junit.Before;
import org.junit.Test;

public class SwingDsmFeatureTest {

	private SwingDsm SwingDsm;

	@Before
	public void setUp() {
		DsmViewFactoryImpl factory = new DsmViewFactoryImpl();
		SwingDsm = factory.createSwingDsm();
	}

	@Test
	public void testSimpleDsmContent() {
		DependencyGraphBuilder builder = new DependencyGraphBuilder();
		builder.add("foo").dependsOn("bar", 2).and("bay", 3);
		builder.add("bar").dependsOn("bay");
		SwingDsm.getModel().setDsm(
				new DsmEngine(builder.getDependencies()).createDsm(),
				new AnalysisResult(Collections.EMPTY_MAP,
						Collections.EMPTY_SET, true));
		DsmViewDriver dsmView = new DsmViewDriver(SwingDsm.getView().getJComponent());

		assertEquals(4, dsmView.getColumCount());
		assertEquals(3, dsmView.getRowCount());

		assertEquals("  1 foo (0)", dsmView.getCellText(0, 0));
		assertEquals("  2 bar (0)", dsmView.getCellText(1, 0));
		assertEquals("  3 bay (0)", dsmView.getCellText(2, 0));

		assertEquals("", dsmView.getCellText(0, 1));
		assertEquals("2", dsmView.getCellText(1, 1));
		assertEquals("3", dsmView.getCellText(2, 1));

		assertEquals("", dsmView.getCellText(0, 2));
		assertEquals("", dsmView.getCellText(1, 2));
		assertEquals("1", dsmView.getCellText(2, 2));

		assertEquals("", dsmView.getCellText(0, 3));
		assertEquals("", dsmView.getCellText(1, 3));
		assertEquals("", dsmView.getCellText(2, 3));
	}
}
