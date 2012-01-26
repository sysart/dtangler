// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.testsuites.rubycollector;

import java.io.File;

import org.dtangler.swingui.testutil.SnapShotTaker;

import com.agical.bumblebee.ruby.RubyCollector;

public class DtanglerRubyCollector extends RubyCollector {

	public DtanglerRubyCollector() {
		super(getSourceRoots());
		SnapShotTaker.setNapshotsEnabled(true);
	}

	private static File[] getSourceRoots() {
		String path = "../";
		String dtroot = System.getProperty("dtangler-root");
		if (dtroot != null)
			path = dtroot;
		File testsuites = new File(path + "/dtangler-testsuites/test");
		File core = new File(path + "/dtangler-core/test");
		File ui = new File(path + "/dtangler-ui/test");
		File swingui = new File(path + "/dtangler-swingui/test");
		return new File[] { testsuites, core, ui, swingui };
	}
}
