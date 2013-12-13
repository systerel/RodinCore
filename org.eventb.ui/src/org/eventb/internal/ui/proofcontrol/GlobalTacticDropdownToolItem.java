/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.TacticUIRegistry;
import org.eventb.internal.ui.prover.registry.DropdownInfo;
import org.eventb.internal.ui.prover.registry.TacticUIInfo;

/**
 * @author htson
 *         <p>
 *         This class implements the dropdown toolitem in the Proof Control
 *         View.
 */
public abstract class GlobalTacticDropdownToolItem {

	private ToolItem item;

	private DropdownInfo info;

	private GlobalDropdownSelectionListener listener;

	private TacticUIInfo active = null;

	static final TacticUIRegistry registry = TacticUIRegistry.getDefault();

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the "drop down" functionality for our
	 *         dropdown tool items.
	 */
	@SuppressWarnings("synthetic-access")
	private class GlobalDropdownSelectionListener extends SelectionAdapter {
		ToolItem dropdown;

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
		 */
		public void add(TacticUIInfo tactic) {
			if (menu.getItemCount() == 0) { // First Item becomes default item
				active = tactic;
				dropdown.setToolTipText(tactic.getTooltip());
				dropdown.setImage(tactic.getIcon());
			}
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setImage(tactic.getIcon());
			menuItem.setText(tactic.getTooltip());

			menuItem.setData(tactic);
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					final MenuItem selected = (MenuItem) event.widget;
					active = (TacticUIInfo) selected.getData();
					dropdown.setToolTipText(active.getTooltip());
					dropdown.setImage(active.getIcon());

					dropdown.getParent().redraw();
					apply(active);
				}
			});
		}

		/**
		 * Called when either the button itself or the dropdown arrow is clicked
		 * 
		 * @param event
		 *            the event that trigged this call
		 */
		@Override
		public void widgetSelected(SelectionEvent event) {
			// If they clicked the arrow, we show the list
			if (event.detail == SWT.ARROW) {
				// Determine where to put the dropdown list
				ToolItem item1 = (ToolItem) event.widget;
				Rectangle rect = item1.getBounds();
				Point pt = item1.getParent().toDisplay(
						new Point(rect.x, rect.y));
				menu.setLocation(pt.x, pt.y + rect.height);
				menu.setVisible(true);
			} else {
				// They pushed the button; take appropriate action
				// UIUtils.debugProverUI("Applied: "
				// + active.getTactic().getClass());
				apply(active);
			}
		}
	}

	public GlobalTacticDropdownToolItem(ToolItem item, DropdownInfo info) {
		this.item = item;
		this.info = info;
		listener = new GlobalDropdownSelectionListener(item);
		item.addSelectionListener(listener);
	}

	/**
	 * Apply a tactic (response to a button click)
	 * <p>
	 * 
	 */
	public abstract void apply(TacticUIInfo tactic);

	/**
	 * Add a tactic to the dropdown.
	 * <p>
	 * 
	 * @param tactic
	 */
	public void addTactic(TacticUIInfo tactic) {
		listener.add(tactic);
	}

	/**
	 * Update the status of the dropdown according to the current proof tree
	 * node and the optional string input.
	 * <p>
	 * 
	 * @param input
	 *            the (optional) string input
	 */
	public void updateStatus(IUserSupport us, String input) {
		item.setEnabled(shouldBeEnabled(us, input));
	}

	private boolean shouldBeEnabled(IUserSupport us, String input) {
		final Object application = active.getGlobalApplication(us, input);
		return application != null;
	}

	/**
	 * Get the ID of the dropdown
	 * <p>
	 * 
	 * @return the String represents the extension ID of this dropdown
	 */
	public String getID() {
		return info.getID();
	}

}
