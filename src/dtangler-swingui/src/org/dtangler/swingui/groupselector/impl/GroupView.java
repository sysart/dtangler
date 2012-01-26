// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

import org.dtangler.swingui.actionfactory.ActionKey;

public interface GroupView {

	enum Actions implements ActionKey {
		cancel, ok, addItem, removeItems, addExcludedItem, removeExcludedItems, updateActionStates
	}

}
