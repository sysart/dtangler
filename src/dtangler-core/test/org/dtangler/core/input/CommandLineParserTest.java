// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class CommandLineParserTest {
	@Test
	public void test() {
		String a = "a";
		String b = "b";
		String c = "c";
		String[] allowed = { a, b, c };
		String value1 = "123";
		String value2 = "fsdfs";
		String value3 = "432 rewrw;tertert";

		String args[] = { "-" + a + "=" + value1, "-" + b + "=" + value2,
				"-" + c + "=" + value3 };

		Map<String, String> result = new CommandLineParser(allowed)
				.parseValues(args);
		assertEquals(3, result.size());
		assertTrue(result.containsKey(a));
		assertTrue(result.containsKey(b));
		assertTrue(result.containsKey(c));

		assertEquals(value1, result.get(a));
		assertEquals(value2, result.get(b));
		assertEquals(value3, result.get(c));
	}

	@Test
	public void formatArgumentsTest() {
		String arg1 = "ARG1";
		String arg2 = "ARG2";
		String[] allowed = { arg1, arg2 };
		String[] args = { "-" + arg1 + "=p1", "cannot", "depend", "on", "p2",
				"-" + arg2 + "=true" };

		Map<String, String> result = new CommandLineParser(allowed)
				.parseValues(args);
		assertEquals(2, result.size());
		assertTrue(result.containsKey(arg1));
		assertTrue(result.containsKey(arg2));

		assertEquals("p1 cannot depend on p2", result.get(arg1));
		assertEquals("true", result.get(arg2));
	}
}
