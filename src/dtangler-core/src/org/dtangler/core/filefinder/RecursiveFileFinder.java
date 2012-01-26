// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.filefinder;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RecursiveFileFinder {

	private final Map<File, String> files = new HashMap();
	private FileFilter fileFilter = new AlwaysValidFileFilter();

	public void findFiles(String path) {
		File file = new File(path);
		path = file.getAbsolutePath();
		findFiles(file, path);
	}

	private void findFiles(File file, String path) {
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				findFiles(subFile, path);
			}
		} else if (file.isFile())
			addFile(file, path);
	}

	private void addFile(File file, String path) {
		if (fileFilter.accept(file)) {
			files.put(file, path);
		}
	}

	public Set<File> getFiles() {
		return files.keySet();
	}

	public void setFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public Map<File, String> getFilesWithPaths() {
		return files;
	}
}
