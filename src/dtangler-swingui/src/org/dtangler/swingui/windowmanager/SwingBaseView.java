//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GraphicsEnvironment;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TooManyListenersException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import org.dtangler.swingui.actionfactory.ActionExecutionListener;
import org.dtangler.swingui.actionfactory.ActionFactory;
import org.dtangler.swingui.actionfactory.ActionKey;
import org.dtangler.swingui.actionfactory.DocumentChangeActionAdapter;
import org.dtangler.swingui.actionfactory.ListSelectionActionAdapter;
import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.resource.icons.IconProvider;

import com.jgoodies.forms.builder.ButtonStackBuilder;

public abstract class SwingBaseView implements SwingView,
		ActionExecutionListener {

	private final ActionFactory actionFactory;
	private JComponent viewComponent;
	private WindowInteractionProvider windowInteractionProvider;
	private DropTarget dropTarget;

	protected SwingBaseView(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
		actionFactory.setActionExecutionListener(this);
	}

	protected void addCommonKeyEvent(JComponent parent, int key,
			ActionKey action) {
		parent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(key, 0), action.name());
		parent.getActionMap().put(action.name(), getAction(action));
	}

	protected JButton createButton(String caption, ActionKey key) {
		return createButton(caption, key, null);
	}

	protected JButton createButton(String caption, ActionKey key,
			IconKey iconKey) {
		JButton button = new JButton(getAction(key));
		button.setName(key.name());
		button.setText(caption);
		if (iconKey != null) {
			button.setHorizontalAlignment(SwingConstants.LEFT);
			button.setIcon(getIcon(iconKey));
		}
		return button;
	}

	protected Icon getIcon(IconKey iconKey) {
		return IconProvider.getIcon(iconKey);
	}

	protected JCheckBoxMenuItem createCheckBoxMenuItem(String caption,
			char mnemonic, ActionKey key, IconKey iconKey,
			KeyStroke accelleratorKey) {
		return decorateMenuItem(caption, mnemonic, key, iconKey,
				accelleratorKey, new JCheckBoxMenuItem(getAction(key)));
	}

	protected JMenuItem createMenuItem(String caption, char mnemonic,
			ActionKey key, KeyStroke accelleratorKey) {
		return decorateMenuItem(caption, mnemonic, key, null, accelleratorKey,
				new JMenuItem(getAction(key)));
	}

	protected JMenuItem createMenuItem(String caption, char mnemonic,
			ActionKey key, IconKey iconKey, KeyStroke accelleratorKey) {
		return decorateMenuItem(caption, mnemonic, key, iconKey,
				accelleratorKey, new JMenuItem(getAction(key)));
	}

	private <T extends JMenuItem> T decorateMenuItem(String caption,
			char mnemonic, ActionKey key, IconKey iconKey,
			KeyStroke accelleratorKey, T item) {
		item.setName(key.name());
		item.setMnemonic(mnemonic);
		item.setText(caption);
		if (iconKey != null)
			item.setIcon(getIcon(iconKey));
		item.setAccelerator(accelleratorKey);
		return item;
	}

	protected JMenu createMenu(String caption, char mnemonic) {
		JMenu menu = new JMenu(caption);
		menu.setMnemonic(mnemonic);
		return menu;
	}

	public JMenuBar getMenuBar() {
		return null;
	}

	public final JComponent getViewComponent() {
		if (viewComponent == null) {
			viewComponent = buildViewComponent();
			viewComponent.setDropTarget(dropTarget);
		}
		return viewComponent;
	}

	protected abstract JComponent buildViewComponent();

	public void onBeforeExecution() {
		getViewComponent().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void onAfterExecution() {
		getViewComponent().setCursor(Cursor.getDefaultCursor());
	}

	public final void setWindowInteractionProvider(
			WindowInteractionProvider windowInteractionProvider) {
		this.windowInteractionProvider = windowInteractionProvider;
	}

	protected final void updateTitle() {
		if (windowInteractionProvider != null)
			windowInteractionProvider.updateTitle(getTitle());
	}

	protected final DocumentChangeActionAdapter createDocumentChangeAdapter(
			ActionKey actionKey) {
		return new DocumentChangeActionAdapter(getAction(actionKey));
	}

	protected Action getAction(ActionKey actionKey) {
		return actionFactory.getAction(actionKey);
	}

	public Component getFirstComponentToFocus() {
		// Overwrite this if you need it
		return null;
	}

	protected JPanel createButtonStack(JButton... buttons) {
		ButtonStackBuilder builder = new ButtonStackBuilder();
		builder.addButtons(buttons);
		return builder.getPanel();
	}

	public ListSelectionListener createSelectionActionAdapter(
			ActionKey actionKey) {
		return new ListSelectionActionAdapter(actionFactory
				.getAction(actionKey));
	}

	public MouseListener createDoubleClickAdapter(final ActionKey actionKey) {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					actionFactory.getAction(actionKey).actionPerformed(null);
			}
		};
	}

	public void addDropTargetListener(DropTargetListener l) {
		// TODO Get rid of these headless problems on the build server
		if (GraphicsEnvironment.isHeadless())
			return;
		if (dropTarget == null)
			dropTarget = new DropTarget();

		try {
			dropTarget.addDropTargetListener(l);
		} catch (TooManyListenersException e) {
			throw new RuntimeException(e);
		}
	}
}
