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
import org.eventb.core.IEvent;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An extension of {@link ContributionItem} for contributing actions to
 *         the context menu. The actions is for showing the corresponding
 *         abstraction of an event in different abstract models.
 */
public class ShowAbstractEventContribution extends ContributionItem {

	/**
	 * The concrete event.
	 */
	private IEvent event;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param event
	 *            the concrete event.
	 */
	public ShowAbstractEventContribution(IEvent event) {
		this.event = event;
	}

	/**
	 * Loop to find get all the abstract event corresponding to the input event.
	 * For each abstract event, create the corresponding menu item.
	 * 
	 * @see org.eclipse.jface.action.ContributionItem#fill(org.eclipse.swt.widgets.Menu,
	 *      int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		try {
			IEvent abs_evt = event;
			while (abs_evt != null) {
				abs_evt = EventBUtils.getAbstractEvent(abs_evt);
				if (abs_evt != null)
					createMenuItem(menu, abs_evt);
				else
					break;
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
	 * @param abs_evt
	 *            the abstract event.
	 */
	private void createMenuItem(Menu menu,
			final IEvent abs_evt) {
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);

		menuItem.setText(abs_evt.getRodinFile().getBareName());
		menuItem
				.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REFINES));

		// If the menu item is selected, then open the Event-B Editor for
		// editing the abstract event.
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event ev) {
				switch (ev.type) {
				case SWT.Selection:
					UIUtils.linkToEventBEditor(abs_evt);
					break;
				}
			}
		};
		menuItem.addListener(SWT.Selection, listener);
	}

}
