// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.dependencies.Dependable;

public class JavaClass {

	private static final String OBJECT = "java.lang.Object";
	private final Map<String, Integer> dependencies = new HashMap();
	private final String className;
	private boolean isAbstract;
	private String sourceFileName;
	private int contentCount = 1;
	private String location = "";

	public JavaClass(String className) {
		this.className = className;
	}

	public void setAbstract(boolean b) {
		isAbstract = b;
	}

	public void setSourceFile(String sourceFile) {
		sourceFileName = sourceFile;
	}

	public void addDependency(String className) {
		if (className.equals(this.className))
			return; // offcourse we depend on ourselves.
		if (className.equals(OBJECT))
			return; // Every class depends on Object
		String baseClassName = getBaseClassName(className);
		Integer weight = dependencies.get(baseClassName);
		if (weight == null) {
			dependencies.put(baseClassName, 1);
		} else {
			dependencies.put(baseClassName, ++weight);
		}
	}

	public String getFullName() {
		return className;
	}

	public String getBaseClassName() {
		return getBaseClassName(getFullName());
	}

	private String getBaseClassName(String fullName) {
		if (!isInnerClass(fullName))
			return fullName;
		return fullName.split("\\$")[0];
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public Map<String, Integer> getDependencies() {
		return dependencies;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public String getName() {
		int lastDotIndex = className.lastIndexOf(".");
		if (lastDotIndex >= 0 && lastDotIndex < className.length())
			return className.substring(lastDotIndex + 1);
		return className;
	}

	public String getPackage() {
		int lastDotIndex = className.lastIndexOf(".");
		if (lastDotIndex >= 0 && lastDotIndex < className.length())
			return className.substring(0, lastDotIndex);
		return "default";
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JavaClass))
			return false;
		JavaClass other = (JavaClass) obj;
		return getFullName().equals(other.getFullName());
	}

	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}

	@Override
	public String toString() {
		return String.format("JavaClass[%s]", getFullName());
	}

	public Dependable toDependable() {
		String fullName = getLocation() + ": " + getPackage() + "." + getName();
		return new Dependable(JavaScope.classes, fullName, getName(),
				contentCount);
	}

	public boolean isInnerClass() {
		return isInnerClass(getFullName());
	}

	private boolean isInnerClass(String fullName) {
		return fullName.contains("$");
	}

	public void addInnerClass(JavaClass innerClass) {
		for (String dep : innerClass.getDependencies().keySet())
			addDependency(dep);
		contentCount++;
	}

	public void addInnerClasses(Set<JavaClass> innerClasses) {
		for (JavaClass clazz : innerClasses)
			addInnerClass(clazz);
	}
}
