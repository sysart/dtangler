//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm.impl;

public interface ColumnAndRowSelectionModel {

	boolean isRowOrColumnInCrossHair(int row, int col);

	boolean isSelected(int row, int col);

}
