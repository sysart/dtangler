// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;

public class ArgumentParser {
	protected Arguments args = new Arguments();

	public Arguments getArguments() {
		return args;
	}

	public Arguments parseArguments(Map<String, String> values) {
		if (values.containsKey(ParserConstants.CLASS_PATH_KEY)
				|| values.containsKey(ParserConstants.INPUT_KEY))
			parseInput(values);
		if (values.containsKey(ParserConstants.IGNORE_FILE_MASK_KEY))
			parseIgnoredFileMasks(values);
		if (values.containsKey(ParserConstants.GROUP_KEY)
				|| values.containsKey(ParserConstants.GROUPS_KEY))
			parseGroups(values);
		if (values.containsKey(ParserConstants.CYCLES_ALLOWED_KEY))
			parseCyclesAllowed(values);
		if (values.containsKey(ParserConstants.RULES_KEY))
			parseRules(values);
		if (values.containsKey(ParserConstants.CONFIG_FILE_KEY))
			parseConfigFileName(values);
		if (values.containsKey(ParserConstants.SCOPE_KEY))
			parseScope(values);
		if (values.containsKey(ParserConstants.DEPENDENCY_ENGINE_ID_KEY))
			parseDependencyEngineId(values);
		return args;
	}

	private void parseScope(Map<String, String> values) {
		String value = values.get(ParserConstants.SCOPE_KEY);
		args.setScope(value);
	}

	private void parseConfigFileName(Map<String, String> values) {
		args.setConfigFileName(values.get(ParserConstants.CONFIG_FILE_KEY));
	}

	private void parseDependencyEngineId(Map<String, String> values) {
		args.setDependencyEngineId(values
				.get(ParserConstants.DEPENDENCY_ENGINE_ID_KEY));
	}

	private void parseInput(Map<String, String> values) {
		List<String> valueList = getValues(values,
				ParserConstants.CLASS_PATH_KEY, ParserConstants.INPUT_KEY);
		args.setInput(valueList);
	}

	private List<String> getValues(Map<String, String> values,
			String... possibleKeys) {
		return getValueList(getValue(values, possibleKeys),
				ParserConstants.BIG_SEPARATOR);
	}

	private String getValue(Map<String, String> values, String... possibleKeys) {
		for (String key : possibleKeys) {
			String value = values.get(key);
			if (value != null)
				return value;
		}
		return new String();
	}

	private void parseIgnoredFileMasks(Map<String, String> values) {
		args.setIgnoredFileMasks(getValueList(values
				.get(ParserConstants.IGNORE_FILE_MASK_KEY),
				ParserConstants.BIG_SEPARATOR));
	}

	private void parseGroups(Map<String, String> values) {
		Map<String, Group> result = new HashMap();
		String value = getValue(values, ParserConstants.GROUP_KEY,
				ParserConstants.GROUPS_KEY);
		String[] list = value.split(ParserConstants.BIG_SEPARATOR);

		for (String group : list) {
			if (!group.contains(ParserConstants.CONTAINS))
				continue;

			String[] parts = group.split(ParserConstants.CONTAINS, 2);
			String name = parts[0].trim();

			if (parts[1].contains(ParserConstants.DOES_NOT_CONTAIN)) {
				String[] subParts = parts[1]
						.split(ParserConstants.DOES_NOT_CONTAIN);
				result.put(name, new Group(name, parseItems(subParts[0]),
						parseItems(subParts[1])));
			} else {
				result.put(name, new Group(name, parseItems(parts[1])));
			}
		}
		args.setGroups(result);
	}

	private void parseCyclesAllowed(Map<String, String> values) {
		boolean cyclesAllowed = false;
		String key = ParserConstants.CYCLES_ALLOWED_KEY;
		String value = values.get(key);

		if (value.equalsIgnoreCase(ParserConstants.VALUE_TRUE)) {
			cyclesAllowed = true;
		}
		args.setCyclesAllowed(cyclesAllowed);
	}

	private void parseRules(Map<String, String> values) {
		Map<String, Set<String>> cannotDepend = new HashMap();
		Map<String, Set<String>> canDepend = new HashMap();

		String[] ruleList = values.get(ParserConstants.RULES_KEY).split(
				ParserConstants.BIG_SEPARATOR);

		for (String ruleString : ruleList) {
			if (ruleString.contains(ParserConstants.CANNOT_DEPEND))
				addRule(ruleString, cannotDepend);
			else if (ruleString.contains(ParserConstants.CAN_DEPEND))
				addRule(ruleString, canDepend);
			else
				continue;
		}

		args.setForbiddenDependencies(cannotDepend);
		args.setAllowedDependencies(canDepend);
	}

	private void addRule(String rule, Map<String, Set<String>> rules) {
		Map<String, Set<String>> newRule = parseRule(rule);

		for (String ruleKey : newRule.keySet()) {
			Set<String> newValues = newRule.get(ruleKey);

			if (rules.containsKey(ruleKey)) {
				newValues.addAll(rules.get(ruleKey));
				newRule.put(ruleKey, newValues);
			}
		}
		rules.putAll(newRule);
	}

	private Map<String, Set<String>> parseRule(String rule) {
		String ruleType;

		if (rule.contains(ParserConstants.CANNOT_DEPEND))
			ruleType = ParserConstants.CANNOT_DEPEND;
		else if (rule.contains(ParserConstants.CAN_DEPEND))
			ruleType = ParserConstants.CAN_DEPEND;
		else
			return Collections.EMPTY_MAP;

		String[] separateSides = rule.split(ruleType, 2);
		String leftSide = separateSides[0];
		String rightSide = separateSides[1];

		Set<String> leftSet = new HashSet();
		Set<String> rightSet = new HashSet();

		leftSet = parseItems(leftSide);
		rightSet = parseItems(rightSide);
		Map<String, Set<String>> formattedRule = new HashMap();

		for (String leftRule : leftSet) {
			Set<String> oldRules = formattedRule.get(leftRule);
			// if the rule was already specified, add possible new rules
			// to it:
			if (oldRules != null) {
				for (String old : oldRules) {
					rightSet.add(old);
				}
			}
			formattedRule.put(leftRule, rightSet);
		}

		return formattedRule;
	}

	private List<String> getValueList(String value, String separator) {
		List<String> list = new ArrayList();
		for (String item : value.split(separator)) {
			list.add(item);
		}
		return list;
	}

	private Set<String> parseItems(String items) {
		Set<String> itemSet = new HashSet();

		String[] allItems = items.split(ParserConstants.SMALL_SEPARATOR);
		for (String item : allItems) {
			itemSet.add(item.trim());
		}
		return itemSet;
	}
}
