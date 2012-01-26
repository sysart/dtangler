//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.input;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.junit.Before;
import org.junit.Test;

public class RuleParserTest {
	private static String item1 = "foo ";
	private static String item2 = " bar";
	private static String item3 = " bay";
	private static String item4 = "boo ";

	private ArgumentParser parser;

	@Before
	public void setUp() {
		parser = new ArgumentParser();
	}

	@Test
	public void parseCannotRulesTest() {
		Arguments argument = parse(ParserConstants.RULES_KEY, item1
				+ ParserConstants.SMALL_SEPARATOR + item4
				+ ParserConstants.CANNOT_DEPEND + item2
				+ ParserConstants.SMALL_SEPARATOR + item3);

		Map<String, Set<String>> forbidden = new HashMap();
		Set<String> rightSide = new HashSet();

		rightSide.add(item2.trim());
		rightSide.add(item3.trim());

		forbidden.put(item1.trim(), rightSide);
		forbidden.put(item4.trim(), rightSide);

		assertEquals(forbidden, argument.getForbiddenDependencies());
	}

	@Test
	public void parseCanRulesTest() {
		Arguments argument = parse(ParserConstants.RULES_KEY, item1
				+ ParserConstants.SMALL_SEPARATOR + item4
				+ ParserConstants.CAN_DEPEND + item2
				+ ParserConstants.SMALL_SEPARATOR + item3);

		Map<String, Set<String>> forbidden = new HashMap();
		Set<String> rightSide = new HashSet();

		rightSide.add(item2.trim());
		rightSide.add(item3.trim());

		forbidden.put(item1.trim(), rightSide);
		forbidden.put(item4.trim(), rightSide);

		assertEquals(forbidden, argument.getAllowedDependencies());
	}

	@Test
	public void parseCombinedRulesTest() {
		String rule = item1 + ParserConstants.SMALL_SEPARATOR + item4
				+ ParserConstants.CANNOT_DEPEND + item2
				+ ParserConstants.SMALL_SEPARATOR + item3
				+ ParserConstants.BIG_SEPARATOR + item1
				+ ParserConstants.CAN_DEPEND + item4;

		Arguments argument = parse(ParserConstants.RULES_KEY, rule);

		Map<String, Set<String>> forbidden = new HashMap();
		Set<String> rightSide = new HashSet();

		rightSide.add(item2.trim());
		rightSide.add(item3.trim());

		forbidden.put(item1.trim(), rightSide);
		forbidden.put(item4.trim(), rightSide);

		assertEquals(forbidden, argument.getForbiddenDependencies());

		Map<String, Set<String>> allowed = new HashMap();
		rightSide = new HashSet();
		rightSide.add(item4.trim());

		allowed.put(item1.trim(), rightSide);

		assertEquals(allowed, argument.getAllowedDependencies());
	}

	private Arguments parse(String key, String value) {
		parser.parseArguments(createValues(key, value));
		return parser.getArguments();
	}

	private Map<String, String> createValues(String key, String value) {
		Map<String, String> values = new Hashtable<String, String>();
		values.put(key, value);
		return values;
	}
}
