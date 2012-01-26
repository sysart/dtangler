//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.rulesselector.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.swingui.groupselector.MockGroupSelector;
import org.dtangler.swingui.rulememberselector.MockRuleMemberSelector;
import org.dtangler.swingui.rulesselector.RulesSelector;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;

public class RulesSelectorFeatureTest {

	private RulesSelector selector;
	private MockWindowManager windowManager;
	private MockRuleMemberSelector ruleMemberSelector;
	private Arguments arguments;
	private MockGroupSelector groupSelector;

	@Before
	public void setUp() {
		windowManager = new MockWindowManager();
		ruleMemberSelector = new MockRuleMemberSelector();
		Map<String, Set<String>> forbiddenDeps = new HashMap();
		forbiddenDeps.put("rule1", new HashSet(Arrays.asList("foo")));
		forbiddenDeps.put("rule2", new HashSet(Arrays.asList("foo", "bar")));
		arguments = new Arguments();
		arguments.setForbiddenDependencies(forbiddenDeps);
		groupSelector = new MockGroupSelector();
		selector = new RulesSelectorImpl(ruleMemberSelector, windowManager,
				groupSelector);
	}

	@Test
	public void testAddAndRemoveForbiddenRules() {
		selector.selectRules(new Arguments());
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		testAddAndRemoveRules(view.forbiddenDeps);
	}

	@Test
	public void testAddAndRemoveAllowedRules() {
		selector.selectRules(new Arguments());
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		testAddAndRemoveRules(view.allowedDeps);
	}

	private void testAddAndRemoveRules(RuleSelectorViewDriver view) {
		ruleMemberSelector.setNextValue("foo");
		view.addRuleButton.click();

		ruleMemberSelector.setNextValue("bar");
		view.addRuleButton.click();

		ruleMemberSelector.setNextValue("bay");
		view.addRuleButton.click();

		assertTrue(view.rules.contentEquals(
				new String[] { "bar", "bay", "foo" }).isTrue());

		view.rules.selectIndices(new int[] { 0, 2 });
		view.removeRulesButton.click();

		assertTrue(view.rules.contentEquals(new String[] { "bay" }).isTrue());

		ruleMemberSelector.setNextValue(null);
		view.addRuleButton.click();
		assertTrue(view.rules.contentEquals(new String[] { "bay" }).isTrue());
		ruleMemberSelector.setNextValue("bay");
		view.addRuleButton.click();
		assertTrue(view.rules.contentEquals(new String[] { "bay" }).isTrue());
	}

	@Test
	public void testAddAndRemoveForbiddenRuleItems() {
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		testAddAndRemoveRuleItems(view.forbiddenDeps);
	}

	@Test
	public void testAddAndRemoveAllowedRuleItems() {
		arguments.setAllowedDependencies(arguments.getForbiddenDependencies());
		arguments.setForbiddenDependencies(Collections.EMPTY_MAP);
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		testAddAndRemoveRuleItems(view.allowedDeps);
	}

	private void testAddAndRemoveRuleItems(RuleSelectorViewDriver view) {
		view.rules.select("rule2");

		assertTrue(view.ruleItems.contentEquals(new String[] { "bar", "foo" })
				.isTrue());
		ruleMemberSelector.setNextValue("bay");
		view.addRuleItemButton.click();

		assertTrue(view.ruleItems.contentEquals(
				new String[] { "bar", "bay", "foo" }).isTrue());

		view.ruleItems.selectIndices(new int[] { 0, 2 });
		view.removeRuleItemsButton.click();

		assertTrue(view.ruleItems.contentEquals(new String[] { "bay" })
				.isTrue());

		ruleMemberSelector.setNextValue(null);
		view.addRuleItemButton.click();
		assertTrue(view.ruleItems.contentEquals(new String[] { "bay" })
				.isTrue());
		ruleMemberSelector.setNextValue("bay");
		view.addRuleItemButton.click();
		assertTrue(view.ruleItems.contentEquals(new String[] { "bay" })
				.isTrue());
	}

	@Test
	public void testRemoveForbiddenRuleButtonIsOnlyEnabledWhenRulesSelected() {
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		testRemoveRuleButtonIsOnlyEnabledWhenRulesSelected(view.forbiddenDeps);
	}

	@Test
	public void testRemoveAllowedRuleButtonIsOnlyEnabledWhenRulesSelected() {
		arguments.setAllowedDependencies(arguments.getForbiddenDependencies());
		arguments.setForbiddenDependencies(Collections.EMPTY_MAP);
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		testRemoveRuleButtonIsOnlyEnabledWhenRulesSelected(view.allowedDeps);
	}

	private void testRemoveRuleButtonIsOnlyEnabledWhenRulesSelected(
			RuleSelectorViewDriver view) {

		assertFalse(view.removeRulesButton.isEnabled().isTrue());
		view.rules.select("rule1");
		assertTrue(view.removeRulesButton.isEnabled().isTrue());
		view.rules.select(new String[] { "rule1", "rule2" });
		assertTrue(view.removeRulesButton.isEnabled().isTrue());
	}

	@Test
	public void testOk() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				RulesSelectorViewDriver view = new RulesSelectorViewDriver(
						windowManager.getLastShownView());
				ruleMemberSelector.setNextValue("newForbiddenRule");
				view.forbiddenDeps.addRuleButton.click();
				ruleMemberSelector.setNextValue("newAllowedRule");
				view.allowedDeps.addRuleButton.click();
				groupSelector.setNextResult(new Group("newGroup",
						Collections.EMPTY_SET));
				view.newGroupButton.click();
				view.okButton.click();
			}
		});
		Arguments result = selector.selectRules(arguments);
		assertNotNull(result);
		assertNotSame(arguments, result);

		assertRulesContains(arguments.getForbiddenDependencies(), result
				.getForbiddenDependencies());
		assertTrue(result.getForbiddenDependencies().containsKey(
				"newForbiddenRule"));
		assertRulesContains(arguments.getAllowedDependencies(), result
				.getAllowedDependencies());
		assertTrue(result.getAllowedDependencies()
				.containsKey("newAllowedRule"));
		assertTrue(result.getGroups().keySet().containsAll(
				arguments.getGroups().keySet()));
		assertTrue(result.getGroups().containsKey("newGroup"));
		assertNull("window was closed", windowManager.getLastShownView());
	}

	private void assertRulesContains(Map<String, Set<String>> expected,
			Map<String, Set<String>> actual) {
		for (Entry<String, Set<String>> entry : expected.entrySet())
			assertEquals(entry.getValue(), actual.get(entry.getKey()));
	}

	@Test
	public void testCancel() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				RulesSelectorViewDriver view = new RulesSelectorViewDriver(
						windowManager.getLastShownView());
				view.cancelButton.click();
			}
		});
		assertNull(selector.selectRules(arguments));
		assertNull("window was closed", windowManager.getLastShownView());
	}

	@Test
	public void testGetGroupNames() {
		arguments.setGroups(createGroups("Foo", "Bar"));

		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());

		assertTrue(view.groups.contentEquals(new String[] { "Bar", "Foo" })
				.isTrue());
	}

	private Map<String, Group> createGroups(String... names) {
		Map<String, Group> groups = new HashMap();
		for (String name : names)
			groups.put(name, new Group(name, Collections.EMPTY_SET));
		return groups;
	}

	@Test
	public void testGroupNamesAreProvidedToRuleMemberSelector() {
		arguments.setGroups(createGroups("bar", "bay", "foo"));

		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());

		view.forbiddenDeps.addRuleButton.click();

		assertEquals(Arrays.asList("bar", "bay", "foo"), ruleMemberSelector
				.getLastUsedGroupNames());
	}

	@Test
	public void testAddGroup() {
		arguments.setGroups(createGroups("Foo", "Bar"));
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());

		groupSelector.setNextResult(null);
		view.newGroupButton.click();
		assertTrue(view.groups.contentEquals(new String[] { "Bar", "Foo" })
				.isTrue());

		groupSelector.setNextResult(new Group("Coco", Collections.EMPTY_SET));
		view.newGroupButton.click();
		assertTrue(view.groups.contentEquals(
				new String[] { "Bar", "Coco", "Foo" }).isTrue());
	}

	@Test
	public void testRemoveGroup() {
		arguments
				.setGroups(createGroups("Group1", "Group2", "Group3", "Group4"));
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		view.groups.select("Group2");
		view.removeGroupButton.click();
		assertTrue(view.groups.contentEquals(
				new String[] { "Group1", "Group3", "Group4" }).isTrue());

		view.groups.select(new String[] { "Group1", "Group4" });
		view.removeGroupButton.click();
		assertTrue(view.groups.contentEquals(new String[] { "Group3" })
				.isTrue());
	}

	@Test
	public void testEditGroup() {
		Group groupToEdit = new Group("MyGroup", Collections.EMPTY_SET);

		arguments.setGroups(Collections.singletonMap(groupToEdit.getName(),
				groupToEdit));
		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());

		groupSelector.setNextResult(null);
		view.groups.select("MyGroup");
		view.editGroupButton.click();

		assertTrue(view.groups.contentEquals(new String[] { "MyGroup" })
				.isTrue());
		assertSame(groupToEdit, groupSelector.lastEditedGroup());

		groupSelector.setNextResult(new Group("Edited Group",
				Collections.EMPTY_SET));

		view.groups.select("MyGroup");
		view.editGroupButton.click();

		assertTrue(view.groups.contentEquals(new String[] { "Edited Group" })
				.isTrue());
		assertSame(groupToEdit, groupSelector.lastEditedGroup());
	}

	@Test
	public void testRemoveGroupIsOnlyEnabledWhenOneOrMoreGroupsSelected() {
		arguments.setGroups(createGroups("bar", "bay", "foo"));

		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		view.groups.selectIndex(-1);
		assertFalse(view.removeGroupButton.isEnabled().isTrue());

		view.groups.select("bar");
		assertTrue(view.removeGroupButton.isEnabled().isTrue());

		view.groups.select(new String[] { "bar", "bay" });
		assertTrue(view.removeGroupButton.isEnabled().isTrue());
	}

	@Test
	public void testEditGroupIsOnlyEnabledWhenOneGroupSelected() {
		arguments.setGroups(createGroups("bar", "bay", "foo"));

		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());
		view.groups.selectIndex(-1);
		assertFalse(view.editGroupButton.isEnabled().isTrue());

		view.groups.select("bar");
		assertTrue(view.editGroupButton.isEnabled().isTrue());

		view.groups.select(new String[] { "bar", "bay" });
		assertFalse(view.editGroupButton.isEnabled().isTrue());
	}

	@Test
	public void testGroupNameChangeAlsoUpdatesRules() {
		Set<String> ruleItems = new HashSet(Arrays.asList("@group1", "@group2"));
		arguments.setForbiddenDependencies(Collections.singletonMap("@group1",
				ruleItems));
		arguments.setAllowedDependencies(Collections.singletonMap("@group1",
				ruleItems));
		arguments.setGroups(createGroups("group1", "group2"));

		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());

		groupSelector.setNextResult(new Group("EditedGroup",
				Collections.EMPTY_SET));

		view.groups.select("group1");
		view.editGroupButton.click();

		assertTrue(view.groups.contentEquals(
				new String[] { "EditedGroup", "group2" }).isTrue());
		assertTrue(view.forbiddenDeps.rules.contentEquals(
				new String[] { "@EditedGroup" }).isTrue());
		view.forbiddenDeps.rules.select("@EditedGroup");
		assertTrue(view.forbiddenDeps.ruleItems.contentEquals(
				new String[] { "@EditedGroup", "@group2" }).isTrue());
		assertTrue(view.allowedDeps.rules.contentEquals(
				new String[] { "@EditedGroup" }).isTrue());
		view.allowedDeps.rules.select("@EditedGroup");
		assertTrue(view.allowedDeps.ruleItems.contentEquals(
				new String[] { "@EditedGroup", "@group2" }).isTrue());

	}

	@Test
	public void testRemoveGroupAlsoremovesGroupFromRules() {
		Set<String> ruleItems = new HashSet(Arrays.asList("@group1", "@group2"));
		Map<String, Set<String>> forbiddenDeps = new HashMap();
		forbiddenDeps.put("@group1", ruleItems);
		forbiddenDeps.put("@group2", ruleItems);
		arguments.setForbiddenDependencies(forbiddenDeps);
		Map<String, Set<String>> allowedDeps = new HashMap();
		allowedDeps.put("@group1", ruleItems);
		allowedDeps.put("@group2", ruleItems);
		arguments.setAllowedDependencies(allowedDeps);

		arguments.setGroups(createGroups("group1", "group2"));

		selector.selectRules(arguments);
		RulesSelectorViewDriver view = new RulesSelectorViewDriver(
				windowManager.getLastShownView());

		view.groups.select("group1");
		view.removeGroupButton.click();

		assertTrue(view.groups.contentEquals(new String[] { "group2" })
				.isTrue());
		assertTrue(view.forbiddenDeps.rules.contentEquals(
				new String[] { "@group2" }).isTrue());
		view.forbiddenDeps.rules.select("@group2");
		assertTrue(view.forbiddenDeps.ruleItems.contentEquals(
				new String[] { "@group2" }).isTrue());
		assertTrue(view.allowedDeps.rules.contentEquals(
				new String[] { "@group2" }).isTrue());
		view.allowedDeps.rules.select("@group2");
		assertTrue(view.allowedDeps.ruleItems.contentEquals(
				new String[] { "@group2" }).isTrue());

	}
}
