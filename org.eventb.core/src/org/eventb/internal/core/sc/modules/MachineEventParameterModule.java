/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IParameter;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventParameterModule extends IdentifierModule {

	public static final IModuleType<MachineEventParameterModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventParameterModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected IConcreteEventInfo concreteEventInfo;
	protected boolean isInitialisation;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;

		IParameter[] parameters = event.getParameters();

		if (parameters.length != 0)
			fetchSymbols(parameters, target, repository, monitor);

		if (!concreteEventInfo.getSymbolInfo().hasError()) {
			// The error might be caused by incompatible parameter types
			patchTypeEnvironment();
		}
	}

	/**
	 * Adds abstract parameters to type environment when they are not also present
	 * in the concrete event.
	 */
	private void patchTypeEnvironment() throws CoreException {
		if (concreteEventInfo.eventIsNew())
			return;
		final List<IAbstractEventInfo> infos = concreteEventInfo
				.getAbstractEventInfos();
		for (final IAbstractEventInfo info : infos) {
			for (final FreeIdentifier freeIdentifier : info.getParameters()) {
				final String name = freeIdentifier.getName();
				if (identifierSymbolTable.getSymbolInfoFromTop(name) == null) {
					typeEnvironment.add(freeIdentifier);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.IdentifierModule#insertIdentifierSymbol
	 * (org.eventb.core.sc.IIdentifierSymbolTable,
	 * org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo)
	 */
	@Override
	protected boolean insertIdentifierSymbol(IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {
		if (super.insertIdentifierSymbol(element, newSymbolInfo)) {
			if (isInitialisation) {
				createProblemMarker(element,
						GraphProblem.InitialisationVariableError);
				newSymbolInfo.setError();
				return false;
			} else
				return true;
		} else
			return false;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
		isInitialisation = ((IEvent) element).getLabel().contains(
				IEvent.INITIALISATION);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		concreteEventInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		return SymbolFactory.getInstance().makeLocalParameter(name, true,
				element, concreteEventInfo.getEventLabel());
	}

}
