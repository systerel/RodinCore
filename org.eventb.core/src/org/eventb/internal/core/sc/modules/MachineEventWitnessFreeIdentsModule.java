/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - got factory from repository
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCVariable;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventWitnessFreeIdentsModule extends
		MachineFormulaFreeIdentsModule {

	public static final IModuleType<MachineEventWitnessFreeIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventWitnessFreeIdentsModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	FormulaFactory factory;

	private boolean isInitialisation;
	IConcreteEventInfo concreteEventInfo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#initModule
	 * (org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
		isInitialisation = concreteEventInfo.isInitialisation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#getSymbolInfo
	 * (org.eventb.core.ast.FreeIdentifier)
	 */
	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		boolean primed = freeIdentifier.isPrimed();
		FreeIdentifier identifier = primed ? freeIdentifier
				.withoutPrime(factory) : freeIdentifier;
		if (!primed) { // abstract local variables are not contained in the
			// symbol table; fake them!
			String label = ((IWitness) element).getLabel();
			if (label.equals(freeIdentifier.getName())) {
				return SymbolFactory.getInstance().makeLocalParameter(label,
						false, null, concreteEventInfo.getEventLabel());
			}
		}
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element,
				identifier, monitor);
		if (symbolInfo != null
				&& symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE) {
			if (!symbolInfo
					.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE)) {
				String label = ((IWitness) element).getLabel();
				if (primed && !label.equals(freeIdentifier.getName())) {
					// error: only the primed abstract disappearing variable
					// of the label may appear in the witness predicate
					createProblemMarker(element, getAttributeType(),
							freeIdentifier.getSourceLocation().getStart(),
							freeIdentifier.getSourceLocation().getEnd(),
							GraphProblem.WitnessFreeIdentifierError,
							freeIdentifier.getName());
					return null;
				}
			}
			if (isInitialisation && !primed) {
				// error: unprimed variables cannot occur in initialisation
				// witness predicates
				createProblemMarker(element, getAttributeType(), freeIdentifier
						.getSourceLocation().getStart(), freeIdentifier
						.getSourceLocation().getEnd(),
						GraphProblem.InitialisationActionRHSError,
						freeIdentifier.getName());
				return null;
			}
		}
		return symbolInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#endModule
	 * (org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(repository, monitor);
		factory = null;
		concreteEventInfo = null;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

}
