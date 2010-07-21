/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofsimplifier;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class ProofSimplifyAction implements IObjectActionDelegate {

	private static class Simplify implements IRunnableWithProgress {

		final IPRProof[] proofs;

		public Simplify(IPRProof[] proofs) {
			this.proofs = proofs;
		}

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				RodinCore.run(new IWorkspaceRunnable() {
					public void run(IProgressMonitor pm) throws CoreException {
						final SubMonitor subMonitor = SubMonitor.convert(pm,
								proofs.length);
						subMonitor.setTaskName(Messages.proofSimplification_symplifyingProofs);
						for (IPRProof proof : proofs) {
							final IEventBRoot root = (IEventBRoot) proof
									.getRoot();
							final FormulaFactory ff = root.getFormulaFactory();
							try {
								EventBPlugin.simplifyProof(proof, ff,
										subMonitor.newChild(1));
							} catch (RodinDBException e) {
								UIUtils.log(e, "while simplifying proof: " //$NON-NLS-1$
										+ proof);
							}
						}
					}
				}, monitor);
			} catch (RodinDBException e) {
				UIUtils.log(e, "while simplifying proofs"); //$NON-NLS-1$
			} finally {
				monitor.done();
			}
		}

	}

	private static class FecthProofs implements IRunnableWithProgress {

		private final List<?> proofContainers;
		private final List<IPRProof> proofs = new ArrayList<IPRProof>();
		private boolean wasCancelled = false;

		public FecthProofs(List<?> proofContainers) {
			this.proofContainers = proofContainers;
		}

		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				final SubMonitor sm = SubMonitor.convert(monitor,
						Messages.proofSimplification_fetchingProofs, proofContainers.size());
				for (Object o : proofContainers) {
					if (o instanceof IPSStatus) {
						proofs.add(((IPSStatus) o).getProof());
					} else if (o instanceof IEventBRoot) {
						addAllProofs((IEventBRoot) o, proofs, sm.newChild(1));
					} else if (o instanceof IRodinProject) {
						final IRodinProject rodinProject = (IRodinProject) o;
						if (rodinProject.isOpen()) {
							addAllProofs(rodinProject, proofs, sm.newChild(1));
						} else {
							sm.worked(1);
						}
					} else {
						sm.worked(1);
					}
					if (monitor.isCanceled()) {
						proofs.clear();
						wasCancelled = true;
						return;
					}
				}
			} finally {
				monitor.done();
			}
		}

		public IPRProof[] getProofs() {
			return proofs.toArray(new IPRProof[proofs.size()]);
		}

		private static void addAllProofs(final IRodinProject project,
				final List<IPRProof> result, IProgressMonitor monitor) {
			try {
				final IPRRoot[] prRoots = project
						.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
				final SubMonitor sm = SubMonitor.convert(monitor,
						prRoots.length);
				for (IPRRoot root : prRoots) {
					addAllProofs(root, result, sm.newChild(1));
					if (monitor.isCanceled())
						return;
				}
			} catch (RodinDBException e) {
				UIUtils.log(e, "while fetching proof files in project: " //$NON-NLS-1$
						+ project);
			} finally {
				monitor.done();
			}
		}

		private static void addAllProofs(IEventBRoot root,
				final List<IPRProof> result, IProgressMonitor monitor) {
			try {
				final IPRProof[] proofs = root.getPRRoot().getProofs();
				result.addAll(Arrays.asList(proofs));
			} catch (RodinDBException e) {
				UIUtils.log(e, "while fetching proofs in file: " + root); //$NON-NLS-1$
			} finally {
				monitor.done();
			}
		}

		public boolean wasCancelled() {
			return wasCancelled;
		}

	}

	private IWorkbenchPartSite site;
	private IStructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		site = targetPart.getSite();
	}

	public void run(IAction action) {
		if (selection == null) {
			UIUtils.showInfo(Messages.proofSimplification_invalidSelection);
			return;
		}
		final FecthProofs fetchProofs = new FecthProofs(selection.toList());
		runOp(fetchProofs);
		if (fetchProofs.wasCancelled())
			return;
		IPRProof[] input = fetchProofs.getProofs();

		if (input.length == 0) {
			UIUtils.showInfo(Messages.proofSimplification_noProofsToSimplify);
			return;
		}
		final Simplify simplify = new Simplify(input);
		runOp(simplify);
	}

	private void runOp(final IRunnableWithProgress op) {
		try {
			new ProgressMonitorDialog(site.getShell()).run(true, true, op);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			UIUtils.showUnexpectedError(cause, "while simplifying proofs");
		} catch (InterruptedException e) {
			// Propagate the interruption
			Thread.currentThread().interrupt();
		}
	}

	public void selectionChanged(IAction action, ISelection s) {
		if (s instanceof IStructuredSelection) {
			selection = (IStructuredSelection) s;
		} else {
			selection = null;
		}
	}

}
