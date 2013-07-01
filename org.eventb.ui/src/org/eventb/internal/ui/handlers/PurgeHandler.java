/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.handlers;

import static org.rodinp.core.RodinCore.asRodinElement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofpurger.ProofPurger;
import org.eventb.internal.ui.proofpurger.ProofPurgerContentProvider;
import org.eventb.internal.ui.proofpurger.ProofPurgerSelectionDialog;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Action to be performed when purging proofs.
 * 
 * @author Laurent Voisin, Nicolas Beauger
 * 
 */
public class PurgeHandler extends AbstractHandler {

	abstract static class Operation implements IRunnableWithProgress {

		protected boolean wasCancelled;

		public boolean wasCancelled() {
			return wasCancelled;
		}
	}
	
	/**
	 * Encapsulates a call to the purger to compute unused proofs. The
	 * constructor parameter is the list of proof files to be looked at. The
	 * resulting list of proofs can be obtained from get methods, which
	 * return an empty list if a problem occurred or the search was
	 * cancelled.
	 */
	private static class ComputeUnused extends Operation {

		private final IRodinElement[] prFiles;

		private final List<IPRProof> unusedProofs;
		private final List<IPRRoot> unusedFiles;

		public ComputeUnused(IRodinElement[] prFiles) {
			this.prFiles = prFiles;
			this.unusedProofs = new ArrayList<IPRProof>();
			this.unusedFiles = new ArrayList<IPRRoot>();
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				if (prFiles == null)
					return;
				ProofPurger.getDefault().computeUnused(prFiles,
						monitor, unusedProofs, unusedFiles);
				if (monitor.isCanceled()) {
					wasCancelled = true;
				}
			} catch (RodinDBException e) {
				UIUtils.showInfo(Messages.filepurgeaction_rodindberror
						+ " File: "
						+ e.getMessage());
				wasCancelled = true;
			} finally {
				monitor.done();
			}
		}

		public IPRProof[] getUnusedProofs() {
			return unusedProofs.toArray(new IPRProof[unusedProofs.size()]);
		}

		public IPRRoot[] getUnusedProofFiles() {
			return unusedFiles.toArray(new IPRRoot[unusedFiles.size()]);
		}

	}

	/**
	 * Encapsulates a call to the purger to perform proofs deletion. The
	 * constructor parameter is the list of proofs to delete.
	 */
	private static class PurgeProofs extends Operation {

		private final List<IPRProof> proofs;
		private final List<IPRRoot> files;

		public PurgeProofs(List<IPRProof> proofs, List<IPRRoot> files) {
			this.proofs = proofs;
			this.files = files;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				if (proofs == null)
					return;
				ProofPurger.getDefault().purgeUnused(proofs,
						files, monitor);
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

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbench w = PlatformUI.getWorkbench();
		final IWorkbenchWindow ww = w.getActiveWorkbenchWindow();
		if (ww == null) {
			return null;
		}
		final ISelectionService selectionService = ww.getSelectionService();
		final ISelection selection = selectionService.getSelection();
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection sselection = (IStructuredSelection) selection;
		final IRodinElement[] input = getSelectedElements(sselection);
		if (input == null) {
			UIUtils.showInfo(Messages.filepurgeaction_invalidselection);
			return null;
		}
		final ComputeUnused computeUnused =
				new ComputeUnused(input);
		final Shell shell = ww.getShell();
		launchPurgerOperation(shell, computeUnused);
		if (computeUnused.wasCancelled())
			return null;
		final IPRProof[] unusedProofs = computeUnused.getUnusedProofs();
		final IPRRoot[] unusedFiles = computeUnused.getUnusedProofFiles();
		// FIXME sometimes null sometimes []
		if (unusedProofs.length == 0 && unusedFiles.length == 0) {
			UIUtils.showInfo(Messages.filepurgeaction_noproofstopurge);
			return null;
		}
		final List<IPRProof> selectedProofs = new ArrayList<IPRProof>();
		final List<IPRRoot> selectedFiles = new ArrayList<IPRRoot>();
		final boolean purge =
				launchPurgerSelectionDialog(shell, unusedProofs, unusedFiles,
						selectedProofs, selectedFiles);
		if (purge) {
			final PurgeProofs purgeProofs =
					new PurgeProofs(selectedProofs, selectedFiles);
			launchPurgerOperation(shell, purgeProofs);
		}
		return null;
	}

	private IRodinElement[] getSelectedElements(IStructuredSelection selection) {
		if (selection == null) {
			return null;
		}
		final List<IRodinElement> result =
				new ArrayList<IRodinElement>(selection.size());
		for (Object o : selection.toList()) {
			final IRodinElement elem = asRodinElement(o);
			if (isProjectOrEventBRoot(elem)) {
				result.add(elem);
			}
		}
		return result.toArray(new IRodinElement[result.size()]);
	}

	private boolean isProjectOrEventBRoot(IRodinElement elem) {
		return (elem instanceof IRodinProject || elem instanceof IEventBRoot);
	}

	private boolean launchPurgerSelectionDialog(Shell shell, IPRProof[] unusedProofs,
			IPRRoot[] unusedFiles, List<IPRProof> selectedProofs,
			List<IPRRoot> selectedFiles) {
		ProofPurgerSelectionDialog dialog =
				new ProofPurgerSelectionDialog(shell,
						new ProofPurgerContentProvider(unusedProofs,
								unusedFiles));
		dialog.create();
		final int userAction = dialog.open();
		if (userAction == Window.OK) {
			selectedProofs.addAll(dialog.getSelectedProofs());
			selectedFiles.addAll(dialog.getSelectedFiles());
			return true;
		}
		return false;
	}

	private void launchPurgerOperation(Shell shell, Operation operation) {
		try {
			new ProgressMonitorDialog(shell).run(true, true,
					operation);
		} catch (InvocationTargetException e) {
			final String errorMessage =
					Messages.filepurgeaction_runningpurgeroperation
							+ operation.toString();
			UIUtils.showUnexpectedError(e.getCause(), errorMessage);
		} catch (InterruptedException e) {
			// Propagate the interruption
			Thread.currentThread().interrupt();
		}
	}

}
