// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.input;

import static com.agical.bumblebee.junit4.Storage.store;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.input.CommandLineParser;
import org.junit.Test;

public class ConfigurationParsingAcceptanceTest {

	/*!!
	 #{set_header 'Run options: Configuration'}
	 */

	private final String cycleKey = CommandLineParser
			.getKeyString(ParserConstants.CYCLES_ALLOWED_KEY);
	private final String groupsKey = CommandLineParser
			.getKeyString(ParserConstants.GROUPS_KEY);
	private final String groupKey = CommandLineParser
			.getKeyString(ParserConstants.GROUP_KEY);
	private final String rulesKey = CommandLineParser
			.getKeyString(ParserConstants.RULES_KEY);

	@Test
	public void cyclesAreDisallowedByDefault() {
		/*!
		 If the **=#{cycles}=** option is omitted, cycles are treated as errors.
		 */
		store("cycles", ParserConstants.CYCLES_ALLOWED_KEY);

		Arguments arguments = new ArgumentBuilder().build(new String[] {});
		assertFalse("Cycles should be denied by default", arguments
				.getCyclesAllowed());
	}

	@Test
	public void allowingCyclicDependencies() {
		/*!
		#{exclude}
		 */
		String allowed = cycleKey + ParserConstants.VALUE_TRUE;
		store("allowed", allowed);

		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { allowed });
		assertTrue(arguments.getCyclesAllowed());
	}

	@Test
	public void disallowingCyclicDependencies() {

		/*!
		#{exclude}
		 */
		String disallowed = cycleKey + ParserConstants.VALUE_FALSE;
		store("disallowed", disallowed);

		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { disallowed });
		assertFalse(arguments.getCyclesAllowed());
	}

	@Test
	public void specifyGroupsToSimplifyRules() {
		/*! 
		Configuring dependency rules that apply to multiple items is easier when 
		done with groups. You can also use asterisks (*) as wildcards when entering 
		group members. 
		
		Usage:
		>>>>
		#{groupsKey}\<group name\> #{contains} \<member name\>
		<<<<
		
		 - You can enter *multiple members* to a group by separating them with commas. 
		 - You can enter *multiple groups* by separating them with semi-colons.
		
		For example:
		>>>>
		#{groupExample1}
		<<<<
		*Backward compatibility:* the older =#{group}= run option is interchangeable
		with =#{groups}=. 	
		
		 */

		String groupValue = "MyGroup " + ParserConstants.CONTAINS + " foo"
				+ ParserConstants.SMALL_SEPARATOR + " bar"
				+ ParserConstants.BIG_SEPARATOR + " PublicGroup "
				+ ParserConstants.CONTAINS + " *public*";
		String newGroup = groupsKey + groupValue;
		String oldGroup = groupKey + groupValue;

		store("groupsKey", groupsKey);
		store("group", ParserConstants.GROUP_KEY);
		store("groups", ParserConstants.GROUPS_KEY);
		store("contains", ParserConstants.CONTAINS);
		store("groupExample1", newGroup);

		Arguments newArguments = new ArgumentBuilder()
				.build(new String[] { newGroup });
		Arguments oldArguments = new ArgumentBuilder()
				.build(new String[] { oldGroup });

		Map<String, Group> parsedGroups = newArguments.getGroups();

		assertEquals(oldArguments, newArguments);
		assertGroupByName(parsedGroups.get("MyGroup"), Arrays.asList("foo",
				"bar"), Collections.EMPTY_LIST);
		assertGroupByName(parsedGroups.get("PublicGroup"), Arrays
				.asList("*public*"), Collections.EMPTY_LIST);
	}

	@Test
	public void excludeItemsFromGroups() {
		/*!
		 Items can be excluded from a group with the **=#{doesNotContain}=** option.
		 
		 For example:
		 >>>>
		 #{groupExample2}
		 <<<<
		 */
		store("doesNotContain", ParserConstants.DOES_NOT_CONTAIN);

		String group = groupsKey + "Org " + ParserConstants.CONTAINS
				+ " org.* " + ParserConstants.DOES_NOT_CONTAIN
				+ " org.public.*" + ParserConstants.SMALL_SEPARATOR
				+ "org.util.*";
		store("groupExample2", group);

		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { group });
		assertGroupByName(arguments.getGroups().get("Org"), Arrays
				.asList("org.*"), Arrays.asList("org.public.*", "org.util.*"));
	}

	private void assertGroupByName(Group group, List<String> expectedIncl,
			List<String> expectedExcl) {
		assertNotNull(group);
		assertMembers(group.getGroupItems(), expectedIncl);
		assertMembers(group.getExcludedItems(), expectedExcl);
	}

	private void assertMembers(Set<String> actual, List<String> expected) {
		assertEquals(expected.size(), actual.size());
		for (String member : actual) {
			assertTrue(expected.contains(member));
		}
	}

	@Test
	public void forbiddenDependencies() {
		/*!		  
		  Specifies what dependencies are *not allowed.*

		  Usage:
		  >>>>
		  #{rulesKey}\<item name\> #{forbidden} \<item name\>
		  <<<<

		   - You can enter *multiple items* by separating them with commas (,). 
		   - You can enter *multiple rules* by separating them with semi-colons (;).
		   
		  For example:
		  >>>>
		  #{forbiddenDependenciesExample1}
		  <<<<
		*/
		String rule = rulesKey + "y " + ParserConstants.CANNOT_DEPEND + " x"
				+ ParserConstants.SMALL_SEPARATOR + " z"
				+ ParserConstants.BIG_SEPARATOR + " b"
				+ ParserConstants.SMALL_SEPARATOR + " c "
				+ ParserConstants.CANNOT_DEPEND + " a";
		store("forbidden", ParserConstants.CANNOT_DEPEND);
		store("rulesKey", rulesKey);
		store("forbiddenDependenciesExample1", rule);

		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { rule });
		assertRuleByName(arguments.getForbiddenDependencies().get("y"), Arrays
				.asList("x", "z"));
		assertRuleByName(arguments.getForbiddenDependencies().get("b"), Arrays
				.asList("a"));
		assertRuleByName(arguments.getForbiddenDependencies().get("c"), Arrays
				.asList("a"));
	}

	@Test
	public void allowedDependencies() {

		/*!
		Allowed dependencies provide a way to make exceptions to forbidden dependencies.
		Allowed dependencies **override** forbidden dependencies.
		
		Allowed dependencies are listed alongside forbidden dependencies  
		in **=#{rules}=**, with the parameter **=#{can}=**. 
		
		Consider the following example:
		>>>>
		#{groupExample3}
		#{ruleExample1}
		<<<<
		
		 - A group that contains all items starting with 'org.domain.' is specified. 
		 - All dependencies between the items in the group are forbidden.
		 - The '#{can}' rule overrides the '#{cannot}' rule and allows 
		   dependencies from org.domain.public.foo to org.domain.public.bar.
		 */

		String group = groupsKey + "Domain " + ParserConstants.CONTAINS
				+ " org.domain.*";
		String rule = rulesKey + "@Domain " + ParserConstants.CANNOT_DEPEND
				+ " @Domain" + ParserConstants.BIG_SEPARATOR
				+ " org.domain.public.foo " + ParserConstants.CAN_DEPEND
				+ " org.domain.public.bar";

		store("cannot", ParserConstants.CANNOT_DEPEND);
		store("can", ParserConstants.CAN_DEPEND);
		store("rules", ParserConstants.RULES_KEY);
		store("groupExample3", group);
		store("ruleExample1", rule);

		Arguments arguments = new ArgumentBuilder().build(new String[] { rule,
				group });

		assertRuleByName(arguments.getAllowedDependencies().get(
				"org.domain.public.foo"), Arrays
				.asList("org.domain.public.bar"));
		assertRuleByName(arguments.getForbiddenDependencies().get("@Domain"),
				Arrays.asList("@Domain"));
	}

	private void assertRuleByName(Set<String> actual, List<String> expected) {
		assertNotNull(actual);
		assertEquals(expected.size(), actual.size());
		for (String item : actual) {
			assertTrue(expected.contains(item));
		}
	}
}
