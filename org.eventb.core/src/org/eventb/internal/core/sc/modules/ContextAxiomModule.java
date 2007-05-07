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
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAccuracyInfo;
import org.eventb.core.sc.state.IContextAccuracyInfo;
import org.eventb.core.sc.state.IContextLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.AxiomSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextAxiomModule extends PredicateWithTypingModule<IAxiom> {

	public static final IModuleType<ContextAxiomModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".contextAxiomModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private static String AXIOM_NAME_PREFIX = "AXM";

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {
		
		monitor.subTask(Messages.bind(Messages.progress_ContextAxioms));
		
		if (formulaElements.length == 0)
			return;
		
		checkAndType(
				element.getElementName(),
				repository,
				monitor);
		
		saveAxioms((ISCContextFile) target, null);
		
	}
	
	private void saveAxioms(
			ISCContextFile target, 
			IProgressMonitor monitor) throws RodinDBException {
		
		int index = 0;
		
		for (int i=0; i<formulaElements.length; i++) {
			if (formulas[i] == null)
				continue;
			ISCAxiom scAxiom = target.getSCAxiom(AXIOM_NAME_PREFIX + index++);
			scAxiom.create(null, monitor);
			scAxiom.setLabel(formulaElements[i].getLabel(), monitor);
			scAxiom.setPredicate(formulas[i], null);
			scAxiom.setSource(formulaElements[i], monitor);
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
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IContextLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new AxiomSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

	@Override
	protected IAxiom[] getFormulaElements(IRodinElement element) throws CoreException {
		IContextFile contextFile = (IContextFile) element;
		return contextFile.getAxioms();
	}

	@Override
	protected IAccuracyInfo getAccuracyInfo(ISCStateRepository repository) throws CoreException {
		return (IContextAccuracyInfo) repository.getState(IContextAccuracyInfo.STATE_TYPE);
	}

}
