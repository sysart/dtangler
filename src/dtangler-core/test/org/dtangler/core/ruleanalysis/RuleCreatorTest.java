//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.ruleanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.exception.DtException;
import org.dtangler.core.testutil.dependenciesbuilder.DependencyGraphBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RuleCreatorTest {
	private Map<String, Set<String>> forbiddenDependencies = new HashMap();
	private Map<String, Set<String>> allowedDependencies = new HashMap();
	private Map<String, Group> groups = new HashMap();

	protected static String item1Name = "c1";
	protected static String item2Name = "c2";
	protected static String item3Name = "c3";
	protected static String item4Name = "c4";
	protected Dependable item1dep;
	protected Dependable item2dep;
	protected Dependable item3dep;
	protected Dependable item4dep;
	protected SingleRuleMember item1;
	protected SingleRuleMember item2;
	protected SingleRuleMember item3;
	protected SingleRuleMember item4;

	protected static String group1Name = "Group 1";
	protected static String group1ID = ParserConstants.GROUP_IDENTIFIER
			+ group1Name;
	protected static String group2Name = "Group 2";
	protected static String group2ID = ParserConstants.GROUP_IDENTIFIER
			+ group2Name;

	protected DependencyGraph deps;

	@Before
	public void setUp() {
		DependencyGraphBuilder builder = new DependencyGraphBuilder();
		builder.add(item1Name).dependsOn(item2Name);
		builder.add(item1Name).dependsOn(item3Name);
		builder.add(item2Name).dependsOn(item3Name);
		builder.add(item4Name).dependsOn(item3Name);

		deps = builder.getDependencies();
		item1dep = deps.getItemByName(item1Name);
		item2dep = deps.getItemByName(item2Name);
		item3dep = deps.getItemByName(item3Name);
		item4dep = deps.getItemByName(item4Name);
		item1 = new SingleRuleMember(item1Name);
		item2 = new SingleRuleMember(item2Name);
		item3 = new SingleRuleMember(item3Name);
		item4 = new SingleRuleMember(item4Name);
	}

	@Test
	public void testRules() {
		addToMap(forbiddenDependencies, item1Name, item2Name, item3Name);
		addToMap(forbiddenDependencies, item2Name, item3Name);
		addToMap(allowedDependencies, item1Name, item3Name);
		addToMap(allowedDependencies, item2Name, item3Name);

		List<Rule> list = createRules();
		assertEquals(4, list.size());

		testRule(item1, list, Rule.Type.cannotDepend, 2, item2, item3);
		testRule(item2, list, Rule.Type.cannotDepend, 1, item3);
		testRule(item1, list, Rule.Type.canDepend, 1, item3);
		testRule(item2, list, Rule.Type.canDepend, 1, item3);
	}

	@Test
	public void testRulesWithGroups() {
		Group g1 = addGroup(group1Name, item1Name, item2Name);
		GroupRuleMember group1 = new GroupRuleMember(g1);
		Group g2 = addGroup(group2Name, item2Name, item3Name);
		GroupRuleMember group2 = new GroupRuleMember(g2);

		addToMap(forbiddenDependencies, group1ID, item3Name);
		addToMap(forbiddenDependencies, group2ID, item3Name);
		addToMap(forbiddenDependencies, item4Name, item3Name);
		addToMap(allowedDependencies, group1ID, item3Name);

		List<Rule> list = createRules();
		assertEquals(4, list.size());
		testRule(group1, list, Rule.Type.cannotDepend, 1, item3);
		testRule(group2, list, Rule.Type.cannotDepend, 1, item3);
		testRule(item4, list, Rule.Type.cannotDepend, 1, item3);
		testRule(group1, list, Rule.Type.canDepend, 1, item3);

	}

	private void testRule(RuleMember item, List<Rule> ruleList,
			Rule.Type ruleType, int size, RuleMember... containedMembers) {
		Rule rule = getRuleFromList(ruleList, item, ruleType);
		assertEquals(ruleType, rule.getType());
		assertEquals(size, rule.getRightSide().size());
		for (RuleMember member : containedMembers) {
			assertTrue(rule.getRightSide().contains(member));
		}
	}

	@Test
	public void testUndefinedGroup() {
		// use group in rule, but don't define it
		addToMap(forbiddenDependencies, group1ID, item1Name);

		try {
			createRules();
		} catch (DtException e) {
			// should catch "undefined group" exception
			return;
		}
		Assert.fail();
	}

	@Test
	public void testNonExisting() {
		String nonExisting = "this.does.not.exist";
		addToMap(forbiddenDependencies, nonExisting, item2Name, item3Name);

		// rule creation should not crash or throw exception if rule definition
		// contains items that do not exist or are not available
		List<Rule> list = createRules();
		assertEquals(1, list.size());
		assertEquals(SingleRuleMember.class, list.get(0).getLeftSide()
				.getClass());
	}

	private List<Rule> createRules() {
		return new RuleCreator(forbiddenDependencies, allowedDependencies,
				groups).createRules();
	}

	private Rule getRuleFromList(List<Rule> list, Object item, Rule.Type type) {
		for (Rule rule : list) {
			if (rule.getType().equals(type) && rule.getLeftSide().equals(item))
				return rule;
		}
		return null;
	}

	protected void addToMap(Map<String, Set<String>> map, String name,
			String... items) {
		Set<String> values = new HashSet();
		for (String item : items)
			values.add(item.trim());
		map.put(name.trim(), values);
	}

	protected Group addGroup(String name, String... items) {
		Set<String> values = new HashSet();
		for (String item : items)
			values.add(item.trim());
		Group group = new Group(name.trim(), values);
		groups.put(name.trim(), group);
		return group;
	}
}
