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

package org.eventb.internal.ui.proofcontrol;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.prover.TacticUIRegistry;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;

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

	String active = null;

	static final TacticUIRegistry registry = TacticUIRegistry.getDefault();

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the "drop down" functionality for our
	 *         dropdown tool items.
	 */
	class GlobalDropdownSelectionListener extends SelectionAdapter {
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
		public void add(String tacticID) {
			if (menu.getItemCount() == 0) { // First Item becomes default item
				active = tacticID;
				dropdown.setToolTipText(registry.getTip(tacticID));
				dropdown.setImage(registry.getIcon(tacticID));
			}
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setImage(registry.getIcon(tacticID));
			menuItem.setText(registry.getTip(tacticID));

			menuItem.setData(tacticID);
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					MenuItem selected = (MenuItem) event.widget;
					active = (String) selected.getData();
					dropdown.setToolTipText(registry.getTip(active));
					dropdown.setImage(registry.getIcon(active));

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
	 */
	public abstract void apply(String tacticID);

	/**
	 * Add a tactic to the dropdown.
	 * <p>
	 * 
	 * @param tactic
	 */
	public void addTactic(String tactic) {
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
		final ITacticProvider provider = registry.getTacticProvider(active);
		if (provider != null) {
			final IProofState currentPO = us.getCurrentPO();
			if (currentPO == null) {
				return false;
			}
			final IProofTreeNode node = currentPO.getCurrentNode();
			return provider.getApplicablePositions(node, null, input) != null;
		}

		final IProofCommand command = registry.getProofCommand(active,
				TacticUIRegistry.TARGET_GLOBAL);
		if (command != null) {
			return command.isApplicable(us, null, input);
		}
		return false;
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
