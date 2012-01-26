//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dependencies.Dependencies.DependencyFilter;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.exception.DtException;
import org.dtangler.swingui.fileinput.FileInputSelection;
import org.dtangler.swingui.fileinput.FileInputSelector;
import org.dtangler.swingui.rulesselector.RulesSelector;
import org.dtangler.ui.dsm.DsmGuiModel;
import org.dtangler.ui.dsm.DsmGuiModelChangeListener;
import org.dtangler.ui.dsm.DsmGuiModel.DisplayNameFormat;

public class DefaultMainViewModel implements MainViewModel {

	private final DsmGuiModel dsmModel;
	private final ConfigurationModel configModel;
	private final FileInputSelector inputSelector;
	private final DependencyEngineFactory dependencyEngineFactory;
	private DependencyEngine dependencyEngine;
	private final RulesSelector rulesSelector;

	private class ScopeLevelState {
		private final Set<Dependable> currentParents = new HashSet<Dependable>();
		private Dependencies.DependencyFilter dependencyFilter;
		private List<Integer> selectedRows;
		private List<Integer> selectedCols;

		public Set<Dependable> getCurrentParents() {
			return currentParents;
		}

		public void setDependencyFilter(
				Dependencies.DependencyFilter dependencyFilter) {
			this.dependencyFilter = dependencyFilter;
		}

		public Dependencies.DependencyFilter getDependencyFilter() {
			return dependencyFilter == null ? DependencyFilter.none
					: dependencyFilter;
		}

		public void setSelectedCells(List<Integer> selectedRows,
				List<Integer> selectedCols) {
			this.selectedRows = selectedRows;
			this.selectedCols = selectedCols;
		}

		public List<Integer> getSelectedRows() {
			return selectedRows == null ? Collections.<Integer> emptyList()
					: selectedRows;
		}

		public List<Integer> getSelectedCols() {
			return selectedCols == null ? Collections.<Integer> emptyList()
					: selectedCols;
		}
	}

	private final Map<Scope, ScopeLevelState> scopeLevelStates = new HashMap<Scope, ScopeLevelState>();
	private AnalysisResult analysisResult;
	private Dependencies dependencies;
	private Scope scope;
	private DependencyGraph dependencyGraph;

	public DefaultMainViewModel(DsmGuiModel dsmModel,
			ConfigurationModel configModel, FileInputSelector inputSelector,
			RulesSelector rulesSelector,
			DependencyEngineFactory dependencyEngineFactory) {
		this.dsmModel = dsmModel;
		this.configModel = configModel;
		this.inputSelector = inputSelector;
		this.rulesSelector = rulesSelector;
		this.dependencyEngineFactory = dependencyEngineFactory;
		refresh();
	}

	private void setDependencyEngine(Arguments arguments) {
		if (dependencyEngineFactory != null) {
			dependencyEngine = dependencyEngineFactory
					.getDependencyEngine(arguments);
		}
		if (dependencyEngineFactory == null || dependencyEngine == null)
			throw new DtException(
					"unable to determine the dependency engine to be used");
	}

	public void refresh() {
		setDependencyEngine(getArguments());
		dependencies = dependencyEngine.getDependencies(getArguments());
		clearAllScopeLevelState();
		updateScope();
		updateAnalysisResult();
		updateDependencyGraph(true);
	}

	private Arguments getArguments() {
		return configModel.getArguments();
	}

	private void updateScope() {
		if (scope == null) {
			scope = dependencies.getDefaultScope();
		}
		if (!dependencies.getAvailableScopes().contains(scope)
				&& !dependencies.getAvailableScopes().isEmpty())
			scope = dependencies.getAvailableScopes().get(0);
	}

	private void updateDependencyGraph(boolean updateIfEmpty) {
		dependencyGraph = getDependencyGraph();
		if (!updateIfEmpty
				&& (dependencyGraph == null
						|| dependencyGraph.getAllItems() == null || dependencyGraph
						.getAllItems().size() == 0))
			return;
		updateDsm(dependencyGraph);
	}

	private void updateDsm(DependencyGraph graph) {
		Dsm dsm = new DsmEngine(graph).createDsm();
		dsmModel.setDsm(dsm, analysisResult);
	}

	private void updateAnalysisResult() {
		analysisResult = new ConfigurableDependencyAnalyzer(getArguments())
				.analyze(this.dependencies);
	}

	private ScopeLevelState getScopeLevelState(Scope scope) {
		ScopeLevelState scopeLevelState = scopeLevelStates.get(scope);
		if (scopeLevelState == null) {
			scopeLevelState = new ScopeLevelState();
			scopeLevelStates.put(scope, scopeLevelState);
		}
		return scopeLevelState;
	}

	private void clearAllScopeLevelState() {
		scopeLevelStates.clear();
	}

	private void updateDependencyGraph(Scope scope, Set<Dependable> parents,
			Dependencies.DependencyFilter dependencyFilter,
			boolean updateIfEmpty) {
		this.scope = scope;
		ScopeLevelState scopeLevelState = getScopeLevelState(scope);
		scopeLevelState.getCurrentParents().clear();
		scopeLevelState.getCurrentParents().addAll(parents);
		scopeLevelState.setDependencyFilter(dependencyFilter);
		updateDependencyGraph(updateIfEmpty);
	}

	private DependencyGraph getDependencyGraph() {
		Dependencies.DependencyFilter dependencyFilter = getScopeLevelState(scope).dependencyFilter;
		if (getScopeLevelState(scope).getCurrentParents().isEmpty())
			return dependencies.getDependencyGraph(scope);
		if (getScopeLevelState(scope).getCurrentParents().size() < 2) {
			dependencyFilter = Dependencies.DependencyFilter.none;
			getScopeLevelState(scope).setDependencyFilter(dependencyFilter);
		}
		DependencyGraph graph = dependencies
				.getDependencyGraph(scope, getScopeLevelState(scope)
						.getCurrentParents(), dependencyFilter);
		return graph;
	}

	public void addDsmModelChangeListener(DsmGuiModelChangeListener listener) {
		dsmModel.addChangeListener(listener);
	}

	public List<String> getAllViolations() {
		if (dependencyGraph == null)
			return Collections.emptyList();
		return sortAndFormatViolations(analysisResult
				.getViolations(dependencyGraph.getAllItems()));
	}

	public List<String> getSelectionViolations() {
		Set<Violation> violations = new HashSet<Violation>();
		for (Dependency dep : dsmModel.getSelectionDependencies())
			violations.addAll(analysisResult.getViolations(dep));
		for (Dependable dep : dsmModel.getSelectionDependables())
			violations.addAll(analysisResult.getChildViolations(Collections
					.singleton(dep)));

		return sortAndFormatViolations(violations);
	}

	private List<String> sortAndFormatViolations(Set<Violation> violations) {
		List<String> result = new ArrayList<String>();
		for (Violation v : violations)
			result.add(v.asText());

		Collections.sort(result);
		return result;
	}

	public void selectNewInput() {
		FileInputSelection currentFileInputSelection = new FileInputSelection(
				dependencyEngine.getDependencyEngineId(), getArguments()
						.getInput(), getArguments().getIgnoredFileMasks());
		FileInputSelection newInputSelection = inputSelector
				.selectInput(currentFileInputSelection);
		if (newInputSelection != null) {
			getArguments().setDependencyEngineId(newInputSelection.getEngine());
			getArguments().setInput(newInputSelection.getPaths());
			getArguments().setIgnoredFileMasks(
					newInputSelection.getIgnoredFileMasks());
			clearWindowState();
			refresh();
		}
	}

	public void editRules() {
		Arguments newArguments = rulesSelector.selectRules(getArguments());
		if (newArguments == null)
			return;
		configModel.setArguments(newArguments);
		refresh();
	}

	public void addForbiddenDeps() {
		Map<String, Set<String>> forbiddenDeps = new HashMap<String, Set<String>>(
				getArguments().getForbiddenDependencies());
		Set<Dependency> newForbiddenDeps = dsmModel.getSelectionDependencies();

		for (Dependency dep : newForbiddenDeps)
			addForbiddenDep(forbiddenDeps, dep);
		getArguments().setForbiddenDependencies(forbiddenDeps);
		refresh();
	}

	private void addForbiddenDep(Map<String, Set<String>> forbiddenDeps,
			Dependency dep) {
		if (dep.getDependant().equals(dep.getDependee()))
			return;

		Set<String> newDependees = new HashSet<String>();
		newDependees.add(dep.getDependee().getDisplayName());
		String dependant = dep.getDependant().getDisplayName();
		Set<String> currentDependees = forbiddenDeps.get(dependant);
		if (currentDependees != null)
			newDependees.addAll(currentDependees);
		forbiddenDeps.put(dependant, newDependees);
	}

	public boolean cellSelectionExists() {
		return !dsmModel.getSelectionDependencies().isEmpty();
	}

	public boolean selectionExists() {
		return !dsmModel.getSelectionDependables().isEmpty();
	}

	public void openConfiguration() {
		if (!configModel.openConfiguration())
			return;
		clearWindowState();
		refresh();
	}

	public void save() {
		configModel.save();
	}

	public void saveAs() {
		configModel.saveAs();
	}

	public String getFileName() {
		return configModel.getFileName();
	}

	public void newConfiguration() {
		if (!configModel.newConfiguration())
			return;
		clearWindowState();
		refresh();
	}

	public boolean isDirty() {
		return configModel.isDirty();
	}

	public List<? extends Scope> getScopes() {
		if (dependencies == null)
			return Collections.emptyList();
		return dependencies.getAvailableScopes();
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		clearAllScopeLevelState();
		updateDependencyGraph(scope, Collections.<Dependable> emptySet(),
				Dependencies.DependencyFilter.none, true);
	}

	private boolean isDataCellSelected() {
		if (!isValidCellSelection(dsmModel.getSelectedRows(), dsmModel
				.getSelectedCols())) {
			return false;
		}
		for (Integer col : dsmModel.getSelectedCols()) {
			if (col != null && col >= 0)
				return true;
		}
		return false;
	}

	public void zoomIn() {
		if (!deeperLevelExists())
			return;
		Dependencies.DependencyFilter dependencyFilter = Dependencies.DependencyFilter.none;
		if (isDataCellSelected())
			dependencyFilter = Dependencies.DependencyFilter.itemsContributingToTheParentDependencyWeight;
		zoomIn(dependencyFilter);
	}

	public void zoomIn(Dependencies.DependencyFilter dependencyFilter) {
		if (!deeperLevelExists())
			return;
		saveCurrentCellSelectionToWindowState();
		updateDependencyGraph(dependencies.getChildScope(scope), dsmModel
				.getSelectionDependables(), dependencyFilter, false);
		if (dependencyGraph == null || dependencyGraph.getAllItems() == null
				|| dependencyGraph.getAllItems().size() == 0) {
			String errorMessage = "No contents found at the scope level "
					+ scope + ".";
			if (getScopeLevelState(scope).getDependencyFilter() == DependencyFilter.itemsContributingToTheParentDependencyWeight) {
				errorMessage = "No dependencies between the selected items at the scope level "
						+ scope + ".";
			}
			JOptionPane.showMessageDialog(null, errorMessage, "DSM",
					JOptionPane.INFORMATION_MESSAGE);
			zoomOut();
		}
	}

	private boolean isValidCellSelection(List<Integer> selectedRows,
			List<Integer> selectedCols) {
		int selectedRowCount = selectedRows.size();
		int selectedColCount = selectedCols.size();
		if (selectedRowCount > 0 || selectedColCount > 0) {
			Integer rowCount = dsmModel.getRowCount();
			Integer colCount = dsmModel.getColumnCount();
			for (Integer row : selectedRows) {
				if (row != null && row < rowCount) {
					continue;
				}
				return false;
			}
			for (Integer col : selectedCols) {
				if (col != null && col < colCount) {
					continue;
				}
				return false;
			}
			return true;
		}
		return false;
	}

	private void saveCurrentCellSelectionToWindowState() {
		ScopeLevelState scopeLevelState = getScopeLevelState(this.scope);
		scopeLevelState.setSelectedCells(dsmModel.getSelectedRows(), dsmModel
				.getSelectedCols());
	}

	private void restoreCellSelectionFromWindowState() {
		ScopeLevelState scopeLevelState = getScopeLevelState(this.scope);
		if (isValidCellSelection(scopeLevelState.getSelectedRows(),
				scopeLevelState.getSelectedCols())) {
			dsmModel.selectCells(scopeLevelState.getSelectedRows(),
					scopeLevelState.getSelectedCols());
			// TODO set the popup menu here
		}
	}

	public void zoomOut() {
		Scope parentScope = dependencies.getParentScope(scope);
		this.scope = parentScope;
		updateDependencyGraph(true);
		restoreCellSelectionFromWindowState();
	}

	public boolean deeperLevelExists() {
		return scope != null && dependencies.getChildScope(scope) != null;
	}

	public boolean higherLevelExists() {
		return scope != null && dependencies.getParentScope(scope) != null;
	}

	public boolean canExit() {
		return configModel.canLooseCurrentConfiguration();
	}

	public boolean isShortNameEnabled() {
		return dsmModel.getDisplayNameFormat().equals(
				DisplayNameFormat.shortened);
	}

	public void toggleShortName() {
		dsmModel
				.setDisplayNameFormat(isShortNameEnabled() ? DisplayNameFormat.full
						: DisplayNameFormat.shortened);
	}

	public void addLocations(List<File> files) {
		List<String> input = new ArrayList<String>(getArguments().getInput());
		for (File file : files) {
			if (!input.contains(file.getAbsolutePath()))
				input.add(file.getAbsolutePath());
		}
		getArguments().setInput(input);
		refresh();
	}

	private void clearWindowState() {
		scope = null;
		clearAllScopeLevelState();
	}

	public void openConfiguration(File file) {
		configModel.openConfiguration(file.getAbsolutePath());
		clearWindowState();
		refresh();
	}

}
