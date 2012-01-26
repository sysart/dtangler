// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.mainview.impl;

import java.io.File;
import java.util.Map;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.input.ArgumentParser;
import org.dtangler.core.input.ConfigFileParser;
import org.dtangler.core.input.ConfigFileWriter;
import org.dtangler.swingui.fileselector.FileSelector;
import org.dtangler.swingui.windowmanager.DialogManager;
import org.dtangler.swingui.windowmanager.DialogManager.DialogResult;

public class ConfigurationModel {

	private final FileSelector fileSelector;
	private final DialogManager dialogManager;
	private Arguments currentArguments;
	private Arguments lastSavedArguments;

	public ConfigurationModel(final FileSelector fileSelector,
			final DialogManager dialogManager, Arguments arguments) {
		this.fileSelector = fileSelector;
		this.dialogManager = dialogManager;
		currentArguments = arguments;
		lastSavedArguments = currentArguments.createDeepCopy();
	}

	public boolean openConfiguration() {
		if (!canLooseCurrentConfiguration())
			return false;
		String fileName = selectFile("Open");
		if (fileName == null)
			return false;
		openConfigFile(fileName);
		return true;
	}

	public boolean openConfiguration(String fileName) {
		if (!canLooseCurrentConfiguration())
			return false;
		openConfigFile(fileName);
		return true;
	}

	public void save() {
		if (currentArguments.getConfigFileName() == null)
			saveAs();
		else
			saveConfiguration();
	}

	public void saveAs() {
		String fileName = selectFile("Save");
		if (fileName == null)
			return;
		currentArguments.setConfigFileName(fileName);
		saveConfiguration();
	}

	private void saveConfiguration() {
		saveConfigFile(currentArguments.getConfigFileName());
		lastSavedArguments = currentArguments.createDeepCopy();
	}

	public String getFileName() {
		String configFileName = currentArguments.getConfigFileName();
		return configFileName != null ? configFileName : "unsaved settings";
	}

	public boolean newConfiguration() {
		if (!canLooseCurrentConfiguration())
			return false;
		currentArguments = new Arguments();
		lastSavedArguments = currentArguments.createDeepCopy();
		return true;
	}

	public boolean canLooseCurrentConfiguration() {
		if (!isDirty())
			return true;
		DialogResult result = dialogManager.showYesNoCancelDialog(
				"dtangler settings have been modified. Save canges?",
				"save dtangler settings");
		if (result.equals(DialogResult.yes)) {
			save();
			return !isDirty(); // check wether actually saved
		}
		return result.equals(DialogResult.no);
	}

	public boolean isDirty() {
		return !currentArguments.equals(lastSavedArguments);
	}

	public Arguments getArguments() {
		return currentArguments;
	}

	public void setArguments(Arguments newArguments) {
		currentArguments = newArguments;
	}

	private String selectFile(String functionText) {
		return fileSelector.selectFile(functionText, ".properties",
				"dtangler properties file");
	}

	protected void openConfigFile(String fileName) {
		Map<String, String> configFileValues = new ConfigFileParser(new File(
				fileName), ParserConstants.VALID_KEYS).parseValues();
		setOpenedArguments(fileName, new ArgumentParser()
				.parseArguments(configFileValues));
	}

	protected void setOpenedArguments(String fileName, Arguments parseArguments) {
		currentArguments = parseArguments;
		currentArguments.setConfigFileName(fileName);
		lastSavedArguments = currentArguments.createDeepCopy();
	}

	protected void saveConfigFile(String fileName) {
		new ConfigFileWriter(new File(fileName)).save(currentArguments);
	}

}
