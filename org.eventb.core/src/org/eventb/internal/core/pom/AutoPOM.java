/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.pom;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRSequent;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.Lib;
import org.eventb.internal.core.protosc.ContextSC;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author fmehta
 *
 */
public class AutoPOM implements IAutomaticTool, IExtractor {

//	private IInterrupt interrupt;
	private IProgressMonitor monitor;

	private IPOFile poFile;
	private IPRFile prFile;
	
	
	public void init(
			@SuppressWarnings("hiding") IPOFile poFile, 
			@SuppressWarnings("hiding") IPRFile prFile, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) {
		this.monitor = monitor;
		this.poFile = poFile;
		this.prFile = prFile;
	}
	
	
	public boolean run(IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {

		IPRFile newPRFile = (IPRFile) RodinCore.create(file);
		IPOFile poIn = newPRFile.getPOFile();
		
		if (! poIn.exists()) {
			ContextSC.makeError("Source PO file does not exist.");
		}
		
		init(poIn, newPRFile, monitor);

		// Create the resulting PR file atomically.
		RodinCore.run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor saveMonitor) throws CoreException {
						Map<String, IPRProofTree> oldProofs = getOldProofs();
						Map<String, Boolean> newValidity = computeNewValidity(oldProofs);
						createFreshPRFile(newValidity,oldProofs);
					}
				}, monitor);
		
		new AutoProver().run(newPRFile);
		return true;
	}

	Map<String, IPRProofTree> getOldProofs() throws RodinDBException{
		if (prFile.exists())
			return prFile.getProofTrees();
		return new HashMap<String, IPRProofTree>();
	}
	
	Map<String, Boolean> computeNewValidity(Map<String, IPRProofTree> oldProofs) throws RodinDBException
	{
		Map<String, IProverSequent> newPOs = POUtil.readPOs(poFile);		
		Map<String, Boolean> newValidity = new HashMap<String, Boolean>();
		
		for (Map.Entry<String, IProverSequent> newPO : newPOs.entrySet()){
			String newPOname = newPO.getKey();
			IProverSequent newPOseq = newPO.getValue();
			IPRProofTree oldProof = oldProofs.get(newPOname);
			IProofDependencies oldProofDependencies = null;
			if 	(oldProof != null) oldProofDependencies = oldProof.getProofDependencies();
			if  (oldProof != null &&
					oldProofDependencies != null &&
					Lib.proofReusable(oldProofDependencies,newPOseq))
				{
					newValidity.put(newPOname,true);
				}
			else
				{
					if (oldProof == null) 
						newValidity.put(newPOname,true);
					else
						newValidity.put(newPOname,false);
				}
		}
		return newValidity;
	}
	
	
	public void clean(IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);
	}

	public void extract(IFile file, IGraph graph) throws CoreException {
		
		IPOFile in = (IPOFile) RodinCore.create(file);
		IPRFile target = in.getPRFile();
		
		IPath inPath = in.getPath();
		IPath targetPath = target.getPath();
		
		graph.addNode(targetPath, POMCore.AUTO_POM_TOOL_ID);
		graph.putToolDependency(inPath, targetPath, POMCore.AUTO_POM_TOOL_ID, true);
		graph.updateGraph();
	}

	void createFreshPRFile(Map<String, Boolean> newValidity, Map<String, IPRProofTree> oldProofs) throws CoreException {

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
		
		copyGlobalInfo();
		copySequents(newValidity,oldProofs);
		
		prFile.save(monitor, true);
	}
	
	private void copySequents(Map<String, Boolean> newValidity, Map<String, IPRProofTree> oldProofs) throws RodinDBException{
		IPOSequent[] poSequents = poFile.getSequents();
		
		for (IPOSequent poSeq : poSequents)
		{
			IPRSequent prSeq = 
				(IPRSequent) prFile.createInternalElement(
						IPRSequent.ELEMENT_TYPE, poSeq.getName(), null, monitor);
			IRodinElement[] children = poSeq.getChildren();
			
			for (IRodinElement child : children){
				((IInternalElement)child).copy(prSeq,null,null,false,monitor);
			}
			
			assert (newValidity.get(poSeq.getName()) != null);
			prSeq.setProofBroken(! newValidity.get(poSeq.getName()));
			
			IPRProofTree oldProof = oldProofs.get(prSeq.getName());
			if (oldProof == null)
			{
				// create a fresh proof
				IPRProofTree proof =
					(IPRProofTree) prFile.createInternalElement(
							IPRProofTree.ELEMENT_TYPE,prSeq.getName(), null, monitor);
				proof.initialize();
			}
		}
	}
	
	private void copyGlobalInfo() throws RodinDBException{
		
		IRodinElement[] children = ((RodinElement)poFile).getChildren();
		for (IRodinElement child : children){
			if (!(child.getElementType().equals(IPOSequent.ELEMENT_TYPE))){
				((IInternalElement)child).copy(prFile,null,null,false,monitor);
			}
		}
	}
}
