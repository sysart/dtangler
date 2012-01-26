//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.fileinput.impl;

import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.directoryselector.DirectorySelector;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.fileinput.FileInputSelector;
import org.dtangler.swingui.textinput.TextInputSelector;
import org.dtangler.swingui.windowmanager.WindowManager;

public class FileInputSelectorImpl implements FileInputSelector {

	private final DirectorySelector directorySelector;
	private final TextInputSelector textInputSelector;
	private final WindowManager windowManager;
	private final DependencyEngineFactory dependencyEngineFactory;
	
	public FileInputSelectorImpl(DirectorySelector directorySelector,
			TextInputSelector textInputSelector, WindowManager windowManager, DependencyEngineFactory dependencyEngineFactory) {
		this.directorySelector = directorySelector;
		this.textInputSelector = textInputSelector;
		this.windowManager = windowManager;
		this.dependencyEngineFactory = dependencyEngineFactory;
	}

	public FileInputSelection selectInput(FileInputSelection defaultInput) {
		ActionFactory actionFactory = new ActionFactory();
		SwingFileInputSelectorView view = new SwingFileInputSelectorView(
				actionFactory);
		FileInputSelectorModel model = new DefaultFileInputSelectorModel(
				directorySelector, textInputSelector, dependencyEngineFactory, defaultInput);
		FileInputSelectorPresenter presenter = new FileInputSelectorPresenter(
				view, model);
		new FileInputSelectorGlue(actionFactory, view, presenter, windowManager);
		windowManager.showModal(view);
		return model.getAppliedInputSelection();
	}

}
