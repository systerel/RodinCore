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
import org.eventb.core.IEvent;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ICurrentEvent;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.sc.util.GraphProblem;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.EventVariableSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventWitnessFreeIdentsModule extends MachineFormulaFreeIdentsModule {

	public static final IModuleType<MachineEventWitnessFreeIdentsModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventWitnessFreeIdentsModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	FormulaFactory factory;
	
	private boolean isInitialisation;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		ICurrentEvent currentEvent = (ICurrentEvent) repository.getState(ICurrentEvent.STATE_TYPE);
		isInitialisation = 
			currentEvent.getCurrentEvent().getLabel().equals(IEvent.INITIALISATION);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#getSymbolInfo(org.eventb.core.ast.FreeIdentifier)
	 */
	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(
			IInternalElement element, 
			FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		boolean primed = freeIdentifier.isPrimed();
		FreeIdentifier identifier = primed ? 
				freeIdentifier.withoutPrime(factory) : 
				freeIdentifier;
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element, identifier, monitor);
		if (symbolInfo != null && symbolInfo instanceof IVariableSymbolInfo) {
			IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
			if (!variableSymbolInfo.isLocal() && !variableSymbolInfo.isConcrete()) {
				String label = ((IWitness) element).getLabel();
				if (primed && !label.equals(freeIdentifier.getName())) {
					// error: only the primed abstract disappearing variable
					// of the label may appear in the witness predicate
					return null;
				}
			}
			if (isInitialisation && !primed) {
				// error: unprimed variables cannot occur in initialisation witness predicates
				return null;
			}
		}
		if (symbolInfo == null) { // abstract local variables are not contained in the symbol table; fake them!
			String label = ((IWitness) element).getLabel();
			if (label.equals(freeIdentifier.getName()))
				return new EventVariableSymbolInfo(label, null, null, null);
		}
		return symbolInfo;
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(repository, monitor);
		factory = null;
	}

	@Override
	protected IRodinProblem declaredFreeIdentifierError() {
		return GraphProblem.WitnessFreeIdentifierError;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

}
