/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.pom;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPRStatus;
import org.eventb.core.IPRStatus.Status;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.internal.core.protosc.ContextSC;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;
import org.rodinp.core.builder.IInterrupt;

/**
 * @author halstefa
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
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) {
//		this.interrupt = interrupt;
		this.monitor = monitor;
		this.poFile = poFile;
		this.prFile = prFile;
	}
	
	// just for testing. 
	public void writePRFile() throws CoreException {
		createFreshPRFile();
	}
	
	public void runAutoProver() throws CoreException {
		Map<String, IProverSequent> prSeqs = PRUtil.readPOs(prFile);
		Map<String, Status> prStatus = PRUtil.readStatus(prFile);
		AutoProver autoProver = new AutoProver();
		for (String name : prSeqs.keySet()){
			if (prStatus.get(name) == Status.PENDING) {
				IProofTree pt = SequentProver.makeProofTree(prSeqs.get(name));
				autoProver.run(pt);
				if (pt.isDischarged()) PRUtil.updateStatus(prFile,name,Status.DISCHARGED);
			}
		}
	}
	
	public boolean run(IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
						
		IPRFile newPRFile = (IPRFile) RodinCore.create(file);
		newPRFile.getRodinProject().createRodinFile(newPRFile.getElementName(), true, null);
		
		// TODO: the explicit file extension should be replaced by a request to the content type manager
		@SuppressWarnings("hiding") 
		IFile poFile = workspace.getRoot().getFile(file.getFullPath().removeFileExtension().addFileExtension("bpo"));
		
		IPOFile poIn = (IPOFile) RodinCore.create(poFile);
		if(!poIn.exists())
			// TODO : maybe this is not correct.
			ContextSC.makeError("Source PO file does not exist.");
		
		init(poIn, newPRFile, interrupt, monitor);
		
		writePRFile();
		runAutoProver();
		return true;
		
	}

	public void clean(IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);
	}

	public void extract(IFile file, IGraph graph) throws CoreException {
		// TODO Auto-generated method stub
		IPath target = file.getFullPath().removeFileExtension().addFileExtension("bpr");
		graph.addNode(target, POMCore.AUTO_POM_TOOL_ID);
		IPath[] paths = graph.getDependencies(target, POMCore.AUTO_POM_TOOL_ID);
		if(paths.length == 1 && paths[0].equals(target))
			return;
		else {
			graph.removeDependencies(target, POMCore.AUTO_POM_TOOL_ID);
			graph.addToolDependency(file.getFullPath(), target, POMCore.AUTO_POM_TOOL_ID, true);
		}
	}

	private void createFreshPRFile() throws CoreException {
		// TODO : Erase previous contents of the prFile.
		copyGlobalInfo();
		copySequents();
		
		prFile.save(monitor, true);
	}
	
	private void copySequents() throws RodinDBException{
		IRodinElement[] poSequents = poFile.getChildrenOfType(IPOSequent.ELEMENT_TYPE);
		
		for (IRodinElement poSeq : poSequents)
		{
			IPRSequent prSeq = 
				(IPRSequent) prFile.createInternalElement(IPRSequent.ELEMENT_TYPE,((IPOSequent)poSeq).getName(),null,monitor);
			IRodinElement[] children = ((IPOSequent)poSeq).getChildren();
		
			for (IRodinElement child : children){
				((IInternalElement)child).copy(prSeq,null,null,false,monitor);
			}
			IPRStatus status = (IPRStatus) prSeq.createInternalElement(IPRStatus.ELEMENT_TYPE, "", null, monitor);
			status.setContents("PENDING");
		}
		
	}
	
	private void copyGlobalInfo() throws RodinDBException{
		
		IRodinElement[] children = ((RodinElement)poFile).getChildren();
		for (IRodinElement child : children){
			if (!(child.getElementType().equals(IPRSequent.ELEMENT_TYPE))){
				((IInternalElement)child).copy(prFile,null,null,false,monitor);
			}
		}
	}
}
