// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.validation;

import static com.agical.bumblebee.junit4.Storage.store;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.analysis.ChildViolation;
import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.cycleanalysis.DependencyCycle;
import org.dtangler.core.cycleanalysis.TestDependencyCycle;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.ruleanalysis.Rule;
import org.dtangler.core.testutil.dependenciesbuilder.DependenciesBuilder;
import org.dtangler.core.testutil.dependenciesbuilder.DependencyGraphBuilder;
import org.dtangler.core.testutil.ruleanalysis.MockRule;
import org.dtangler.core.testutil.ruleanalysis.MockRuleViolation;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Test;

public class ReturnValuesAcceptanceTest {

	/*!!
	 #{set_header 'Return values and error messages'}
	 
	 If any of the specified rules are broken in the analysed code, 
	 dtangler will produce a rule violation message and exit with a non-zero value.
		
	 Cycles are always reported. They cause a non-zero exit value by default. 
	 */

	private final String rulesKey = CommandLineParser
			.getKeyString(ParserConstants.RULES_KEY);
	private final String groupsKey = CommandLineParser
			.getKeyString(ParserConstants.GROUPS_KEY);

	@Test
	public void cyclesFoundWhenCyclesAreAllowed() {
		/*!
		 If cycles are allowed, cycles are reported but they do not cause an error.
		 
		 For example, a possible output could be: 
		 >>>>
		 #{example}	
		 <<<<
		 */
		String item1 = "Dependable1";
		String item2 = "Dependable2";
		Dependencies cyclic = createWithCycles(item1, item2);
		DependencyCycle expected = new TestDependencyCycle(Arrays.asList(item1,
				item2, item1));

		store("example", expected.asText());

		Arguments arguments = new Arguments();
		arguments.setCyclesAllowed(true);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(cyclic);

		assertTrue(analysisResult.isValid());
		assertTrue(analysisResult.getAllViolations().contains(expected));
	}

	@Test
	public void cyclesFoundWhenCyclesAreDisallowed() {
		/*!
		Cycles are reported, and dtangler's exits with a non-zero value.
		 */
		String item1 = "Part1";
		String item2 = "Part2";
		Dependencies cyclic = createWithCycles(item1, item2);
		DependencyCycle expected = new TestDependencyCycle(Arrays.asList(item1,
				item2, item1));

		Arguments arguments = new Arguments();
		arguments.setCyclesAllowed(false);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(cyclic);

		assertFalse(analysisResult.isValid());
		assertTrue(analysisResult.getAllViolations().contains(expected));
	}

	private Dependencies createWithCycles(String item1, String item2) {
		DependencyGraphBuilder builder = new DependencyGraphBuilder();
		builder.add(item1).dependsOn(item2).dependsOn(item1);
		return new DependenciesBuilder().addDependencies(builder
				.getDependencies());
	}

	@Test
	public void noCyclesFound() {
		/*!
		 If cycles are not found, no cycle violations are reported and the exit
		 value is not affected.
		 */
		Dependencies noCycles = createSimpleDependency("basic1", "basic2");
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				new Arguments()).analyze(noCycles);

		assertTrue(analysisResult.isValid());
		assertTrue(analysisResult.getAllViolations().isEmpty());
	}

	@Test
	public void noRuleViolations() {
		/*!
		 If no rule violations are found, the exit value is not affected. 
		 */
		Dependencies simple = createSimpleDependency("A", "B");
		Arguments arguments = new Arguments();
		arguments.setForbiddenDependencies(createMap("B", "A"));
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(simple);
		assertTrue(analysisResult.isValid());
		assertTrue(analysisResult.getAllViolations().isEmpty());
	}

	@Test
	public void ruleViolationsBetweenSingleItems() {
		/*!
		 When a forbidden dependency has been found, the broken rule is 
		 reported in the output.
		 		 
		 For example, if the rule 
		 >>>>
		 #{simpleRule}
		 <<<<
		 is broken, the following violation is produced:		 
		 >>>>
		 #{ruleViolation1}
		 <<<<
		 */

		Dependencies simple = createSimpleDependency("A", "B");
		String[] rule = { rulesKey + "A " + ParserConstants.CANNOT_DEPEND
				+ " B" };
		Arguments arguments = new ArgumentBuilder().build(rule);
		arguments.setForbiddenDependencies(createMap("A", "B"));
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(simple);

		assertFalse(analysisResult.isValid());
		assertFalse(analysisResult.getAllViolations().isEmpty());

		store("simpleRule", rule[0]);
		store("ruleViolation1", analysisResult.getAllViolations().iterator()
				.next().asText());
	}

	@Test
	public void ruleViolationsBetweenGroups() {
		/*!
		 Rules constructed with groups are handled in the same way as 
		 rules with single items.
		 
		 Consider the following arguments:		 
		 >>>>
		 #{groupDefs}
		 #{ruleWith2Groups}
		 <<<<
		 
		 If there is a dependency between any members of the groups, the output will be:
		 >>>>
		 #{ruleViolation2}
		 <<<<
		 */

		Dependencies simple = createSimpleDependency("org.util",
				"org.application");
		String[] args = {
				groupsKey + "Util " + ParserConstants.CONTAINS + " *util*"
						+ ParserConstants.BIG_SEPARATOR + "App "
						+ ParserConstants.CONTAINS + " *app*",
				rulesKey + ParserConstants.GROUP_IDENTIFIER + "Util "
						+ ParserConstants.CANNOT_DEPEND + " "
						+ ParserConstants.GROUP_IDENTIFIER + "App" };
		Arguments arguments = new ArgumentBuilder().build(args);

		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(simple);
		MockRuleViolation expectedViolation = new MockRuleViolation("org.util",
				"org.application", new MockRule(Rule.Type.cannotDepend,
						arguments.getGroups().get("Util"), arguments
								.getGroups().get("App")));

		assertFalse(analysisResult.isValid());
		assertTrue(analysisResult.getAllViolations()
				.contains(expectedViolation));

		store("ruleViolation2", expectedViolation.asText());
		store("groupDefs", args[0]);
		store("ruleWith2Groups", args[1]);
	}

	private Dependencies createSimpleDependency(String dependant,
			String dependee) {
		DependencyGraphBuilder builder = new DependencyGraphBuilder();
		builder.add(dependant).dependsOn(dependee);
		return new DependenciesBuilder().addDependencies(builder
				.getDependencies());
	}

	@Test
	public void ruleViolationsAndExcludedItems() {
		/*!
		 Dependencies to and from items that were *excluded* from groups
		 do not affect the outcome of group rules.  
		 
		 For example, with dependencies =A->C->B=, the following arguments
		 won't generate a violation because C has been excluded from the group:		 
		 >>>>
		 #{groupWithExcluded}
		 #{ruleForGroupWithExcluded}
		 <<<<
		 */
		Dependencies deps = createTransitiveDeps("A", "C", "B");
		String[] args = {
				groupsKey + "All " + ParserConstants.CONTAINS + " * "
						+ ParserConstants.DOES_NOT_CONTAIN + " C",
				rulesKey + ParserConstants.GROUP_IDENTIFIER + "All "
						+ ParserConstants.CANNOT_DEPEND + " "
						+ ParserConstants.GROUP_IDENTIFIER + "All" };
		store("groupWithExcluded", args[0]);
		store("ruleForGroupWithExcluded", args[1]);

		Arguments arguments = new ArgumentBuilder().build(args);
		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(deps);

		assertTrue(analysisResult.isValid());
		assertTrue(analysisResult.getAllViolations().isEmpty());
	}

	@Test
	public void usingGroupsAndSingleItemsTogether() {
		/*!
		 You can combine single items and group items freely in rule definitions:
		 
		 >>>>
		 #{groupsForCombinations}
		 #{combinationRule1}		 
		 <<<<
		 
		 With dependencies =eg.foo->eg.bar->eg.bay=, the resulting violation output 
		 would look like this:
		 
		 >>>>
		 #{combinationResult}
		 <<<<
		 */
		String group = groupsKey + "Foo " + ParserConstants.CONTAINS
				+ " eg.foo*" + ParserConstants.BIG_SEPARATOR + " Bay "
				+ ParserConstants.CONTAINS + " eg.bay*";
		String rules = rulesKey + ParserConstants.GROUP_IDENTIFIER + "Foo "
				+ ParserConstants.CANNOT_DEPEND + " eg.bar"
				+ ParserConstants.BIG_SEPARATOR + " eg.bar "
				+ ParserConstants.CANNOT_DEPEND + " "
				+ ParserConstants.GROUP_IDENTIFIER + "Bay";
		Arguments arguments = new ArgumentBuilder().build(new String[] { group,
				rules });
		Dependencies deps = createTransitiveDeps("eg.foo", "eg.bar", "eg.bay");

		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				arguments).analyze(deps);
		Set<Violation> actualViolations = analysisResult.getAllViolations();
		MockRuleViolation fooExpected = new MockRuleViolation("eg.foo",
				"eg.bar", new MockRule(Rule.Type.cannotDepend, arguments
						.getGroups().get("Foo"), "eg.bar"));
		MockRuleViolation barExpected = new MockRuleViolation("eg.bar",
				"eg.bay", new MockRule(Rule.Type.cannotDepend, "eg.bar",
						arguments.getGroups().get("Bay")));

		assertFalse(analysisResult.isValid());
		assertEquals(2, actualViolations.size());
		assertTrue(actualViolations.contains(fooExpected));
		assertTrue(actualViolations.contains(barExpected));

		Iterator<Violation> iterator = analysisResult.getAllViolations()
				.iterator();
		store("combinationRule1", rules);
		store("groupsForCombinations", group);
		store("combinationResult", iterator.next().asText() + "\n"
				+ iterator.next().asText());
	}

	private Dependencies createTransitiveDeps(String item1, String item2,
			String item3) {
		DependencyGraphBuilder builder = new DependencyGraphBuilder();
		builder.add(item1).dependsOn(item2).dependsOn(item3);
		return new DependenciesBuilder().addDependencies(builder
				.getDependencies());
	}

	@Test
	public void violationsOnLowerScope() {
		/*!
		 A notification is printed if an item contains violations 
		 on any of its more detailed scopes. 
		 
		 For example, a cycle between two classes, *#{class1}* and *#{class2}*, 
		 both inside the same Java package *#{package1}*, will produce a 
		 notification of this type on package scope:
		 >>>>
		 #{violationOnLowerScope}
		 <<<<
		 
		 */
		Dependencies dependencies = createCycleOnLowerScope("animals", "Cat",
				"Dog");

		ChildViolation expected = new ChildViolation(new TestDependable(
				"animals", JavaScope.packages), new TestDependencyCycle(Arrays
				.asList("Cat", "Dog", "Cat")));

		AnalysisResult analysisResult = new ConfigurableDependencyAnalyzer(
				new Arguments()).analyze(dependencies);
		Set<Violation> childViolations = analysisResult.getAllChildViolations();

		assertFalse(analysisResult.isValid());
		assertFalse(childViolations.isEmpty());
		assertTrue(childViolations.contains(expected));
		store("violationOnLowerScope", expected.toString());
		store("class1", "Cat");
		store("class2", "Dog");
		store("package1", "animals");
	}

	private Dependencies createCycleOnLowerScope(String parentName,
			String cycleParticipant1Name, String cycleParticipant2Name) {
		Dependable parent = new TestDependable(parentName, JavaScope.packages);
		Dependable part1 = new TestDependable(cycleParticipant1Name,
				JavaScope.classes);
		Dependable part2 = new TestDependable(cycleParticipant2Name,
				JavaScope.classes);

		Dependencies dependencies = new Dependencies();
		dependencies.addDependencies(part1, createMap(part2));
		dependencies.addDependencies(part2, createMap(part1));
		dependencies.addChild(parent, part1);
		dependencies.addChild(parent, part2);

		return dependencies;
	}

	private Map<String, Set<String>> createMap(String key, String... values) {
		Map<String, Set<String>> result = new HashMap();
		result.put(key, new HashSet(Arrays.asList(values)));
		return result;
	}

	private Map<Dependable, Integer> createMap(Dependable... items) {
		Map<Dependable, Integer> result = new HashMap();
		for (Dependable item : items) {
			result.put(item, 1);
		}
		return result;
	}
}
