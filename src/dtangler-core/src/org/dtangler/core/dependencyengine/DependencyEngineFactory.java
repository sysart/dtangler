package org.dtangler.core.dependencyengine;

import java.util.List;

import org.dtangler.core.configuration.Arguments;

public class DependencyEngineFactory {

	private final DependencyEnginePool dependencyEnginePool;
	
	public DependencyEngineFactory() {
		dependencyEnginePool = new DependencyEnginePool();
	}

	public DependencyEngineFactory(DependencyEngine... dependencyEngines) {
		dependencyEnginePool = new DependencyEnginePool(dependencyEngines);
	}

	public DependencyEngine getDependencyEngine(Arguments arguments) {
		return dependencyEnginePool.get(arguments);
	}

	public DependencyEngine getDependencyEngine(String dependencyEngineId) {
		return dependencyEnginePool.get(dependencyEngineId);
	}

	public void addDependencyEngine(String dependencyEngineId, DependencyEngine dependencyEngine) {
		dependencyEnginePool.add(dependencyEngineId, dependencyEngine);
	}

	public List<String> getDependencyEngineIds() {
		return dependencyEnginePool.getDependencyEngineIds();
	}

}
