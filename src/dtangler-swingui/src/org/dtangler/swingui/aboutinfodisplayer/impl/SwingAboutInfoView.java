//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.aboutinfodisplayer.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dtangler.core.versioninfo.VersionInfo;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.windowmanager.SwingBaseView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class SwingAboutInfoView extends SwingBaseView implements AboutInfoView {

	private JButton okButton;

	protected SwingAboutInfoView(ActionFactory actionFactory) {
		super(actionFactory);
		okButton = createButton("Ok", Actions.close);
	}

	public Dimension getPreferredSize() {
		return null;
	}

	public String getTitle() {
		return "About dtangler";
	}

	public JComponent buildViewComponent() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:p:grow", "fill:p:grow,4dlu,p,p,4dlu,p,p,4dlu,p,p,4dlu,p"));
		builder.setDefaultDialogBorder();
		builder.append(createVersionPanel());
		builder.nextRow();
		builder.appendSeparator("License");
		builder.append(createLicensePanel());
		builder.nextRow();
		builder.appendSeparator("Credits");
		builder.append(createCreditsPanel());
		builder.nextRow();
		builder.appendSeparator("Sponsors");
		builder.append(createSponsorPanel());
		builder.nextRow();
		JPanel buttonBar = ButtonBarFactory.buildRightAlignedBar(okButton);
		buttonBar.setOpaque(false);
		builder.append(buttonBar);
		JPanel panel = builder.getPanel();
		addCommonKeyEvent(panel, KeyEvent.VK_ENTER, Actions.close);
		addCommonKeyEvent(panel, KeyEvent.VK_ESCAPE, Actions.close);
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		return panel;
	}

	private Component createVersionPanel() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("p",
				"p,10dlu,p,4dlu,p"));
		builder.setDefaultDialogBorder();
		builder.append(new JLabel(getIcon(IconKey.dtangler_logo)));
		builder.nextRow();
		builder.append("dtangler version " + VersionInfo.getVersionInfo()
				+ " (c) 2008 by contributors");
		builder.nextRow();
		builder
				.append("check www.dtangler.org for new versions and additional information");

		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		return panel;
	}

	private Component createLicensePanel() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("p",
				"p,0dlu,p,4dlu,p,0dlu,p"));
		builder.setDefaultDialogBorder();
		builder
				.append("dtangler is an Open Source initiative provided under the terms of");
		builder.nextRow();
		builder.append("EPL (Eclipse Public License)  version 1.0.");
		builder.nextRow();
		builder.append("The full license text can be read from:");
		builder.nextRow();
		builder.append("www.eclipse.org/org/documents/epl-v10.php");
		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		return panel;
	}

	private Component createCreditsPanel() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"p,12dlu,p,12dlu,p", "p"));
		builder.setDefaultDialogBorder();
		builder.append(new JLabel("Pertti Erkkil\u00E4"), new JLabel(
				"Pertti Lehtisaari"), new JLabel("Seppo Suorsa"));
		builder.append(new JLabel("Samuli J\u00E4rvel\u00E4"), new JLabel(
				"Marko Oikarinen"), new JLabel("Daniel Wellner"));
		builder.append(new JLabel("Kaisa Kittil\u00E4"), new JLabel("Annika Ruohtula"),
				new JLabel(""));

		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		return panel;
	}

	private Component createSponsorPanel() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout("p",
				"p,p"));
		builder.setDefaultDialogBorder();
		builder.append(new JLabel(getIcon(IconKey.sysart)));
		builder.append(new JLabel("www.sysart.fi"));
		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		return panel;
	}
}
