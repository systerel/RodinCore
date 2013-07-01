/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class ProofSimplification {

	public static class Simplify implements IRunnableWithProgress {
	
		final IPRProof[] proofs;
	
		public Simplify(IPRProof[] proofs) {
			this.proofs = proofs;
		}
	
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				RodinCore.run(new IWorkspaceRunnable() {
					@Override
					public void run(IProgressMonitor pm) throws CoreException {
						final SubMonitor subMonitor = SubMonitor.convert(pm,
								proofs.length);
						subMonitor.setTaskName(Messages.proofSimplification_symplifyingProofs);
						for (IPRProof proof : proofs) {
							try {
								EventBPlugin.simplifyProof(proof,
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

	public static class FecthProofs implements IRunnableWithProgress {
	
		private final List<?> proofContainers;
		private final List<IPRProof> proofs = new ArrayList<IPRProof>();
		private boolean wasCancelled = false;
	
		public FecthProofs(List<?> proofContainers) {
			this.proofContainers = proofContainers;
		}
	
		@Override
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
						if (rodinProject.exists()) {
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

}
