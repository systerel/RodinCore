/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MSCTool extends SCTool implements IExtractor, IAutomaticTool {
	
	private static final String MSC = "MSC";
	// Id of this tool
	private static String SC_ID = "org.rodinp.core.tests.testMSC";
	
	public void clean(IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_CLEAN)
			ToolTrace.addTrace(MSC, "clean", file);

		if (file.getFileExtension().equals("msc"))
			file.delete(true, monitor);
	}
	
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		if (!RUN_SC)
			return;
		if (SCTool.SHOW_EXTRACT)
			ToolTrace.addTrace(MSC, "extract", file);
		
		graph.openGraph();
		
		IMachine mch = (IMachine) RodinCore.valueOf(file);
		
		ISCMachine smch = mch.getCheckedVersion();
		IFile scFile = smch.getResource();
		graph.addNode(scFile, SC_ID);
		graph.addToolDependency(mch.getResource(), scFile, SC_ID, true);
		
		ISCMachine machine = mch.getReferencedMachine();
		if (machine != null) {
			graph.addUserDependency(
					mch.getResource(), machine.getResource(), scFile, SC_ID, false);
		}
		
		HashSet<IFile> newSources = new HashSet<IFile>(mch.getUsedContexts().length * 4 / 3 + 1);
		for (IContext usedContext: mch.getUsedContexts()) {
			IFile source = usedContext.getCheckedVersion().getResource();
			newSources.add(source);
		}
		for (IFile newSrc : newSources)
			graph.addUserDependency(mch.getResource(), newSrc, scFile, SC_ID, false);
		
		graph.closeGraph();
	}
	
	public boolean run(IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_RUN)
			ToolTrace.addTrace(MSC, "run", file);

		ISCMachine target = (ISCMachine) RodinCore.valueOf(file);
		IMachine mch = target.getUncheckedVersion(); 
		
		// First clean up target
		target.create(true, null);
		
		// Populate with a copy of inputs
		copyDataElements(mch, target);
		
		if (mch.getReferencedMachine() != null)
			copyDataElements(mch.getReferencedMachine(), target);
		
		for (IContext usedContext: mch.getUsedContexts()) {
			copyDataElements(usedContext.getCheckedVersion(), target);
		}
		
		target.save(null, true);
		return true;
	}
	
	public void remove(IFile file, IFile origin, IProgressMonitor monitor) throws CoreException {
		remove(file, origin, monitor, MSC);
	}

}
