// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.filefinder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class FullPathWildCardFileFilterTest {

	@Test
	public void testExtensionFilter() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Collections.EMPTY_LIST);
		assertTrue(f.accept(new File("/foo.class")));
		assertTrue(f.accept(new File("/BAR.CLASS")));
		assertTrue(f.accept(new File("/foo/bar.class")));
		assertFalse(f.accept(new File("/foo.java")));
		assertFalse(f.accept(new File("/foo/bar.java")));
		assertFalse(f.accept(new File("/class/foo.java")));
	}

	@Test
	public void testExcludeLiteral() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Arrays.asList(new File("/foo.class")
				.getAbsolutePath()));
		assertFalse(f.accept(new File("/foo.class")));
		assertTrue(f.accept(new File("/FOO.CLASS")));
		assertTrue(f.accept(new File("/foo1.class")));
		assertTrue(f.accept(new File("/1foo.class")));
	}

	@Test
	public void testExcludeMaskWithWildCardStart() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Arrays.asList("*foo.class"));
		assertFalse(f.accept(new File("/foo.class")));
		assertFalse(f.accept(new File("/bar/foo.class")));
		assertFalse(f.accept(new File("anotherfoo.class")));
		assertTrue(f.accept(new File("foobar.class")));
		assertTrue(f.accept(new File("foo/bar.class")));
	}

	@Test
	public void testExcludeMaskWithWildCardEnd() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Arrays.asList(new File("/foo*")
				.getAbsolutePath()));
		assertFalse(f.accept(new File("/foo.class")));
		assertFalse(f.accept(new File("/foobar.class")));
		assertFalse(f.accept(new File("/foo/bar.class")));
		assertTrue(f.accept(new File("/bar/foo.class")));
		assertTrue(f.accept(new File("/afoo.class")));
	}

	@Test
	public void testExcludeMaskWithMultipleWildCards() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Arrays.asList("*foo*bar*"));
		assertTrue(f.accept(new File("/foo.class")));
		assertTrue(f.accept(new File("/bar.class")));
		assertFalse(f.accept(new File("/foobar.class")));
		assertFalse(f.accept(new File("/foo/bar.class")));
		assertFalse(f.accept(new File("/foo/bay/bar.class")));
	}

	@Test
	public void testInsameWildCards() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Arrays.asList("***foo*****bar**"));
		assertTrue(f.accept(new File("/foo.class")));
		assertTrue(f.accept(new File("/bar.class")));
		assertFalse(f.accept(new File("/foobar.class")));
		assertFalse(f.accept(new File("/foo/bar.class")));
		assertFalse(f.accept(new File("/foo/bay/bar.class")));
	}

	@Test
	public void testMultipleMasks() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Arrays.asList("*foo*", "*bar*"));
		assertFalse(f.accept(new File("/foo.class")));
		assertFalse(f.accept(new File("/bar.class")));
		assertTrue(f.accept(new File("/bay.class")));
	}

	@Test
	public void testMultipleExtensions() {
		FullPathWildCardFileFilter f = new FullPathWildCardFileFilter(Arrays
				.asList(".class", ".jar"), Collections.EMPTY_LIST);
		assertTrue(f.accept(new File("/foo.class")));
		assertTrue(f.accept(new File("/foo.jar")));
		assertFalse(f.accept(new File("/foo.jarclass")));
	}
}
