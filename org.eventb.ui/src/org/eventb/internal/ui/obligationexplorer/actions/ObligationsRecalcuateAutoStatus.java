/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.obligationexplorer.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.core.pom.RecalculateAutoStatus;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class ObligationsRecalcuateAutoStatus implements IViewActionDelegate {

	ISelection sel;

	TreeViewer viewer;
	
	public ObligationsRecalcuateAutoStatus() {
		// TODO Auto-generated constructor stub
	}

	public void init(IViewPart view) {
		// The enablement condition should guarantee that the part is the
		// Obligation Explorer.
		assert view instanceof ObligationExplorer;
		
		viewer = ((ObligationExplorer) view).getTreeViewer();
		// The viewer must be initialised.
		assert viewer != null;
	}

	public void run(IAction action) {
		// Rerun the auto prover on selected elements.
		// The enablement condition guarantees that only machineFiles and
		// contextFiles are selected.
		
		assert (sel instanceof IStructuredSelection);
		IStructuredSelection ssel = (IStructuredSelection) sel;
		
		final Object [] objects = TreeSupports.treeSelectionToSet(viewer, ssel); 
				
		// Run the auto prover on all remaining POs
		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				for (Object obj : objects) {
					if (obj instanceof IRodinProject) {
						// Run the Auto Prover on all IPSFile in this project
						IRodinProject rodinPrj = (IRodinProject) obj;
						IPSRoot[] psRoot;
						try {
							psRoot = rodinPrj
									.getChildrenOfType(IPSRoot.ELEMENT_TYPE);
						} catch (RodinDBException e) {
							EventBUIExceptionHandler
									.handleGetChildrenException(e,
											UserAwareness.IGNORE);
							continue;
						}
						for (IPSRoot root : psRoot) {
							IPRRoot prRoot = root.getPRRoot();
							IPSStatus[] statuses;
							try {
								statuses = root.getStatuses();
							} catch (RodinDBException e) {
								EventBUIExceptionHandler
										.handleGetChildrenException(e,
												UserAwareness.IGNORE);
								continue;
							}
							try {
								// AutoProver.run(prFile, psFile, statuses, monitor);
								RecalculateAutoStatus.run(prRoot.getRodinFile(), root.getRodinFile(), statuses, monitor);
							} catch (RodinDBException e) {
								EventBUIExceptionHandler.handleRodinException(
										e, UserAwareness.IGNORE);
								continue;
							}							
						}
					}
					if (obj instanceof IRodinFile) {
						IRodinFile rf = (IRodinFile) obj;
						IInternalElement root = rf.getRoot();
						if (root instanceof IMachineRoot
								|| root instanceof IContextRoot) {
							IPSRoot psRoot = ((IEventBRoot) root).getPSRoot();
							IRodinFile psFile = psRoot.getRodinFile();
							IRodinFile prFile = psRoot.getPRRoot()
									.getRodinFile();
							IPSStatus[] statuses;
							try {
								statuses = psRoot.getStatuses();
							} catch (RodinDBException e) {
								EventBUIExceptionHandler
										.handleGetChildrenException(e,
												UserAwareness.IGNORE);
								continue;
							}
							try {
								// AutoProver.run(prFile, psFile, statuses,
								// monitor);
								RecalculateAutoStatus.run(prFile, psFile,
										statuses, monitor);
							} catch (RodinDBException e) {
								EventBUIExceptionHandler.handleRodinException(
										e, UserAwareness.IGNORE);
								continue;
							}
						}
					}
					
					if (obj instanceof IPSStatus) {
						
						IPSStatus status = (IPSStatus)obj;
						IRodinFile psFile = (IRodinFile) status.getOpenable();
						IPSRoot psRoot = (IPSRoot) psFile.getRoot();
						IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
						IPSStatus[] statuses = new IPSStatus[]{status};
						try {
							// AutoProver.run(prFile, psFile, statuses, monitor);
							RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
						} catch (RodinDBException e) {
							EventBUIExceptionHandler.handleRodinException(
									e, UserAwareness.IGNORE);
							continue;
						}

					}
				}
			}
			
		};
		
		runWithProgress(op);
	}


	private void runWithProgress(IRunnableWithProgress op) {
		final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(true, true, op);
		} catch (InterruptedException exception) {
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			if (ProofControlUtils.DEBUG)
				ProofControlUtils.debug("Interrupt");
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.sel = selection;
	}

}
