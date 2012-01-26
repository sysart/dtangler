//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.acceptancetests.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysis.ChildViolation;
import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.dependencies.TestScope;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.ruleanalysis.Rule;
import org.dtangler.core.ruleanalysis.RuleMember;
import org.dtangler.core.ruleanalysis.RuleViolation;
import org.dtangler.core.ruleanalysis.SingleRuleMember;
import org.junit.Test;

public class ViolationsAndScopesTest {

	private final String rulesKey = CommandLineParser
			.getKeyString(ParserConstants.RULES_KEY);

	private String fooName = "eg.foo";
	private String barName = "eg.bar";

	private Dependable packageFoo = new TestDependable(fooName,
			TestScope.scope2);
	private Dependable packageBar = new TestDependable(barName,
			TestScope.scope2);

	private String fooClass1Name = "eg.foo.Class1";
	private String fooClass2Name = "eg.foo.Class2";
	private String barClass3Name = "eg.bar.Class3";
	private String barClass4Name = "eg.bar.Class4";
	private String barClass5Name = "eg.bar.Class5";

	private Dependable fooClass1 = new TestDependable(fooClass1Name,
			TestScope.scope3);
	private Dependable fooClass2 = new TestDependable(fooClass2Name,
			TestScope.scope3);
	private Dependable barClass3 = new TestDependable(barClass3Name,
			TestScope.scope3);
	private Dependable barClass4 = new TestDependable(barClass4Name,
			TestScope.scope3);
	private Dependable barClass5 = new TestDependable(barClass5Name,
			TestScope.scope3);

	private Dependencies createDependencies() {
		Dependencies dependencies = new Dependencies();
		dependencies.addChild(packageFoo, fooClass1);
		dependencies.addChild(packageFoo, fooClass2);
		dependencies.addChild(packageBar, barClass3);
		dependencies.addChild(packageBar, barClass4);
		dependencies.addChild(packageBar, barClass5);
		return dependencies;
	}

	@Test
	public void testPackagePackageRules() {

		Dependencies dependencies = createDependencies();
		dependencies.addDependencies(packageFoo, createMap(packageBar));
		dependencies.addDependencies(fooClass1, createMap(barClass3));

		String[] rules = { rulesKey + fooName + " "
				+ ParserConstants.CANNOT_DEPEND + " " + barName };
		Arguments arguments = new ArgumentBuilder().build(rules);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(dependencies);
		assertEquals(2, analysisResult.getAllViolations().size());
		assertFalse(analysisResult.isValid());
	}

	@Test
	public void testPackageClassRules() {
		Dependencies dependencies = createDependencies();
		dependencies.addDependencies(packageFoo, createMap(packageBar));
		dependencies
				.addDependencies(fooClass1, createMap(barClass3, barClass5));

		String[] rules = { rulesKey + fooName + " "
				+ ParserConstants.CANNOT_DEPEND + barClass3Name
				+ ParserConstants.SMALL_SEPARATOR + barClass4Name };

		Arguments args = new ArgumentBuilder().build(rules);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(args)
				.analyze(dependencies);

		assertEquals(1, analysisResult.getAllViolations().size());
		assertFalse(analysisResult.isValid());
	}

	@Test
	public void testClassPackageRules() {
		Dependencies dependencies = createDependencies();
		dependencies.addDependencies(packageFoo, createMap(packageBar));
		dependencies
				.addDependencies(fooClass1, createMap(fooClass2, barClass5));
		dependencies.addDependencies(fooClass2, createMap(barClass3, barClass4,
				barClass5));

		String[] rules = { rulesKey + fooClass1Name + " "
				+ ParserConstants.CANNOT_DEPEND + " " + barName };

		Arguments args = new ArgumentBuilder().build(rules);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(args)
				.analyze(dependencies);

		assertEquals(1, analysisResult.getAllViolations().size());
		assertFalse(analysisResult.isValid());
	}

	@Test
	public void testClassClassRules() {
		Dependencies dependencies = createDependencies();
		dependencies.addDependencies(packageFoo, createMap(packageBar));
		dependencies.addDependencies(fooClass1, createMap(barClass3));
		dependencies.addDependencies(fooClass2, createMap(barClass5));
		dependencies.addDependencies(barClass4, createMap(barClass5));

		String[] rules = { rulesKey + fooClass2Name + " "
				+ ParserConstants.CANNOT_DEPEND + " " + barClass5Name };

		Arguments args = new ArgumentBuilder().build(rules);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(args)
				.analyze(dependencies);

		assertEquals(1, analysisResult.getAllViolations().size());
		assertFalse(analysisResult.isValid());
	}

	@Test
	public void testAllViolationsIncludeChildViolations() {

		String[] rules = { rulesKey + fooClass1Name + " "
				+ ParserConstants.CANNOT_DEPEND + " " + fooClass2Name
				+ ParserConstants.BIG_SEPARATOR + fooClass2Name + " "
				+ ParserConstants.CANNOT_DEPEND + " " + barClass3Name };

		Arguments args = new ArgumentBuilder().build(rules);
		Dependencies dependencies = createDependencies();
		dependencies.addDependencies(fooClass1, createMap(fooClass2));
		dependencies.addDependencies(fooClass2, createMap(barClass3));
		dependencies.addDependencies(packageFoo, createMap(packageBar));
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(args)
				.analyze(dependencies);

		Set<Violation> allViolations = analysisResult.getAllViolations();

		assertEquals(3, allViolations.size());
		assertTrue(allViolations.contains(new ChildViolation(packageFoo,
				createRuleViolation(fooClass1, fooClass2))));
		assertFalse(analysisResult.isValid());
	}

	private RuleViolation createRuleViolation(Dependable dependant,
			Dependable dependee) {
		Dependency dependency = new Dependency(dependant, dependee);
		Rule rule = createRule(dependant, dependee);
		return new RuleViolation(dependency, rule);
	}

	private Rule createRule(Dependable left, Dependable... right) {
		Set<RuleMember> rightSide = new HashSet();
		for (Dependable dep : right) {
			rightSide.add(new SingleRuleMember(dep.getDisplayName()));
		}
		return new Rule(Rule.Type.cannotDepend, new SingleRuleMember(left
				.getDisplayName()), rightSide);
	}

	protected Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}
}