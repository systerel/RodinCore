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
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.ConcreteConstantSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextConstantModule extends IdentifierModule {

	public static final String CONTEXT_CONSTANT_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".contextConstantAcceptor";

	private IFilterModule[] rules;

	public ContextConstantModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		rules = manager.getFilterModules(CONTEXT_CONSTANT_ACCEPTOR);
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
		
		monitor.subTask(Messages.bind(Messages.progress_ContextConstants));
		
		fetchSymbols(
				constants,
				target,
				rules,
				repository,
				monitor);
		
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name, IIdentifierElement element) {
		return new ConcreteConstantSymbolInfo(
				name, element, 
				EventBAttributes.IDENTIFIER_ATTRIBUTE, element.getParent().getElementName());
	}

}
