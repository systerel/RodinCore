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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPart2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 *
 */
public class SelectionManager implements IPartListener2, ISelectionListener{
	
	private final IWorkbenchPart2 workbenchPart;
	private final String partId;
	private final IWorkbenchWindow workbenchWindow;
	private final ISelectionService selectionService;
	private final IManagedForm managedForm;
	private IPSStatus currentStatus;

	public SelectionManager(IWorkbenchPart2 workbenchPart,
			IManagedForm managedForm) {
		this.workbenchPart = workbenchPart;
		this.partId = workbenchPart.getSite().getId();
		this.workbenchWindow = workbenchPart.getSite().getWorkbenchWindow();
		this.selectionService = workbenchWindow.getSelectionService();
		this.managedForm = managedForm;
	}

	public void selectionChanged(IWorkbenchPart sourcepart,
			ISelection selection) {
		filterAndProcessNewSelection(sourcepart, selection);
	}

	private void filterAndProcessNewSelection(IWorkbenchPart sourcepart,
			ISelection selection) {
	
		IPSStatus status = null;
		if (sourcepart == workbenchPart) {
			return;
		} else if (!selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			final Object firstElement =
					((IStructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IPSStatus) {
				status = (IPSStatus) firstElement;
			}
		}
		if (status != null && !status.equals(currentStatus)) { 
			final IPRProof proof = status.getProof();
			processNewProof(proof);
			currentStatus = status;
		}
	}

	private void processNewProof(final IPRProof proof) {
		final String partName = workbenchPart.getPartName();
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				try {
					final IProofTree prTree =
							ProofSkeletonBuilder.buildProofTree(proof,
									null);
					if (prTree != null) {
						setInput(prTree);
					} else {
						setInput(new ProofErrorInput(proof));
					}
				} catch (RodinDBException e) {
					UIUtils.showInfo(partName
							+ ": the following proof could not be computed\n"
							+ proof.getElementName()
							+ "\nReason:\n"
							+ e.getLocalizedMessage());
					setInput(DefaultMasterInput.getDefault());
				}
			}
		});
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
	
	void setInput(Object input) {
		if(!managedForm.getForm().isDisposed()) {
			managedForm.setInput(input);
		}
	}

}
