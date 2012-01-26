//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.windowmanager.impl;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dtangler.core.exception.DtException;
import org.dtangler.swingui.resource.icons.IconKey;
import org.dtangler.swingui.resource.icons.IconProvider;
import org.dtangler.swingui.windowmanager.DialogManager;
import org.dtangler.swingui.windowmanager.SwingView;
import org.dtangler.swingui.windowmanager.UIExceptionHandler;
import org.dtangler.swingui.windowmanager.WindowManager;

public class SwingWindowManager implements WindowManager, DialogManager,
		UIExceptionHandler {

	private final Map<SwingView, Window> windows = new HashMap();
	private SwingView mainView;

	public SwingWindowManager() {
		setLookAndFeel();
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
	}

	public void showModal(SwingView view) {
		Window parent = getActiveWindow();
		JDialog dlg;
		if (parent instanceof JFrame)
			dlg = new JDialog((JFrame) parent, true);
		else
			dlg = new JDialog((JDialog) parent, true);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.setTitle(view.getTitle());
		dlg.getContentPane().add(view.getViewComponent());
		if (view.getPreferredSize() != null)
			dlg.setSize(view.getPreferredSize());
		else
			dlg.pack();
		view
				.setWindowInteractionProvider(new WindowInteractionProviderJDialogImpl(
						dlg));
		center(dlg, parent.getSize(), parent.getLocation());
		open(view, dlg);
	}

	public void showMainView(SwingView mainView) {
		this.mainView = mainView;
		JFrame frame = new JFrame();
		frame.setTitle(mainView.getTitle());
		frame.setSize(mainView.getPreferredSize());
		frame.getContentPane().add(mainView.getViewComponent());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(mainView.getMenuBar());
		frame.setIconImage(IconProvider.getIcon(IconKey.dtangler16).getImage());
		mainView
				.setWindowInteractionProvider(new WindowInteractionProviderJFrameImpl(
						frame));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		center(frame, screenSize, new Point(0, 0));
		open(mainView, frame);
	}

	private Window getActiveWindow() {
		for (Window w : windows.values())
			if (w.isActive())
				return w;
		return windows.get(mainView);
	}

	private void open(SwingView view, Window window) {
		windows.put(view, window);
		if (view.getFirstComponentToFocus() != null)
			view.getFirstComponentToFocus().requestFocusInWindow();
		showWindow(window);
	}

	protected void showWindow(Window window) {
		window.setVisible(true);
	}

	public void close(SwingView view) {
		windows.get(view).dispose();
		windows.remove(view);
	}

	protected void center(Window window, Dimension parentSize,
			Point parentLocation) {
		Dimension size = window.getSize();
		double x = (parentSize.getWidth() - size.getWidth()) / 2;
		double y = (parentSize.getHeight() - size.getHeight()) / 2;
		x = x + parentLocation.getX();
		y = y + parentLocation.getY();
		Point cornerPoint = new Point((int) Math.max(x, 0), (int) Math
				.max(y, 0));
		window.setLocation(cornerPoint);
	}

	public void handleUIException(Throwable t) {
		if (t instanceof DtException)
			handleApplicationError(t);
		else
			handleInternalError(t);
	}

	private void handleApplicationError(Throwable t) {
		Window window = getActiveWindow();
		JOptionPane.showMessageDialog(window, t.getMessage(),
				"Application error", JOptionPane.ERROR_MESSAGE);
	}

	private void handleInternalError(Throwable t) {
		t.printStackTrace();
		Window window = getActiveWindow();
		JOptionPane.showMessageDialog(window, new ErrorDisplayComponent(t)
				.getViewComponent(), "Internal error",
				JOptionPane.ERROR_MESSAGE);
	}

	public DialogResult showYesNoCancelDialog(String message, String title) {
		int result = JOptionPane.showConfirmDialog(getActiveWindow(), message,
				title, JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null);
		if (result == JOptionPane.YES_OPTION)
			return DialogResult.yes;
		if (result == JOptionPane.NO_OPTION)
			return DialogResult.no;
		return DialogResult.cancel;
	}
}
