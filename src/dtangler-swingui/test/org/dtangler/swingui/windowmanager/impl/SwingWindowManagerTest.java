//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import org.dtangler.swingui.windowmanager.SwingView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SwingWindowManagerTest {

	private SwingWindowManager windowManager;
	private Window lastOpenedWindow;
	private static boolean isHeadless;

	@BeforeClass
	public static void checkHeadless() {
		isHeadless = GraphicsEnvironment.isHeadless();
	}

	@Before
	public void setUp() {
		windowManager = new SwingWindowManager() {

			protected void showWindow(Window window) {
				lastOpenedWindow = window;
				// do not actually do this in tests, since it only slows them
				// down
			}
		};
	}

	private boolean isHeadless() {
		if (isHeadless) {
			String warning = "Warning: cannot run tests from SwingWindowManagerTest because host is headless";
			System.out.println(warning);
			System.err.println(warning);
		}
		return isHeadless;
	}

	@Test
	public void testShowMainView() {
		if (isHeadless())
			return;

		JComponent cmp = new JLabel();
		JMenuBar menu = new JMenuBar();

		SwingView mainView = new MockSwingView("myTitle", cmp, new Dimension(
				50, 30), menu);
		windowManager.showMainView(mainView);
		assertNotNull(lastOpenedWindow);
		JFrame frame = (JFrame) lastOpenedWindow;
		assertEquals("myTitle", frame.getTitle());
		assertSame(cmp, frame.getContentPane().getComponent(0));
		assertSame(menu, frame.getJMenuBar());
		assertEquals(JFrame.EXIT_ON_CLOSE, frame.getDefaultCloseOperation());
	}

	@Test
	public void testShowModal() {
		if (isHeadless())
			return;

		windowManager.showMainView(new MockSwingView("", new JLabel(),
				new Dimension(10, 10)));
		Window mainWindow = lastOpenedWindow;

		JComponent cmp = new JLabel();
		SwingView mainView = new MockSwingView("myTitle", cmp, new Dimension(
				50, 30));
		windowManager.showModal(mainView);
		assertNotNull(lastOpenedWindow);
		JDialog dlg = (JDialog) lastOpenedWindow;
		assertTrue(dlg.isModal());
		assertSame(mainWindow, dlg.getParent());
		assertEquals("myTitle", dlg.getTitle());
		assertSame(cmp, dlg.getContentPane().getComponent(0));
		assertEquals(JDialog.DISPOSE_ON_CLOSE, dlg.getDefaultCloseOperation());
	}

	@Test
	public void testCenterWithoutOffset() {
		if (isHeadless())
			return;

		Window dlg = new JDialog();
		dlg.setSize(100, 100);
		windowManager.center(dlg, new Dimension(400, 200), new Point(0, 0));
		assertEquals(new Point(150, 50), dlg.getLocation());
	}

	@Test
	public void testCenterWithOffSet() {
		if (isHeadless())
			return;

		Window dlg = new JDialog();
		dlg.setSize(100, 100);
		windowManager.center(dlg, new Dimension(400, 200), new Point(50, 50));
		assertEquals(new Point(200, 100), dlg.getLocation());
	}

}
