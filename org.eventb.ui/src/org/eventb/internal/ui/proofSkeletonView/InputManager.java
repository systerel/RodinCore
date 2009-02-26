/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.core.IPSStatus;

/**
 * @author Nicolas Beauger
 * 
 */
public class InputManager implements IPartListener2, ISelectionListener {

	private final ProofSkeletonView view;
	private final String partId;
	private final IWorkbenchWindow workbenchWindow;
	private final ISelectionService selectionService;
	private volatile IPSStatus currentInput;

	public InputManager(ProofSkeletonView view) {
		this.view = view;
		this.partId = view.getSite().getId();
		this.workbenchWindow = view.getSite().getWorkbenchWindow();
		this.selectionService = workbenchWindow.getSelectionService();
	}

	public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
		filterAndProcessNewSelection(sourcepart, selection);
	}

	private void filterAndProcessNewSelection(IWorkbenchPart sourcepart,
			ISelection selection) {

		IPSStatus status = null;
		if (sourcepart == view) {
			return;
		} else if (!selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			final Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof IPSStatus) {
				status = (IPSStatus) firstElement;
			}
		}
		if (status != null && !status.equals(currentInput)) {
			currentInput = status;
			view.setInput(currentInput);
		}
	}

	public void partVisible(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId)) {
			selectionService.addSelectionListener(this);
			fireCurrentSelection();
		}
	}

	public void partHidden(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId)) {
			selectionService.removeSelectionListener(this);
		}
	}

	public void partActivated(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
		// do nothing
	}

	public void partOpened(IWorkbenchPartReference partRef) {
		// do nothing
	}

	private void fireCurrentSelection() {
		final IWorkbenchPage activePage = workbenchWindow.getActivePage();
		if (activePage != null) {
			final ISelection selection = activePage.getSelection();
			if (selection != null) {
				final IWorkbenchPart activePart = activePage.getActivePart();
				this.selectionChanged(activePart, selection);
			}
		}
	}

}
