/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.navigator.actionProviders;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.internal.core.pom.RecalculateAutoStatus;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * This is mostly coppied from 
 * org.eventb.internal.ui.obligationexplorer.actions.ObligationsRecalcuateAutoStatus
 *
 */
public class RecalculateAutoStatusAction extends Action {

	public RecalculateAutoStatusAction(StructuredViewer viewer) {
		this.viewer = viewer;
		setText("Re&calculate Auto Status");
		setToolTipText("Rerun the Auto Prover on all selected proof obligations to recalculate the auto proven status");
		setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_DISCHARGED_PATH));
	}

    private StructuredViewer viewer;

	public void run() {
		// Rerun the auto prover on selected elements.
		// The enablement condition guarantees that only machineFiles and
		// contextFiles are selected.
		ISelection sel = viewer.getSelection();
		assert (sel instanceof IStructuredSelection);
		IStructuredSelection ssel = (IStructuredSelection) sel;
		
		final Object [] objects = ssel.toArray(); 
				
		// Run the auto prover on all remaining POs
		IRunnableWithProgress op = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				for (Object obj : objects) {
					if (obj instanceof IRodinProject) {
						// Run the Auto Prover on all IPSFile in this project
						IRodinProject rodinPrj = (IRodinProject) obj;
						IPSFile[] psFiles;
						try {
							psFiles = rodinPrj
									.getChildrenOfType(IPSFile.ELEMENT_TYPE);
						} catch (RodinDBException e) {
							EventBUIExceptionHandler
									.handleGetChildrenException(e,
											UserAwareness.IGNORE);
							continue;
						}
						for (IPSFile psFile : psFiles) {
							IPRFile prFile = psFile.getPRFile();
							IPSStatus[] statuses;
							try {
								statuses = psFile.getStatuses();
							} catch (RodinDBException e) {
								EventBUIExceptionHandler
										.handleGetChildrenException(e,
												UserAwareness.IGNORE);
								continue;
							}
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
					if (obj instanceof IMachineFile || obj instanceof IContextFile) {
						IPSFile psFile;
						if (obj instanceof IMachineFile)
							psFile = ((IMachineFile) obj).getPSFile();
						else {
							psFile = ((IContextFile) obj).getPSFile();
						}
						IPRFile prFile = psFile.getPRFile();
						IPSStatus[] statuses;
						try {
							statuses = psFile.getStatuses();
						} catch (RodinDBException e) {
							EventBUIExceptionHandler
									.handleGetChildrenException(e,
											UserAwareness.IGNORE);
							continue;
						}
						try {
							// AutoProver.run(prFile, psFile, statuses, monitor);
							RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
						} catch (RodinDBException e) {
							EventBUIExceptionHandler.handleRodinException(
									e, UserAwareness.IGNORE);
							continue;
						}
					}
					
					if (obj instanceof IPSStatus) {
						
						IPSStatus status = (IPSStatus)obj;
						IPSFile psFile = (IPSFile) status.getOpenable();
						IPRFile prFile = psFile.getPRFile();
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
		final Shell shell = Display.getDefault().getActiveShell();
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
    
	
}
