// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.dependencyengine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.javaengine.types.JavaClass;
import org.dtangler.javaengine.types.JavaScope;

public class ClassDependencies {

	private final Map<String, JavaClass> classes;
	private final Map<String, Dependable> packages = new HashMap();
	private final Map<String, Dependable> locations = new HashMap();

	public ClassDependencies(Set<JavaClass> classes) {
		Set<JavaClass> normalizedClasses = new InnerClassNormalizer()
				.normalize(classes);
		this.classes = getFullClassNames(normalizedClasses);
		setUpperScopes(normalizedClasses);
	}

	private Map<String, JavaClass> getFullClassNames(Set<JavaClass> classes) {
		Map<String, JavaClass> map = new HashMap<String, JavaClass>();
		for (JavaClass clazz : classes) {
			map.put(clazz.getFullName(), clazz);
		}
		return map;
	}

	private void setUpperScopes(Set<JavaClass> classes) {
		Map<String, Dependable> pkgs = new HashMap();
		Map<String, Dependable> locs = new HashMap();
		for (JavaClass clazz : classes) {
			setPackageNames(pkgs, clazz);
			setLocationNames(locs, clazz);
		}
		this.packages.putAll(pkgs);
		this.locations.putAll(locs);
	}

	private void setPackageNames(Map<String, Dependable> pkgs, JavaClass clazz) {
		String package1 = clazz.getPackage();
		Dependable pkg = pkgs.get(package1);
		if (pkg == null) {
			String fullName = clazz.getLocation() + ": " + package1;
			pkg = new Dependable(JavaScope.packages, fullName, package1, 0);
			pkgs.put(package1, pkg);
		}
		pkg.setContentCount(pkg.getContentCount() + 1);
	}

	private void setLocationNames(Map<String, Dependable> locs, JavaClass clazz) {
		String location = clazz.getLocation();
		Dependable loc = locations.get(location);
		if (loc == null) {
			String fullName = clazz.getLocation();
			loc = new Dependable(JavaScope.locations, fullName, location, 0);
			locations.put(location, loc);
		}
	}

	public Dependencies getDependencies() {
		Dependencies deps = new Dependencies();
		for (JavaClass javaClass : classes.values()) {
			Dependable clazz = javaClass.toDependable();
			Dependable pkg = getPackageByName(javaClass.getPackage());
			Dependable loc = getLocationByName(javaClass.getLocation());
			deps.addDependencies(clazz, getDependencies(javaClass));
			deps.addChild(pkg, clazz);
			deps.addChild(loc, pkg);
			loc.setContentCount(deps.getChilds(loc).size());
		}
		return deps;
	}

	private Map<Dependable, Integer> getDependencies(JavaClass javaClass) {
		Map<Dependable, Integer> dependencies = new HashMap();
		Map<String, Integer> javaDeps = javaClass.getDependencies();
		for (String depName : javaDeps.keySet()) {
			JavaClass dep = classes.get(depName);
			if (dep == null)
				continue; // outer dep
			dependencies.put(dep.toDependable(), javaDeps.get(depName));
		}
		return dependencies;
	}

	protected Dependable getPackageByName(String name) {
		return packages.get(name);
	}

	protected Dependable getLocationByName(String name) {
		return locations.get(name);
	}
}
