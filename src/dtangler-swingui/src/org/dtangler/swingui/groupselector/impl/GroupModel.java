// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.configuration.Group;
import org.dtangler.swingui.textinput.TextInputSelector;

public class GroupModel {

	private final TextInputSelector textInputSelector;
	private final Set<String> items = new HashSet();
	private final Set<String> excludedItems = new HashSet();
	private String name;
	private Group group;

	public GroupModel(TextInputSelector textInputSelector, Group group) {
		this.textInputSelector = textInputSelector;
		if (group != null) {
			this.name = group.getName();
			this.items.addAll(group.getGroupItems());
			this.excludedItems.addAll(group.getExcludedItems());
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void save() {
		group = new Group(name, items, excludedItems);
	}

	public Group getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public List<String> getItems() {
		List<String> result = new ArrayList(items);
		Collections.sort(result);
		return result;
	}

	public List<String> getExcludedItems() {
		List<String> result = new ArrayList(excludedItems);
		Collections.sort(result);
		return result;
	}

	public void addItem() {
		String newValue = textInputSelector.selectValue(
				"Group member (wildcards allowed)", "Add group member");
		if (newValue != null)
			this.items.add(newValue);
	}

	public void removeItems(List<String> items) {
		this.items.removeAll(items);
	}

	public void removeExcludedItems(List<String> items) {
		this.excludedItems.removeAll(items);
	}

	public void addExcludedItem() {
		String newValue = textInputSelector.selectValue(
				"Exluded Group member (wildcards allowed)",
				"Add excluded group member");
		if (newValue != null)
			excludedItems.add(newValue);
	}
}
