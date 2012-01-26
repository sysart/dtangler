//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileselector.impl;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.dtangler.swingui.fileselector.FileSelector;

public class SwingFileSelector implements FileSelector {

	private String prevPath;

	public SwingFileSelector() {
		prevPath = ".";
	}

	private JFileChooser createFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(prevPath));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(true);
		return chooser;
	}

	public String selectFile(final String functionText,
			final String fileSuffix, final String fileDescription) {
		JFileChooser chooser = createFileChooser();
		chooser.setDialogTitle(functionText);
		chooser.setApproveButtonText(functionText);
		chooser.addChoosableFileFilter(new FileFilter() {

			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(fileSuffix);
			}

			public String getDescription() {
				return fileDescription;
			}
		});
		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return null;
		prevPath = chooser.getCurrentDirectory().getAbsolutePath();
		return chooser.getSelectedFile().getAbsolutePath();
	}

}
