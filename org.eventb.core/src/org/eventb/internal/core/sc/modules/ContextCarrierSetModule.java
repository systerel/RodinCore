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
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextFile;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.ISCFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.ConcreteCarrierSetSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextCarrierSetModule extends IdentifierModule {

	public static final String CONTEXT_CARRIERSET_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".contextCarrierSetAcceptor";

	private ISCFilterModule[] modules;

	public ContextCarrierSetModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getFilterModules(CONTEXT_CARRIERSET_ACCEPTOR);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IContextFile contextFile = (IContextFile) element;
		
		ICarrierSet[] carrierSets = contextFile.getCarrierSets();
		
		if(carrierSets.length == 0)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_ContextCarrierSets));
		
		fetchSymbols(
				carrierSets,
				target,
				modules,
				repository, 
				monitor);
	}
	
	@Override
	protected void typeIdentifierSymbol(
			IIdentifierSymbolInfo newSymbolInfo, 
			ITypeEnvironment environment) throws CoreException {
		environment.addGivenSet(newSymbolInfo.getSymbol());				
			
		newSymbolInfo.setType(environment.getType(newSymbolInfo.getSymbol()));
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name, IIdentifierElement element) {
		return new ConcreteCarrierSetSymbolInfo(
				name, null, element, 
				EventBAttributes.IDENTIFIER_ATTRIBUTE, element.getParent().getElementName());
	}

}
