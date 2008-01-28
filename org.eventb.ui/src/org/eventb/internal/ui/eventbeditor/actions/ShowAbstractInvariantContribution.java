/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Rodin @ ETH Zurich
******************************************************************************/

package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An extension of {@link ContributionItem} for contributing actions to
 *         the context menu. The actions is for showing the invariants of
 *         different abstract models.
 */
public class ShowAbstractInvariantContribution extends ContributionItem {

	/**
	 * The concrete machine.
	 */
	private IMachineFile file;

	/**
	 * Constructor.
	 * <p>
	 * Store the concrete machine.
	 * 
	 * @param file
	 *            the concrete machine.
	 */
	public ShowAbstractInvariantContribution(IRodinFile file) {
		assert file instanceof IMachineFile;
		this.file = (IMachineFile) file;
	}

	/**
	 * Loop to find get all the abstract machine corresponding to the input
	 * machine. For each abstract machine, create the corresponding menu item.
	 * 
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Menu,
	 *      int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		try {
			IMachineFile abstractFile = EventBUtils.getAbstractMachine(file);
			while (abstractFile != null && abstractFile.exists()) {
				createMenuItem(menu, abstractFile);
				abstractFile = EventBUtils.getAbstractMachine(abstractFile);
			}

		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(e,
					UserAwareness.IGNORE);
		}
	}

	/**
	 * Utility method for creating a menu item corresponding to an abstract
	 * event.
	 * 
	 * @param menu
	 *            the parent menu
	 * @param abstractFile the abstract machine
	 * @throws RodinDBException if some problems occur.
	 */
	private void createMenuItem(Menu menu, final IRodinFile abstractFile)
			throws RodinDBException {
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(abstractFile.getBareName());
		menuItem
				.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REFINES));

		final IRodinElement inv;
		IRodinElement[] invs = abstractFile
				.getChildrenOfType(IInvariant.ELEMENT_TYPE);
		if (invs.length != 0)
			inv = invs[0];
		else
			inv = null;
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Selection:
					if (inv != null)
						UIUtils.linkToEventBEditor(inv);
					else
						UIUtils.linkToEventBEditor(abstractFile);
					break;
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);

	}

}
