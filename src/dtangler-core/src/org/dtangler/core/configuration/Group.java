// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.configuration;

import java.util.Collections;
import java.util.Set;

public class Group {
	private String name;
	private Set<String> groupItems;
	private Set<String> excludedItems = Collections.EMPTY_SET;

	public Group(String name, Set<String> groupItems) {
		this.name = name;
		this.groupItems = groupItems;
	}

	public Group(String name, Set<String> groupItems, Set<String> excludedItems) {
		this.name = name;
		this.groupItems = groupItems;
		this.excludedItems = excludedItems;
	}

	public String getName() {
		return name;
	}

	public Set<String> getGroupItems() {
		return groupItems;
	}

	public Set<String> getExcludedItems() {
		return excludedItems;
	}

	@Override
	public String toString() {
		return "Group " + name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((groupItems == null) ? 0 : groupItems.hashCode())
				+ ((excludedItems == null) ? 0 : excludedItems.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Group))
			return false;
		final Group other = (Group) obj;
		return (groupItems.equals(other.groupItems)
				&& (name.equals(other.name)) && (excludedItems
				.equals(other.excludedItems)));
	}
}
