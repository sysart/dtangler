//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.io.File;
import java.util.List;

import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.ui.dsm.DsmGuiModelChangeListener;

public interface MainViewModel {

	void addDsmModelChangeListener(DsmGuiModelChangeListener listener);

	List<String> getAllViolations();

	List<String> getSelectionViolations();

	void selectNewInput();

	void editRules();

	void refresh();

	void addForbiddenDeps();

	boolean cellSelectionExists();

	boolean selectionExists();

	void openConfiguration();

	void save();

	void saveAs();

	String getFileName();

	void newConfiguration();

	boolean isDirty();

	List<? extends Scope> getScopes();

	Scope getScope();

	void setScope(Scope selectedScope);

	void zoomIn();

	void zoomIn(Dependencies.DependencyFilter dependencyFilter);

	void zoomOut();

	boolean deeperLevelExists();

	boolean higherLevelExists();

	boolean canExit();

	void toggleShortName();

	boolean isShortNameEnabled();

	void openConfiguration(File file);

	void addLocations(List<File> files);

}
