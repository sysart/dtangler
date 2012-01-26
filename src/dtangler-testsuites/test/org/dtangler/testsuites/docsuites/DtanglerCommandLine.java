// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.testsuites.docsuites;

import org.dtangler.core.acceptancetests.input.ConfigurationParsingAcceptanceTest;
import org.dtangler.core.acceptancetests.input.GenericEngineArgumentParsingAcceptanceTest;
import org.dtangler.core.acceptancetests.input.JavaArgumentParsingAcceptanceTest;
import org.dtangler.core.acceptancetests.input.SetupParsingAcceptanceTest;
import org.dtangler.core.acceptancetests.validation.ReturnValuesAcceptanceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.agical.bumblebee.junit4.BumbleBeeSubSuiteRunner;

@RunWith(BumbleBeeSubSuiteRunner.class)
@SuiteClasses( { QuickStart.class, SetupParsingAcceptanceTest.class,
		ConfigurationParsingAcceptanceTest.class,
		JavaArgumentParsingAcceptanceTest.class,
		GenericEngineArgumentParsingAcceptanceTest.class,
		ReturnValuesAcceptanceTest.class })
public class DtanglerCommandLine {

	/*!!
	#{set_header 'Command line tool'} 
	
	This section explains how to use the command line version of dtangler. You can either
	provide your configuration straight from the command line or from a properties 
	file. Command line parameters *override* any parameters of the same type given in the 
	properties file.
	 
	dtangler outputs a DSM of the dependencies and reports all encountered violations. 
	The exit value is non-zero if violations were found.
	
	dtangler is run from the command line by entering:
	>>>>
	java -jar dtangler-core.jar <list of run options>
	<<<<
	 
	 */
}
