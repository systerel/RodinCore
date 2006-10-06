/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRSequent;
import org.eventb.core.basis.PRProofTree;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author fmehta
 *
 */
public class AutoPOM implements IAutomaticTool, IExtractor {

	public static boolean DEBUG = false;

	public void remove(IFile file, IFile origin, IProgressMonitor monitor) throws CoreException {
		try {
			
			monitor.beginTask("Removing " + file.getName(), 1);
			
			String s = EventBPlugin.getComponentName(file.getName());
			String t = EventBPlugin.getComponentName(origin.getName());
			if (s.equals(t)) {
				RodinCore.create(file).delete(true, monitor);
			}			
		} finally {
			monitor.done();
		}
	}

	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException {

		IPRFile prFile = (IPRFile) RodinCore.create(file).getMutableCopy();
		IPOFile poFile = (IPOFile) prFile.getPOFile().getSnapshot();
		int noOfPOs = poFile.getSequents().length;
		final int workUnits = 3 + 2 + noOfPOs * 2;
		// 3 : creating fresh PR file
		// 1x : updating eash proof status
		// 2 : saving PR file
		// 1x : autoproving each proof obligation
		
		try {
			
			monitor.beginTask("Managing proofs for " + file.getName(), workUnits);
			
			// remove old proof status
			createFreshPRFile(prFile,null);
			monitor.worked(3);
			if (monitor.isCanceled()) throw new OperationCanceledException();
			
			// create new proof status
			IPOSequent[] prfOblgs = poFile.getSequents();
			// List<IPRSequent> poStatus = Arrays.asList(prFile.getSequents());
			for (int i = 0; i < prfOblgs.length; i++) {
				
				final String name = prfOblgs[i].getName();
				monitor.subTask("Updating status for " + name);
				
				final IPRProofTree prProof = prFile.getProofTree(name);
				
				if (prProof == null){
					prFile.createProofTree(name);
				}
				
				final IPRSequent poState = (IPRSequent) prFile.createInternalElement(IPRSequent.ELEMENT_TYPE,name,null,null);
				
				poState.updateStatus();
				monitor.worked(1);
				if (monitor.isCanceled()) throw new OperationCanceledException();
			}
			
			prFile.save(new SubProgressMonitor(monitor,2), true);
			
//			monitor.beginTask("Managing proofs for " + file.getName(), workUnits);
//		
//			monitor.subTask("Loading old proofs ");
//			Map<String, IPRProofTree> oldProofs = getOldProofs(prFile);
//			monitor.worked(5);
//			if (monitor.isCanceled()) throw new OperationCanceledException();
//			monitor.subTask("Merging proofs ");
//			Map<String, Boolean> newValidity = computeNewValidity(oldProofs, poFile, monitor);
//			if (monitor.isCanceled()) throw new OperationCanceledException();
//			monitor.subTask("Writing new proofs ");
//			createFreshPRFile(newValidity,oldProofs, poFile, prFile, null);
//			prFile.save(new SubProgressMonitor(monitor,5), true);
//			
			
			if (monitor.isCanceled()) throw new OperationCanceledException();
			monitor.subTask("Running auto prover ");
			AutoProver.run(prFile,new SubProgressMonitor(monitor,noOfPOs));
			
			return true;
			
		} finally {
			monitor.done();
		}
	}

//	Map<String, IPRProofTree> getOldProofs(IPRFile prFile) throws RodinDBException{
//		if (prFile.exists())
//			return prFile.getProofTrees();
//		return new HashMap<String, IPRProofTree>();
//	}
//	
//	Map<String, Boolean> computeNewValidity(Map<String, IPRProofTree> oldProofs, IPOFile poFile, IProgressMonitor monitor) throws RodinDBException
//	{
//		Map<String, IProverSequent> newPOs = POLoader.readPOs(poFile);		
//		Map<String, Boolean> newValidity = new HashMap<String, Boolean>();
//		
//		for (Map.Entry<String, IProverSequent> newPO : newPOs.entrySet()){
//			String newPOname = newPO.getKey();
//			IProverSequent newPOseq = newPO.getValue();
//			IPRProofTree oldProof = oldProofs.get(newPOname);
//			IProofDependencies oldProofDependencies = null;
//			if 	(oldProof != null) oldProofDependencies = oldProof.getProofDependencies();
//			if  (oldProof != null &&
//					oldProofDependencies != null &&
//					Lib.proofReusable(oldProofDependencies,newPOseq))
//				{
//					newValidity.put(newPOname,true);
//				}
//			else
//				{
//					if (oldProof == null) 
//						newValidity.put(newPOname,true);
//					else
//						newValidity.put(newPOname,false);
//				}
//			monitor.worked(1);
//		}
//		return newValidity;
//	}
	
	
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException {
		
		IPRFile prFile = (IPRFile) RodinCore.create(file).getMutableCopy();
		
		try {
		
			final IRodinElement[] children = prFile.getChildren();
			
			monitor.beginTask("Cleaning " + file.getName(), children.length + 5);
			
			for (IRodinElement child : children){
				// do not delete interactive proofs !
				if ((! child.getElementType().equals(IPRProofTree.ELEMENT_TYPE)) ||
						((PRProofTree)child).isAutomaticallyGenerated())
					((InternalElement)child).delete(true,null);
				monitor.worked(1);
			}
			
			prFile.save(new SubProgressMonitor(monitor,5),true);
			
		} finally {
			monitor.done();
		}
		
	}

	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {	
		
		try {
			
			monitor.beginTask("Extracting " + file.getName(), 1);
		
			IPOFile source = (IPOFile) RodinCore.create(file);
			IPRFile target = source.getPRFile();
		
			graph.openGraph();
			graph.addNode(target.getResource(), POMCore.AUTO_POM_TOOL_ID);
			graph.putToolDependency(
					source.getResource(), 
					target.getResource(), POMCore.AUTO_POM_TOOL_ID, true);
			graph.closeGraph();
			
		} finally {
			monitor.done();
		}
	}

//	void createFreshPRFile(
//			Map<String, Boolean> newValidity, 
//			Map<String, IPRProofTree> oldProofs, 
//			IPOFile poFile,
//			IPRFile prFile, 
//			IProgressMonitor monitor) throws CoreException {
//
//		if (prFile.exists()) 
//		{
//			for (IRodinElement child : prFile.getChildren()){
//				// do not delete proofs !
//				if (!(child.getElementType().equals(IPRProofTree.ELEMENT_TYPE)))
//				((InternalElement)child).delete(true,null);
//			}			
//		}
//		else
//		{
//			IRodinProject project = prFile.getRodinProject();
//			project.createRodinFile(prFile.getElementName(), true, null);
//		}
//		copySequents(newValidity,oldProofs, poFile, prFile, monitor);
//	}
	
	private void createFreshPRFile(
			IPRFile prFile, 
			IProgressMonitor monitor) throws CoreException {

		if (prFile.exists()) 
		{
			for (IRodinElement child : prFile.getChildren()){
				// do not delete proofs !
				if (!(child.getElementType().equals(IPRProofTree.ELEMENT_TYPE)))
				((InternalElement)child).delete(true,null);
			}			
		}
		else
		{
			IRodinProject project = prFile.getRodinProject();
			project.createRodinFile(prFile.getElementName(), true, null);
		}
	}
	
//	private void copySequents(
//			Map<String, Boolean> newValidity, 
//			Map<String, IPRProofTree> oldProofs,
//			IPOFile poFile,
//			IPRFile prFile,
//			IProgressMonitor monitor) throws RodinDBException{
//		IPOSequent[] poSequents = poFile.getSequents();
//		
//		for (IPOSequent poSeq : poSequents)
//		{
//			IPRSequent prSeq = 
//				(IPRSequent) prFile.createInternalElement(
//						IPRSequent.ELEMENT_TYPE, poSeq.getName(), null, monitor);
//		
//			assert (newValidity.get(poSeq.getName()) != null);
//			((PRSequent)prSeq).setProofBroken(! newValidity.get(poSeq.getName()));
//			
//			IPRProofTree oldProof = oldProofs.get(prSeq.getName());
//			if (oldProof == null)
//			{
//				// create a fresh proof
//				IPRProofTree proof =
//					(IPRProofTree) prFile.createInternalElement(
//							IPRProofTree.ELEMENT_TYPE,prSeq.getName(), null, monitor);
//				proof.initialize();
//			}
//		}
//	}
	
}
