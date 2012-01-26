//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.input.CommandLineParser;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.core.testutil.output.FileUtil;
import org.dtangler.core.testutil.output.MockWriter;
import org.junit.Test;

public class CommandLineAppTest {

	private static final String inputKey = CommandLineParser
			.getKeyString(ParserConstants.INPUT_KEY);
	private static final String cyclesAllowedKey = CommandLineParser
			.getKeyString(ParserConstants.CYCLES_ALLOWED_KEY);
	private static final String rulesKey = CommandLineParser
			.getKeyString(ParserConstants.RULES_KEY);

	private CommandLineApp app = new CommandLineApp(new MockWriter());

	private String inputArg1 = inputKey
			+ ClassPathEntryFinder.getPathContaining("testdata-cyclic");
	private String inputArg2 = inputKey
			+ ClassPathEntryFinder.getPathContaining("testdata-good-deps");

	private final String package1 = "org.dtangler.core.testutil.cyclic.part1";
	private final String package2 = "org.dtangler.core.testutil.cyclic.part2";

	@Test
	public void testRunWithWrongPath() {
		String wrongPath[] = { inputKey + "no\\such\\path" };
		assertTrue(app.run(wrongPath));
	}

	@Test
	public void testRunWithCyclesAllowed() {

		String cyclesAllowed[] = { inputArg1, cyclesAllowedKey + "true" };
		assertTrue(app.run(cyclesAllowed));
	}

	@Test
	public void testRunWithCyclesDenied() {
		String cyclesDenied[] = { inputArg1, cyclesAllowedKey + "false" };
		String cyclesDeniedByDefault[] = { inputArg1 };
		assertFalse(app.run(cyclesDenied));
		assertFalse(app.run(cyclesDeniedByDefault));
	}

	@Test
	public void testRunWithAllowedDependencies() {
		String dependenciesAllowed[] = {
				inputArg1,
				rulesKey + package1 + " " + ParserConstants.CAN_DEPEND + " "
						+ package2 };
		String cyclesOK[] = {
				inputArg1,
				rulesKey + package1 + " " + ParserConstants.CAN_DEPEND + " "
						+ package2, cyclesAllowedKey + "true" };
		assertFalse(app.run(dependenciesAllowed));
		assertTrue(app.run(cyclesOK));

	}

	@Test
	public void testRunWithForbiddenDependencies() {
		String dependenciesDenied[] = {
				inputArg1,
				rulesKey + package1 + " " + ParserConstants.CANNOT_DEPEND + " "
						+ package2 };
		String cyclesOK[] = {
				inputArg1,
				rulesKey + package1 + " " + ParserConstants.CANNOT_DEPEND + " "
						+ package2, cyclesAllowedKey + "true" };
		assertFalse(app.run(dependenciesDenied));
		assertFalse(app.run(cyclesOK));
	}

	@Test
	public void testDsmWithViolationsOutsideScope() {

		String[] args = {
				inputArg2,
				rulesKey + "eg.foo.good.deps.client "
						+ ParserConstants.CANNOT_DEPEND + " "
						+ "eg.foo.good.deps.impl" };

		String expected = ClassPathEntryFinder
				.getPathContaining("testdata-good-deps")
				+ "/expectedWithRules.txt";
		assertDsm(expected, args);

	}

	private void assertDsm(String expectedPath, String[] args) {
		MockWriter writer = new MockWriter();
		new CommandLineApp(writer).run(args);
		String expected = FileUtil.readFile(expectedPath);
		assertEquals(expected.replaceAll("\r", ""), writer.getOutput()
				.replaceAll("\r", ""));
	}

}
