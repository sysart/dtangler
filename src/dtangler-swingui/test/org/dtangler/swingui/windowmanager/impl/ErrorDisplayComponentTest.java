//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uispec4j.Panel;

public class ErrorDisplayComponentTest {

	@Test
	public void testDetailsContentForExceptionWithoutMessage() {
		Throwable ex = new IllegalArgumentException();
		ex
				.setStackTrace(new StackTraceElement[] {
						new StackTraceElement("myclass1", "myMethod1",
								"myfile1", 20),
						new StackTraceElement("myclass2", "myMethod2",
								"myfile2", 123) });

		String details = getDetails(ex);
		assertEquals(
				"IllegalArgumentException\nat:\nmyclass1.myMethod1(myfile1:20)\nmyclass2.myMethod2(myfile2:123)\n",
				details);
	}

	@Test
	public void testDetailsContentForExceptionWithMessage() {
		Throwable ex = new IllegalStateException("My Message");
		ex.setStackTrace(new StackTraceElement[] { new StackTraceElement(
				"myclass3", "myMethod3", "myfile3", 456) });

		String details = getDetails(ex);
		assertEquals(
				"IllegalStateException\nmessage: My Message\nat:\nmyclass3.myMethod3(myfile3:456)\n",
				details);
	}

	private String getDetails(Throwable ex) {
		Panel panel = new Panel(new ErrorDisplayComponent(ex)
				.getViewComponent());
		String details = panel.getTextBox("detailsField").getText();
		return details;
	}
}
