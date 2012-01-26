//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.input;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dtangler.core.util.WildcardMatch;
import org.junit.Test;

public class WildcardMatchTest {
	@Test
	public void testStartsWith() {
		WildcardMatch match = new WildcardMatch("a*");

		assertTrue(match.isMatch("a"));
		assertTrue(match.isMatch("abc"));
		assertTrue(match.isMatch("a12"));
		assertTrue(match.isMatch("a bc"));

		assertFalse(match.isMatch("b"));
		assertFalse(match.isMatch("bcd"));
		assertFalse(match.isMatch(" abc"));
		assertFalse(match.isMatch("123abc"));
	}

	@Test
	public void testEndsWith() {
		WildcardMatch match = new WildcardMatch("*a");

		assertTrue(match.isMatch("a"));
		assertTrue(match.isMatch("cba"));
		assertTrue(match.isMatch("12a"));
		assertTrue(match.isMatch("bc a"));

		assertFalse(match.isMatch("b"));
		assertFalse(match.isMatch("bac"));
		assertFalse(match.isMatch("a "));
		assertFalse(match.isMatch(" a2"));
	}

	@Test
	public void testAnywhere() {
		WildcardMatch match = new WildcardMatch("*a*");

		assertTrue(match.isMatch("a"));
		assertTrue(match.isMatch("cba"));
		assertTrue(match.isMatch("12a123"));
		assertTrue(match.isMatch("bc a "));

		assertFalse(match.isMatch("b"));
		assertFalse(match.isMatch("bc"));
		assertFalse(match.isMatch("1 "));
	}

	@Test
	public void testString() {
		WildcardMatch match = new WildcardMatch("*foobar*");

		assertTrue(match.isMatch("foobar"));
		assertTrue(match.isMatch("afoobarb"));
		assertTrue(match.isMatch("1foobar2"));
		assertTrue(match.isMatch("foobar "));

		assertFalse(match.isMatch("oobar"));
		assertFalse(match.isMatch("fooba"));
		assertFalse(match.isMatch("foo bar"));
	}
}
