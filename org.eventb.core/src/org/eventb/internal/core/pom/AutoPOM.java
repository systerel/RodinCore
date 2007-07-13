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
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
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

	public static boolean DEBUG = false;
	
	private static FormulaFactory ff = FormulaFactory.getDefault();

	public boolean run(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		
		IPSFile psFile = (IPSFile) RodinCore.valueOf(file).getMutableCopy();
		IPRFile prFile = (IPRFile) psFile.getPRFile().getMutableCopy();
		IPOFile poFile = (IPOFile) psFile.getPOFile().getSnapshot();
		
		final String componentName = prFile.getComponentName();
		
		final IPOSequent[] pos = poFile.getSequents();
		final int noOfPOs = pos.length;
		final int workUnits = 3 + 2 + noOfPOs * 2;
		// 3 : creating fresh PR file
		// 1x : updating eash proof status
		// 2 : saving PR file
		// 1x : autoproving each proof obligation
		
		try {
			monitor.beginTask("Proving " + componentName, workUnits);
			
			// remove old proof status
			monitor.subTask("cleaning up");
			makeFresh(prFile,psFile,null);
			monitor.worked(3);
			checkCancellation(monitor, prFile, psFile);
			
			// create new proof status
			for (int i = 0; i < pos.length; i++) {
				
				final String name = pos[i].getElementName();
				monitor.subTask("updating status for " + name);
				
				final IPRProof prProof = prFile.getProof(name);
				
				if (! prProof.exists()) {
					prProof.create(null, null);
					// prProof.initialize(null);
				}
				
				
				final IPSStatus oldStatus = ((IPSFile) psFile.getSnapshot()).getStatus(name);
				IPSStatus status = psFile.getStatus(name);
				// TODO : for some reason, oldStatus never exists
				if (oldStatus.exists())
				{
					oldStatus.copy(psFile, null, null, true, null);
				}
				else
				{
					status.create(null, monitor);					
				}
				try {
					updateStatus(status,monitor);
				} catch (Exception e) {
					Util.log(e, "Exception thrown while updating ststus for "+status.getElementName());
				}
				
				monitor.worked(1);
				checkCancellation(monitor, prFile, psFile);
			}
			
			monitor.subTask("saving");
			prFile.save(new SubProgressMonitor(monitor, 1), true, true);
			psFile.save(new SubProgressMonitor(monitor, 1), true, false);
			
			checkCancellation(monitor, prFile, psFile);

			SubProgressMonitor spm = new SubProgressMonitor(monitor, noOfPOs,
					SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK					
			);
			AutoProver.run(prFile, psFile, spm);
			return true;
		} finally {
			monitor.done();
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
	
	public void clean(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		
		IPSFile psFile = (IPSFile) RodinCore.valueOf(file);
		if (psFile.exists()) {
			psFile.delete(true, null);
		}

		// Don't delete the PR file, it contains user proofs.
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {	
		
		try {
			
			monitor.beginTask("Extracting " + file.getName(), 1);
		
			IPOFile source = (IPOFile) RodinCore.valueOf(file);
			// IPRFile target = source.getPRFile();
			IPSFile target = source.getPSFile();
		
			graph.addTarget(target.getResource());
			graph.addToolDependency(
					source.getResource(), 
					target.getResource(), true);
			
		} finally {
			monitor.done();
		}
	}

	private void makeFresh(IPRFile prFile, IPSFile psFile,
			IProgressMonitor monitor) throws RodinDBException {
		
		// IRodinProject project = prFile.getRodinProject();

		// Create a new PR file if none exists
		if (! prFile.exists())
			prFile.create(true, monitor);
			//project.createRodinFile(prFile.getElementName(), true, monitor);
		
		// Create a fresh PS file
		// TODO : modify once signatures are implemented
		if (! psFile.exists()) 
			psFile.create(true, monitor);
		else
		{
			IPSStatus[] statuses = psFile.getStatuses();
			for (IPSStatus status : statuses) {
				status.delete(true, monitor);
			}
		}
	}
	
	
//	 lock po & pr files before calling this method
// TODO : a version with Proof tree from seqProver
	public static void updateStatus(IPSStatus status, IProgressMonitor monitor)
			throws RodinDBException {

		IProverSequent seq =  POLoader.readPO(status.getPOSequent());
		final IPRProof prProof = status.getProof();
		final boolean broken;
		if (prProof.exists()) {
			IProofDependencies deps = prProof.getProofDependencies(ff, monitor);
			broken = ! ProverLib.proofReusable(deps,seq);
		} else {
			broken = false;
		}
		// status.setProofConfidence(null);
		status.copyProofInfo(null);
		status.setBroken(broken, null);
	}
	
}
