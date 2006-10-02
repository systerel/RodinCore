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
import org.eventb.core.IAxiom;
import org.eventb.core.IContextFile;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IContextLabelSymbolTable;
import org.eventb.core.sc.ILabelSymbolTable;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
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
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		IContextFile contextFile = (IContextFile) element;
		
		IAxiom[] axioms = contextFile.getAxioms();
		
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
		
		saveAxioms(target, axioms, predicates, null);
		
	}
	
	private void saveAxioms(
			IInternalParent parent, 
			IAxiom[] axioms, 
			Predicate[] predicates,
			IProgressMonitor monitor) throws RodinDBException {
		
		int index = 0;
		
		for (int i=0; i<axioms.length; i++) {
			if (predicates[i] == null)
				continue;
			ISCAxiom scAxiom = 
				(ISCAxiom) parent.createInternalElement(
						ISCAxiom.ELEMENT_TYPE, 
						AXIOM_NAME_PREFIX + index++, 
						null, 
						monitor);
			scAxiom.setLabel(axioms[i].getLabel(monitor), monitor);
			scAxiom.setPredicate(predicates[i]);
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

}
