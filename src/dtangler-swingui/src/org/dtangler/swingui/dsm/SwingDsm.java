//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.dsm;

import org.dtangler.ui.dsm.DsmGuiModel;

public class SwingDsm {

	private final DsmView dsmView;
	private final DsmGuiModel model;

	public SwingDsm(DsmView dsmView, DsmGuiModel model) {
		this.dsmView = dsmView;
		this.model = model;
	}

	public final DsmView getView() {
		return dsmView;
	}

	public final DsmGuiModel getModel() {
		return model;
	}

}
