// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.ui.dsm.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class ShortenedNameFormatterTest {

	@Test
	public void testSingleItem() {
		String item = "foo.bar";
		assertEquals("foo.bar", new ShortenedNameFormatter(Arrays.asList(item))
				.format(item));
	}

	@Test
	public void testMultipleItemsWithDifferentPrefix() {
		String item1 = "foo.bar";
		String item2 = "bar.foo";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2));
		assertEquals("foo.bar", formatter.format(item1));
		assertEquals("bar.foo", formatter.format(item2));
	}

	@Test
	public void testMultipleItemsWithEqualPrefix() {
		String item1 = "foo.bar";
		String item2 = "foo.oof";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2));
		assertEquals("bar", formatter.format(item1));
		assertEquals("oof", formatter.format(item2));
	}

	@Test
	public void testRoot() {
		String item1 = "foo.bar.bay";
		String item2 = "foo.bar";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2));
		assertEquals("bar.bay", formatter.format(item1));
		assertEquals("bar", formatter.format(item2));
	}

	@Test
	public void testMultipleItemsWithEqualPrefixFragment() {
		String item1 = "foa.bar";
		String item2 = "fob.oof";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2));
		assertEquals("foa.bar", formatter.format(item1));
		assertEquals("fob.oof", formatter.format(item2));
	}

	@Test
	public void testMultipleItemsWithEqualPostFix() {
		String item1 = "abc.bar";
		String item2 = "def.bar";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2));
		assertEquals("abc", formatter.format(item1));
		assertEquals("def", formatter.format(item2));
	}

	@Test
	public void testMultipleItemsWithEqualPreAndPostFixes() {
		String item1 = "abc.foo.def.ghi";
		String item2 = "abc.bar.bay.def.ghi";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2));
		assertEquals("foo", formatter.format(item1));
		assertEquals("bar.bay", formatter.format(item2));
	}

	@Test
	public void testMultipleItemsWithEqualPreAndPostFixesWithOneUnmatched() {
		String item1 = "abc.foo.def.ghi";
		String item2 = "abc.bar.bay.def.ghi";
		String item3 = "Xbc.bar.bay.def.ghZ";
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList(item1,
				item2, item3));
		assertEquals("abc.foo.def.ghi", formatter.format(item1));
		assertEquals("abc.bar.bay.def.ghi", formatter.format(item2));
		assertEquals("Xbc.bar.bay.def.ghZ", formatter.format(item3));
	}

	@Test
	public void testEmpty() {
		Formatter formatter = new ShortenedNameFormatter(Arrays.asList("aa",
				"bb", ""));
		assertEquals("", formatter.format(""));
	}
}
