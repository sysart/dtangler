//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.actionfactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class ActionFactory {

	private class GuiAction extends AbstractAction {

		private ActionListener impl;

		void setImplementation(ActionListener impl) {
			this.impl = impl;
		}

		public void actionPerformed(ActionEvent e) {
			if (!isEnabled())
				return;
			fireBeforeExecution();
			try {
				performExecution(e);
			} finally {
				fireAfterExecution();
			}
		}

		private void fireBeforeExecution() {
			if (actionExecutionListener != null)
				actionExecutionListener.onBeforeExecution();
		}

		private void performExecution(ActionEvent e) {
			if (impl != null)
				impl.actionPerformed(e);
		}

		private void fireAfterExecution() {
			if (actionExecutionListener != null)
				actionExecutionListener.onAfterExecution();
		}

	}

	private final Map<ActionKey, GuiAction> actions = new HashMap();
	private ActionExecutionListener actionExecutionListener;

	public Action getAction(ActionKey key) {
		return getOrCreateAction(key);
	}

	public void setImplementation(ActionKey key, ActionListener impl) {
		getOrCreateAction(key).setImplementation(impl);
	}

	public void setEnabled(ActionKey key, boolean b) {
		getOrCreateAction(key).setEnabled(b);
	}

	private GuiAction getOrCreateAction(ActionKey key) {
		GuiAction action = actions.get(key);
		if (action == null) {
			action = new GuiAction();
			actions.put(key, action);
		}
		return action;
	}

	public void setActionExecutionListener(
			ActionExecutionListener actionExecutionListener) {
		this.actionExecutionListener = actionExecutionListener;
	}

}
