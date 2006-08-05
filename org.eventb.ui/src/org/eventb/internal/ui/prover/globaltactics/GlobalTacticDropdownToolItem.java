/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover.globaltactics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.ui.prover.IGlobalTactic;

/**
 * @author htson
 *         <p>
 *         This class implements the dropdown toolitem in the Proof Control
 *         View.
 */
public abstract class GlobalTacticDropdownToolItem {

	private ToolItem item;

	private String ID;

	private GlobalDropdownSelectionListener listener;

	private GlobalTacticUI active = null;

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the "drop down" functionality for our
	 *         dropdown tool items.
	 */
	class GlobalDropdownSelectionListener extends SelectionAdapter {
		private ToolItem dropdown;

		private Menu menu;

		/**
		 * Constructs a DropdownSelectionListener
		 * 
		 * @param dropdown
		 *            the dropdown this listener belongs to
		 */
		public GlobalDropdownSelectionListener(ToolItem dropdown) {
			this.dropdown = dropdown;
			menu = new Menu(dropdown.getParent().getShell());
		}

		/**
		 * Adds an item (ProofTactic) to the dropdown list
		 * 
		 * @param item
		 *            the item (a Proof Tactic) to add
		 */
		public void add(GlobalTacticUI tactic) {
			if (menu.getItemCount() == 0) { // First Item becomes default item
				active = tactic;
				dropdown.setToolTipText(active.getTips());
				dropdown.setImage(EventBUIPlugin.getDefault()
						.getImageRegistry().get(active.getImage()));
			}
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setImage(EventBUIPlugin.getDefault().getImageRegistry()
					.get(tactic.getImage()));

			menuItem.setData(tactic);
			menuItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					MenuItem selected = (MenuItem) event.widget;
					active = (GlobalTacticUI) selected.getData();
					dropdown.setToolTipText(active.getTips());
					dropdown.setImage(EventBUIPlugin.getDefault()
							.getImageRegistry().get(active.getImage()));

					dropdown.getParent().redraw();
					apply((IGlobalTactic) active.getTactic());

				}
			});
		}

		/**
		 * Called when either the button itself or the dropdown arrow is clicked
		 * 
		 * @param event
		 *            the event that trigged this call
		 */
		public void widgetSelected(SelectionEvent event) {
			// If they clicked the arrow, we show the list
			if (event.detail == SWT.ARROW) {
				// Determine where to put the dropdown list
				ToolItem item = (ToolItem) event.widget;
				Rectangle rect = item.getBounds();
				Point pt = item.getParent()
						.toDisplay(new Point(rect.x, rect.y));
				menu.setLocation(pt.x, pt.y + rect.height);
				menu.setVisible(true);
			} else {
				// They pushed the button; take appropriate action
				// UIUtils.debugProverUI("Applied: "
				// + active.getTactic().getClass());
				apply(active.getTactic());
			}
		}
	}

	/**
	 * Contructor.
	 * <p>
	 * 
	 * @param item
	 *            The tool item to be used as a dropdown
	 * @param ID
	 *            The id (extension ID) of the dropdown
	 */
	public GlobalTacticDropdownToolItem(ToolItem item, String ID) {
		this.item = item;
		this.ID = ID;
		listener = new GlobalDropdownSelectionListener(item);
		item.addSelectionListener(listener);
	}

	/**
	 * Apply a tactic (response to a button click)
	 * <p>
	 * 
	 * @param tactic
	 */
	public abstract void apply(IGlobalTactic tactic);

	/**
	 * Add a tactic to the dropdown.
	 * <p>
	 * 
	 * @param tactic
	 */
	public void addTactic(GlobalTacticUI tactic) {
		listener.add(tactic);
	}

	/**
	 * Update the status of the dropdown according to the current proof tree
	 * node and the optional string input.
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node
	 * @param input
	 *            the (optional) string input
	 */
	public void updateStatus(IProofTreeNode node, String input) {
		item.setEnabled(active.getTactic().isApplicable(node, input));
	}

	/**
	 * Get the ID of the dropdown
	 * <p>
	 * 
	 * @return the String represents the extension ID of this dropdown
	 */
	public String getID() {
		return ID;
	}

}
