//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import org.dtangler.swingui.rulesselector.impl.RulesModel.RuleModel;
import org.dtangler.swingui.rulesselector.impl.RulesView.RuleView;

public class RulesPresenter {

	class RulePresenter {

		private final RuleView view;
		private final RuleModel model;

		RulePresenter(RuleView view, RuleModel model) {
			this.view = view;
			this.model = model;
		}

		public void onAddRule() {
			model.addRule();
			updateRules();
		}

		private void updateRules() {
			view.setRules(model.getRules());
		}

		private void updateRuleItems() {
			view.setRuleItems(model.getRuleItems(view.getSelectedRules()));
		}

		public void onRemoveRule() {
			model.removeRules(view.getSelectedRules());
			updateRules();
		}

		public boolean canRemoveRule() {
			return !view.getSelectedRules().isEmpty();
		}

		public void onRuleSelectionChanged() {
			updateRuleItems();
		}

		public void onAddRuleItems() {
			model.addRuleItem(view.getSelectedRules().get(0));
			updateRuleItems();
		}

		public void onRemoveRuleItems() {
			model.removeRuleItem(view.getSelectedRules().get(0), view
					.getSelectedRuleItems());
			updateRuleItems();
		}

		public boolean canRemoveRuleItems() {
			return !view.getSelectedRuleItems().isEmpty();
		}

		public boolean canAddRuleItems() {
			return view.getSelectedRules().size() == 1;
		}

	}

	private final RulesView view;
	private final RulesModel model;
	private final RulePresenter forbiddenDepsPresenter;
	private final RulePresenter allowedDepsPresenter;

	public RulesPresenter(RulesView view, RulesModel model) {
		this.view = view;
		this.model = model;
		forbiddenDepsPresenter = new RulePresenter(view.forbiddenDeps(), model
				.forbiddenDepsModel());
		allowedDepsPresenter = new RulePresenter(view.allowedDeps(), model
				.allowedDepsModel());
		updateUi();
	}

	private void updateUi() {
		view.setGroupNames(model.getGroupNames());
		forbiddenDepsPresenter.updateRules();
		allowedDepsPresenter.updateRules();
	}

	public void onOk() {
		model.save();
	}

	public RulePresenter forbiddenDeps() {
		return forbiddenDepsPresenter;
	}

	public RulePresenter allowedDeps() {
		return allowedDepsPresenter;
	}

	public void onNewGroup() {
		model.newGroup();
		view.setGroupNames(model.getGroupNames());
	}

	public void onRemoveGroups() {
		model.removeGroups(view.getSelectedGroups());
		updateUi();
	}

	public void onEditGroup() {
		model.editGroup(view.getSelectedGroups().get(0));
		updateUi();
	}

	public boolean canEditGroup() {
		return view.getSelectedGroups().size() == 1;
	}

	public boolean canRemoveGroups() {
		return !view.getSelectedGroups().isEmpty();

	}

}
