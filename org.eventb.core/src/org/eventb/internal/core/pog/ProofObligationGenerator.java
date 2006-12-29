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
import org.eventb.core.IPOFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pog.IProcessorModule;
import org.eventb.core.pog.state.IStateRepository;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ProofObligationGenerator  implements IAutomaticTool, IExtractor {

	public static String PRD_NAME_PREFIX = "PRD";
	
	public static boolean DEBUG = false;
	
	protected IStateRepository createRepository(
			IPOFile target, 
			IProgressMonitor monitor) throws CoreException {
		
		final FormulaFactory factory = FormulaFactory.getDefault();
		
		final IStateRepository repository = new POGStateRepository(factory, target);
		
		return repository;
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IAutomaticTool#clean(org.eclipse.core.resources.IFile, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(IFile source, IFile file, IProgressMonitor monitor)
			throws CoreException {
		
		try {
		
			monitor.beginTask(Messages.bind(Messages.build_cleaning, file.getName()), 1);
			
			if (file.exists())
				file.delete(true, monitor);
			
			monitor.worked(1);
			
		} finally {
			monitor.done();
		}

	}

	protected void runModules(
			IRodinFile file, 
			IPOFile target, 
			IProcessorModule[] modules, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		for(IProcessorModule module : modules) {
			
			module.initModule(
					file, 
					repository,
					monitor);
	
		}		
	
		for(IProcessorModule module : modules) {
			
			module.process(
					file, 
					repository,
					monitor);
	
		}		
		
		for(IProcessorModule module : modules) {
			
			module.endModule(
					file, 
					repository,
					monitor);
	
		}		
	}

}
