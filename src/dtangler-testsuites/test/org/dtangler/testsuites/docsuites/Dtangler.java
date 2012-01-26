// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.testsuites.docsuites;

import org.dtangler.testsuites.rubycollector.DtanglerRubyCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.agical.bumblebee.collector.BumblebeeCollectors;
import com.agical.bumblebee.junit4.BumbleBeeSuiteRunner;

@RunWith(BumbleBeeSuiteRunner.class)
@SuiteClasses( { DtanglerCommandLine.class, DtanglerGui.class })
@BumblebeeCollectors(DtanglerRubyCollector.class)
public class Dtangler {

	/*!!

	#{versionFilePath = '/dtangler-core/src/org/dtangler/core/versioninfo/VersionInfo.java';''}
	#{versionFile=(File.exist?('..' + versionFilePath) ? File.new( '..' + versionFilePath) : File.new( '.' + versionFilePath));''}
	#{versionNumber=versionFile.read.match('VERSION_INFO = \"(.*)\"')[1];''}
	#{configuration.document_title='dtangler documentation ' + versionNumber;''}
	#{set_header 'dtangler ' + versionNumber}
	
	
	dtangler is a tool for analysing dependencies and producing DSMs (*Dependency
	Structure Matrices*). Currently, dtangler supports the analysis of Java classes
	(from .class or .jar files) and dependency definition files (.dt).
				
	
	With dtangler, you can define project-specific *design rules* that
	specify what dependencies are allowed, and what aren't. This encourages
	SW architects to consider and communicate the dependency structure in detail,
	and helps identify any possible deviations from the defined rules.
	
	
	dtangler comes with a command line UI and a GUI. The command line version 
	is mostly useful when integrating dtangler with automated builds. Its return
	value tells whether or not the analysed input contains violations. The GUI 
	version can be used to examine DSMs in various detail levels.  
	
	
	The [[http://www.dtangler.org/about_dependencies][about dependencies page]]
	has more background information on dependencies and DSMs; the purpose of this 
	document is to describe how to use dtangler. 
	 

	*/
}
