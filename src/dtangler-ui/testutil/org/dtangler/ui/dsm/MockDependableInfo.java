//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm;

public class MockDependableInfo implements DependableInfo {

	private final String name;
	private final int contentCount;
	private final int index;
	private final boolean isSelected;

	public MockDependableInfo(String name, int index, int contentCount,
			boolean isSelected) {

		this.name = name;
		this.index = index;
		this.contentCount = contentCount;
		this.isSelected = isSelected;
	}

	public int getContentCount() {
		return contentCount;
	}

	public String getDisplayName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public boolean isInCrosshair() {
		return isSelected;
	}

	public String getFullyQualifiedName() {
		return null;
	}

	public boolean isSelected() {
		return false;
	}

	public boolean containsErrors() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsWarnings() {
		// TODO Auto-generated method stub
		return false;
	}

}
