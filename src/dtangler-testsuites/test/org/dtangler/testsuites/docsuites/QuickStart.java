// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.testsuites.docsuites;

import static com.agical.bumblebee.junit4.Storage.store;

import org.dtangler.core.HelpText;
import org.junit.Test;

public class QuickStart {

	@Test
	public void summaryOfRunOptions() {
		/*!
		 >>>>
		 #{usage}
		 <<<<
		 #{assert.contains usage, ''}
		 */
		store("usage", HelpText.helpText);
	}
}
