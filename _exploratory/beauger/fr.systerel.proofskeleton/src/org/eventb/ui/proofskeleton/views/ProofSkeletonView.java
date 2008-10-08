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
package org.eventb.ui.proofskeleton.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.proofskeleton.PrfSklMasterDetailsBlock;
import org.eventb.ui.proofskeleton.ProofSkeletonBuilder;
import org.eventb.ui.proofskeleton.PrfSklMasterDetailsBlock.DefaultMasterInput;
import org.rodinp.core.RodinDBException;

/**
 * ViewPart for displaying proof skeletons.
 * 
 * @author Nicolas Beauger
 *
 */
public class ProofSkeletonView extends ViewPart {

	protected PrfSklMasterDetailsBlock masterDetailsBlock;
	protected IManagedForm managedForm;
	private IPSStatus currentProof;
	private ISelection currentSelection;
	private ISelectionService selectionService;
	
	private final ISelectionListener listener = new ISelectionListener() {

		public void selectionChanged(IWorkbenchPart sourcepart,
				ISelection selection) {
			System.out.println(sourcepart + " | " + selection);
			filterAndProcessNewSelection(sourcepart, selection);
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		managedForm = new ManagedForm(parent);
		masterDetailsBlock = new PrfSklMasterDetailsBlock();
		masterDetailsBlock.createContent(managedForm);

		final IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
		selectionService = workbenchWindow.getSelectionService();
		
		// set input to current selection when relevant
		final IWorkbenchPage activePage = workbenchWindow.getActivePage();
		if (activePage != null) {
			final ISelection selection = activePage.getSelection();
			if (selection != null) {
				final IWorkbenchPart activePart = activePage.getActivePart();
				listener.selectionChanged(activePart, selection);
			}
		}
		selectionService.addSelectionListener(listener);
	}

	@Override
	public void setFocus() {
		// Do nothing
	}
	
	/**
	 * Expand or collapse the master part tree viewer.
	 * 
	 * @param expand
	 *            expands all when <code>true</code>; collapses all when
	 *            <code>false</code>.
	 */
	public void changeExpansionState(boolean expand) {
		if (expand) {
			masterDetailsBlock.getViewer().expandAll();
		} else {
			masterDetailsBlock.getViewer().collapseAll();
		}
	}


	private void filterAndProcessNewSelection(IWorkbenchPart sourcepart,
			ISelection selection) {

		IPSStatus proof = null;
		if (sourcepart instanceof ProverUI) {
			proof = ((ProverUI) sourcepart).getCurrentProverSequent();
		} else if (sourcepart == ProofSkeletonView.this
				|| selection.equals(currentSelection)) {
			return;
		} else if (!selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			final Object firstElement = ((IStructuredSelection) selection)
					.getFirstElement();
			if (firstElement instanceof IPSStatus) {
				proof = (IPSStatus) firstElement;
			}
		}
		if (proof != null && proof != currentProof) {
			currentSelection = selection;
			currentProof = proof;
			processNewProof(proof.getProof());
		}
	}

	private void processNewProof(final IPRProof proof) {
		try {
			final IProofTree prTree = ProofSkeletonBuilder
			.buildProofTree(proof, null);
			if (prTree != null && !managedForm.getForm().isDisposed()) {
				managedForm.setInput(prTree);
			}
		} catch (RodinDBException e) {
			UIUtils.showInfo("Proof Skeleton View: the following proof could not be computed\n"
					+ proof.getElementName()
					+ "\nReason:\n" + e.getMessage());
			managedForm.setInput(DefaultMasterInput.getDefault());
		}

	}

	
}
