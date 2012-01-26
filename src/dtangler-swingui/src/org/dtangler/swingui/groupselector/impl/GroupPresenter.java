// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

public class GroupPresenter {

	private final SwingGroupView view;
	private final GroupModel model;

	public GroupPresenter(SwingGroupView view, GroupModel model) {
		this.view = view;
		this.model = model;
		view.setName(model.getName());
		updateUi();
	}

	private void updateUi() {
		view.setItems(model.getItems());
		view.setExcludedItems(model.getExcludedItems());
	}

	public void onOk() {
		model.setName(view.getName());
		model.save();
	}

	public void onAddItem() {
		model.addItem();
		updateUi();
	}

	public void onRemoveItems() {
		model.removeItems(view.getSelectedItems());
		updateUi();

	}

	public void onRemoveExcludedItems() {
		model.removeExcludedItems(view.getSelectedExcludedItems());
		updateUi();

	}

	public void onAddExcludedItem() {
		model.addExcludedItem();
		updateUi();

	}

	public boolean canRemoveItems() {
		return !view.getSelectedItems().isEmpty();
	}

	public boolean canRemoveExcludedItems() {
		return !view.getSelectedExcludedItems().isEmpty();
	}

	public boolean canOk() {
		if (view.getName().trim().equals(""))
			return false;
		return !model.getItems().isEmpty();
	}
}
