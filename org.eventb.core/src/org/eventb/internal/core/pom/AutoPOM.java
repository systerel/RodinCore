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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.IPRStatus;
import org.eventb.core.IPRStatus.Overview;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.SequentProver;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.internal.core.protosc.ContextSC;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
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
		Map<String, Overview> prStatus = PRUtil.readStatus(prFile);
		AutoProver autoProver = new AutoProver();
		for (String name : prSeqs.keySet()){
			if (prStatus.get(name) == Overview.PENDING) {
//				System.out.println("Running autoProver on "
//						+ prFile.getElementName()
//						+ ", PO: "
//						+ name
//				);
				IProofTree pt = SequentProver.makeProofTree(prSeqs.get(name));
				autoProver.run(pt);
				if (pt.isDischarged()) PRUtil.updateStatus(prFile,name,Overview.DISCHARGED);
			}
		}
	}
	
	public boolean run(IFile file, 
			@SuppressWarnings("hiding") IInterrupt interrupt, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		
		IPRFile newPRFile = (IPRFile) RodinCore.create(file);
		IPOFile poIn = newPRFile.getPOFile();
		
		if (! poIn.exists()) {
			ContextSC.makeError("Source PO file does not exist.");
		}
		
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
		
		IPOFile in = (IPOFile) RodinCore.create(file);
		IPRFile target = in.getPRFile();
		
		IPath inPath = in.getPath();
		IPath targetPath = target.getPath();
		
		graph.addNode(targetPath, POMCore.AUTO_POM_TOOL_ID);
		IPath[] paths = graph.getDependencies(targetPath, POMCore.AUTO_POM_TOOL_ID);
		if(paths.length == 1 && paths[0].equals(targetPath))
			return;
		else {
			graph.removeDependencies(targetPath, POMCore.AUTO_POM_TOOL_ID);
			graph.addToolDependency(inPath, targetPath, POMCore.AUTO_POM_TOOL_ID, true);
		}
	}

	private void createFreshPRFile() throws CoreException {
		IRodinProject project = prFile.getRodinProject();
		project.createRodinFile(prFile.getElementName(), true, null);
		copyGlobalInfo();
		copySequents();
		prFile.save(monitor, true);
	}
	
	private void copySequents() throws RodinDBException{
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
			IPRStatus status =
				(IPRStatus) prSeq.createInternalElement(
						IPRStatus.ELEMENT_TYPE, "", null, monitor);
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
