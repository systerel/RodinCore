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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class MSCTool extends SCTool {
	
	private static final String MSC = "MSC";
	
	public void clean(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_CLEAN)
			ToolTrace.addTrace(MSC, "clean", file);

		if (file.getFileExtension().equals("msc"))
			RodinCore.valueOf(file).delete(true, monitor);
	}
	
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		if (!RUN_SC)
			return;
		if (SCTool.SHOW_EXTRACT)
			ToolTrace.addTrace(MSC, "extract", file);
		
		IRodinFile mchFile = RodinCore.valueOf(file);
		IMachineRoot mch = (IMachineRoot) mchFile.getRoot();
		
		ISCMachineRoot smch = mch.getCheckedVersion();
		IFile scFile = smch.getResource();
		graph.addTarget(scFile);
		graph.addToolDependency(mchFile.getResource(), scFile, true);
		
		ISCMachineRoot machine = mch.getReferencedMachine();
		if (machine != null) {
			graph.addUserDependency(
					mchFile.getResource(), machine.getResource(), scFile, false);
		}
		
		HashSet<IFile> newSources = new HashSet<IFile>(mch.getUsedContexts().length * 4 / 3 + 1);
		for (IContextRoot usedContext: mch.getUsedContexts()) {
			IFile source = usedContext.getCheckedVersion().getResource();
			newSources.add(source);
		}
		for (IFile newSrc : newSources)
			graph.addUserDependency(mch.getResource(), newSrc, scFile, false);
		
	}
	
	public boolean run(IFile source, IFile file, IProgressMonitor monitor) throws CoreException {
		if (SCTool.SHOW_RUN)
			ToolTrace.addTrace(MSC, "run", file);

		IRodinFile targetFile = RodinCore.valueOf(file);
		ISCMachineRoot target = (ISCMachineRoot) targetFile.getRoot();
		IMachineRoot mch = target.getUncheckedVersion();
		
		// First clean up target
		targetFile.create(true, null);
		
		// Populate with a copy of inputs
		copyDataElements(mch, target);
		
		if (mch.getReferencedMachine() != null) {
			if (mch.getReferencedMachine().exists())
				copyDataElements(mch.getReferencedMachine(), target);
			else
				mch.getResource().createMarker(RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER);
		}
		
		for (IContextRoot usedContext: mch.getUsedContexts()) {
			if (usedContext.getCheckedVersion().exists())
				copyDataElements(usedContext.getCheckedVersion(), target);
			else
				mch.getResource().createMarker(RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER);
		}
		
		targetFile.save(null, true);
		return true;
	}

}
