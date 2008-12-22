/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.proofskeleton;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPart2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IManagedForm;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.proofskeleton.PrfSklMasterDetailsBlock.DefaultMasterInput;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 *
 */
public class SelectionManager implements IPartListener2 {
	
	private final IWorkbenchPart2 workbenchPart;
	private final String partId;
	private final IWorkbenchWindow workbenchWindow;
	private final ISelectionService selectionService;
	private final IManagedForm managedForm;
	private IPRProof currentProof;

	public SelectionManager(IWorkbenchPart2 workbenchPart,
			IManagedForm managedForm) {
		this.workbenchPart = workbenchPart;
		this.partId = workbenchPart.getSite().getId();
		this.workbenchWindow = workbenchPart.getSite().getWorkbenchWindow();
		this.selectionService = workbenchWindow.getSelectionService();
		this.managedForm = managedForm;
	}

	private final ISelectionListener selListener = new ISelectionListener() {

		public void selectionChanged(IWorkbenchPart sourcepart,
				ISelection selection) {
			filterAndProcessNewSelection(sourcepart, selection);
		}
	};

	private void filterAndProcessNewSelection(IWorkbenchPart sourcepart,
			ISelection selection) {
	
		IPSStatus proof = null;
		if (sourcepart == workbenchPart) {
			return;
		} else if (!selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			final Object firstElement =
					((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IPSStatus) {
				proof = (IPSStatus) firstElement;
			}
		}
		if (proof != null && proof != currentProof) {
			processNewProof(proof.getProof());
		}
	}

	private void processNewProof(final IPRProof proof) {
		try {
			final IProofTree prTree =
					ProofSkeletonBuilder.buildProofTree(proof, null);
			if (prTree != null && !managedForm.getForm().isDisposed()) {
				setInput(prTree);
			} else {
				setInput(DefaultMasterInput.getDefault());
			}
		} catch (RodinDBException e) {
			UIUtils.showInfo(workbenchPart.getPartName()
					+ ": the following proof could not be computed\n"
					+ proof.getElementName()
					+ "\nReason:\n"
					+ e.getLocalizedMessage());
			setInput(DefaultMasterInput.getDefault());
		}
	
	}

	public void partVisible(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId)) {
			selectionService.addSelectionListener(selListener);
			fireCurrentSelection();
		}
	}

	public void partHidden(IWorkbenchPartReference partRef) {
		if (partRef.getId().equals(partId)) {
			selectionService.removeSelectionListener(selListener);
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
				selListener.selectionChanged(activePart, selection);
			}
		}
	}
	
	private void setInput(Object input) {
		managedForm.setInput(input);
	}

}
