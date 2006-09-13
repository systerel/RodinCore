/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class POTool extends SCTool implements IExtractor, IAutomaticTool {

	protected void clean(IFile file, IProgressMonitor monitor, String name) throws CoreException {
		ToolTrace.addTrace(name, "clean", file);
	
		if (file.getFileExtension().equals("po"))
			file.delete(true, monitor);
	}

	protected void extract(IFile file, IGraph graph, String name, String ID, boolean runPO, IProgressMonitor monitor) {
		if (!runPO)
			return;
		ToolTrace.addTrace(name, "extract", file);
		
		ISCProvable prv = (ISCProvable) RodinCore.create(file);
		
		IPOFile po = prv.getPOFile();
		IPath scPath = po.getResource().getFullPath();
		graph.addNode(scPath, ID);
		graph.putToolDependency(prv.getResource().getFullPath(), scPath, ID, true);
				
		graph.updateGraph();
	}

	protected void run(IFile file, IProgressMonitor monitor, String name) throws RodinDBException {
		ToolTrace.addTrace(name, "run", file);
	
		ISCContext target = (ISCContext) RodinCore.create(file);
		IContext ctx = target.getUncheckedVersion(); 
		
		// First clean up target
		if (target.exists()) {
			target.delete(true, null);
		}
		target = (ISCContext) target.getRodinProject().createRodinFile(target.getElementName(), true, null);
		
		// Populate with a copy of inputs
		copyDataElements(ctx, target);
		
		target.save(null, true);
	}

}
