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
package fr.systerel.eventb.proofpurger.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eventb.core.IEventBFile;
import org.eventb.core.IPRProof;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Action to be performed when purging proofs.
 * 
 * @author Laurent Voisin, Nicolas Beauger
 * 
 */
public class FilePurgeAction implements IObjectActionDelegate {

	private IWorkbenchPartSite site;
	private IStructuredSelection selection;

	abstract static class Operation implements IRunnableWithProgress {

		protected boolean wasCancelled;

		public boolean wasCancelled() {
			return wasCancelled;
		}
	}

	/**
	 * Encapsulates a call to the purger to compute unused proofs. The
	 * constructor parameter is the list of proof files to be looked at. The
	 * resulting list of proofs can be obtained from {@link #getResult}, which
	 * returns <code>null</code> if a problem occurred or the search was
	 * canceled.
	 */
	private static class ComputeUnused extends Operation {

		private final IRodinElement[] prFiles;

		private IPRProof[] result;

		public ComputeUnused(IRodinElement[] prFiles) {
			this.prFiles = prFiles;
		}

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				if (prFiles == null)
					return;
				result = ProofPurger.getDefault().computeUnusedProofs(prFiles,
						monitor);
				if (monitor.isCanceled()) {
					wasCancelled = true;
				}
			} catch (RodinDBException e) {
				UIUtils.showInfo(Messages.filepurgeaction_rodindberror
						+ " File: " + e.getMessage());
				wasCancelled = true;
			} finally {
				monitor.done();
			}
		}

		public IPRProof[] getResult() {
			return result;
		}
	}

	/**
	 * Encapsulates a call to the purger to perform proofs deletion. The
	 * constructor parameter is the list of proofs to delete.
	 */
	private static class PurgeProofs extends Operation {

		private final IPRProof[] proofs;

		public PurgeProofs(IPRProof[] proofs) {
			this.proofs = proofs;
		}

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				if (proofs == null)
					return;
				ProofPurger.getDefault().purgeUnusedProofs(proofs, monitor);
				if (monitor.isCanceled()) {
					wasCancelled = true;
				}
			} catch (IllegalArgumentException e) {
				UIUtils.showInfo(Messages.filepurgeaction_usedproofs);
				wasCancelled = true;
			} catch (RodinDBException e) {
				UIUtils.showInfo(Messages.filepurgeaction_rodindberror);
				wasCancelled = true;
			} finally {
				monitor.done();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection s) {
		if (s instanceof IStructuredSelection) {
			selection = (IStructuredSelection) s;
		} else {
			selection = null;
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		site = targetPart.getSite();
	}

	public void run(IAction action) {
		IRodinElement[] prFiles = getSelectedPRFiles();
		if (prFiles == null) {
			UIUtils.showInfo(Messages.filepurgeaction_invalidselection);
			return;
		}
		final ComputeUnused computeUnused = new ComputeUnused(prFiles);
		launchPurgerOperation(computeUnused);
		if (computeUnused.wasCancelled())
			return;
		IPRProof[] prProofs = computeUnused.getResult();
		// FIXME sometimes null sometimes []
		if (prProofs.length == 0) {
			UIUtils.showInfo(Messages.filepurgeaction_noproofstopurge);
			return;
		}
		final IPRProof[] proofsToDelete = launchPurgerSelectionDialog(prProofs);
		if (proofsToDelete != null) {
			launchPurgerOperation(new PurgeProofs(proofsToDelete));
		}
	}

	private IRodinElement[] getSelectedPRFiles() {
		if (selection == null) {
			return null;
		}
		final List<IRodinElement> result = new ArrayList<IRodinElement>(
				selection.size());
		for (Object o : selection.toList()) {
			final IRodinElement elem = asRodinElement(o);
			if (elem instanceof IRodinProject || elem instanceof IEventBFile) {
				result.add(elem);
			}
		}
		return result.toArray(new IRodinElement[result.size()]);
	}

	private IRodinElement asRodinElement(Object o) {
		if (o instanceof IRodinElement) {
			return (IRodinElement) o;
		}
		if (o instanceof IAdaptable) {
			final IAdaptable adaptable = (IAdaptable) o;
			return (IRodinElement) adaptable.getAdapter(IRodinElement.class);
		}
		return null;
	}

	private IPRProof[] launchPurgerSelectionDialog(IPRProof[] prProofs) {
		ProofPurgerSelectionDialog dialog = new ProofPurgerSelectionDialog(site
				.getShell(), prProofs);
		dialog.create();
		final int userAction = dialog.open();
		if (userAction == Window.OK) {
			return dialog.getSelectedProofs();
		}
		return null;
	}

	private void launchPurgerOperation(IRunnableWithProgress operation) {
		try {
			new ProgressMonitorDialog(site.getShell()).run(true, true,
					operation);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			final String errorMessage = Messages.filepurgeaction_runningpurgeroperation
					+ operation.toString();
			UIUtils.log(cause, errorMessage);
			if (cause instanceof CoreException) {
				UIUtils.showUnexpectedError((CoreException) cause);
			}
		} catch (InterruptedException e) {
			// Propagate the interruption
			Thread.currentThread().interrupt();
		}
	}

}
