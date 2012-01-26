// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import java.util.List;

import org.dtangler.swingui.actionfactory.ActionKey;

public interface RuleMemberView {

	enum Actions implements ActionKey {
		ok, cancel, updateActionStates;
	}

	enum MemberType {
		group, literal
	}

	void setGroupNames(List<String> groupNames);

	MemberType getSelectedMemberType();

	String getLiteral();

	String getSelectedGroup();

}
