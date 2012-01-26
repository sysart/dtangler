//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

public class FileInputSelectorPresenter {

	private final FileInputSelectorView view;
	private final FileInputSelectorModel model;

	public FileInputSelectorPresenter(FileInputSelectorView view,
			FileInputSelectorModel model) {
		this.view = view;
		this.model = model;
		updateView();
	}

	private void updateView() {
		view.setPaths(model.getPaths());
		view.setMasks(model.getMasks());
		view.setEngines(model.getEngines());
		view.setEngineSelection(model.getEngine());
	}

	public void onSelectEngine() {
		if (view.getEngineSelection() != null) {
			model.setEngine(view.getEngineSelection());
		}
		updateView();
	}
	
	public void onAddPath() {
		model.addPath();
		updateView();
	}

	public void onAddMask() {
		model.addMask();
		updateView();
	}

	void onRemovePath() {
		model.removePaths(view.getPathSelection());
		updateView();
	}

	void onRemoveMask() {
		model.removeMasks(view.getMaskSelection());
		updateView();
	}

	public boolean canRemovePath() {
		return !view.getPathSelection().isEmpty();
	}

	public boolean canRemoveMask() {
		return !view.getMaskSelection().isEmpty();
	}

	public boolean canOk() {
		return model.isValidInputSelection();
	}

	public void onOk() {
		model.applySelection();
	}

}
