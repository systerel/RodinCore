/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core.tests.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class POTool extends SCTool {
	
	public static boolean SHOW_CLEAN = false;
	public static boolean SHOW_RUN = false;
	public static boolean SHOW_EXTRACT = false;
	
	public static boolean RUN_PO = false;

	protected void clean(IFile file, IProgressMonitor monitor, String name) throws CoreException {
		ToolTrace.addTrace(name, "clean", file);
	
		if (file.getFileExtension().equals("po"))
			RodinCore.valueOf(file).delete(true, monitor);
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
		
		final IRodinFile prvFile = RodinCore.valueOf(file);
		final ISCProvableRoot prv = (ISCProvableRoot) prvFile.getRoot();
		final IPORoot po = prv.getPORoot();

		graph.addTarget(po.getResource());
		graph.addToolDependency(prv.getResource(), po.getResource(), true);
				
	}

	protected void run(IFile source, IFile file, IProgressMonitor monitor, String name) throws RodinDBException {
		if (SHOW_RUN)
			ToolTrace.addTrace(name, "run", file);
	
		IRodinFile targetFile = RodinCore.valueOf(file);
		IPORoot target = (IPORoot) targetFile.getRoot();
		IRodinFile srcFile = RodinCore.valueOf(source);
		IInternalElement src = srcFile.getRoot();
		
		// First clean up target
		targetFile.create(true, null);
		
		// Populate with a copy of inputs
		copyDataElements(src, target);
		
		targetFile.save(null, true);
	}

}
