//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dependencies.Dependencies.DependencyFilter;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.actionfactory.ActionKey;
import org.dtangler.swingui.dsm.DsmView;
import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.windowmanager.SwingBaseView;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

public class SwingMainView extends SwingBaseView implements MainView {

	public static final String menuItemZoomInNameHeader = "Zoom in (show contents)";
	public static final String menuItemZoomInNameData = "Zoom in (show dependencies)";

	private final JList selectionViolations = new JList();
	private final JList allViolations = new JList();
	private final JButton newButton;
	private final JButton rulesButton;
	private final JButton refreshButton;
	private final DsmView dsmView;
	private final JComboBox scopeCombo = new JComboBox();
	private final JToggleButton shortNameButton;
	private final JMenuItem shortNameMenuItem;
	private String fileName;

	public SwingMainView(ActionFactory actionFactory, DsmView dsmView) {
		super(actionFactory);
		this.dsmView = dsmView;
		newButton = createButton("Input...", Actions.input, IconKey.input24);
		rulesButton = createButton("Rules...", Actions.rules, IconKey.police24);
		refreshButton = createButton("Refresh", Actions.refresh,
				IconKey.refresh24);
		dsmView.getJComponent().addMouseListener(createDoubleClickAdapter(Actions.zoomInDependencies));
		scopeCombo.addActionListener(getAction(Actions.changeScope));
		selectionViolations.setName("selectionViolations");
		allViolations.setName("allViolations");
		shortNameButton = createToolToggleButton(Actions.toggleShortName,
				IconKey.shorten16, "Show shortened names");
		shortNameMenuItem = createCheckBoxMenuItem("Show shortened names", 'n',
				Actions.toggleShortName, IconKey.shorten16, null);
	}

	private JPopupMenu createPopupMenu(Dependencies.DependencyFilter dependencyFilter) {
		JPopupMenu popupMenu = new JPopupMenu();
		if (dependencyFilter == DependencyFilter.itemsContributingToTheParentDependencyWeight) {
			popupMenu.add(createMenuItem(menuItemZoomInNameData, 'i', Actions.zoomInDependencies,
					IconKey.zoomin16, null));
		} else {
			popupMenu.add(createMenuItem(menuItemZoomInNameHeader, 'i', Actions.zoomInContents,
					IconKey.zoomin16, null));			
		}
		popupMenu.add(createMenuItem("Zoom out", 'o', Actions.zoomOut,
				IconKey.zoomout16, null));
		popupMenu.addSeparator();
		popupMenu.add(createMenuItem("Add to forbidden dependencies", 'f',
				Actions.addforbiddendeps, null));
		return popupMenu;
	}

	protected JComponent buildViewComponent() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:10dlu:grow", "fill:10dlu:grow,4dlu,p"));
		builder.setDefaultDialogBorder();
		builder.append(createSplitPane());
		builder.nextRow();
		builder.append(ButtonBarFactory.buildLeftAlignedBar(newButton,
				rulesButton, refreshButton));
		return builder.getPanel();
	}

	private Component createSplitPane() {
		JSplitPane splitPane = createSplitPane(JSplitPane.VERTICAL_SPLIT,
				createDsmView(), createViolationsPanel());
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(370);
		return splitPane;
	}

	private Component createDsmView() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:10dlu:grow", "p,4dlu,fill:10dlu:grow"));
		builder.append(createScopePanel());
		builder.nextRow();
		JScrollPane scrollPane = new JScrollPane(dsmView.getJComponent());
		builder.append(scrollPane);
		
		JPopupMenu popupMenuForHeaderCells = createPopupMenu(DependencyFilter.none);
		JPopupMenu popupMenuForDataCells = createPopupMenu(DependencyFilter.itemsContributingToTheParentDependencyWeight);
		dsmView.setPopupMenuForDataCells(popupMenuForDataCells);
		dsmView.setPopupMenuForHeaderCells(popupMenuForHeaderCells);
		dsmView.refreshPopupMenu();
		scrollPane.setComponentPopupMenu(popupMenuForHeaderCells);

		return builder.getPanel();
	}

	private Component createScopePanel() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"p,4dlu,max(60dlu;p),4dlu,20,2dlu,20,4dlu,20", "fill:p"));
		JButton zoomInBtn = createToolButton(Actions.zoomIn, IconKey.zoomin16,
				"Zoom in onto selection");
		JButton zoomOutBtn = createToolButton(Actions.zoomOut,
				IconKey.zoomout16, "Zoom out");
		builder.append("Scope");
		builder.append(scopeCombo);
		builder.append(zoomInBtn, zoomOutBtn);
		builder.append(shortNameButton);
		return builder.getPanel();
	}

	private JButton createToolButton(ActionKey actionKey, IconKey iconKey,
			String tooltipText) {
		JButton btn = new JButton(getAction(actionKey));
		btn.setName(actionKey.name());
		btn.setToolTipText(tooltipText);
		btn.setIcon(getIcon(iconKey));
		btn.setBorder(null);
		btn.setFocusable(false);
		return btn;
	}

	private JToggleButton createToolToggleButton(ActionKey actionKey,
			IconKey iconKey, String tooltipText) {
		JToggleButton btn = new JToggleButton(getAction(actionKey));
		btn.setName(actionKey.name());
		btn.setToolTipText(tooltipText);
		btn.setIcon(getIcon(iconKey));
		btn.setFocusable(false);
		btn.setBorder(null);
		return btn;
	}

	private JSplitPane createSplitPane(int orientation,
			Component firstComponent, Component secondComponent) {
		JSplitPane splitpane = new JSplitPane(orientation, firstComponent,
				secondComponent);
		splitpane.setUI(new BasicSplitPaneUI() {
			public void installUI(JComponent c) {
				super.installUI(c);
				divider.setBorder(null);
			}
		});
		splitpane.setBorder(null);
		return splitpane;
	}

	private Component createViolationsPanel() {
		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
				"fill:10dlu:grow", "p,fill:30dlu:grow"));
		builder.appendSeparator("Violations");
		builder.append(buildViolationsTabGroup());
		return builder.getPanel();
	}

	private Component buildViolationsTabGroup() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("All violations", createAllViolationsPanel());
		tabbedPane.addTab("Selected violations", createCellViolationsPanel());
		return tabbedPane;
	}

	private Component createCellViolationsPanel() {
		return new JScrollPane(selectionViolations);
	}

	private Component createAllViolationsPanel() {
		return new JScrollPane(allViolations);
	}

	public void setSelectionViolations(List<String> violations) {
		selectionViolations.setListData(violations.toArray());
	}

	public void setAllViolations(List<String> violations) {
		allViolations.setListData(violations.toArray());
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	public String getTitle() {
		StringBuilder sb = new StringBuilder();
		sb.append("DTangler DsmUI");
		if (fileName != null) {
			sb.append(" - ");
			sb.append(fileName);
		}
		return sb.toString();
	}

	public JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(createMenu("File", 'f'));
		fileMenu.add(createMenuItem("New", 'n', Actions.clear, KeyStroke
				.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)));
		fileMenu.add(createMenuItem("Open...", 'o', Actions.open, KeyStroke
				.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)));
		fileMenu.add(createMenuItem("Save", 's', Actions.save, KeyStroke
				.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)));
		fileMenu
				.add(createMenuItem("Save as...", 'a', Actions.saveas,
						KeyStroke.getKeyStroke(KeyEvent.VK_S,
								InputEvent.SHIFT_DOWN_MASK
										| InputEvent.CTRL_DOWN_MASK)));
		fileMenu.addSeparator();
		fileMenu.add(createMenuItem("Exit", 'x', Actions.exit, null));

		JMenu dsmMenu = menuBar.add(createMenu("Model", 'd'));
		dsmMenu.add(createMenuItem("Refresh", 'f', Actions.refresh, KeyStroke
				.getKeyStroke(KeyEvent.VK_F5, 0)));
		dsmMenu.addSeparator();
		dsmMenu
				.add(createMenuItem("Dependency input...", 'i', Actions.input,
						KeyStroke.getKeyStroke(KeyEvent.VK_I,
								InputEvent.CTRL_DOWN_MASK)));
		dsmMenu.add(createMenuItem("Rules...", 'r', Actions.rules, KeyStroke
				.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)));

		JMenu viewMenu = menuBar.add(createMenu("View", 'd'));
		viewMenu.add(shortNameMenuItem);
		viewMenu.addSeparator();
		viewMenu.add(createMenuItem("Zoom in onto selection", 'i',
				Actions.zoomIn, IconKey.zoomin16, KeyStroke.getKeyStroke('+')));
		viewMenu.add(createMenuItem("Zoom out", 'o', Actions.zoomOut,
				IconKey.zoomout16, KeyStroke.getKeyStroke('-', 0)));

		JMenu helpMenu = menuBar.add(createMenu("Help", 'h'));
		helpMenu.add(createMenuItem("About dtangler...", 'a', Actions.about,
				null));

		return menuBar;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		updateTitle();
	}

	public void setScopes(List<? extends Scope> scopes) {
		// FIXME: not like this!!!
		getAction(Actions.changeScope).setEnabled(false);

		this.scopeCombo.removeAllItems();
		for (Scope scope : scopes)
			this.scopeCombo.addItem(scope);
		getAction(Actions.changeScope).setEnabled(true);
	}

	public void setScope(Scope scope) {
		getAction(Actions.changeScope).setEnabled(false);
		scopeCombo.setSelectedItem(scope);
		getAction(Actions.changeScope).setEnabled(true);
	}

	public Scope getSelectedScope() {
		return (Scope) scopeCombo.getSelectedItem();
	}

	public boolean isShortNameEnabled() {
		return shortNameButton.isSelected();
	}

	public void setShortNameEnabled(boolean b) {
		shortNameButton.setSelected(b);
		shortNameMenuItem.setSelected(b);
	}

	public void addFileListDropListener(final FileListDropListener l) {
		DropTargetAdapter adapter = new DropTargetAdapter() {
			public void drop(DropTargetDropEvent dtde) {
				try {
					targetDropped(l, dtde);
				} catch (UnsupportedFlavorException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};

		addDropTargetListener(adapter);
	}

	private void targetDropped(final FileListDropListener l,
			DropTargetDropEvent dtde) throws UnsupportedFlavorException,
			IOException {
		Transferable transferable = dtde.getTransferable();
		DataFlavor flavor = transferable.getTransferDataFlavors()[0];
		if (!DataFlavor.javaFileListFlavor.equals(flavor)) {
			dtde.rejectDrop();
			return;
		}
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		onBeforeExecution();
		try {
			List<File> files = (List<File>) transferable
					.getTransferData(flavor);
			l.fileListDropped(files);
		} finally {
			onAfterExecution();
		}
		dtde.dropComplete(true);
	}
}
