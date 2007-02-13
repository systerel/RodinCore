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

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

/**
 * @author htson
 *         <p>
 *         This class implements the dropdown toolitem in the Proof Control
 *         View.
 */
public class DropdownToolItem {

	private ToolItem item;

	IInternalElement element;

	private DropdownSelectionListener listener;

	String active = null;

	private IEventBEditor editor;

	private IInternalParent parent;

	IInternalElementType<? extends IInternalElement> type;

	/**
	 * @author htson
	 *         <p>
	 *         This class provides the "drop down" functionality for our
	 *         dropdown tool items.
	 */
	class DropdownSelectionListener extends SelectionAdapter {
		ToolItem dropdown;

		private Menu menu;

		/**
		 * Constructs a DropdownSelectionListener
		 * 
		 * @param dropdown
		 *            the dropdown this listener belongs to
		 */
		public DropdownSelectionListener(ToolItem dropdown) {
			this.dropdown = dropdown;
			menu = new Menu(dropdown.getParent().getShell());
		}

		/**
		 * Adds an item (ProofTactic) to the dropdown list
		 * 
		 */
		public void add(String actionID) {
			if (menu.getItemCount() == 0) { // First Item becomes default item
				setActive(actionID);
			}
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			// menuItem.setImage(registry.getIcon(tacticID));
			EditSectionRegistry sectionRegistry = EditSectionRegistry
					.getDefault();
			menuItem.setText(sectionRegistry.getName(actionID, type));
			menuItem.setData(actionID);
			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					MenuItem selected = (MenuItem) event.widget;
					setActive((String) selected.getData());
					dropdown.getParent().redraw();
					apply(active);
				}
			});
		}

		void setActive(String actionID) {
			active = actionID;
			EditSectionRegistry sectionRegistry = EditSectionRegistry
					.getDefault();

			dropdown.setToolTipText(sectionRegistry.getToolTip(actionID, type));
			dropdown.setText(sectionRegistry.getName(actionID, type));
			// dropdown.setImage(registry.getIcon(tacticID));
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
	 */
	public DropdownToolItem(IEventBEditor editor, ToolItem item,
			IInternalParent parent, IInternalElement element,
			IInternalElementType<? extends IInternalElement> type) {
		this.item = item;
		this.element = element;
		this.editor = editor;
		this.parent = parent;
		this.type = type;
		listener = new DropdownSelectionListener(item);
		item.addSelectionListener(listener);
	}

	/**
	 * Apply a tactic (response to a button click)
	 * <p>
	 * 
	 */
	public void apply(String actionID) {
		EditSectionRegistry sectionRegistry = EditSectionRegistry.getDefault();
		try {
			sectionRegistry.run(actionID, editor, parent, element, type);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
	 */
	public void updateStatus() {
		item.setEnabled(true);
	}

}
