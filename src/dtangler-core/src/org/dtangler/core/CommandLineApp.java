//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core;

import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.textui.DSMWriter;
import org.dtangler.core.textui.ViolationWriter;
import org.dtangler.core.textui.Writer;

public class CommandLineApp {

	private final Writer writer;

	public CommandLineApp(Writer writer) {
		this.writer = writer;
	}

	public boolean run(String[] args) {
		if (args.length == 0)
			throw new MissingArgumentsException();
		return run(new ArgumentBuilder().build(args));
	}

	public boolean run(Arguments arguments) {
		DependencyEngine engine = new DependencyEngineFactory()
				.getDependencyEngine(arguments);
		Dependencies dependencies = engine.getDependencies(arguments);
		DependencyGraph dependencyGraph = dependencies
				.getDependencyGraph();
		AnalysisResult analysisResult = getAnalysisResult(arguments,
				dependencies);

		printDsm(dependencyGraph, analysisResult);
		return analysisResult.isValid();
	}

	private AnalysisResult getAnalysisResult(Arguments arguments,
			Dependencies dependencies) {
		return new ConfigurableDependencyAnalyzer(arguments)
				.analyze(dependencies);
	}

	private void printDsm(DependencyGraph dependencies,
			AnalysisResult analysisResult) {
		DSMWriter textUI = new DSMWriter(writer);
		textUI
				.printDsm(new DsmEngine(dependencies).createDsm(),
						analysisResult);
		ViolationWriter violationWriter = new ViolationWriter(writer);
		violationWriter.printViolations(analysisResult
				.getViolations(dependencies.getAllItems()));
	}
}
