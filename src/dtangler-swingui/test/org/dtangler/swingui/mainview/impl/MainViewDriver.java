// This product is provided under the terms of EPL (Eclipse Public License)
// version 1.0.
//
// The full license text can be read from:
// http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.mainview.impl;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;

import org.dtangler.swingui.dsm.impl.DsmViewDriver;
import org.dtangler.swingui.windowmanager.SwingView;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.ListBox;
import org.uispec4j.MenuBar;
import org.uispec4j.Panel;
import org.uispec4j.ToggleButton;

public class MainViewDriver {

	public final Button inputButton;
	public final Button rulesButton;
	public final Button refreshBtn;
	public final ListBox cellViolations;
	public final ListBox allViolations;
	public final MenuBar menuBar;
	public final ComboBox scope;
	private final SwingView view;
	public final DsmViewDriver dsm;
	public final ToggleButton showShortNamesButton;
	public final Button zoomInButton;
	public final Button zoomOutButton;

	public MainViewDriver(SwingView view) {
		this.view = view;
		Panel panel = new Panel(view.getViewComponent());
		inputButton = panel.getButton("input");
		rulesButton = panel.getButton("rules");
		refreshBtn = panel.getButton("refresh");
		cellViolations = panel.getListBox("selectionViolations");
		allViolations = panel.getListBox("allViolations");
		scope = panel.getComboBox();
		dsm = new DsmViewDriver(panel.getTable());
		menuBar = new MenuBar(view.getMenuBar());
		zoomInButton = panel.getButton("zoomIn");
		zoomOutButton = panel.getButton("zoomOut");
		showShortNamesButton = panel.getToggleButton("toggleShortName");
	}

	public String getTitle() {
		return view.getTitle();
	}

	public void simulateDrop(final DataFlavor flavor, final Object data) {
		final Transferable transferable = new Transferable() {

			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				return data;
			}

			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { flavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return true;
			}
		};
		DropTargetDropEvent event = new DropTargetDropEvent(view
				.getViewComponent().getDropTarget().getDropTargetContext(),
				new Point(0, 0), DnDConstants.ACTION_COPY, 0) {

			@Override
			public Transferable getTransferable() {
				return transferable;
			}
		};
		view.getViewComponent().getDropTarget().drop(event);
	}

	public SwingView getView() {
		return view;
	}

}
