// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.mainview.impl;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.swingui.fileselector.FileSelector;
import org.dtangler.swingui.windowmanager.DialogManager;

public class TestableConfigurationModel extends ConfigurationModel {

	private String lastOpenedFile;
	private Arguments argumentsToOpen;
	private String lastSavedFile;

	public TestableConfigurationModel(FileSelector fileSelector,
			DialogManager dialogManager, Arguments arguments) {
		super(fileSelector, dialogManager, arguments);
	}

	@Override
	protected void openConfigFile(String fileName) {
		lastOpenedFile = fileName;
		setOpenedArguments(fileName, argumentsToOpen);
	}

	public String getLastOpenedFile() {
		return lastOpenedFile;
	}

	public void setArgumentsToOpen(Arguments argumentsToOpen) {
		this.argumentsToOpen = argumentsToOpen;
	}

	@Override
	protected void saveConfigFile(String fileName) {
		lastSavedFile = fileName;
	}

	public String getLastSavedFile() {
		return lastSavedFile;
	}

}
