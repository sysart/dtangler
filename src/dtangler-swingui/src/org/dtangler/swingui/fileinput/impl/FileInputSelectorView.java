//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import java.util.List;

import org.dtangler.swingui.actionfactory.ActionKey;

public interface FileInputSelectorView {

	enum Actions implements ActionKey {
		cancel, selectEngine, addPath, removePath, addMask, removeMask, ok, updateActionStates
	}

	void setEngines(List<String> engines);

	void setEngineSelection(String engine);

	void setPaths(List<String> paths);

	void setMasks(List<String> masks);

	String getEngineSelection();

	List<String> getPathSelection();

	List<String> getMaskSelection();

}
