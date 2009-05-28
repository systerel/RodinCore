/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.core.pom.RecalculateAutoStatus;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * This is mostly copied from 
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

	@Override
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
					if (obj instanceof IRodinProject || obj instanceof IProject) {
						IRodinProject rodinPrj;
						if (obj instanceof IProject ){
							rodinPrj = RodinCore.valueOf((IProject) obj);
						} else{
							rodinPrj = (IRodinProject) obj;
						}
						if (rodinPrj.exists()) {
							final IPSRoot[] psRoots;
							try {
								psRoots = rodinPrj
										.getRootElementsOfType(IPSRoot.ELEMENT_TYPE);
							} catch (RodinDBException e) {
								EventBUIExceptionHandler
										.handleGetChildrenException(e,
												UserAwareness.IGNORE);
								continue;
							}
							for (IPSRoot root : psRoots) {
								treatRoot(root, monitor);
							}
						}
					}
					if (obj instanceof IEventBRoot) {
						IEventBRoot root = (IEventBRoot) obj;
						if (root instanceof IMachineRoot
								|| root instanceof IContextRoot) {
							treatRoot(root, monitor);
						}
					}
					
					if (obj instanceof IPSStatus) {
						
						IPSStatus status = (IPSStatus)obj;
						IRodinFile psFile = status.getOpenable();
						IPSRoot psRoot = (IPSRoot) psFile.getRoot();
						IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
						IPSStatus[] statuses = new IPSStatus[]{status};
						try {
							// AutoProver.run(prFile, psFile, statuses, monitor);
							RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
							continue;
						} catch (RodinDBException e) {
							EventBUIExceptionHandler.handleRodinException(
									e, UserAwareness.IGNORE);
							continue;
						}

					}
					
					if (obj instanceof IElementNode) {
						treateNode(monitor, (IElementNode) obj);
						continue;
					}
					
					//invariants, events, theorems, axioms
					if (obj instanceof IRodinElement) {
						IModelElement element = ModelController.getModelElement(obj);
						treatElement(monitor, element);
						continue;
						
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
	
	void treatRoot(IEventBRoot root, IProgressMonitor monitor) {
		IPSRoot psRoot = root.getPSRoot();
		IRodinFile psFile = psRoot.getRodinFile();
		IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
		IPSStatus[] statuses;
		try {
			statuses = psRoot.getStatuses();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler
					.handleGetChildrenException(e,
							UserAwareness.IGNORE);
			return;
		}
		try {
			// AutoProver.run(prFile, psFile, statuses,
			// monitor);
			RecalculateAutoStatus.run(prFile, psFile,
					statuses, monitor);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(
					e, UserAwareness.IGNORE);
		}
		
	}
	
	void treatElement(IProgressMonitor monitor,
			IModelElement element) {
		if (element != null) {
			ArrayList<Object> stats = new ArrayList<Object>();
			stats.addAll(Arrays.asList(element.getChildren(IPSStatus.ELEMENT_TYPE, false)));
			ArrayList<IPSStatus> result = new ArrayList<IPSStatus>();
			IPSStatus status = null;
			for (Object stat : stats) {
				if (stat instanceof IPSStatus) {
					result.add((IPSStatus) stat);
					status = (IPSStatus) stat;
				}
			}
			// at least one status found.
			if (status != null) {
				IRodinFile psFile = status.getRodinFile();
				IPSRoot psRoot = (IPSRoot) status.getRoot();
				IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
				try {
					RecalculateAutoStatus.run(prFile, psFile, result.toArray(new IPSStatus[result.size()]), monitor);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleRodinException(
							e, UserAwareness.IGNORE);
				}
			}
		}
	}


	void treateNode(IProgressMonitor monitor, IElementNode node) {
		if (node.getChildrenType() == IPSStatus.ELEMENT_TYPE) {
			IEventBRoot root = node.getParent();
			treatRoot(root, monitor);						
		} else {
			try {
				Object[] children=node.getParent().getChildrenOfType(node.getChildrenType());
				ArrayList<Object> stats = new ArrayList<Object>();
				
				for (Object child : children) {
					IModelElement element = ModelController.getModelElement(child);
					if (element != null) {
						stats.addAll(Arrays.asList(element.getChildren(IPSStatus.ELEMENT_TYPE, false)));
					}
				}
				ArrayList<IPSStatus> result = new ArrayList<IPSStatus>();
				for (Object stat : stats) {
					if (stat instanceof IPSStatus) {
						result.add((IPSStatus) stat);
					}
				}
				IRodinFile psFile = node.getParent().getPSRoot().getRodinFile();
				IPSRoot psRoot = (IPSRoot) psFile.getRoot();
				IRodinFile prFile = psRoot.getPRRoot().getRodinFile();
				RecalculateAutoStatus.run(prFile, psFile, result.toArray(new IPSStatus[result.size()]), monitor);
				
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleRodinException(
						e, UserAwareness.IGNORE);
			}
		}
	}
    
	
}
