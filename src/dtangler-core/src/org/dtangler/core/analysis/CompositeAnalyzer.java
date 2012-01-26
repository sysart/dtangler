//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.analysis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;

public class CompositeAnalyzer extends DependencyAnalyzer {

	private final Set<DependencyAnalyzer> analyzers = new HashSet();
	private boolean isValid = false;

	public boolean isValidResult() {
		return isValid;
	}

	public void add(DependencyAnalyzer analyzer) {
		analyzers.add(analyzer);
	}

	public void doAnalyze(Dependencies dependencies) {
		isValid = true;
		for (DependencyAnalyzer analyzer : analyzers) {
			analyzer.doAnalyze(dependencies);
			addViolations(analyzer.getViolations());
			if (!analyzer.isValidResult())
				isValid = false;
		}
	}

	private void addViolations(Map<Dependency, Set<Violation>> newViolations) {
		for (Entry<Dependency, Set<Violation>> entry : newViolations.entrySet())
			addViolations(entry.getKey(), entry.getValue());
	}
}
