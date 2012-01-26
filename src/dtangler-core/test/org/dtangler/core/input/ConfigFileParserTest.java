// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.exception.DtException;
import org.junit.Test;

public class ConfigFileParserTest {
	private static final String TEST_CONFIG1 = "testdata/TestConfig1.properties";
	private static final String TEST_CONFIG2 = "testdata/TestConfig2.properties";
	private Map<String, String> result;

	private void setUp(String configFile) throws IOException {
		InputStream stream = getClass().getResourceAsStream(configFile);
		result = new ConfigFileParser(stream, ParserConstants.VALID_KEYS)
				.parseValues();
	}

	@Test
	public void noSuchFile() {
		File file = new File("no-such-file.txt");
		try {
			new ConfigFileParser(file, ParserConstants.VALID_KEYS)
					.parseValues();
			fail("did not throw");
		} catch (DtException e) {
			assertTrue(e.getMessage().contains("no-such-file.txt"));
			assertTrue(e.getCause() instanceof FileNotFoundException);
		}

	}

	@Test
	public void config1Test() throws IOException {
		setUp(TEST_CONFIG1);

		assertEquals(5, result.size());
		assertTrue(result.containsKey(ParserConstants.INPUT_KEY));
		assertTrue(result.containsKey(ParserConstants.CYCLES_ALLOWED_KEY));
		assertTrue(result.containsKey(ParserConstants.RULES_KEY));
		assertTrue(result.containsKey(ParserConstants.GROUPS_KEY));
		assertTrue(result.containsKey(ParserConstants.SCOPE_KEY));

		assertEquals(
				"c:\\folder-1;c:\\long path\\with\\subfolders\\;/relative/path/",
				result.get(ParserConstants.INPUT_KEY));
		assertEquals("true", result.get(ParserConstants.CYCLES_ALLOWED_KEY));
		assertEquals(
				"item1 cannot depend on item3, item4;item2, item3 cannot depend on item1",
				result.get(ParserConstants.RULES_KEY));
		assertEquals(
				"group1 contains a,b,c;group2 contains b,c,d does not contain c.*",
				result.get(ParserConstants.GROUPS_KEY));
		assertEquals("classes", result.get(ParserConstants.SCOPE_KEY));
	}

	@Test
	public void config2Test() throws IOException {
		setUp(TEST_CONFIG2);

		assertEquals(2, result.size());
		assertTrue(result.containsKey(ParserConstants.CYCLES_ALLOWED_KEY));
		assertTrue(result.containsKey(ParserConstants.RULES_KEY));

		assertEquals("false", result.get(ParserConstants.CYCLES_ALLOWED_KEY));
		assertEquals("item4 cannot depend on item1", result
				.get(ParserConstants.RULES_KEY));
	}
}
