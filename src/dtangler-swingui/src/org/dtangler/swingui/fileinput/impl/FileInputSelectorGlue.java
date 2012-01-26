//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.fileinput.impl.FileInputSelectorView.Actions;
import org.dtangler.swingui.windowmanager.SwingView;
import org.dtangler.swingui.windowmanager.WindowManager;

public class FileInputSelectorGlue {

	private final ActionFactory actionFactory;
	private final FileInputSelectorPresenter presenter;

	public FileInputSelectorGlue(ActionFactory actionFactory,
			final SwingView view, final FileInputSelectorPresenter presenter,
			final WindowManager windowManager) {
		this.actionFactory = actionFactory;
		this.presenter = presenter;
		updateActionStates();

		actionFactory.setImplementation(Actions.selectEngine, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onSelectEngine();
			}
		});

		actionFactory.setImplementation(Actions.addPath, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onAddPath();
				updateActionStates();
			}
		});

		actionFactory.setImplementation(Actions.removePath,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onRemovePath();
						updateActionStates();
					}
				});

		actionFactory.setImplementation(Actions.addMask, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onAddMask();
				updateActionStates();
			}
		});

		actionFactory.setImplementation(Actions.removeMask,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onRemoveMask();
						updateActionStates();
					}
				});

		actionFactory.setImplementation(Actions.ok, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onOk();
				windowManager.close(view);
			}
		});

		actionFactory.setImplementation(Actions.cancel, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowManager.close(view);
			}
		});

		actionFactory.setImplementation(Actions.updateActionStates,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						updateActionStates();
					}
				});

	}

	private void updateActionStates() {
		actionFactory.setEnabled(Actions.removePath, presenter.canRemovePath());
		actionFactory.setEnabled(Actions.removeMask, presenter.canRemoveMask());
		actionFactory.setEnabled(Actions.ok, presenter.canOk());
	}
}
