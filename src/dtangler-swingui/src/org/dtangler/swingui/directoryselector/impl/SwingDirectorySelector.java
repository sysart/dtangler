//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.directoryselector.impl;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import org.dtangler.swingui.directoryselector.DirectorySelector;

//TODO: refactor FileSelector to support input location selection, then remove this class
public class SwingDirectorySelector implements DirectorySelector {

	private String prevPath;
	private JFileChooser chooser;
	
	public SwingDirectorySelector() {
		prevPath = ".";
	}

	private JFileChooser getCachedChooser() {
		if (chooser != null)
			return chooser;
		chooser = new JFileChooser();
		return chooser;
	}
	
	private JFileChooser createFileChooser(String dialogTitle,
			String fileTypesDescription, boolean isDirectoryInputAllowed,
			List<String> fileNameExtensions) {
		JFileChooser chooser = getCachedChooser();
		chooser.setCurrentDirectory(new File(prevPath));
		chooser.setDialogTitle(dialogTitle);
		if (isDirectoryInputAllowed) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
		if (fileNameExtensions != null && fileNameExtensions.size() > 0) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					fileTypesDescription, (String[]) fileNameExtensions
							.toArray());
			chooser.setFileFilter(filter);
		}
		chooser.setApproveButtonText("Select");
		chooser.setAcceptAllFileFilterUsed(false);
		return chooser;
	}

	public String selectDirectory(String dialogTitle,
			String fileTypesDescription, boolean isDirectoryInputAllowed,
			List<String> fileNameExtensions) {
		JFileChooser chooser = getChooser(dialogTitle, fileTypesDescription,
				isDirectoryInputAllowed, fileNameExtensions);
		if (show(chooser) != JFileChooser.APPROVE_OPTION)
			return null;
		prevPath = getCurrentPath(chooser);
		return getSelectedPath(chooser);
	}

	private String getDefaultDialogTitle() {
		return "Select input location";
	}

	private String getDefaultFilesTypesDescription() {
		return "";
	}

	private List<String> getDefaultFileNameExtensions() {
		return null;
	}

	private boolean getDefaultDirectoryInputAllowed() {
		return true;
	}

	public String selectDirectory() {
		return selectDirectory(getDefaultDialogTitle(),
				getDefaultFilesTypesDescription(),
				getDefaultDirectoryInputAllowed(),
				getDefaultFileNameExtensions());
	}

	private String getSelectedPath(JFileChooser chooser) {
		return chooser.getSelectedFile().getAbsolutePath();
	}

	private String getCurrentPath(JFileChooser chooser) {
		return chooser.getCurrentDirectory().getAbsolutePath();
	}

	protected int show(JFileChooser chooser) {
		return chooser.showOpenDialog(null);
	}

	protected int show() {
		return getChooser().showOpenDialog(null);
	}

	/**
	 * filechooser creation is lazy because for some reason it is very slow on
	 * Win32, making the test suite slow down too much.
	 */
	protected JFileChooser getChooser(String dialogTitle,
			String fileTypesDescription, boolean isDirectoryInputAllowed,
			List<String> fileNameExtensions) {
		return createFileChooser(dialogTitle, fileTypesDescription,
				isDirectoryInputAllowed, fileNameExtensions);
	}

	protected JFileChooser getChooser() {
		return createFileChooser(getDefaultDialogTitle(),
				getDefaultFilesTypesDescription(),
				getDefaultDirectoryInputAllowed(),
				getDefaultFileNameExtensions());
	}

}
