// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector;

import org.dtangler.core.configuration.Group;

public class MockGroupSelector implements GroupSelector {
	private Group nextResult;
	private Group lastEditedGroup;

	public Group createGroup() {
		return nextResult;
	}

	public void setNextResult(Group group) {
		this.nextResult = group;
	}

	public Group lastEditedGroup() {
		return lastEditedGroup;
	}

	public Group editGroup(Group group) {
		lastEditedGroup = group;
		return nextResult;
	}
}
