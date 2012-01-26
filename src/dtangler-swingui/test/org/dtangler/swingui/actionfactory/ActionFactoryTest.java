//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.actionfactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;

import org.junit.Before;
import org.junit.Test;

public class ActionFactoryTest {

	private enum Keys implements ActionKey {
		key1, key2
	}

	private ActionFactory actionFactory;

	@Before
	public void setUp() {
		actionFactory = new ActionFactory();
	}

	@Test
	public void testGetAction() {
		Action action1 = actionFactory.getAction(Keys.key1);
		assertNotNull(action1);
		Action action2 = actionFactory.getAction(Keys.key1);
		assertSame(action1, action2);
	}

	@Test
	public void testSetImplementation() {
		MockActionListener impl = new MockActionListener();
		actionFactory.setImplementation(Keys.key1, impl);

		actionFactory.getAction(Keys.key1).actionPerformed(null);
		assertEquals(1, impl.timesPerformed);

		actionFactory.getAction(Keys.key2).actionPerformed(null);
		assertEquals(1, impl.timesPerformed);
	}

	@Test
	public void testExecuteActionWihoutImplementation() {
		actionFactory.getAction(Keys.key1).actionPerformed(null);
	}

	@Test
	public void testSetEnabled() {
		assertTrue(actionFactory.getAction(Keys.key1).isEnabled());
		assertTrue(actionFactory.getAction(Keys.key2).isEnabled());

		actionFactory.setEnabled(Keys.key2, false);

		assertTrue(actionFactory.getAction(Keys.key1).isEnabled());
		assertFalse(actionFactory.getAction(Keys.key2).isEnabled());
	}

	@Test
	public void testBeforeAndAfterActionEvents() {
		final StringBuffer sb = new StringBuffer();
		actionFactory.setActionExecutionListener(new ActionExecutionListener() {

			public void onBeforeExecution() {
				sb.append("onBefore");
			}

			public void onAfterExecution() {
				sb.append(" onAfter");
			}

		});

		actionFactory.setImplementation(Keys.key1, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sb.append(" execute");
			}
		});

		actionFactory.getAction(Keys.key1).actionPerformed(null);
		assertEquals("onBefore execute onAfter", sb.toString());
	}

	@Test
	public void testAfterActionEventIsFiredEvenIfExecutionThrowsException() {
		final StringBuffer sb = new StringBuffer();
		actionFactory.setActionExecutionListener(new ActionExecutionListener() {

			public void onBeforeExecution() {
				sb.append("onBefore");
			}

			public void onAfterExecution() {
				sb.append(" onAfter");
			}

		});

		actionFactory.setImplementation(Keys.key1, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				throw new RuntimeException("my error");
			}
		});

		try {
			actionFactory.getAction(Keys.key1).actionPerformed(null);
			fail("did not throw");
		} catch (RuntimeException e) {
			assertEquals("my error", e.getMessage());
		}
		assertEquals("onBefore onAfter", sb.toString());
	}

}
