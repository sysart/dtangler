// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.jarfileparser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.dtangler.core.filefinder.FullPathWildCardFileFilter;
import org.dtangler.core.filefinder.RecursiveFileFinder;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.javaengine.classfileparser.ClassFileParser;
import org.dtangler.javaengine.jarfileparser.JarFileParser;
import org.dtangler.javaengine.types.JavaClass;
import org.junit.Test;

public class JarFileParserTest {

	@Test
	public void testClassesFromJarFileEqualClassesFromClassFiles()
			throws IOException {

		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String jarPath = corePath
				+ "/org/dtangler/javaengine/jarfileparser/testdata/testdata.jar";
		String expectedClasses = corePath
				+ "/org/dtangler/javaengine/classfileparser/testdata";

		Set<JavaClass> jarClasses = new JarFileParser()
				.parse(new File(jarPath));
		Set<JavaClass> classClasses = getExpectedClasses(expectedClasses);

		assertEquals(classClasses, jarClasses);
	}

	private Set<JavaClass> getExpectedClasses(String path) {
		RecursiveFileFinder fileFinder = new RecursiveFileFinder();
		Set<JavaClass> classes = new HashSet();
		ClassFileParser parser = new ClassFileParser();

		fileFinder.setFilter(new FullPathWildCardFileFilter(Arrays
				.asList(".class"), Collections.EMPTY_LIST));
		fileFinder.findFiles(path);
		for (File file : fileFinder.getFiles()) {
			classes.add(parser.parse(file));
		}
		return classes;
	}
}
