// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulesselector.impl;

import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;

public class RuleSelectorViewDriver {

	public final Button addRuleButton;
	public final Button removeRulesButton;
	public final Button addRuleItemButton;
	public final Button removeRuleItemsButton;
	public final ListBox rules;
	public final ListBox ruleItems;

	public RuleSelectorViewDriver(Panel panel, String identifier) {
		addRuleButton = panel.getButton("add" + identifier + "Rule");
		removeRulesButton = panel.getButton("remove" + identifier + "Rule");
		addRuleItemButton = panel.getButton("add" + identifier + "ruleitem");
		removeRuleItemsButton = panel.getButton("remove" + identifier
				+ "ruleitems");
		rules = panel.getListBox("rules");
		ruleItems = panel.getListBox("ruleitems");
	}

}
