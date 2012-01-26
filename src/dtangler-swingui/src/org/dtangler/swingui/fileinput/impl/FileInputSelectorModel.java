//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import java.util.List;

import org.dtangler.swingui.fileinput.FileInputSelection;

public interface FileInputSelectorModel {

	List<String> getEngines();

	String getEngine();

	void setEngine(String engine);

	List<String> getPaths();

	List<String> getMasks();

	void removePaths(List<String> paths);

	void removeMasks(List<String> masks);

	boolean isValidInputSelection();

	void addPath();

	void addMask();

	FileInputSelection getAppliedInputSelection();

	void applySelection();

}
