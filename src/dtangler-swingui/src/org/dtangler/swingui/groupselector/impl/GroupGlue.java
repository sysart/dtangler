// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.groupselector.impl.GroupView.Actions;
import org.dtangler.swingui.windowmanager.WindowManager;

public class GroupGlue {

	private final ActionFactory actionFactory;
	private final GroupPresenter presenter;

	public GroupGlue(ActionFactory actionFactory,
			final WindowManager windowManager, final SwingGroupView view,
			final GroupPresenter presenter) {

		this.actionFactory = actionFactory;
		this.presenter = presenter;
		updateActionStates();
		actionFactory.setImplementation(Actions.cancel, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowManager.close(view);
			}
		});

		actionFactory.setImplementation(Actions.ok, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onOk();
				windowManager.close(view);
			}
		});

		actionFactory.setImplementation(Actions.addItem, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onAddItem();
				updateActionStates();
			}
		});

		actionFactory.setImplementation(Actions.removeItems,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onRemoveItems();
						updateActionStates();
					}
				});

		actionFactory.setImplementation(Actions.addExcludedItem,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onAddExcludedItem();
						updateActionStates();
					}
				});

		actionFactory.setImplementation(Actions.removeExcludedItems,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onRemoveExcludedItems();
						updateActionStates();
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
		actionFactory.getAction(Actions.removeItems).setEnabled(
				presenter.canRemoveItems());
		actionFactory.getAction(Actions.removeExcludedItems).setEnabled(
				presenter.canRemoveExcludedItems());
		actionFactory.getAction(Actions.ok).setEnabled(presenter.canOk());
	}

}
