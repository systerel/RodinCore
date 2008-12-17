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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Provides proof purging facilities. Being given projects or files, it allows
 * to determine potentially unused proofs (some of them might be intended to be
 * used in the future). It can also perform actual deletion of unused proofs.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofPurger implements IProofPurger {

	private static final boolean DEBUG = false;

	private static ProofPurger instance;

	private ProofPurger() {
		// Singleton: Private default constructor
	}

	public static ProofPurger getDefault() {
		if (instance == null)
			instance = new ProofPurger();
		return instance;
	}

	/**
	 * Computes an array of potentially unused proofs. Actually, it filters on
	 * proofs which have no associated PO.
	 * 
	 * @param projectsOrFiles
	 *            Selection of projects or files to be searched in. Its elements
	 *            types should be either IRodinProject or IEventBFile.
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            canceled.
	 * @throws RodinDBException
	 */
	public void computeUnusedProofsOrFiles(IRodinElement[] projectsOrFiles,
			IProgressMonitor monitor, List<IPRProof> unusedProofs,
			List<IPRRoot> unusedProofFiles) throws RodinDBException {
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
				unusedProofs, unusedProofFiles);
		if (progress.isCanceled() || unusedProofs == null)
			return;
		
		debugHook();
	}

	/**
	 * Deletes all given unused proofs. If any of these proofs are actually
	 * used, throws IllegalArgumentException.
	 * 
	 * @param proofs
	 *            An array containing proofs to delete.
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            canceled.
	 * @throws IllegalArgumentException
	 * @throws RodinDBException
	 */
	public void purgeUnusedProofsOrFiles(List<IPRProof> proofs,
			List<IPRRoot> files, IProgressMonitor monitor)
			throws IllegalArgumentException, RodinDBException {
		// TODO encapsulate into RodinCore.run()
		SubMonitor progress = SubMonitor.convert(monitor, 100);
		progress.beginTask(Messages.proofpurger_deletingselectedproofs, 100);
		progress.subTask(Messages.proofpurger_verifyingselectedproofs);
		debugHook();
		verifyProofsAndFiles(proofs, files, progress);
		progress.worked(10);
		if (progress.isCanceled())
			return;

		progress.subTask(Messages.proofpurger_deleting);
		debugHook();
		final Set<IPRRoot> openProofFiles = new LinkedHashSet<IPRRoot>();
		deleteProofs(proofs, progress.newChild(50), openProofFiles);
		progress.setWorkRemaining(40);
		if (progress.isCanceled())
			return;
		debugHook();

		final List<IPRRoot> toDelete = new ArrayList<IPRRoot>();
		final List<IPRRoot> toSave = new ArrayList<IPRRoot>();
		computeDeleteSave(openProofFiles, progress.newChild(10), toDelete, toSave);
		if (progress.isCanceled())
			return;
		
		toDelete.addAll(files);
		
		deleteFiles(toDelete, progress.newChild(20));
		if (progress.isCanceled())
			return;
		debugHook();

		progress.subTask(Messages.proofpurger_savingchanges);
		debugHook();
		saveFiles(toSave, progress.newChild(10));
	}

	private static void verifyProofsAndFiles(List<IPRProof> proofs,
			List<IPRRoot> files, SubMonitor progress) throws RodinDBException {
		if (!areAllUnusedProofs(proofs)) {
			throw new IllegalArgumentException(
					Messages.proofpurger_tryingtodeleteusedproofs);
		}
		if (progress.isCanceled())
			return;
		
		if (!areAllUnusedFiles(files)) {
			throw new IllegalArgumentException(
					Messages.proofpurger_tryingtodeleteusedfiles);
		}
	}

	private static void deleteFiles(List<IPRRoot> toDelete, SubMonitor monitor) throws RodinDBException {
		saveOrDeleteFiles(toDelete, monitor, false);		
	}

	private static void saveFiles(List<IPRRoot> toSave, SubMonitor monitor) throws RodinDBException {
		saveOrDeleteFiles(toSave, monitor, true);
	}

	
	/**
	 * @param files
	 * @param toDelete
	 * @param toSave
	 * @throws RodinDBException 
	 */
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

	private static void debugHook() {
		if (DEBUG) {
			for (int i = 0; i < 80000000; i++) {
				Integer g = new Integer(i);
				g = g + 1;
			}
		}
	}

	private static boolean isUsed(IPRProof pr) {
		final String name = pr.getElementName();
		final IPORoot poRoot =
				((IPRRoot) pr.getRodinFile().getRoot()).getPORoot();
		return poRoot.getSequent(name).exists();
	}

	private static boolean areAllUnusedProofs(List<IPRProof> proofs) {
		for (IPRProof pr : proofs) {
			if (isUsed(pr)) {
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

	private static void extractUnusedProofsOrFiles(
			Set<IPRRoot> prFilesToProcess, IProgressMonitor monitor,
			List<IPRProof> unusedProofs, List<IPRRoot> unusedProofFiles)
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
				unusedProofFiles.add(current);
			} else {				
				addUnusedProofs(current.getProofs(), unusedProofs);
			}
			progress.worked(1);
			debugHook();
			if (progress.isCanceled()) {
				return;
			}
		}
	}

	private static void addUnusedProofs(final IPRProof[] proofs,
			List<IPRProof> unusedProofs) {
		for (IPRProof pr : proofs) {
			if (!isUsed(pr)) {
				unusedProofs.add(pr);
			}
		}
	} 


	private static void addProject(Set<IPRRoot> prFilesToProcess,
			IRodinProject rodinProjectToAdd) throws RodinDBException {
		final IPRRoot[] prRoots =
				rodinProjectToAdd.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
		prFilesToProcess.addAll(Arrays.asList(prRoots));
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

	private static boolean noProofNoPS(IPRRoot prRoot) throws RodinDBException {
		return prRoot.getProofs().length == 0
				&& !prRoot.getPSRoot().getRodinFile().exists();
	}
}
