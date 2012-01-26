// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.testsuites.docsuites;

import org.dtangler.swingui.mainview.impl.BasicFeaturesTest;
import org.dtangler.swingui.mainview.impl.DsmInteractionTest;
import org.dtangler.swingui.mainview.impl.ManagingConfigurationsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.agical.bumblebee.junit4.BumbleBeeSubSuiteRunner;

@RunWith(BumbleBeeSubSuiteRunner.class)
@SuiteClasses( { BasicFeaturesTest.class, ManagingConfigurationsTest.class,
		DsmInteractionTest.class })
public class DtanglerGui {
	/*!!  
	#{set_header 'GUI'}
	  
	The dtangler GUI is an interactive, graphical standalone tool to help you
	analyze dependencies. The GUI can also be used to create configurations
	that can be used with the command line dtangler to automatically analyze
	dependencies. This is particularly useful when this is done through
	continuous integration.
	*/
}
