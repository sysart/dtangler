//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm.impl;

import org.dtangler.core.dsm.DsmRow;
import org.dtangler.ui.dsm.DependableInfo;

public class DependableInfoImpl implements DependableInfo {

	private final DsmRow dsmRow;
	private final int index;
	private final boolean isSelected;
	private final boolean isInCrossHair;
	private final boolean containsErrors;
	private final boolean containsWarnings;
	private final Formatter displayNameFormatter;

	public DependableInfoImpl(DsmRow dsmRow, int index, boolean isInCrossHair,
			boolean isSelected, boolean containsErrors,
			boolean containsWarnings, Formatter displayNameFormatter) {
		this.dsmRow = dsmRow;
		this.index = index;
		this.isInCrossHair = isInCrossHair;
		this.isSelected = isSelected;
		this.containsErrors = containsErrors;
		this.containsWarnings = containsWarnings;
		this.displayNameFormatter = displayNameFormatter;
	}

	public int getContentCount() {
		return dsmRow.getDependee().getContentCount();
	}

	public String getDisplayName() {
		return displayNameFormatter.format(dsmRow.getDependee()
				.getDisplayName());
	}

	public int getIndex() {
		return index;
	}

	public boolean isInCrosshair() {
		return isInCrossHair;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public boolean containsErrors() {
		return containsErrors;
	}

	public boolean containsWarnings() {
		return containsWarnings;
	}

	public String getFullyQualifiedName() {
		return dsmRow.getDependee().getFullyQualifiedName();
	}

}
