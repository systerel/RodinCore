/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISeesContext;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

public class ShowSeesContextContribution extends ContributionItem {

	private final IMachineRoot root;

	private final IEventBProject evbProject;

	public ShowSeesContextContribution(IMachineRoot root) {
		this.root = root;
		this.evbProject = root.getEventBProject();
	}

	@Override
	public void fill(Menu menu, int index) {
		final ISeesContext[] seesClauses;
		try {
			seesClauses = root.getSeesClauses();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleGetChildrenException(e);
			return;
		}
		for (ISeesContext seesClause : seesClauses) {
			final String name;
			try {
				name = seesClause.getSeenContextName();
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleGetAttributeException(e,
						UserAwareness.IGNORE);
				continue;
			}
			final IRodinFile contextFile = evbProject.getContextFile(name);
			if (contextFile != null & contextFile.exists()) {
				createMenuItem(menu, contextFile);
			}
		}
	}

	private void createMenuItem(Menu menu, final IRodinFile contextFile) {
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(contextFile.getBareName());
		menuItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_CONTEXT));

		final Listener listener = new Listener() {
			public void handleEvent(Event event) {
				UIUtils.linkToEventBEditor(contextFile);
			}
		};
		menuItem.addListener(SWT.Selection, listener);
	}

}
