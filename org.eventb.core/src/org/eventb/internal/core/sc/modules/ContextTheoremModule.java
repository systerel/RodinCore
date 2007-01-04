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
import org.eventb.core.IContextFile;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ITheorem;
import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IContextLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IState;
import org.eventb.core.sc.state.IStateRepository;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.tool.state.IToolStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.TheoremSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextTheoremModule extends TheoremModule {

	public static final String CONTEXT_THEOREM_FILTER = 
		EventBPlugin.PLUGIN_ID + ".contextTheoremFilter";

	private IFilterModule[] filterModules;

	public ContextTheoremModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		filterModules = manager.getFilterModules(CONTEXT_THEOREM_FILTER);
	}

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		monitor.subTask(Messages.bind(Messages.progress_ContextTheorems));
		
		if (formulaElements.length == 0)
			return;
		
		checkAndSaveTheorems(
				target, 
				0,
				filterModules,
				repository,
				monitor);
		
	}

	@Override
	protected void makeProgress(IProgressMonitor monitor) {
		monitor.worked(1);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IToolStateRepository<IState> repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IContextLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ISCTheorem getSCTheorem(IInternalParent target, String elementName) {
		return ((ISCContextFile) target).getSCTheorem(elementName);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new TheoremSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

	@Override
	protected ITheorem[] getFormulaElements(IRodinElement element) throws CoreException {
		IContextFile contextFile = (IContextFile) element;
		return contextFile.getTheorems();
	}

}
