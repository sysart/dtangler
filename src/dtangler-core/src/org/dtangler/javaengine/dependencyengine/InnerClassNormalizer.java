// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.dependencyengine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.javaengine.types.JavaClass;

public class InnerClassNormalizer {

	public Set<JavaClass> normalize(Set<JavaClass> classes) {

		Map<String, Set<JavaClass>> allInnerClasses = getInnerClasses(classes);

		Set<JavaClass> result = new HashSet();
		for (JavaClass clazz : classes) {
			if (clazz.isInnerClass())
				continue;

			Set<JavaClass> innerClasses = allInnerClasses.get(clazz
					.getBaseClassName());
			if (innerClasses != null)
				clazz.addInnerClasses(innerClasses);
			result.add(clazz);
		}
		return result;
	}

	private Map<String, Set<JavaClass>> getInnerClasses(Set<JavaClass> classes) {
		Map<String, Set<JavaClass>> result = new HashMap();
		for (JavaClass clazz : classes) {
			if (!clazz.isInnerClass())
				continue;
			Set<JavaClass> innerClasses = result.get(clazz.getBaseClassName());
			if (innerClasses == null) {
				innerClasses = new HashSet();
				result.put(clazz.getBaseClassName(), innerClasses);
			}
			innerClasses.add(clazz);
		}
		return result;

	}
}
