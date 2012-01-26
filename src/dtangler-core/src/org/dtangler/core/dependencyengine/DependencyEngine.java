// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencyengine;

import java.util.List;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;

public interface DependencyEngine {
	
	public enum ArgumentsMatch {
		yes, maybe, no;
	}

	void setDependencyEngineId(String dependencyEngineId);
	
	String getDependencyEngineId();

	Dependencies getDependencies(Arguments arguments);

	ArgumentsMatch getArgumentsMatchThisEngine(Arguments arguments);

	List<String> getInputFileNameExtensions();

	String getInputFilesDescription();

	boolean isDirectoryInputSupported();

}
