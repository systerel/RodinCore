/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import static fr.systerel.internal.explorer.navigator.ExplorerUtils.runWithProgress;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.internal.core.pom.AutoProver;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
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
 * org.eventb.internal.ui.obligationexplorer.actions.ObligationsAutoProver
 *
 */
public class RetryAutoProversAction extends Action {
	
	public RetryAutoProversAction(StructuredViewer viewer) {
		this.viewer = viewer;
		setText("&Retry Auto Provers");
		setToolTipText(" Retry the Automatic Provers on the selected proof obligations");
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
				final IProofManager pm = EventBPlugin.getProofManager();
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
							for (IPSRoot root: psRoots) {
								treatRoot(pm, root, monitor);
							}
						}
						continue;
					}
					if (obj instanceof IEventBRoot) {
						treatRoot(pm, (IEventBRoot) obj, monitor);
						continue;
					}
					
					if (obj instanceof IPSStatus) {
						
						IPSStatus status = (IPSStatus)obj;
						IRodinFile psFile = status.getOpenable();
						IPSRoot psRoot = (IPSRoot) psFile.getRoot();
						final IProofComponent pc = pm.getProofComponent(psRoot);
						IPSStatus[] statuses = new IPSStatus[]{status};
						try {
							AutoProver.run(pc, statuses, monitor);
							// RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
						} catch (RodinDBException e) {
							EventBUIExceptionHandler.handleRodinException(
									e, UserAwareness.IGNORE);
						}
						continue;

					}
					if (obj instanceof IElementNode) {
						treateNode(monitor, pm, (IElementNode) obj);
						continue;
					}
					
					//invariants, events, theorems, axioms
					if (obj instanceof IRodinElement) {
						IModelElement element = ModelController.getModelElement(obj);
						treatElement(monitor, element, pm);
						continue;
						
					}
					
				}
			}
			
		};
		
		runWithProgress(op);
	}

	void treatRoot(IProofManager pm, IEventBRoot root, IProgressMonitor monitor){
		final IProofComponent pc = pm.getProofComponent(root);
		IPSStatus[] statuses;
		try {
			statuses = pc.getPSRoot().getStatuses();
		} catch (RodinDBException e) {
			EventBUIExceptionHandler
					.handleGetChildrenException(e,
							UserAwareness.IGNORE);
			return;
		}
		try {
			AutoProver.run(pc, statuses, monitor);
			// RecalculateAutoStatus.run(prFile, psFile, statuses, monitor);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleRodinException(
					e, UserAwareness.IGNORE);
		}
		
	}

	void treateNode(IProgressMonitor monitor, final IProofManager pm,
			IElementNode node) {
		if (node.getChildrenType() == IPSStatus.ELEMENT_TYPE) {
			IEventBRoot root = node.getParent();
			treatRoot(pm, root, monitor);
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
				IPSRoot psRoot = node.getParent().getPSRoot();
				final IProofComponent pc = pm.getProofComponent(psRoot);
				AutoProver.run(pc, result.toArray(new IPSStatus[result.size()]), monitor);
				
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleRodinException(
						e, UserAwareness.IGNORE);
			}
			
		}
	}
	
	void treatElement(IProgressMonitor monitor,
			IModelElement element, IProofManager pm) {
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
				IPSRoot psRoot = (IPSRoot) psFile.getRoot();
				try {
					final IProofComponent pc = pm.getProofComponent(psRoot);
					AutoProver.run(pc, result.toArray(new IPSStatus[result.size()]), monitor);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleRodinException(
							e, UserAwareness.IGNORE);
				}
			}
		}
	}
	
}
