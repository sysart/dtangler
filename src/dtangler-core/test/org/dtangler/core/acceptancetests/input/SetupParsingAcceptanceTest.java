// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.input;

import static com.agical.bumblebee.junit4.Storage.store;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.TestDependable;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.javaengine.dependencyengine.JavaDependencyEngine;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Test;

public class SetupParsingAcceptanceTest {

	/*!!
	 #{set_header 'Run options: Setup'}
	 */
	private String classPathKey = CommandLineParser
			.getKeyString(ParserConstants.CLASS_PATH_KEY);
	private String inputKey = CommandLineParser
			.getKeyString(ParserConstants.INPUT_KEY);
	private String engineKey = CommandLineParser
			.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY);

	private String configKey = CommandLineParser
			.getKeyString(ParserConstants.CONFIG_FILE_KEY);
	private String maskKey = CommandLineParser
			.getKeyString(ParserConstants.IGNORE_FILE_MASK_KEY);
	private String cyclesAllowed = CommandLineParser
			.getKeyString(ParserConstants.CYCLES_ALLOWED_KEY);
	private String rulesKey = CommandLineParser
			.getKeyString(ParserConstants.RULES_KEY);

	@Test
	public void configFile() {
		/*!
		 You can specify a properties file path if you want to use a previously 
		 created configuration.
		 
		 For example:
		 >>>>
		 #{configFilePath}
		 <<<<
		 
		 Config file parameters are similar to the command line parameters, 
		 with the following exceptions: 
		  - The leading '-' from each parameter is omitted
		  - Double quotes from all paths are omitted
		  - Each run option is specified on its own row
		  - You can divide long parameters to multiple rows by adding '\\' 
		    to the end of the row being divided

		 The contents of an example config file:
		 >>>>
		 #{File.new(path).read}
		 <<<<

		 */
		String path = getConfigFilePath();
		String[] args = { configKey + path };
		store("configFilePath", args[0]);
		store("path", path);

		Arguments arguments = new ArgumentBuilder().build(args);

		assertEquals(path, arguments.getConfigFileName());
	}

	@Test
	public void commandLineParametersOverrideConfigFileParameters() {
		/*!
		 If the same run option is defined in both the command line 
		 and properties file, the command line version is used.
		 
		 */

		String path = getConfigFilePath();
		String utilGroup = ParserConstants.GROUP_IDENTIFIER + "util";
		String[] args = {
				configKey + path,
				cyclesAllowed + ParserConstants.VALUE_FALSE,
				rulesKey + utilGroup + " " + ParserConstants.CANNOT_DEPEND
						+ " org.app" };

		Arguments arguments = new ArgumentBuilder().build(args);
		Map<String, Set<String>> forbiddenDependencies = arguments
				.getForbiddenDependencies();

		assertFalse(arguments.getCyclesAllowed());
		assertEquals(1, forbiddenDependencies.size());
		assertTrue(forbiddenDependencies.containsKey(utilGroup));
		assertTrue(forbiddenDependencies.containsValue(new HashSet(Arrays
				.asList("org.app"))));
	}

	@Test
	public void analysisPath() {
		/*!
		**=#{input}=** defines the path to folder(s) containing the items 
		for the dependency analysis or the path to file(s) containing the
		dependency definitions. You can enter multiple 
		paths or files by separating them with semicolons (;). 
		
		Items folder example:
		>>>>
		#{classPathExample1}
		<<<<

		If the path contains empty spaces, remember to surround the path with 
		double quotes (e.g. \"C:/Documents and Settings\").	

		Dependency definition file example:
		>>>>
		#{classPathExample2}
		<<<<

		Reading dependency definitions from standard input:
		>>>>
		#{classPathExample3}
		<<<<
		
		*Backward compatibility:* the older =#{classPath}= run option is interchangeable
		with =#{input}=.   
		 */
		String path1 = "c:/projects/myProgram/build";
		String path2 = "temp/stuff";
		String path3 = "c:/projects/myProgram/dependencies.dt";
		String oldStyleArgs = classPathKey + path1
				+ ParserConstants.BIG_SEPARATOR + path2;
		String newStyleArgs = inputKey + path1 + ParserConstants.BIG_SEPARATOR
				+ path2;
		String newStyleArgsExample2 = inputKey + path3 + " " + engineKey + ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC;
		String newStyleArgsExample3 = inputKey + ParserConstants.INPUT_KEY_VALUE_STANDARD_INPUT + " " + engineKey + ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC;
		store("classPath", ParserConstants.CLASS_PATH_KEY);
		store("input", ParserConstants.INPUT_KEY);
		store("classPathExample1", newStyleArgs);
		store("classPathExample2", newStyleArgsExample2);
		store("classPathExample3", newStyleArgsExample3);

		Arguments oldArgs = new ArgumentBuilder()
				.build(new String[] { oldStyleArgs });
		Arguments newArgs = new ArgumentBuilder()
				.build(new String[] { newStyleArgs });

		assertEquals(oldArgs, newArgs);
		assertEquals(2, newArgs.getInput().size());
		assertTrue(newArgs.getInput().contains(path1));
		assertTrue(newArgs.getInput().contains(path2));
	}

	@Test
	public void supportedFileTypes() {
		/*!
		  
		   - .class
		   - .jar 
		   - .dt
		
		 */
		final String s = File.separator;
		String path = ClassPathEntryFinder.getPathContaining("core") + s
				+ "org" + s + "dtangler" + s + "core" + s + "acceptancetests"
				+ s + "testdata";
		String jarPath = path + s + "jarexample.jar";
		String classPath = path + s + "classes";

		Arguments args = new ArgumentBuilder()
				.build(new String[] { classPathKey + jarPath
						+ ParserConstants.BIG_SEPARATOR + classPath });

		Dependencies dependencies = new JavaDependencyEngine()
				.getDependencies(args);
		Set<Dependable> actual = dependencies.getAllItems();

		Set<Dependable> expected = createDependables("ClassOutsideJar",
				"org.dtangler.core.acceptancetests.testdata.classes", classPath);
		expected.addAll(createDependables("JarClass1", "jar.example", jarPath));
		expected.addAll(createDependables("JarClass2", "jar.example", jarPath));

		assertEquals(expected, actual);
	}

	private Set<Dependable> createDependables(String className,
			String packageName, String locationName) {
		Dependable dep = new TestDependable(locationName + ": " + packageName
				+ "." + className, JavaScope.classes);
		Dependable parent = new TestDependable(locationName + ": "
				+ packageName, JavaScope.packages);
		Dependable grandParent = new TestDependable(locationName,
				JavaScope.locations);
		return new HashSet(Arrays.asList(dep, parent, grandParent));
	}

	@Test
	public void ignoreFilesFromTheAnalysis() {
		/*!
		 The =#{ignoreFilemask}= run option lets you ignore files from
		 the dependency analysis.
		  - you can enter multiple masks with semicolons
		  - you can use asterisks (*) as wildcards

		 For example, if you want to exclude test files from the analysis, 
		 you could specify:
		 >>>>
		 #{exampleFilemask}
		 <<<<
		  
		 */
		String mask = maskKey + "*test*" + ParserConstants.BIG_SEPARATOR
				+ "*Test*";
		Arguments arguments = new ArgumentBuilder()
				.build(new String[] { mask });

		assertTrue(arguments.getIgnoredFileMasks().containsAll(
				Arrays.asList("*test*", "*Test*")));

		store("ignoreFilemask", ParserConstants.IGNORE_FILE_MASK_KEY);
		store("exampleFilemask", mask);
	}

	private String getConfigFilePath() {
		String path = SetupParsingAcceptanceTest.class.getResource(
				"exampleConfig.properties").getFile();
		if (path.contains("%20"))
			path = path.replaceAll("%20", " ");

		return path;
	}
}
