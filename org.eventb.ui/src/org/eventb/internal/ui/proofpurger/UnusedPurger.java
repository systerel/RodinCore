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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 *
 */
public class UnusedPurger  implements IWorkspaceRunnable {

	private final List<IPRProof> proofsToPurge;
	private final List<IPRRoot> filesToPurge;
	
	public UnusedPurger(List<IPRProof> proofs,
			List<IPRRoot> files) {
		super();
		this.proofsToPurge = proofs;
		this.filesToPurge = files;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		progress.beginTask(Messages.proofpurger_deletingselectedproofs, 100);
		progress.subTask(Messages.proofpurger_verifyingselectedproofs);
		debugHook();
		verifyProofsAndFiles(progress);
		progress.worked(10);
		if (progress.isCanceled())
			return;

		progress.subTask(Messages.proofpurger_deleting);
		debugHook();
		final Set<IPRRoot> openProofFiles = new LinkedHashSet<IPRRoot>();
		deleteProofs(proofsToPurge, progress.newChild(50), openProofFiles);
		progress.setWorkRemaining(40);
		if (progress.isCanceled())
			return;
		debugHook();

		final List<IPRRoot> toDelete = new ArrayList<IPRRoot>();
		final List<IPRRoot> toSave = new ArrayList<IPRRoot>();
		computeDeleteSave(openProofFiles, progress.newChild(10), toDelete, toSave);
		progress.setWorkRemaining(30);
		if (progress.isCanceled())
			return;
		
		toDelete.addAll(filesToPurge);
		
		deleteFiles(toDelete, progress.newChild(20));
		progress.setWorkRemaining(10);
		if (progress.isCanceled())
			return;
		debugHook();

		progress.subTask(Messages.proofpurger_savingchanges);
		debugHook();
		saveFiles(toSave, progress.newChild(10));
	}

	private void verifyProofsAndFiles(SubMonitor progress) throws RodinDBException {
		if (!areAllUnusedProofs(proofsToPurge)) {
			throw new IllegalArgumentException(
					Messages.proofpurger_tryingtodeleteusedproofs);
		}
		if (progress.isCanceled())
			return;
		
		if (!areAllUnusedFiles(filesToPurge)) {
			throw new IllegalArgumentException(
					Messages.proofpurger_tryingtodeleteusedfiles);
		}
	}

	private static void deleteProofs(List<IPRProof> proofs,
			IProgressMonitor monitor, Set<IPRRoot> prOpenFiles)
			throws RodinDBException {
		SubMonitor progress = SubMonitor.convert(monitor);
		final int size = proofs.size();
		if (size == 0) {
			progress.done();
			return;
		}
		progress.setWorkRemaining(size);
		for (IPRProof pr : proofs) {
			if (pr.exists()) {
				prOpenFiles.add((IPRRoot) pr.getRodinFile().getRoot());
				pr.delete(false, null);
			}
			progress.worked(1);
			debugHook();
			if (progress.isCanceled()) {
				return;
			}
		}
	}

	private static void computeDeleteSave(Set<IPRRoot> files, IProgressMonitor monitor,
			List<IPRRoot> toDelete, List<IPRRoot> toSave) throws RodinDBException {
		
		SubMonitor progress = SubMonitor.convert(monitor);
		final int size = files.size();

		if (size == 0) {
			progress.done();
			return;
		}
		progress.setWorkRemaining(size);
		for (IPRRoot prRoot : files) {
			final IRodinFile prFile = prRoot.getRodinFile();
			if (prFile.exists()) {
				if (noProofNoPS(prRoot)) {
					toDelete.add(prRoot);
				} else if (prFile.hasUnsavedChanges()) {
						toSave.add(prRoot);
				}
			}
			progress.worked(1);
			if (progress.isCanceled()) {
				return;
			}
		}

		
	}

	private static void deleteFiles(List<IPRRoot> toDelete, SubMonitor monitor) throws RodinDBException {
		saveOrDeleteFiles(toDelete, monitor, false);		
	}

	private static void saveFiles(List<IPRRoot> toSave, SubMonitor monitor) throws RodinDBException {
		saveOrDeleteFiles(toSave, monitor, true);
	}

	private static void saveOrDeleteFiles(List<IPRRoot> files,
			IProgressMonitor monitor, boolean save) throws RodinDBException {
		final int size = files.size();
		SubMonitor progress = SubMonitor.convert(monitor);

		if (size == 0) {
			progress.done();
			return;
		}
		progress.setWorkRemaining(size);
		for (IPRRoot prRoot : files) {
			final IRodinFile prFile = prRoot.getRodinFile();
			if(save) {
				prFile.save(null, false);
			} else {
				prFile.delete(false, null);
			}
			
			progress.worked(1);
			if (progress.isCanceled()) {
				return;
			}
		}
	}

	private static boolean areAllUnusedProofs(List<IPRProof> proofs) {
		for (IPRProof pr : proofs) {
			if (!isToPurge(pr)) {
				return false;
			}
		}
		return true;
	}

	private static boolean areAllUnusedFiles(List<IPRRoot> files) throws RodinDBException {
		for (IPRRoot prRoot: files) {
			if (!noProofNoPS(prRoot)) {
				return false;
			}
		}
		return true;
	}

}