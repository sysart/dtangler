package org.dtangler.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

/**
 * Bugfixes:
 * <ul>
 * <li>command line app returned EXITCODE_SUCCESS when given empty args.
 * <li>command line app did not write error messages to the error output.
 * </ul>
 */
public class MainTest {
	private OutputStream stdout;
	private OutputStream stderr;

	@Before
	public void setup() {
		stdout = new ByteArrayOutputStream();
		stderr = new ByteArrayOutputStream();
	}

	@Test
	public void testRunWithNoArgumentsPrintsHelpText() {
		int exitCode = Main.run(new String[] {}, new PrintStream(stdout),
				new PrintStream(stderr));
		assertEquals(Main.EXITCODE_PROBLEM, exitCode);
		assertEquals(HelpText.helpText, errorOutput());
	}

	@Test
	public void testErrorMessageGoesToErrorOutput() {
		int exitCode = Main.run(new String[] { "-configFile=" },
				new PrintStream(stdout), new PrintStream(stderr));
		assertEquals(Main.EXITCODE_PROBLEM, exitCode);
		assertTrue(errorOutput().contains("config file not found"));
	}

	@Test
	public void testInternalErrorMessageGoesToErrorOutput() {
		PrintStream brokenOutput = new PrintStream(stdout) {
			@Override
			public void println(String x) {
				throw new UnsupportedOperationException("Kaboom!");
			}
		};
		int exitCode = Main.run(new String[] {}, brokenOutput, new PrintStream(
				stderr));
		assertEquals(Main.EXITCODE_PROBLEM, exitCode);
		assertTrue(errorOutput().contains("Kaboom!"));
		assertTrue(errorOutput().contains(
				UnsupportedOperationException.class.getName()));

	}

	private String errorOutput() {
		return stderr.toString();
	}
}
