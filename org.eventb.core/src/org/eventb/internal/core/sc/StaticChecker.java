/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.sc.ISCProcessorModule;
import org.eventb.core.sc.state.IContextTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class StaticChecker  implements IAutomaticTool, IExtractor {

	public static boolean DEBUG = false;
	
	public static boolean DEBUG_STATE = false;
	
	public static boolean DEBUG_MARKERS = false;
	
	private final static int IDENT_SYMTAB_SIZE = 2047;

	private final static int CONTEXT_TABLE_SIZE = 137;

	protected ISCStateRepository createRepository(
			IRodinFile file, 
			IProgressMonitor monitor) throws CoreException {
		
		final FormulaFactory factory = FormulaFactory.getDefault();
		
		final SCStateRepository repository = new SCStateRepository(factory);
		
		if (DEBUG_STATE)
			repository.debug();
		
		final IdentifierSymbolTable identifierSymbolTable = 
			new IdentifierSymbolTable(IDENT_SYMTAB_SIZE, factory);
		
		final IContextTable contextTable = new ContextTable(CONTEXT_TABLE_SIZE);

		repository.setState(identifierSymbolTable);
		repository.setState(contextTable);

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

	public static String getStrippedComponentName(String component) {
		int dotPos = component.indexOf('.');
		if (dotPos == -1)
			return component;
		else
			return component.substring(0, dotPos - 1);
	}
	
	@Deprecated
	public static String getParentName(IRodinElement element) {
		return element.getParent().getElementName();
	}

	protected void runProcessorModules(
			IRodinFile file, 
			IInternalParent target, 
			ISCProcessorModule[] modules, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		file.getResource().deleteMarkers(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
				true, 
				IResource.DEPTH_INFINITE);
		
		for(ISCProcessorModule module : modules) {
			
			module.initModule(
					file, 
					repository, 
					monitor);
	
		}		
	
		for(ISCProcessorModule module : modules) {
			
			module.process(
					file, 
					target,
					repository, 
					monitor);
	
		}		
		
		for(ISCProcessorModule module : modules) {
			
			module.endModule(
					file, 
					repository, 
					monitor);
	
		}		
	
	}

}
