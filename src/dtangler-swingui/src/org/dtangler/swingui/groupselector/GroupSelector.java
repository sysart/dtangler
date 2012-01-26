// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector;

import org.dtangler.core.configuration.Group;

public interface GroupSelector {
	Group createGroup();

	Group editGroup(Group group);
}
