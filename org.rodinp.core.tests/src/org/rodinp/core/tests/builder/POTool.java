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
	
	public static boolean SHOW_CLEAN = false;
	public static boolean SHOW_RUN = false;
	public static boolean SHOW_EXTRACT = false;
	public static boolean SHOW_REMOVE = false;
	
	public static boolean RUN_PO = false;

	protected void clean(IFile file, IProgressMonitor monitor, String name) throws CoreException {
		ToolTrace.addTrace(name, "clean", file);
	
		if (file.getFileExtension().equals("po"))
			file.delete(true, monitor);
	}

	protected void extract(
			IFile file, 
			IGraph graph, 
			String name, 
			String ID, 
			IProgressMonitor monitor) throws CoreException {
		if (!RUN_PO)
			return;
		if (SHOW_EXTRACT)
			ToolTrace.addTrace(name, "extract", file);
		
		graph.openGraph();
		
		ISCProvable prv = (ISCProvable) RodinCore.valueOf(file);
		
		IPOFile po = prv.getPOFile();
		graph.addNode(po.getResource(), ID);
		graph.putToolDependency(prv.getResource(), po.getResource(), ID, true);
				
		graph.closeGraph();
	}

	protected void run(IFile file, IProgressMonitor monitor, String name) throws RodinDBException {
		if (SHOW_RUN)
			ToolTrace.addTrace(name, "run", file);
	
		IPOFile target = (IPOFile) RodinCore.valueOf(file);
		ISCContext ctx = target.getCheckedContext();
		
		// First clean up target
		target.create(true, null);
		
		// Populate with a copy of inputs
		copyDataElements(ctx, target);
		
		target.save(null, true);
	}

	public void remove(IFile file, IFile origin, IProgressMonitor monitor, String name) throws CoreException {
		if (SHOW_REMOVE)
			ToolTrace.addTrace(name, "remove", file);
	
		if (AbstractBuilderTest.getComponentName(file.getName()).equals(AbstractBuilderTest.getComponentName(origin.getName())))
			RodinCore.valueOf(file).delete(true, monitor);
	}

}
