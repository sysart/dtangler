// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.windowmanager.SwingBaseView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class SwingRuleMemberView extends SwingBaseView implements
		RuleMemberView {

	private final JComboBox groups = new JComboBox();
	private final JTextField literal = new JTextField();
	private final JRadioButton groupRadio;
	private final JRadioButton literalRadio;
	private final JButton okButton;
	private final JButton cancelButton;

	protected SwingRuleMemberView(ActionFactory actionFactory) {
		super(actionFactory);
		ButtonGroup btnGroup = new ButtonGroup();
		groupRadio = createRadioButton("Group", btnGroup);
		literalRadio = createRadioButton("Item by name", btnGroup);
		literal.setName("literal");
		okButton = createButton("Ok", Actions.ok);
		cancelButton = createButton("Cancel", Actions.cancel);
		literalRadio.setSelected(true);
		literal.getDocument().addDocumentListener(
				createDocumentChangeAdapter(Actions.updateActionStates));
		groups.addActionListener(getAction(Actions.updateActionStates));
		updatRuleMemberMode();
	}

	private JRadioButton createRadioButton(String name, ButtonGroup buttonGroup) {
		JRadioButton btn = new JRadioButton(name);
		buttonGroup.add(btn);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatRuleMemberMode();
			}
		});
		return btn;
	}

	private void updatRuleMemberMode() {
		literal.setEnabled(literalRadio.isSelected());
		groups.setEnabled(groupRadio.isSelected());
		getAction(Actions.updateActionStates).actionPerformed(null);
	}

	@Override
	protected JComponent buildViewComponent() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"p,4dlu,fill:150dlu:grow", "p,4dlu,p,4dlu, p"));
		builder.setDefaultDialogBorder();
		builder.append(literalRadio, literal);
		builder.nextRow();
		builder.append(groupRadio, groups);
		builder.nextRow();
		builder.append("", ButtonBarFactory.buildRightAlignedBar(okButton,
				cancelButton));
		JPanel panel = builder.getPanel();
		addCommonKeyEvent(panel, KeyEvent.VK_ENTER, Actions.ok);
		addCommonKeyEvent(panel, KeyEvent.VK_ESCAPE, Actions.cancel);
		return panel;
	}

	public Dimension getPreferredSize() {
		return null;
	}

	public String getTitle() {
		return "Rule member selection";
	}

	public String getLiteral() {
		return literal.getText();
	}

	public String getSelectedGroup() {
		return (String) groups.getSelectedItem();
	}

	public MemberType getSelectedMemberType() {
		if (groupRadio.isSelected())
			return MemberType.group;
		return MemberType.literal;
	}

	public void setGroupNames(List<String> groupNames) {
		groups.removeAllItems();
		for (String name : groupNames)
			groups.addItem(name);
		groupRadio.setEnabled(!groupNames.isEmpty());
	}

	@Override
	public Component getFirstComponentToFocus() {
		return literal;
	}

}
