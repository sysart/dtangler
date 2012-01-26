// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.rulememberselector.impl.RuleMemberView.Actions;
import org.dtangler.swingui.windowmanager.SwingView;
import org.dtangler.swingui.windowmanager.WindowManager;

public class RuleMemberGlue {

	private final ActionFactory actionFactory;
	private final RuleMemberPresenter presenter;

	public RuleMemberGlue(ActionFactory actionFactory,
			final RuleMemberPresenter presenter,
			final WindowManager windowManager, final SwingView view) {
		this.actionFactory = actionFactory;
		this.presenter = presenter;
		updateActionStates();

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
		actionFactory.getAction(Actions.ok).setEnabled(presenter.canOk());
	}

}
