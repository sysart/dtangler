// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.end2end;

import static org.junit.Assert.assertEquals;

import org.dtangler.core.CommandLineApp;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.core.testutil.output.FileUtil;
import org.dtangler.core.testutil.output.MockWriter;
import org.junit.Test;

/**
 * Example scenarios are in separate source folders (testdata-xxx). Each folder
 * has a file that describes the expected DSM output (expected.txt) The Eclipse
 * project has a separate output folder for each testdata source (for example
 * "testdata-minimal-classes")
 */
public class ClassFile2TextUiAcceptanceTest {

	private final String inputKey = CommandLineParser
			.getKeyString(ParserConstants.INPUT_KEY);

	@Test
	public void testGoodDependencies() {
		assertDsmResult(ClassPathEntryFinder
				.getPathContaining("testdata-good-deps"));
	}

	private void assertDsmResult(String path) {
		MockWriter writer = new MockWriter();
		new CommandLineApp(writer).run(new String[] { inputKey + path });
		String dsm = writer.getOutput();
		String expected = FileUtil.readFile(path + "/expected.txt");
		assertEquals(expected.replaceAll("\r", ""), dsm.replaceAll("\r", ""));
	}

}