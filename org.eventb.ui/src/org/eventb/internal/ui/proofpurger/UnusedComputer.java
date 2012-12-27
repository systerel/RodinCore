/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofpurger;

import static org.eventb.internal.ui.proofpurger.ProofPurger.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 *
 */
public class UnusedComputer implements IWorkspaceRunnable {

	private final IRodinElement[] projectsOrFiles;
	private final List<IPRProof> unusedProofs;
	private final List<IPRRoot> unusedFiles;
	
	UnusedComputer(IRodinElement[] projectsOrFiles, List<IPRProof> unusedProofs,
			List<IPRRoot> unusedProofFiles) {
		this.projectsOrFiles = projectsOrFiles;
		this.unusedProofs = unusedProofs;
		this.unusedFiles = unusedProofFiles;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		progress.beginTask(Messages.proofpurger_computingunusedproofs, 100);
		progress.subTask(Messages.proofpurger_extractingprooffiles);
		debugHook();
		Set<IPRRoot> prFilesToProcess =
				extractProofFiles(projectsOrFiles, progress.newChild(20));
		if (progress.isCanceled() || prFilesToProcess == null)
			return;

		progress.subTask(Messages.proofpurger_extractingunusedproofs);
		debugHook();
		extractUnusedProofsOrFiles(prFilesToProcess, progress.newChild(80),
				unusedProofs, unusedFiles);
		if (progress.isCanceled() || unusedProofs == null)
			return;
		
		debugHook();

	}

	private static void debugHook() {
		if (DEBUG) {
			for (int i = 0; i < 80000000; i++) {
				Integer g = new Integer(i);
				g = g + 1;
			}
		}
	}

	private static Set<IPRRoot> extractProofFiles(
			IRodinElement[] projectsOrFiles, IProgressMonitor monitor)
			throws RodinDBException {
		Set<IPRRoot> prFilesToProcess = new LinkedHashSet<IPRRoot>();
		SubMonitor progress = SubMonitor.convert(monitor);

		if (projectsOrFiles.length > 0) {
			progress.setWorkRemaining(projectsOrFiles.length);
			for (IRodinElement elem : projectsOrFiles) {
				if (elem instanceof IEventBRoot) {
					final IPRRoot prRoot = ((IEventBRoot) elem).getPRRoot();
					if (prRoot.exists()) {
						prFilesToProcess.add(prRoot);
					}
				} else if (elem instanceof IRodinProject) {
					addProject(prFilesToProcess, (IRodinProject) elem);
				}
				progress.worked(1);
			}
		} else {
			progress.done();
		}
		return prFilesToProcess;
	}

	private static void addProject(Set<IPRRoot> prFilesToProcess,
			IRodinProject rodinProjectToAdd) throws RodinDBException {
		final IPRRoot[] prRoots =
				rodinProjectToAdd.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
		prFilesToProcess.addAll(Arrays.asList(prRoots));
	}

	private static void extractUnusedProofsOrFiles(
			Set<IPRRoot> prFilesToProcess, IProgressMonitor monitor,
			List<IPRProof> unusedPs, List<IPRRoot> unusedFs)
			throws RodinDBException {
		final int size = prFilesToProcess.size();
		SubMonitor progress = SubMonitor.convert(monitor);

		if (size == 0) {
			progress.done();
			return;
		}

		progress.setWorkRemaining(size);
		for (IPRRoot current : prFilesToProcess) {

			if (noProofNoPS(current)) {
				unusedFs.add(current);
			} else {				
				addUnusedProofs(current.getProofs(), unusedPs);
			}
			progress.worked(1);
			debugHook();
			if (progress.isCanceled()) {
				return;
			}
		}
	}

	private static boolean noProofNoPS(IPRRoot prRoot) throws RodinDBException {
		return prRoot.getProofs().length == 0
				&& !prRoot.getPSRoot().getRodinFile().exists();
	}
	
	private static void addUnusedProofs(final IPRProof[] proofs,
			List<IPRProof> unused) {
		for (IPRProof pr : proofs) {
			if (isToPurge(pr)) {
				unused.add(pr);
			}
		}
	}

}
