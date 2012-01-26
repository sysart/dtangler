//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.io.File;
import java.util.List;

import org.dtangler.core.dependencies.Dependencies;

public class MainViewPresenter implements FileListDropListener {

	private final MainViewModel model;
	private final MainView view;

	MainViewPresenter(MainViewModel model, MainView view) {
		this.model = model;
		this.view = view;
		view.setFileName(model.getFileName());
		view.addFileListDropListener(this);
	}

	public void onDsmGuiModelChanged() {
		view.setSelectionViolations(model.getSelectionViolations());
	}

	public void onEditInput() {
		model.selectNewInput();
		updateView();
	}

	public void onRefresh() {
		model.refresh();
		updateView();
	}

	public void onEditRules() {
		model.editRules();
		updateView();
	}

	public void onAddForbiddenDeps() {
		model.addForbiddenDeps();
		updateView();
	}

	public boolean canAddForbiddenDeps() {
		return model.cellSelectionExists();
	}

	public void onOpen() {
		model.openConfiguration();
		updateView();
	}

	public void onSave() {
		model.save();
		updateView();
	}

	public void onSaveAs() {
		model.saveAs();
		updateView();
	}

	public void onNew() {
		model.newConfiguration();
		updateView();
	}

	private void updateView() {
		StringBuilder sb = new StringBuilder();
		if (model.isDirty())
			sb.append("* ");
		sb.append(model.getFileName());
		view.setFileName(sb.toString());
		view.setScopes(model.getScopes());
		view.setScope(model.getScope());
		view.setAllViolations(model.getAllViolations());
	}

	public void onChangeScope() {
		model.setScope(view.getSelectedScope());
		updateView();
	}

	public boolean canZoomIn() {
		return model.selectionExists() && model.deeperLevelExists();
	}

	public boolean canZoomOut() {
		return model.higherLevelExists();
	}

	public void onZoomIn() {
		model.zoomIn();
		updateView();
	}

	public void onZoomIn(Dependencies.DependencyFilter dependencyFilter) {
		model.zoomIn(dependencyFilter);
		updateView();
	}

	public void onZoomOut() {
		model.zoomOut();
		updateView();
	}

	public boolean canExit() {
		return model.canExit();
	}

	public void onToggleShortName() {
		model.toggleShortName();
		view.setShortNameEnabled(model.isShortNameEnabled());

	}

	public void fileListDropped(List<File> files) {
		if (files.size() == 1 && files.get(0).isFile()
				&& files.get(0).getName().toLowerCase().endsWith(".properties"))
			model.openConfiguration(files.get(0));
		else
			model.addLocations(files);
		updateView();

	}
}
