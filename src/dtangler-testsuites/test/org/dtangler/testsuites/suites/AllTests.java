//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.testsuites.suites;

import java.util.List;

import org.dtangler.testcollectorrunner.ClassPathTestCollector;
import org.dtangler.testcollectorrunner.TestCollection;
import org.dtangler.testcollectorrunner.TestCollectionRunner;
import org.junit.runner.RunWith;

@RunWith(TestCollectionRunner.class)
public class AllTests implements TestCollection {

	public List<Class> testClasses() {
		return new ClassPathTestCollector("org.dtangler").testClasses();
	}

}
