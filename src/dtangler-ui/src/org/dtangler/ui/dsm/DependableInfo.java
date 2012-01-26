//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.ui.dsm;

public interface DependableInfo {

	String getDisplayName();

	int getContentCount();

	int getIndex();

	boolean isInCrosshair();

	boolean isSelected();

	boolean containsErrors();

	boolean containsWarnings();

	String getFullyQualifiedName();

}
