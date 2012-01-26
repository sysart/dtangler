//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.analysis.configurableanalyzer;

import org.dtangler.core.analysis.CompositeAnalyzer;
import org.dtangler.core.analysis.DependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.cycleanalysis.CycleValidator;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.ruleanalysis.ForbiddenDependencyFinder;
import org.dtangler.core.ruleanalysis.RuleCreator;

public class ConfigurableDependencyAnalyzer {

	private DependencyAnalyzer analyzer;

	public ConfigurableDependencyAnalyzer(Arguments arguments) {
		analyzer = buildAnalyzer(arguments);
	}

	private DependencyAnalyzer buildAnalyzer(Arguments args) {
		CompositeAnalyzer analyzer = new CompositeAnalyzer();
		analyzer.add(new CycleValidator(args.getCyclesAllowed()));
		analyzer.add(new ForbiddenDependencyFinder(new RuleCreator(args
				.getForbiddenDependencies(), args.getAllowedDependencies(),
				args.getGroups()).createRules()));
		return analyzer;
	}

	public AnalysisResult analyze(Dependencies dependencies) {
		analyzer.analyze(dependencies);
		AnalysisResult analysisResult = new AnalysisResult(analyzer
				.getViolations(), analyzer.getChildViolations(), analyzer
				.isValidResult());
		return analysisResult;
	}
}