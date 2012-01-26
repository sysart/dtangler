//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.swingui.aboutinfodisplayer.AboutInfoDisplayer;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.mainview.impl.MainView.Actions;
import org.dtangler.swingui.windowmanager.SwingView;
import org.dtangler.swingui.windowmanager.WindowManager;
import org.dtangler.ui.dsm.DsmGuiModelChangeListener;

public class MainViewGlue implements DsmGuiModelChangeListener {

	private final MainViewPresenter presenter;
	private final ActionFactory actionfactory;

	public MainViewGlue(ActionFactory actionfactory,
			final MainViewPresenter presenter, MainViewModel model,
			final WindowManager windowManager, final SwingView view,
			final AboutInfoDisplayer aboutInfoDisplayer) {

		this.actionfactory = actionfactory;
		this.presenter = presenter;
		model.addDsmModelChangeListener(this);
		updateActionStates();
		actionfactory.setImplementation(Actions.input, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onEditInput();
			}
		});

		actionfactory.setImplementation(Actions.rules, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onEditRules();
			}
		});

		actionfactory.setImplementation(Actions.refresh, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onRefresh();
			}
		});

		actionfactory.setImplementation(Actions.about, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutInfoDisplayer.displayAboutInfo();
			}
		});

		actionfactory.setImplementation(Actions.addforbiddendeps,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onAddForbiddenDeps();
					}
				});

		actionfactory.setImplementation(Actions.exit, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (presenter.canExit())
					windowManager.close(view);
			}
		});

		actionfactory.setImplementation(Actions.open, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onOpen();
			}
		});

		actionfactory.setImplementation(Actions.save, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onSave();
			}
		});

		actionfactory.setImplementation(Actions.saveas, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onSaveAs();
			}
		});

		actionfactory.setImplementation(Actions.clear, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onNew();
			}
		});

		actionfactory.setImplementation(Actions.changeScope,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onChangeScope();
					}
				});

		actionfactory.setImplementation(Actions.zoomIn, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onZoomIn();
			}
		});

		actionfactory.setImplementation(Actions.zoomInContents, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onZoomIn(Dependencies.DependencyFilter.none);
			}
		});

		actionfactory.setImplementation(Actions.zoomInDependencies, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onZoomIn(Dependencies.DependencyFilter.itemsContributingToTheParentDependencyWeight);
			}
		});

		actionfactory.setImplementation(Actions.zoomOut, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				presenter.onZoomOut();
			}
		});

		actionfactory.setImplementation(Actions.toggleShortName,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						presenter.onToggleShortName();
					}
				});

	}

	public void dsmDataChanged() {
		presenter.onDsmGuiModelChanged();
		updateActionStates();
	}

	public void dsmGuiModelChanged() {
		presenter.onDsmGuiModelChanged();
		updateActionStates();
	}

	private void updateActionStates() {
		actionfactory.getAction(Actions.addforbiddendeps).setEnabled(
				presenter.canAddForbiddenDeps());
		actionfactory.getAction(Actions.zoomIn).setEnabled(
				presenter.canZoomIn());
		actionfactory.getAction(Actions.zoomInContents).setEnabled(
				presenter.canZoomIn());
		actionfactory.getAction(Actions.zoomInDependencies).setEnabled(
				presenter.canZoomIn());
		actionfactory.getAction(Actions.zoomOut).setEnabled(
				presenter.canZoomOut());
	}

}
