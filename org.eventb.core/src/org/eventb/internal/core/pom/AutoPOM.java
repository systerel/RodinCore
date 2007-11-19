/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSWrapper;
import org.eventb.internal.core.PSWrapper;
import org.eventb.internal.core.Util;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Farhad Mehta
 *
 */
public class AutoPOM implements IAutomaticTool, IExtractor {

	/**
	 * Flag for the POM debug trace
	 */
	public static boolean DEBUG = false;
	
	/**
	 * Flag for the POM proof reuse performance trace
	 */
	public static boolean PERF_PROOFREUSE = false;

	/**
	 * Variable that records the number of runs of the POM during the entire session.
	 * <p>
	 * Note: This value is only updated if the <code>PERF_PROOFREUSE</code> flag is set to
	 * <code>true</code>.
	 * </p> 
	 */
	private static int toalRuns = 0;
	
	
	public boolean run(IFile source, IFile target, IProgressMonitor pm)
			throws RodinDBException {
		
		final IPSFile psFile = (IPSFile) RodinCore.valueOf(target);
		final IPOFile poFile = (IPOFile) psFile.getPOFile().getSnapshot();
		final IPRFile prFile = psFile.getPRFile();

		final String componentName = psFile.getComponentName();
		
		final IPOSequent[] poSequents = poFile.getSequents();
		final int nbOfPOs = poSequents.length;
		final int workUnits = 2 + nbOfPOs * 2 + 1 + nbOfPOs;
		
		try {
			pm.beginTask("Proving " + componentName, workUnits);
			
			createFreshProofFile(prFile, new SubProgressMonitor(pm, 1));
			checkCancellation(pm, prFile, psFile);
			
			// update proof statuses
			final IPSWrapper psWrapper = new PSWrapper(psFile);
			final PSUpdater updater = new PSUpdater(psWrapper,
					new SubProgressMonitor(pm, 1));
			for (final IPOSequent poSequent : poSequents) {
				final IProgressMonitor spm = new SubProgressMonitor(pm, 1);
				updater.updatePO(poSequent, spm);
				checkCancellation(pm, prFile, psFile);
			}
			
			updater.cleanup(new SubProgressMonitor(pm, nbOfPOs));
			
			pm.subTask("saving");
			prFile.save(new SubProgressMonitor(pm, 1), true, true);
			psFile.save(new SubProgressMonitor(pm, 1), true, false);
			
			checkCancellation(pm, prFile, psFile);

			SubProgressMonitor spm = new SubProgressMonitor(pm, nbOfPOs,
					SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK					
			);
			if (AutoProver.isEnabled())
				AutoProver.run(prFile, psFile, updater.getOutOfDateStatuses(), spm);
			return true;
		} finally {
			pm.done();
			
			// Output performance trace if required
			if (PERF_PROOFREUSE)
			{
				toalRuns++;

				// Calculate percentage of proofs reused
				float proofReuse;
				if (PSUpdater.totalPOs - PSUpdater.newPOs == 0){
					proofReuse = 0;
				}
				else{
				proofReuse = ((PSUpdater.recoverablePOsWithProofs + PSUpdater.unchangedPOsWithProofs) * 100)
								 / (PSUpdater.totalPOs - PSUpdater.newPOs);
				}
				
				System.out.println(
						"=========== Cumulative POM Proof reuse performance ==========" +
						"\nTotal runs of the POM: " + toalRuns +						
						"\nTotal # POs processed: " + PSUpdater.totalPOs +
						"\n # Unchanged: " + PSUpdater.unchangedPOs +
						"\t\t(with non-empty proofs: " + PSUpdater.unchangedPOsWithProofs +")" +
						"\n # Recoverable: " + PSUpdater.recoverablePOs + 
						"\t(with non-empty proofs: " + PSUpdater.recoverablePOsWithProofs +")" +
						"\n # Irrrecoverable: " + PSUpdater.irrecoverablePOs +
						"\t(with non-empty proofs: " + PSUpdater.irrecoverablePOsWithProofs +")" +
						"\n # New: " + PSUpdater.newPOs +
						"\n\n%'age Proofs Reused: " + proofReuse +
						"\n=============================================================\n");
			}
		}
	}

	private void checkCancellation(IProgressMonitor monitor, IPRFile prFile, IPSFile psFile) {
//		 TODO harmonize cleanup after cancellation
		if (monitor.isCanceled()) {
			// Cleanup PR & PS files (may have unsaved changes).
			try {
				prFile.makeConsistent(null);
				psFile.makeConsistent(null);
			} catch (RodinDBException e) {
				Util.log(e, "when reverting changes to proof and status files for"
						+ prFile.getElementName());
			}
			throw new OperationCanceledException();
		}
	}
	
	public void clean(IFile source, IFile target, IProgressMonitor monitor)
			throws RodinDBException {
		final IPSFile psFile = (IPSFile) RodinCore.valueOf(target);
		if (psFile.exists()) {
			psFile.delete(true, monitor);
		}
		// Don't delete the PR file, it contains user proofs.
	}

	public void extract(IFile source, IGraph graph, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask("Extracting " + source.getName(), 1);
			final IFile target = getPSResource(source);
			graph.addTarget(target);
			graph.addToolDependency(source, target, true);
		} finally {
			monitor.done();
		}
	}

	private static IFile getPSResource(IFile poResource) {
		final IPOFile poFile = (IPOFile) RodinCore.valueOf(poResource);
		final IPSFile psFile = poFile.getPSFile();
		return psFile.getResource();
	}

	private void createFreshProofFile(IPRFile prFile, IProgressMonitor pm) throws RodinDBException {
		if (!prFile.exists())
			prFile.create(true, pm);
	}
	
}
