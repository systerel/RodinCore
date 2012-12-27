/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added comment tooltip support
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.UIUtils;

/**
 * @author htson
 *         <p>
 *         Utility class supporting the Proof Tree UI, including debug options.
 */
public class ProofTreeUIUtils {

	// The debug flag which will be set by the main plug-in class on loading.
	public static boolean DEBUG = false;

	// The default prefix for debug messages.
	public final static String DEBUG_PREFIX = "*** ProofTreeUI *** "; //$NON-NLS-1$

	/**
	 * Print the debug message by with the default prefix.
	 * 
	 * @param message
	 *            the debug message
	 */
	public static void debug(String message) {
		UIUtils.printDebugMessage(DEBUG_PREFIX, message);
	}

	/**
	 * Makes proof tree node comments displayed on mouse hover.
	 * 
	 * @param viewer
	 *            a viewer of a proof tree
	 */
	public static void setupCommentTooltip(final TreeViewer viewer) {
		final Tree tree = viewer.getTree();
		final String dataKey = "_TREE_ITEM";
		
		final Listener labelListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Label label = (Label) event.widget;
				final Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					final Event e = new Event();
					e.item = (TreeItem) label.getData(dataKey);
					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
					tree.setSelection(new TreeItem[] { (TreeItem) e.item });
					tree.notifyListeners(SWT.Selection, e);
					shell.dispose();
					tree.setFocus();
					break;
				case SWT.MouseExit:
					shell.dispose();
					break;
				}
			}
		};
	
		final Listener treeListener = new Listener() {
			private Shell tip = null;
			private Label label = null;
	
			@Override
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (tip == null)
						break;
					tip.dispose();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseHover: {
					final Point coords = new Point(event.x, event.y);
					final TreeItem item = tree.getItem(coords);
					if (item == null)
						break;
					final Object data = item.getData();
					if (!(data instanceof IProofTreeNode))
						break;
					final String comment = ((IProofTreeNode) data).getComment();
					if (comment.isEmpty())
						break;
					final int columns = tree.getColumnCount();
	
					for (int i = 0; i < columns || i == 0; i++) {
						if (item.getBounds(i).contains(coords)) {
							if (tip != null && !tip.isDisposed())
								tip.dispose();
							tip = new Shell(tree.getShell(), SWT.ON_TOP
									| SWT.NO_FOCUS | SWT.TOOL);
							tip.setBackground(tree.getDisplay().getSystemColor(
									SWT.COLOR_INFO_BACKGROUND));
							final FillLayout layout = new FillLayout();
							layout.marginWidth = 2;
							tip.setLayout(layout);
							label = new Label(tip, SWT.NONE);
							label.setForeground(tree.getDisplay()
									.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
							label.setBackground(tree.getDisplay()
									.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
							label.setData(dataKey, item);
							label.setText(comment);
							label.addListener(SWT.MouseExit, labelListener);
							label.addListener(SWT.MouseDown, labelListener);
							final Point size = tip.computeSize(SWT.DEFAULT,
									SWT.DEFAULT);
							final Rectangle rect = item.getBounds(i);
							final Point pt = tree.toDisplay(rect.x, rect.y);
							tip.setBounds(pt.x, pt.y, size.x, size.y);
							tip.setVisible(true);
							break;
						}
					}
				}
				}
			}
		};
		tree.addListener(SWT.Dispose, treeListener);
		tree.addListener(SWT.KeyDown, treeListener);
		tree.addListener(SWT.MouseMove, treeListener);
		tree.addListener(SWT.MouseHover, treeListener);
	}

}
