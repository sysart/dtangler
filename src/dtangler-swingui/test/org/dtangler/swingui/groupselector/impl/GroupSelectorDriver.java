// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.ListBox;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;

public class GroupSelectorDriver {

	public final Button cancelButton;
	public final Button okButton;
	public final TextBox name;
	public final ListBox items;
	public final ListBox excludedItems;
	public final Button addItemButton;
	public final Button removeItemsButton;
	public final Button addExcludedItemButton;
	public final Button removeExcludedItemsButton;

	public GroupSelectorDriver(SwingView view) {
		Panel panel = new Panel(view.getViewComponent());
		cancelButton = panel.getButton("cancel");
		okButton = panel.getButton("ok");
		name = panel.getTextBox("nameField");
		items = panel.getListBox("itemsField");
		excludedItems = panel.getListBox("excludedItemsField");
		addItemButton = panel.getButton("addItem");
		removeItemsButton = panel.getButton("removeItems");
		addExcludedItemButton = panel.getButton("addExcludedItem");
		removeExcludedItemsButton = panel.getButton("removeExcludedItems");
	}

}
