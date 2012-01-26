package org.dtangler.swingui.directoryselector.impl;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileNameExtensionFilter extends FileFilter
{
	private String[] fileNameExtensions;
	private String description;

	public FileNameExtensionFilter(String description, String[] fileNameExtensions) {
		this.description = description;
		this.fileNameExtensions = new String[fileNameExtensions.length];
		for (int i=fileNameExtensions.length-1; i>=0; i--) {
			this.fileNameExtensions[i] = fileNameExtensions[i].toLowerCase();
		}
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String name = f.getName().toLowerCase();
		for (int i = fileNameExtensions.length - 1; i >= 0; i--) {
			if (name.endsWith(fileNameExtensions[i])) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return description == null ? "" : description;
	}

}