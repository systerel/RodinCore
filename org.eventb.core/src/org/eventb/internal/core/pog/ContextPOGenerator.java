/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextPOGenerator extends ProofObligationGenerator {

	public static final String CONTEXT_POG_TOOL_ID = EventBPlugin.PLUGIN_ID + ".contextPOG"; //$NON-NLS-1$
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#run(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		// TODO implement context static checker
		
		IPOFile target = (IPOFile) RodinCore.create(file);
		ISCContextFile source = target.getSCContext();

		try {
		
			monitor.beginTask(
					Messages.bind(
							Messages.build_runningCPO, 
							EventBPlugin.getComponentName(file.getName())),
					1);
			
			IRodinProject project = target.getRodinProject();
			project.createRodinFile(target.getElementName(), true, null);
		
			target.save(monitor, true);
			return true;
		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IExtractor#extract(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IGraph)
	 */
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor) throws CoreException {
		ISCContextFile source = (ISCContextFile) RodinCore.create(file);
		IPOFile target = source.getContextFile().getPOFile();
				
		graph.openGraph();
		graph.addNode(target.getResource(), CONTEXT_POG_TOOL_ID);
		graph.putToolDependency(
				source.getResource(), 
				target.getResource(), CONTEXT_POG_TOOL_ID, true);
		graph.closeGraph();

	}

}
