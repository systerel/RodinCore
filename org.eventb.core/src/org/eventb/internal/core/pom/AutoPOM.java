/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored for using the Proof Manager API
 *     Systerel - separation of file and root element
 *     Systerel - added formula extensions
 *******************************************************************************/
package org.eventb.internal.core.pom;

import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.internal.core.Util;
import org.rodinp.core.IRodinFile;
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
	private static int totalRuns = 0;
	
	private static final IProofManager manager = EventBPlugin.getProofManager();
	
	@Override
	public boolean run(IFile source, IFile target, IProgressMonitor pm)
			throws RodinDBException {
		
		final IRodinFile psFile = RodinCore.valueOf(target);
		final IPSRoot psRoot = (IPSRoot) psFile.getRoot();
		final IProofComponent pc = manager.getProofComponent(psRoot);

		final String componentName = psRoot.getComponentName();
		
		final IPOSequent[] poSequents = pc.getPORoot().getSequents();
		final int nbOfPOs = poSequents.length;
		final int workUnits = 2 + nbOfPOs * 2 + 2 + nbOfPOs;
		
		try {
			pm.beginTask("Proving " + componentName + ": ", workUnits);
			
			pm.subTask("loading");
			createFreshProofFile(pc, newSubProgressMonitor(pm, 1));
			checkCancellation(pm, pc);
			
			// update proof statuses
			final PSUpdater updater = new PSUpdater(pc,
					newSubProgressMonitor(pm, 1));
			for (final IPOSequent poSequent : poSequents) {
				pm.subTask("updating status of " + poSequent.getElementName());
				updater.updatePO(poSequent, newSubProgressMonitor(pm, 1));
				checkCancellation(pm, pc);
			}
			
			updater.cleanup(newSubProgressMonitor(pm, nbOfPOs));
			
			pm.subTask("saving");
			pc.save(newSubProgressMonitor(pm, 2), true);
			
			checkCancellation(pm, pc);

			IProgressMonitor spm = newSubProgressMonitor(pm, nbOfPOs);
			if (AutoProver.isEnabled()) {
				AutoProver.run(pc, updater.getOutOfDateStatuses(), spm);
			}
			return true;
		} finally {
			pm.done();
			
			// Output performance trace if required
			if (PERF_PROOFREUSE)
			{
				totalRuns++;

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
						"\nTotal runs of the POM: " + totalRuns +						
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

	private IProgressMonitor newSubProgressMonitor(IProgressMonitor pm,
			int ticks) {
		return new SubProgressMonitor(pm, ticks, PREPEND_MAIN_LABEL_TO_SUBTASK);
	}

	private void checkCancellation(IProgressMonitor monitor, IProofComponent pc) {
		if (monitor.isCanceled()) {
			// Cleanup PR & PS files (may have unsaved changes).
			try {
				pc.makeConsistent(null);
			} catch (RodinDBException e) {
				Util.log(e, "when reverting changes to proof and status files for"
						+ ((IPORoot)pc.getPRRoot()).getComponentName());
			}
			throw new OperationCanceledException();
		}
	}
	
	@Override
	public void clean(IFile source, IFile target, IProgressMonitor monitor)
			throws RodinDBException {
		final IRodinFile psFile = RodinCore.valueOf(target);
		if (psFile.exists()) {
			psFile.delete(true, monitor);
		}
		// Don't delete the PR file, it contains user proofs.
	}

	@Override
	public void extract(IFile source, IGraph graph, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask("Extracting " + source.getName(), 1);
			final IPSRoot targetRoot = getPSRoot(source);
			final IFile target = targetRoot.getResource();
			graph.addTarget(target);
			graph.addToolDependency(source, target, true);
		} finally {
			monitor.done();
		}
	}

	private static IPSRoot getPSRoot(IFile poResource) {
		final IRodinFile poFile = RodinCore.valueOf(poResource);
		final IPORoot poRoot = (IPORoot) poFile.getRoot();
		final IPSRoot psRoot = poRoot.getPSRoot();
		return psRoot;
	}

	private void createFreshProofFile(IProofComponent pc, IProgressMonitor pm)
			throws RodinDBException {
		final IRodinFile prFile = pc.getPRRoot().getRodinFile();
		if (!prFile.exists()) {
			prFile.create(true, pm);

			// Register the formula factory to use for the proof file
			final FormulaFactory ff = pc.getFormulaFactory();
			((IPRRoot) prFile.getRoot()).setFormulaFactory(ff);
		}
	}
	
}
