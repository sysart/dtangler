// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

import java.util.ArrayList;
import java.util.List;

public class DependencyPath {

	private final List<Dependable> items = new ArrayList<Dependable>();

	public DependencyPath() {
	}

	public DependencyPath(List<Dependable> items) {
		this.items.addAll(items);
	}

	public void addItem(Dependable item) {
		items.add(item);
	}

	public List<Dependable> getItems() {
		return items;
	}

	public Dependency getDependencyByDependant(Dependable dependant) {
		int index = items.indexOf(dependant);
		if (index < 0 || index > items.size() - 2)
			return null;
		return new Dependency(dependant, items.get(index + 1));
	}

	public Dependency getDependencyByDependee(Dependable dependee) {
		int index = items.indexOf(dependee);
		if (index < 1)
			return null;
		return new Dependency(items.get(index - 1), dependee);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DependencyPath))
			return false;
		DependencyPath other = (DependencyPath) obj;
		return items.equals(other.items);
	}

	@Override
	public int hashCode() {
		return items.hashCode();
	}

	public boolean contains(Dependable item) {
		return items.contains(item);
	}

}
