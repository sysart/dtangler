//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm;

public class NullCellInfo {

	public static final CellInfo instance = new CellInfo() {

		public AnalysisResultInfo getAnalysisResultInfo() {
			throw new UnsupportedOperationException("NullCellInfo");
		}

		public int getDependencyWeight() {
			throw new UnsupportedOperationException("NullCellInfo");
		}

		public boolean isInCrossHair() {
			throw new UnsupportedOperationException("NullCellInfo");
		}

		public boolean isSelected() {
			throw new UnsupportedOperationException("NullCellInfo");
		}
	};

	private NullCellInfo() {
	};

}
