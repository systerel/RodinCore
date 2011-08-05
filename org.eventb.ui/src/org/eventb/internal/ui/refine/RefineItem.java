/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.refine;

import static org.eclipse.ui.menus.CommandContributionItem.STYLE_PUSH;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.eventb.internal.ui.refine.RefinementUIRegistry.RefinementUI;
import org.rodinp.core.IInternalElement;

/**
 * Dynamic contribution item for refine command. The label of the command is set
 * dynamically, depending on the type of the selected element.
 * 
 * @author Nicolas Beauger
 * 
 */
public class RefineItem extends CompoundContributionItem implements
		IWorkbenchContribution {
	
	private static final String REFINE_COMMAND_ID = "org.eventb.ui.refine";

	private static final IContributionItem[] NO_REFINE_CONTRIBUTION_ITEMS = new IContributionItem[0];

	private IServiceLocator serviceLoc;

	public RefineItem() {
		super();
	}

	public RefineItem(String id) {
		super(id);
	}

	private static String findLabel(ISelection selection) {
		if (selection.isEmpty()) {
			return null;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() != 1) {
			return null;
		}
		final Object firstElement = ssel.getFirstElement();
		if (!(firstElement instanceof IInternalElement)) {
			return null;
		}
		final IInternalElement element = (IInternalElement) firstElement;
		final RefinementUI refinementUI = RefinementUIRegistry.getDefault()
				.getRefinementUI(element.getElementType());
		if (refinementUI == null) {
			return null;
		}
		return refinementUI.label;
	}

	@Override
	public void initialize(IServiceLocator serviceLocator) {
		this.serviceLoc = serviceLocator;
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		IWorkbenchPart part = null;
		ISelection selection = null;

		final ISelectionService selService = (ISelectionService) serviceLoc
				.getService(ISelectionService.class);
		if (selService != null) {
			selection = selService.getSelection();
		}
		final IPartService partService = (IPartService) serviceLoc
				.getService(IPartService.class);
		if (partService != null) {
			part = partService.getActivePart();
		}

		// If no part or selection, disable all.
		if (part == null || selection == null) {
			return NO_REFINE_CONTRIBUTION_ITEMS;
		}

		final String label = findLabel(selection);
		if (label == null) {
			return NO_REFINE_CONTRIBUTION_ITEMS;
		}
		final CommandContributionItemParameter param = new CommandContributionItemParameter(
				serviceLoc, null, REFINE_COMMAND_ID, STYLE_PUSH);
		param.label = label;
		return new IContributionItem[] { new CommandContributionItem(param) };
	}

}
