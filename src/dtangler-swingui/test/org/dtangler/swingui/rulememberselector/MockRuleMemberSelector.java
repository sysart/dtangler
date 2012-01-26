// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector;

import java.util.List;

public class MockRuleMemberSelector implements RuleMemberSelector {

	private String nextValue;
	private List<String> groupNames;

	public String selectRuleMember(List<String> groupNames) {
		this.groupNames = groupNames;
		return nextValue;
	}

	public void setNextValue(String nextValue) {
		this.nextValue = nextValue;
	}

	public List<String> getLastUsedGroupNames() {
		return groupNames;
	}

}
