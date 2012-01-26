//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.swingui.directoryselector.DirectorySelector;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.textinput.TextInputSelector;

public class DefaultFileInputSelectorModel implements FileInputSelectorModel {

	private final DirectorySelector directorySelector;
	private final TextInputSelector textInputSelector;
	private final DependencyEngineFactory dependencyEngineFactory;
	private String engine;
	private final List<String> paths;
	private final List<String> masks;
	private FileInputSelection appliedInput;

	public DefaultFileInputSelectorModel(DirectorySelector directorySelector,
			TextInputSelector textInputSelector,
			DependencyEngineFactory dependencyEngineFactory,
			FileInputSelection defaultInput) {
		this.directorySelector = directorySelector;
		this.textInputSelector = textInputSelector;
		this.dependencyEngineFactory = dependencyEngineFactory;
		engine = defaultInput.getEngine();
		paths = new ArrayList(defaultInput.getPaths());
		masks = new ArrayList(defaultInput.getIgnoredFileMasks());
	}

	public String getEngine() {
		if (engine == null)
			return null;
		return new String(engine);
	}

	public List<String> getEngines() {
		if (dependencyEngineFactory == null)
			return null;
		return dependencyEngineFactory.getDependencyEngineIds();
	}

	private List<String> getFileNameExtensionsSupportedByDependencyEngine() {
		if (engine != null)
			return dependencyEngineFactory.getDependencyEngine(engine)
					.getInputFileNameExtensions();
		return new ArrayList<String>();
	}

	private String getInputFilesDescriptionSupportedByDependencyEngine() {
		if (engine != null)
			return dependencyEngineFactory.getDependencyEngine(engine)
					.getInputFilesDescription();
		return "";
	}

	private boolean isDirectoryInputSupportedByDependencyEngine() {
		if (engine != null)
			return dependencyEngineFactory.getDependencyEngine(engine)
					.isDirectoryInputSupported();
		return false;
	}

	private String getFileNameExtensionsDescription() {
		if (engine != null) {
			String txt = "";
			List<String> list = getFileNameExtensionsSupportedByDependencyEngine();
			for (String extension : list) {
				if (txt.length() > 0)
					txt += "; ";
				txt += ("*." + extension);
			}
			String description = getInputFilesDescriptionSupportedByDependencyEngine();
			if (description != null) {
				txt = getInputFilesDescriptionSupportedByDependencyEngine()
						+ " (" + txt + ")";
			}
			return txt;
		}
		return "";
	}

	private String getDirectorySelectorDialogTitle() {
		if (isDirectoryInputSupportedByDependencyEngine()) {
			return "Select input directory or file";
		}
		return "Select input file";
	}

	public List<String> getPaths() {
		return Collections.unmodifiableList(paths);
	}

	public List<String> getMasks() {
		return Collections.unmodifiableList(masks);
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public void removePaths(List<String> pathsToRemove) {
		paths.removeAll(pathsToRemove);
	}

	public void removeMasks(List<String> masksToRemove) {
		masks.removeAll(masksToRemove);
	}

	public boolean isValidInputSelection() {
		return !paths.isEmpty();
	}

	public void addMask() {
		String mask = textInputSelector.selectValue("File mask to ignore",
				"Input Filter");
		if (mask != null && !masks.contains(mask))
			masks.add(mask);
	}

	public void addPath() {
		String newPath = directorySelector.selectDirectory(
				getDirectorySelectorDialogTitle(),
				getFileNameExtensionsDescription(),
				isDirectoryInputSupportedByDependencyEngine(),
				getFileNameExtensionsSupportedByDependencyEngine());
		if (newPath != null && !paths.contains(newPath))
			paths.add(newPath);
	}

	public FileInputSelection getAppliedInputSelection() {
		return appliedInput;
	}

	public void applySelection() {
		appliedInput = new FileInputSelection(engine, paths, masks);
	}

}
