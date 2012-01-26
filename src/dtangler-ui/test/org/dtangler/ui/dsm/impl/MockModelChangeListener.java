//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm.impl;

import org.dtangler.ui.dsm.DsmGuiModelChangeListener;

public class MockModelChangeListener implements DsmGuiModelChangeListener {

	public int timesGuiModelChangedCalled = 0;
	public int timesDataChangedCalled = 0;

	public void dsmGuiModelChanged() {
		timesGuiModelChangedCalled++;
	}

	public void dsmDataChanged() {
		timesDataChangedCalled++;
	}

}
