/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.ModuleManager;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextConstantModule extends IdentifierModule {

	public static final String CONTEXT_CONSTANT_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".contextConstantAcceptor";

	private IAcceptorModule[] rules;

	public ContextConstantModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		rules = manager.getAcceptorModules(CONTEXT_CONSTANT_ACCEPTOR);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {

		IContextFile contextFile = (IContextFile) element;
		
		IConstant[] constants = contextFile.getConstants();
		
		if(constants.length == 0)
			return;
		
		fetchSymbols(
				constants,
				target,
				rules,
				repository,
				monitor);
		
	}

}
