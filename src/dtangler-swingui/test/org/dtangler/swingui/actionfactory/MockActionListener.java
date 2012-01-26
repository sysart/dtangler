//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.actionfactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MockActionListener implements ActionListener {

	public int timesPerformed = 0;

	public void actionPerformed(ActionEvent e) {
		timesPerformed++;
	}

}
