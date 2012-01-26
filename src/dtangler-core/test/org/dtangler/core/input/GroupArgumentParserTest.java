// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;
import org.junit.Test;

public class GroupArgumentParserTest {

	private final String group1 = "foo";
	private final String group1_member1 = "eg.foo.aa";
	private final String group1_member2 = "eg.foo.bb";
	private final String group1_member3 = "eg.foo.cc";

	private final String group2 = "bar";
	private final String group2_member1 = "eg.bar.*";
	private final String group2_excluded1 = "eg.bar.aa";
	private final String group2_excluded2 = "eg.bar.x";

	@Test
	public void testParseGroups() {

		String value1 = group1 + " " + ParserConstants.CONTAINS + " "
				+ group1_member1 + ParserConstants.SMALL_SEPARATOR
				+ group1_member2 + ParserConstants.SMALL_SEPARATOR
				+ group1_member3;

		String value2 = group2 + " " + ParserConstants.CONTAINS + " "
				+ group2_member1;

		Arguments argument = parse(ParserConstants.GROUPS_KEY, value1
				+ ParserConstants.BIG_SEPARATOR + value2);

		assertEquals(2, argument.getGroups().size());

		testGroup(group1, 3, argument, new HashSet(Arrays.asList(
				group1_member1, group1_member2, group1_member3)),
				Collections.EMPTY_SET);
		testGroup(group2, 1, argument, new HashSet(Arrays
				.asList(group2_member1)), Collections.EMPTY_SET);
	}

	@Test
	public void testParseGroupsWithExcludedItems() {
		String value1 = group2 + " " + ParserConstants.CONTAINS + " "
				+ group2_member1 + " " + ParserConstants.DOES_NOT_CONTAIN + " "
				+ group2_excluded1;

		String value2 = group2 + " " + ParserConstants.CONTAINS + " "
				+ group2_member1 + " " + ParserConstants.DOES_NOT_CONTAIN + " "
				+ group2_excluded1 + ParserConstants.SMALL_SEPARATOR
				+ group2_excluded2;

		Arguments argument = parse(ParserConstants.GROUPS_KEY, value1);
		assertEquals(1, argument.getGroups().size());
		testGroup(group2, 1, argument, new HashSet(Arrays
				.asList(group2_member1)), new HashSet(Arrays
				.asList(group2_excluded1)));

		argument = parse(ParserConstants.GROUPS_KEY, value2);
		assertEquals(1, argument.getGroups().size());
		testGroup(group2, 1, argument, new HashSet(Arrays
				.asList(group2_member1)), new HashSet(Arrays.asList(
				group2_excluded1, group2_excluded2)));
	}

	// TODO simplify
	private void testGroup(String groupName, int numberOfGroupItems,
			Arguments argument, Set<String> included, Set<String> excluded) {
		Group group = argument.getGroups().get(groupName);

		assertTrue(argument.getGroups().containsKey(groupName));
		assertEquals(numberOfGroupItems, group.getGroupItems().size());

		for (String item : included) {
			assertTrue(group.getGroupItems().contains(item));
		}
		for (String item : excluded) {
			assertTrue(group.getExcludedItems().contains(item));
		}
	}

	private ArgumentParser parser = new ArgumentParser();

	private Arguments parse(String key, String value) {
		parser.parseArguments(createValues(key, value));
		return parser.getArguments();
	}

	private Map<String, String> createValues(String key, String value) {
		Map<String, String> values = new Hashtable();
		values.put(key, value);
		return values;
	}
}
