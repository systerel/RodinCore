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
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IContextLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.AxiomSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextAxiomModule extends PredicateWithTypingModule {

	public static final String CONTEXT_AXIOM_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".contextAxiomAcceptor";

	private final IAcceptorModule[] modules;

	public ContextAxiomModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = 
			manager.getAcceptorModules(CONTEXT_AXIOM_ACCEPTOR);
	}

	private static String AXIOM_NAME_PREFIX = "AXM";

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		IContextFile contextFile = (IContextFile) element;
		
		IAxiom[] axioms = contextFile.getAxioms(null);
		
		Predicate[] predicates = new Predicate[axioms.length];
		
		if (axioms.length == 0)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_ContextAxioms));
		
		checkAndType(
				axioms, 
				target,
				predicates,
				modules,
				contextFile.getElementName(),
				repository,
				monitor);
		
		saveAxioms((ISCContextFile) target, axioms, predicates, null);
		
	}
	
	private void saveAxioms(
			ISCContextFile target, 
			IAxiom[] axioms, 
			Predicate[] predicates,
			IProgressMonitor monitor) throws RodinDBException {
		
		int index = 0;
		
		for (int i=0; i<axioms.length; i++) {
			if (predicates[i] == null)
				continue;
			ISCAxiom scAxiom = target.getSCAxiom(AXIOM_NAME_PREFIX + index++);
			scAxiom.create(null, monitor);
			scAxiom.setLabel(axioms[i].getLabel(monitor), monitor);
			scAxiom.setPredicate(predicates[i], null);
			scAxiom.setSource(axioms[i], monitor);
		}
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
			IStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IContextLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new AxiomSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

}
