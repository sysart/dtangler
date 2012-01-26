// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.input;

import static com.agical.bumblebee.junit4.Storage.store;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.genericengine.dependencyengine.GenericDependencyEngine;
import org.dtangler.genericengine.types.ItemScope;
import org.junit.Test;

public class GenericEngineArgumentParsingAcceptanceTest {

	/*!!
	 #{set_header 'Run options: Generic engine -specific'}

	 With generic engine, you can read dependency definitions from
	 a text file or standard input.

	 Dependency definitions file syntax:
	 >>>>
	 dependencies         : (dependencyDefinition | itemDefinition)*
	 dependencyDefinition : dependant + ':' dependee + '\\n'
	 itemDefinition       : dependable
	 dependant            : dependable
	 dependee             : dependable
	 dependaple           : displayname | scope '{' fullyqualifiedname '}'
	 fullyqualifiedname   : parentfqn? displayname
	 parentfqn            : fullyqualifiedname
	 displayname          : string
	 <<<<
	 Strings must be URL encoded (for example space must be encoded in format %20).

	 Scopes define the level of detail for the dependency analysis.
	 
	 Simple example - we all know what Homer depends on:
	 >>>>
	 Homer : Beer
	 Homer : Pizza
	 Pizza : Cheese Pepperoni
	 <<<<

	 This has exactly the same meaning:
	 >>>>
	 {Homer} : {Beer}
	 {Homer} : {Pizza}
	 {Pizza} : {Cheese}
	 {Pizza} : {Pepperoni}
	 <<<<
	 
	 Java jars, packages and classes using fully qualified names:
	 >>>>
	 location{foo.jar}
	 package{foo.jar foo.jar/eg.process}
	 package{foo.jar foo.jar/eg.filters}
	 class{foo.jar foo.jar/eg.filters foo.jar/eg.filters.InFilter} : class{foo.jar foo.jar/eg.process foo.jar/eg.process.Process}
	 <<<<
	 
	 That's awfully verbose - it can be shortened like this:
	 >>>>
	 location{foo.jar}
	 package{foo.jar eg.process}
	 package{foo.jar eg.filters}
	 class{foo.jar eg.filters InFilter} : class{foo.jar eg.process Process}
	 <<<<
	 
	 */

	private String scopeKey = CommandLineParser
			.getKeyString(ParserConstants.SCOPE_KEY);
	private String inputKey = CommandLineParser
			.getKeyString(ParserConstants.INPUT_KEY);
	private String engineKey = CommandLineParser
			.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY);

	@Test
	public void defaultScope() {

		/*!
		 If the =#{genericEngineScope}= option is omitted, the first scope
		 in the dependency definitions file is used.
		 
		 For example reading the dependency definitions from a file:
		 >>>>
		 #{jarDefaultScopeArguments}
		 <<<<

		 Reading from standard input:
		 >>>>
		 #{jarDefaultScopeArgumentsStandardInput}
		 <<<<

		 The default scope is 0 in the dependency definitions example below:
		 >>>>
		 #{dtFileContents}
		 <<<<
		 */
		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/core/acceptancetests/input/dependencies.dt";
		
		store("genericEngineScope", scopeKey);
		store("jarDefaultScopeArguments", inputKey
				+ dtPath + " " + engineKey
				+ ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC);
		store("jarDefaultScopeArgumentsStandardInput", 
				inputKey + ParserConstants.INPUT_KEY_VALUE_STANDARD_INPUT + " "
				+ engineKey + ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC);
		try {
			store("dtFileContents", loadFile(new File(dtPath)));
		} catch (IOException e) {
			assertTrue(false);
		}
		Arguments args = new Arguments();
		args.setInput(Arrays.asList(dtPath));
		GenericDependencyEngine engine = new GenericDependencyEngine();
		Dependencies dependencies = engine.getDependencies(args);
		Set<Dependable> scope0Items = dependencies
				.getDependencyGraph(new ItemScope("0", 0))
				.getAllItems();
		assertEquals("0", dependencies.getDefaultScope().getDisplayName());
		assertEquals(1, scope0Items.size());
	}

	private String loadFile(File file) throws IOException {
		String fileContent = "";
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] data = new byte[fileInputStream.available()];
			fileInputStream.read(data, 0, data.length);
			fileContent = new String(data);
		} finally {
			if (fileInputStream != null)
				fileInputStream.close();
		}
		return fileContent;
	}

	@Test
	public void definedScope() {
		/*!
		 You can produce a DSM with selected scope by using the =#{scopeKey}= run option.
		  
		 For example: 
		 >>>>
		 #{jarDefinedScopeArguments}
		 <<<<
		 */

		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/genericengine/dependencyengine/testdata/testParsing2.dt";

		String scope = scopeKey + "2";
		store("genericEngineScope", scope);
		String input = inputKey + dtPath;
		store("jarDefinedScopeArguments", scope + " " + input + " " + engineKey
				+ ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC);
		store("scopeKey", scopeKey);


		Arguments args = new ArgumentBuilder().build(new String[] { scope,
				inputKey + dtPath });

		GenericDependencyEngine engine = new GenericDependencyEngine();
		Dependencies dependencies = engine.getDependencies(args);
		Set<Dependable> scope2Items = dependencies
				.getDependencyGraph(new ItemScope("2", 2))
				.getAllItems();
		assertEquals("2", dependencies.getDefaultScope().getDisplayName());
		assertEquals(5, scope2Items.size());

	}

}
