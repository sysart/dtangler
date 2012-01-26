// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.groupselector.impl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.actionfactory.KeyActionAdapter;
import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.windowmanager.SwingBaseView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class SwingGroupView extends SwingBaseView implements GroupView {

	private final JButton okBtn;
	private final JButton cancelBtn;
	private final JButton addItemBtn;
	private final JButton removeItemBtn;
	private final JList items = new JList();
	private final JButton addExludedItemBtn;
	private final JButton removeExcludedItemBtn;
	private final JList excludedItems = new JList();
	private final JTextField name = new JTextField();

	protected SwingGroupView(ActionFactory actionFactory) {
		super(actionFactory);
		cancelBtn = createButton("Cancel", Actions.cancel);
		okBtn = createButton("Ok", Actions.ok);
		name.setName("nameField");
		name.getDocument().addDocumentListener(
				createDocumentChangeAdapter(Actions.updateActionStates));

		addItemBtn = createButton("Add...", Actions.addItem, IconKey.plus12);
		removeItemBtn = createButton("Remove", Actions.removeItems,
				IconKey.minus12);
		items.setName("itemsField");
		items.getSelectionModel().addListSelectionListener(
				createSelectionActionAdapter(Actions.updateActionStates));
		addExludedItemBtn = createButton("Add...", Actions.addExcludedItem,
				IconKey.plus12);
		removeExcludedItemBtn = createButton("Remove",
				Actions.removeExcludedItems, IconKey.minus12);
		excludedItems.setName("excludedItemsField");
		excludedItems.getSelectionModel().addListSelectionListener(
				createSelectionActionAdapter(Actions.updateActionStates));
		items.addKeyListener(new KeyActionAdapter(KeyEvent.VK_INSERT,
				actionFactory.getAction(Actions.addItem)));
		items.addKeyListener(new KeyActionAdapter(KeyEvent.VK_DELETE,
				actionFactory.getAction(Actions.removeItems)));
		excludedItems.addKeyListener(new KeyActionAdapter(KeyEvent.VK_INSERT,
				actionFactory.getAction(Actions.addExcludedItem)));
		excludedItems.addKeyListener(new KeyActionAdapter(KeyEvent.VK_DELETE,
				actionFactory.getAction(Actions.removeExcludedItems)));

	}

	@Override
	protected JComponent buildViewComponent() {
		DefaultFormBuilder builder = new DefaultFormBuilder(
				new FormLayout("fill:100dlu:grow",
						"p,2dlu,p,8dlu,p,2dlu,fill:60dlu:grow,8dlu,p,2dlu,fill:60dlu:grow,4dlu,p"));
		builder.setDefaultDialogBorder();
		builder.appendSeparator("Name * ");
		builder.nextRow();
		builder.append(name);
		builder.nextRow();
		builder.appendSeparator("Included items * (wildcards allowed)");
		builder.nextRow();
		builder.append(createItemsPanel(items, addItemBtn, removeItemBtn));
		builder.nextRow();
		builder
				.appendSeparator("Excluded items (Overwrites included items, wildcards allowed)");
		builder.nextRow();
		builder.append(createItemsPanel(excludedItems, addExludedItemBtn,
				removeExcludedItemBtn));
		builder.nextRow();
		builder.append(ButtonBarFactory.buildRightAlignedBar(okBtn, cancelBtn));
		JPanel panel = builder.getPanel();
		addCommonKeyEvent(panel, KeyEvent.VK_ENTER, Actions.ok);
		addCommonKeyEvent(panel, KeyEvent.VK_ESCAPE, Actions.cancel);
		return panel;
	}

	private JPanel createItemsPanel(JList list, JButton... buttons) {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:10dlu:grow,4dlu,p", "fill:60dlu:grow"));
		builder.append(new JScrollPane(list), createButtonStack(buttons));
		return builder.getPanel();

	}

	public Dimension getPreferredSize() {
		return new Dimension(420, 380);
	}

	public String getTitle() {
		return "New Group";
	}

	public String getName() {
		return name.getText();
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public void setItems(List<String> items) {
		this.items.setListData(items.toArray());
	}

	public void setExcludedItems(List<String> excludedItems) {
		this.excludedItems.setListData(excludedItems.toArray());
	}

	public List<String> getSelectedItems() {
		return new ArrayList(Arrays.asList(items.getSelectedValues()));
	}

	public List<String> getSelectedExcludedItems() {
		return new ArrayList(Arrays.asList(excludedItems.getSelectedValues()));
	}
}
