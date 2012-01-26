// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.dependencyengine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dependencyengine.AbstractDependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.exception.DtException;
import org.dtangler.core.filefinder.FullPathWildCardFileFilter;
import org.dtangler.core.filefinder.RecursiveFileFinder;
import org.dtangler.javaengine.classfileparser.ClassFileParser;
import org.dtangler.javaengine.jarfileparser.JarFileParser;
import org.dtangler.javaengine.types.JavaClass;
import org.dtangler.javaengine.types.JavaScope;

public class JavaDependencyEngine extends AbstractDependencyEngine {

	private Scope getDefaultScope(Arguments arguments) {
		if (JavaScope.classes.getDisplayName().equalsIgnoreCase(
				arguments.getScope()))
			return JavaScope.classes;
		if (JavaScope.locations.getDisplayName().equalsIgnoreCase(
				arguments.getScope()))
			return JavaScope.locations;
		return JavaScope.packages;
	}

	public Dependencies getDependencies(Arguments arguments) {
		Dependencies dependencies = new ClassDependencies(getJavaClasses(arguments))
				.getDependencies();
		dependencies.setDefaultScope(getDefaultScope(arguments));
		return dependencies;
	}

	public ArgumentsMatch getArgumentsMatchThisEngineExt(Arguments arguments) {
		if (arguments == null)
			throw new DtException("invalid arguments: null");
		DependencyEngine.ArgumentsMatch argumentsMatch = ArgumentsMatch.no;
		for (String path : arguments.getInput()) {
			if (path == null)
				continue;
			if (path.toLowerCase().endsWith(".jar")
					|| path.toLowerCase().endsWith(".class")) {
				argumentsMatch = DependencyEngine.ArgumentsMatch.yes;
			} else {
				File file = new File(path);
				if (!file.isDirectory()) {
					return DependencyEngine.ArgumentsMatch.no;
				} else {
					if (argumentsMatch == ArgumentsMatch.no)
						argumentsMatch = ArgumentsMatch.maybe;
				}
			}
		}
		return argumentsMatch;
	}

	public List<String> getInputFileNameExtensions() {
		return Arrays.asList("jar", "class");
	}

	public boolean isDirectoryInputSupported() {
		return true;
	}

	private Set<JavaClass> getJavaClasses(Arguments arguments) {
		RecursiveFileFinder fileFinder = new RecursiveFileFinder();
		fileFinder.setFilter(new FullPathWildCardFileFilter(Arrays.asList(
				".class", ".jar"), arguments.getIgnoredFileMasks()));
		for (String path : arguments.getInput())
			fileFinder.findFiles(path);

		Set<JavaClass> classes = new HashSet<JavaClass>();
		for (File file : fileFinder.getFiles()) {
			if (file.getName().endsWith(".class")) {
				classes.add(getDataFromClassFile(file, fileFinder));
			} else {
				classes.addAll(getDataFromJarFile(file, fileFinder));
			}
		}
		return classes;
	}

	private Set<JavaClass> getDataFromJarFile(File file,
			RecursiveFileFinder fileFinder) {
		try {
			Set<JavaClass> jarContents = new JarFileParser().parse(file);
			for (JavaClass clazz : jarContents) {
				clazz.setLocation(file.getAbsolutePath());
			}
			return jarContents;
		} catch (IOException e) {
			throw new DtException("Jar file could not be read: "
					+ file.getAbsolutePath(), e);
		}
	}

	private JavaClass getDataFromClassFile(File file,
			RecursiveFileFinder fileFinder) {
		JavaClass parsed = new ClassFileParser().parse(file);
		parsed.setLocation(fileFinder.getFilesWithPaths().get(file));
		return parsed;
	}

	public String getInputFilesDescription() {
		return "Java binary file";
	}

}
