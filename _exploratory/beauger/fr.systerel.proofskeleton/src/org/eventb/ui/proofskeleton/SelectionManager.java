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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
import org.eventb.internal.core.ProofMonitor;
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

	private static class BuildRunner implements IRunnableWithProgress {

		private final IPRProof proof;
		private IProofTree prTree;
		
		public BuildRunner(IPRProof proof) {
			this.proof = proof;
		}
		
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				prTree =
					ProofSkeletonBuilder.buildProofTree(proof,
							new ProofMonitor(monitor));
			} catch (RodinDBException e) {
				throw new InvocationTargetException(e);
			}
		}
		
		public IProofTree getResult() {
			return prTree;
		}
	}

	private void processNewProof(final IPRProof proof) {
		try {
			final BuildRunner buildRunner = new BuildRunner(proof);
			final Display display = PlatformUI.getWorkbench().getDisplay();
			final Shell shell = display.getActiveShell();
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
			dialog.run(true, true, buildRunner);
			
			final IProofTree prTree = buildRunner.getResult();
			if (prTree != null) {
				setInput(prTree);
			} else {
				setInput(DefaultMasterInput.getDefault());
			}
		} catch (InvocationTargetException e) {
			UIUtils.showInfo(workbenchPart.getPartName()
					+ ": the following proof could not be computed\n"
					+ proof.getElementName()
					+ "\nReason:\n"
					+ e.getCause().getLocalizedMessage());
			setInput(DefaultMasterInput.getDefault());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if(!managedForm.getForm().isDisposed()) {
			managedForm.setInput(input);
		}
	}

}
