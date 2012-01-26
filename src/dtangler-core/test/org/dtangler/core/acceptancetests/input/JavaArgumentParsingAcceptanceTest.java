// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.input;

import static com.agical.bumblebee.junit4.Storage.store;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Test;

public class JavaArgumentParsingAcceptanceTest {

	/*!!
	 #{set_header 'Run options: Java-specific'}
	 
	 With scopes, you can select the level of detail for the dependency analysis.
	 For example, with package scope, Java packages will be used as rows and 
	 columns in the DSM.
	 
	 */
	private String scopeKey = CommandLineParser
			.getKeyString(ParserConstants.SCOPE_KEY);
	private String inputKey = CommandLineParser
			.getKeyString(ParserConstants.INPUT_KEY);

	@Test
	public void defaultScope() {
		/*!
		 If the =#{javaScope}= option is omitted, package scope is used.
		 */
		store("javaScope", ParserConstants.SCOPE_KEY);
		Arguments arguments = new ArgumentBuilder().build(new String[] {});
		JavaDependencyEngine engine = new JavaDependencyEngine();
		assertEquals(JavaScope.packages, engine.getDependencies(arguments).getDefaultScope());
	}

	@Test
	public void locationScope() {
		/*!
		 Level of detail: the locations for the class files being analysed, 
		 given with the =#{input}= run option.
		 
		 Analyzing dependencies between locations can come in handy if you 
		 want to see how projects depend on each other, for example.
		 */
		store("input", ParserConstants.INPUT_KEY);

		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { scopeKey
						+ JavaScope.locations.getDisplayName() });
		JavaDependencyEngine engine = new JavaDependencyEngine();
		assertEquals(JavaScope.locations, engine.getDependencies(arguments).getDefaultScope());
	}

	@Test
	public void jarsOnLocationScope() {
		/*!
		 You can make jar files appear on location scope by entering them 
		 as separate values to the =#{input}= run option.
		 
		 For example:
		 >>>>
		 #{jarLocationArguments}
		 <<<<
		 These arguments will produce a DSM with two rows: one for the specified jar file,
		 and one for the specified folder. 
		 */

		final String s = File.separator;
		String path = ClassPathEntryFinder.getPathContaining("core") + s
				+ "org" + s + "dtangler" + s + "core" + s + "acceptancetests"
				+ s + "testdata";
		String jarPath = path + s + "jarexample.jar";
		String classPath = path + s + "classes";

		String scope = scopeKey + JavaScope.locations.getDisplayName();
		String input = inputKey + jarPath + ParserConstants.BIG_SEPARATOR
				+ classPath;

		Arguments args = new ArgumentBuilder().build(new String[] { scope,
				input });

		JavaDependencyEngine engine = new JavaDependencyEngine();
		Set<Dependable> locationScopeItems = engine.getDependencies(args)
				.getDependencyGraph(JavaScope.locations).getAllItems();

		assertContents(locationScopeItems, new HashSet(Arrays.asList(jarPath,
				classPath)));

		store("jarLocationArguments", scope + " " + inputKey
				+ "testdata/jarexample.jar" + ParserConstants.BIG_SEPARATOR
				+ "testdata/classes");
		store("input", ParserConstants.INPUT_KEY);
	}

	private void assertContents(Set<Dependable> actual, Set<String> expected) {
		Iterator<Dependable> i = actual.iterator();
		Set<String> dependableNames = new HashSet();
		while (i.hasNext())
			dependableNames.add(i.next().getDisplayName());
		assertEquals(expected, dependableNames);
	}

	@Test
	public void packageScope() {
		/*!
		 Java package level detail.
		 */
		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { scopeKey
						+ JavaScope.packages.getDisplayName() });
		JavaDependencyEngine engine = new JavaDependencyEngine();
		assertEquals(JavaScope.packages, engine.getDependencies(arguments).getDefaultScope());
	}

	@Test
	public void classScope() {
		/*!
		 Java class level detail.
		 */
		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { scopeKey
						+ JavaScope.classes.getDisplayName() });
		JavaDependencyEngine engine = new JavaDependencyEngine();
		assertEquals(JavaScope.classes, engine.getDependencies(arguments).getDefaultScope());
	}
}
