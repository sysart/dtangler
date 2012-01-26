//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.actionfactory;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Action;

/**
 * KeyActionAdapter allows using key actions with action factory. This applies
 * only to a components capable of having focus, for example a text field. This
 * cannot be used to listen key events in window level.
 */
public class KeyActionAdapter extends KeyAdapter {
	private final int key;
	private final Action action;

	public KeyActionAdapter(int key, Action action) {
		this.key = key;
		this.action = action;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == this.key)
			this.action.actionPerformed(new ActionEvent(e.getSource(), e
					.getID(), ""));
	}
}
