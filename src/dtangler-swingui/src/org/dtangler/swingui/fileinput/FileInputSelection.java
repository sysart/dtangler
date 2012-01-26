//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput;

import java.util.List;

public class FileInputSelection {

	private final List<String> paths;
	private final List<String> ignoredFileMasks;
	private String engine;

	public FileInputSelection(String engine, List<String> paths, List<String> ignoredFileMasks) {
		this.engine = engine;
		this.paths = paths;
		this.ignoredFileMasks = ignoredFileMasks;
	}

	public String getEngine() {
		return engine;
	}

	public List<String> getIgnoredFileMasks() {
		return ignoredFileMasks;
	}

	public List<String> getPaths() {
		return paths;
	}

}
