//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.swingui.rulesselector.RulesSelector;

public class MockRulesSelector implements RulesSelector {

	private boolean rulesSelected;
	private Arguments previousArguments;

	public Arguments selectRules(Arguments previousArguments) {
		this.previousArguments = previousArguments;
		rulesSelected = true;
		return null;
	}

	public boolean wereRulesSelected() {
		return rulesSelected;
	}

	public Arguments getPreviousRules() {
		return previousArguments;
	}

}
