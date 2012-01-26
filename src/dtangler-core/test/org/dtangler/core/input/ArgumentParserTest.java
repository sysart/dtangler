// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.junit.Test;

public class ArgumentParserTest {
	@Test
	public void testParseClassPath() {
		String path1 = "/foo/bar";
		String path2 = "c:/temp";

		Arguments argument = parse(ParserConstants.INPUT_KEY, path1
				+ ParserConstants.BIG_SEPARATOR + path2);
		List<String> classPaths = argument.getInput();

		assertEquals(2, classPaths.size());
		assertEquals(path1, classPaths.get(0));
		assertEquals(path2, classPaths.get(1));
	}

	@Test
	public void testParseIgnoreFileMask() {
		String mask1 = "*foo";
		String mask2 = "*bar*";

		Arguments argument = parse(ParserConstants.IGNORE_FILE_MASK_KEY, mask1
				+ ParserConstants.BIG_SEPARATOR + mask2);
		List<String> ignoreFileMasks = argument.getIgnoredFileMasks();

		assertEquals(2, ignoreFileMasks.size());
		assertEquals(mask1, ignoreFileMasks.get(0));
		assertEquals(mask2, ignoreFileMasks.get(1));
	}

	@Test
	public void testParseCyclesDenied() {
		Arguments argument = parse(ParserConstants.CYCLES_ALLOWED_KEY,
				ParserConstants.VALUE_FALSE);
		assertFalse(argument.getCyclesAllowed());
	}

	@Test
	public void testParseCyclesAllowed() {
		Arguments argument = parse(ParserConstants.CYCLES_ALLOWED_KEY,
				ParserConstants.VALUE_TRUE);
		assertTrue(argument.getCyclesAllowed());
	}

	@Test
	public void testParseCyclesDeniedByDefault() {
		Arguments argument = parse(ParserConstants.INPUT_KEY,
				ParserConstants.VALUE_TRUE);
		assertFalse("cycles denied because cycles allowed key not used",
				argument.getCyclesAllowed());
	}

	@Test
	public void testParseConfigFileName() {
		String fileName = "MyFile.foo";
		Arguments argument = parse(ParserConstants.CONFIG_FILE_KEY, fileName);
		assertEquals(fileName, argument.getConfigFileName());
	}

	@Test
	public void testParseScope() {
		Arguments argument = parse(ParserConstants.SCOPE_KEY, "classes");
		assertEquals("classes", argument.getScope());
	}

	@Test
	public void testOverrideArgument() {
		String classPath1 = "first classPath";
		parse(ParserConstants.INPUT_KEY, classPath1);
		// overrides first class path
		String classPath2 = "second classPath";
		parse(ParserConstants.INPUT_KEY, classPath2);

		Arguments arguments = parser.getArguments();

		assertEquals(1, arguments.getInput().size());
		assertEquals(classPath2, arguments.getInput().get(0));

	}

	@Test
	public void testParseMultipleValues() {
		String item1 = "foo ";
		String item2 = " bar";

		parse(ParserConstants.CYCLES_ALLOWED_KEY, ParserConstants.VALUE_FALSE);
		parse(ParserConstants.RULES_KEY, item1 + ParserConstants.CANNOT_DEPEND
				+ item2);
		Arguments arguments = parser.getArguments();

		assertFalse(arguments.getCyclesAllowed());
		assertEquals(1, arguments.getForbiddenDependencies().size());
		assertTrue(arguments.getForbiddenDependencies().containsKey(
				item1.trim()));
		assertEquals(1, arguments.getForbiddenDependencies().get(item1.trim())
				.size());
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
