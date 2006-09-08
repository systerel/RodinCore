/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pog.IModule;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.StateRepository;
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
			IRodinFile file, 
			IProgressMonitor monitor) throws CoreException {
		
		final FormulaFactory factory = FormulaFactory.getDefault();
		
		final IStateRepository repository = new StateRepository(factory);
		
		return repository;
	}
	
	protected void runModules(
			IRodinFile file, 
			IPOFile target, 
			IModule[] modules, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		for(IModule module : modules) {
			
			module.initModule(
					file, 
					target,
					repository, 
					monitor);
	
		}		
	
		for(IModule module : modules) {
			
			module.process(
					file, 
					target,
					repository, 
					monitor);
	
		}		
		
		for(IModule module : modules) {
			
			module.endModule(
					file, 
					target,
					repository, 
					monitor);
	
		}		
	}

}
