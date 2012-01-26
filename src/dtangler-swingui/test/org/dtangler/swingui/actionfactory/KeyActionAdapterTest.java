//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.actionfactory;

import static org.junit.Assert.assertEquals;

import java.awt.Panel;
import java.awt.event.KeyEvent;

import org.junit.Before;
import org.junit.Test;

public class KeyActionAdapterTest {
	private enum Actions implements ActionKey {
		action
	}

	private ActionFactory actionFactory;
	private KeyActionAdapter adapter;
	private MockActionListener impl;

	@Before
	public void setUp() {
		actionFactory = new ActionFactory();
		adapter = new KeyActionAdapter(KeyEvent.VK_0, actionFactory
				.getAction(Actions.action));
		impl = new MockActionListener();
		actionFactory.setImplementation(Actions.action, impl);
	}

	@Test
	public void testOnlyKeyReleasedCounts() {
		adapter.keyPressed(createEvent(KeyEvent.VK_0));
		assertEquals(0, impl.timesPerformed);

		adapter.keyTyped(createEvent(KeyEvent.VK_0));
		assertEquals(0, impl.timesPerformed);

		adapter.keyReleased(createEvent(KeyEvent.VK_0));
		assertEquals(1, impl.timesPerformed);
	}

	@Test
	public void testOnlyRightKeyCounts() {
		adapter.keyReleased(createEvent(KeyEvent.VK_1));
		assertEquals(0, impl.timesPerformed);

		adapter.keyReleased(createEvent(KeyEvent.VK_F6));
		assertEquals(0, impl.timesPerformed);

		adapter.keyReleased(createEvent(KeyEvent.VK_0));
		assertEquals(1, impl.timesPerformed);
	}

	static KeyEvent createEvent(int key) {
		return new KeyEvent(new Panel(), 0, 0, 0, key, (char) 0);
	}
}
