// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulesselector.impl;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.actionfactory.ActionKey;
import org.dtangler.swingui.actionfactory.KeyActionAdapter;
import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.rulesselector.impl.RulesView.Actions;
import org.dtangler.swingui.rulesselector.impl.RulesView.RuleView;
import org.dtangler.swingui.windowmanager.SwingBaseView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

public class SwingRuleView extends SwingBaseView implements RuleView {

	private final JButton addRuleBtn;
	private final JButton removeRuleBtn;
	private final JButton addRuleItemBtn;
	private final JButton removeRuleItemsBtn;

	private final JList rules = new JList();
	private final JList ruleItems = new JList();

	private final String tabTitle;
	private final String rulesLabel;
	private final String ruleItemsLabel;

	public SwingRuleView(ActionFactory actionFactory, String tabTitle,
			String rulesLabel, String ruleItemsLabel, ActionKey addRule,
			ActionKey removeRule, ActionKey addRuleItem,
			ActionKey removeRuleItems, ActionKey ruleSelectionChanged) {
		super(actionFactory);
		this.tabTitle = tabTitle;
		this.rulesLabel = rulesLabel;
		this.ruleItemsLabel = ruleItemsLabel;
		addRuleBtn = createButton("Add...", addRule, IconKey.plus12);
		removeRuleBtn = createButton("Remove", removeRule, IconKey.minus12);
		addRuleItemBtn = createButton("Add...", addRuleItem, IconKey.plus12);
		removeRuleItemsBtn = createButton("Remove", removeRuleItems,
				IconKey.minus12);
		rules.setName("rules");
		rules.getSelectionModel().addListSelectionListener(
				createSelectionActionAdapter(Actions.updateActionStates));
		rules.getSelectionModel().addListSelectionListener(
				createSelectionActionAdapter(ruleSelectionChanged));
		ruleItems.setName("ruleitems");
		ruleItems.getSelectionModel().addListSelectionListener(
				createSelectionActionAdapter(Actions.updateActionStates));
		rules.addKeyListener(new KeyActionAdapter(KeyEvent.VK_INSERT,
				actionFactory.getAction(addRule)));
		rules.addKeyListener(new KeyActionAdapter(KeyEvent.VK_DELETE,
				actionFactory.getAction(removeRule)));
		ruleItems.addKeyListener(new KeyActionAdapter(KeyEvent.VK_INSERT,
				actionFactory.getAction(addRuleItem)));
		ruleItems.addKeyListener(new KeyActionAdapter(KeyEvent.VK_DELETE,
				actionFactory.getAction(removeRuleItems)));

	}

	public List<String> getSelectedRules() {
		return new ArrayList(Arrays.asList(rules.getSelectedValues()));
	}

	public void setRules(List<String> rules) {
		this.rules.setListData(rules.toArray());
	}

	public void setRuleItems(List<String> ruleItems) {
		this.ruleItems.setListData(ruleItems.toArray());
	}

	public List<String> getSelectedRuleItems() {
		return new ArrayList(Arrays.asList(ruleItems.getSelectedValues()));
	}

	@Override
	protected JComponent buildViewComponent() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:10dlu:grow,4dlu,p",
				"p,4dlu,fill:50dlu:grow,20dlu,p,4dlu,fill:50dlu:grow"));
		builder.setDefaultDialogBorder();
		builder.append(new JLabel(rulesLabel), 3);
		builder.nextRow();
		builder.append(new JScrollPane(rules), createButtonStack(addRuleBtn,
				removeRuleBtn));
		builder.nextRow();
		builder.append(new JLabel(ruleItemsLabel), 3);
		builder.nextRow();
		builder.append(new JScrollPane(ruleItems), createButtonStack(
				addRuleItemBtn, removeRuleItemsBtn));
		JPanel panel = builder.getPanel();
		panel.setName(tabTitle);
		return panel;
	}

	public Dimension getPreferredSize() {
		return null;
	}

	public String getTitle() {
		return tabTitle;
	}

}
