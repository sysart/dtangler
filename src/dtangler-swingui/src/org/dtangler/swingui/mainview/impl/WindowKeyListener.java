// This product is provided under the terms of EPL (Eclipse Public License)
// version 1.0.
//
// The full license text can be read from:
// http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.Action;

public class WindowKeyListener extends EventQueue {
	final Hashtable<Integer, Action> actions = new Hashtable<Integer, Action>();
	final private Component parent;

	public WindowKeyListener(Component parent) {
		super();
		this.parent = parent;
	}

	public void addKeyAction(int keyCode, Action action) {
		actions.put(new Integer(keyCode), action);
	}

	protected void dispatchEvent(AWTEvent event) {
		try {
			if (!event.getSource().equals(this.parent))
				return;
			if (!(event instanceof KeyEvent))
				return;
			if (event.getID() != KeyEvent.KEY_RELEASED)
				return;
			handleKeyEvent((KeyEvent) event);
		} finally {
			super.dispatchEvent(event);
		}
	}

	private void handleKeyEvent(KeyEvent event) {
		Integer key = new Integer(event.getKeyCode());

		if (!actions.contains(key))
			return;
		actions.get(key).actionPerformed(
				new ActionEvent(event.getSource(), event.getID(), ""));
	}
}
