// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import org.dtangler.swingui.rulememberselector.impl.RuleMemberView.MemberType;

public class RuleMemberPresenter {

	private final RuleMemberView view;
	private RuleMemberModel model;

	public RuleMemberPresenter(RuleMemberView view, RuleMemberModel model) {
		this.view = view;
		this.model = model;
		view.setGroupNames(model.getGroupNames());
	}

	public void onOk() {
		if (view.getSelectedMemberType().equals(MemberType.literal))
			model.setLiteral(view.getLiteral());
		else
			model.setGroupName(view.getSelectedGroup());
	}

	public boolean canOk() {
		if (view.getSelectedMemberType().equals(MemberType.literal))
			return view.getLiteral().length() > 0;
		return view.getSelectedGroup() != null;
	}
}
