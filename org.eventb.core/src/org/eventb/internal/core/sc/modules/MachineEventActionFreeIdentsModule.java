/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventActionFreeIdentsModule extends
		MachineFormulaFreeIdentsModule {

	public static final IModuleType<MachineEventActionFreeIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventActionFreeIdentsModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private boolean isInitialisation;

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		IConcreteEventInfo concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
		isInitialisation = concreteEventInfo.isInitialisation();
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(repository, monitor);
	}

	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element,
				freeIdentifier, monitor);
		if (symbolInfo != null
				&& symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE) {
			if (isInitialisation) {
				createProblemMarker(element, getAttributeType(), freeIdentifier
						.getSourceLocation().getStart(), freeIdentifier
						.getSourceLocation().getEnd(),
						GraphProblem.InitialisationActionRHSError,
						freeIdentifier.getName());
				return null;
			} else if (!symbolInfo
					.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE)) {
				createProblemMarker(element, getAttributeType(),
						GraphProblem.VariableHasDisappearedError,
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
	 * org.eventb.internal.core.sc.modules.FormulaFreeIdentsModule#accept(org
	 * .rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		boolean ok = super.accept(element, repository, monitor);

		ok &= checkAssignedIdentifiers((IInternalElement) element,
				(Assignment) parsedFormula.getFormula(), monitor);

		return ok;
	}

	private boolean checkAssignedIdentifiers(IInternalElement element,
			Assignment assignment, IProgressMonitor monitor)
			throws CoreException {

		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();

		for (FreeIdentifier identifier : identifiers) {
			String name = identifier.getName();
			IIdentifierSymbolInfo symbolInfo = symbolTable.getSymbolInfo(name);
			if (symbolInfo != null) {
				if (symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE) {
					if (!symbolInfo
							.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE)) {
						createProblemMarker(element, getAttributeType(),
								GraphProblem.VariableHasDisappearedError, name);
						return false;
					}
				} else if (symbolInfo.getSymbolType() == ISCParameter.ELEMENT_TYPE) {
					createProblemMarker(element, getAttributeType(),
							GraphProblem.AssignmentToParameterError, name);
					return false;
				} else if (symbolInfo.getSymbolType() == ISCCarrierSet.ELEMENT_TYPE) {
					createProblemMarker(element, getAttributeType(),
							GraphProblem.AssignmentToCarrierSetError, name);
					return false;
				} else if (symbolInfo.getSymbolType() == ISCConstant.ELEMENT_TYPE) {
					createProblemMarker(element, getAttributeType(),
							GraphProblem.AssignmentToConstantError, name);
					return false;
				}
			} else {
				createProblemMarker(element, getAttributeType(),
						GraphProblem.AssignedIdentifierNotVariableError, name);
				return false;
			}
		}
		return true;
	}

	@Override
	protected FreeIdentifier[] getFreeIdentifiers() {
		FreeIdentifier[] freeIdentifiers = ((Assignment) parsedFormula
				.getFormula()).getUsedIdentifiers();
		return freeIdentifiers;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

}
