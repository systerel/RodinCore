/*******************************************************************************
 * Copyright (c) 2005, 2017 ETH Zurich and others.
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

import static org.eventb.internal.core.Util.addExtensionDependencies;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
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
			throws CoreException {
		final IRodinFile psFile = RodinCore.valueOf(target);
		final IPSRoot psRoot = (IPSRoot) psFile.getRoot();
		final IProofComponent pc = manager.getProofComponent(psRoot);

		final String componentName = psRoot.getComponentName();
		
		final IPOSequent[] poSequents = pc.getPORoot().getSequents();
		final int nbOfPOs = poSequents.length;
		final int workUnits = 2 + nbOfPOs * 2 + 2 + nbOfPOs;
		final SubMonitor sMonitor = SubMonitor.convert(pm, "Processing PO for " + componentName + ": ", workUnits);
		
		try {
			sMonitor.subTask("loading");
			createFreshProofFile(pc, sMonitor.split(1));
			
			// update proof statuses
			final PSUpdater updater = new PSUpdater(pc, sMonitor.split(1));
			for (final IPOSequent poSequent : poSequents) {
				sMonitor.subTask("updating status of " + poSequent.getElementName());
				updater.updatePO(poSequent, sMonitor.split(1));
			}
			
			updater.cleanup(sMonitor.split(nbOfPOs));
			
			sMonitor.subTask("saving");
			pc.save(sMonitor.split(2), true);
			
			if (AutoProver.isEnabled()) {
				AutoProver.run(pc, updater.getOutOfDateStatuses(), sMonitor.split(nbOfPOs));
			}
			return true;
		} catch(OperationCanceledException e) {
			// Cleanup PR & PS files (may have unsaved changes).
			tryMakeConsistent(pc);
			throw e;
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
								 / (float) (PSUpdater.totalPOs - PSUpdater.newPOs);
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

	public static void tryMakeConsistent(IProofComponent pc) {
		try {
			pc.makeConsistent(null);
		} catch (RodinDBException e) {
			Util.log(e, "when reverting changes to proof and status files for"
					+ ((IPORoot)pc.getPRRoot()).getComponentName());
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
			addExtensionDependencies(graph, targetRoot);
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
		final IPRRoot prRoot = pc.getPRRoot();
		if (!prRoot.exists()) {
			if (prRoot.getResource().exists()) { // file is corrupted
				Util.log(null, "overriding corrupted proof file "
						+ prRoot.getResource().getFullPath().toString());
			}
			prRoot.getRodinFile().create(true, pm);
		}
	}
	
}
