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

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.builder.IGraph;

public class CSCTool extends SCTool {
	
	public static boolean FAULTY_BEFORE_TARGET_CREATION = false;
	
	public static boolean FAULTY_AFTER_TARGET_CREATION = false;
	
	private static final String CSC = "CSC";
	
	public void clean(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_CLEAN)
			ToolTrace.addTrace(CSC, "clean", file);

		if (file.getFileExtension().equals("csc"))
			RodinCore.valueOf(file).delete(true, monitor);
	}
	
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		if (!RUN_SC)
			return;
		if (SCTool.SHOW_EXTRACT)
			ToolTrace.addTrace(CSC, "extract", file);
		
		IRodinFile ctxFile = RodinCore.valueOf(file);
		IContextRoot ctx = (IContextRoot) ctxFile.getRoot();
		
		ISCContextRoot sctx = ctx.getCheckedVersion();
		IFile scFile = sctx.getResource();
		graph.addTarget(scFile);
		graph.addToolDependency(ctxFile.getResource(), scFile, true);
		
		HashSet<IFile> newSources = new HashSet<IFile>(ctx.getUsedContexts().length * 4 / 3 + 1);
		for (IContextRoot usedContext: ctx.getUsedContexts()) {
			IFile source = usedContext.getCheckedVersion().getResource();
			newSources.add(source);
		}
		for (IFile newFile : newSources)
			graph.addUserDependency(ctxFile.getResource(), newFile, scFile, false);
		
	}
	
	public boolean run(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_RUN)
			ToolTrace.addTrace(CSC, "run", file);

		IRodinFile targetFile = RodinCore.valueOf(file);
		ISCContextRoot target = (ISCContextRoot) targetFile.getRoot();
		IContextRoot ctx = target.getUncheckedVersion();

		if (FAULTY_BEFORE_TARGET_CREATION)
			throw new IllegalStateException("internal error before target creation");
		
		// First clean up target
		targetFile.create(true, null);
		
		if (FAULTY_AFTER_TARGET_CREATION)
			throw new IllegalStateException("internal error after target creation");
		
		// Populate with a copy of inputs
		copyDataElements(ctx, target);
		for (IContextRoot usedContext: ctx.getUsedContexts()) {
			if (usedContext.getCheckedVersion().exists())
				copyDataElements(usedContext.getCheckedVersion(), target);
			else
				ctx.getResource().createMarker(
						RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER).setAttribute(
						IMarker.MESSAGE, "ERROR");
		}
		
		targetFile.save(null, true);
		return true;
	}

}
