/*******************************************************************************
 * Copyright (c) 2008, 2010 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ISCParameter;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventCopyParametersModule extends SCProcessorModule {

	public static final IModuleType<MachineEventModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventCopyParametersModule"); //$NON-NLS-1$

	private IAbstractMachineInfo abstractMachineInfo;
	private IConcreteEventInfo concreteEventInfo;
	private IIdentifierSymbolTable identifierSymbolTable;
	private ITypeEnvironment typeEnvironment;

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		if (concreteEventInfo.isInitialisation())
			return;

		ILabelSymbolInfo symbolInfo = concreteEventInfo.getSymbolInfo();

		if (symbolInfo.hasAttribute(EventBAttributes.EXTENDED_ATTRIBUTE)
				&& symbolInfo
						.getAttributeValue(EventBAttributes.EXTENDED_ATTRIBUTE)
				&& concreteEventInfo.getAbstractEventInfos().size() > 0) {

			IAbstractEventInfo abstractEventInfo = concreteEventInfo
					.getAbstractEventInfos().get(0);
			String abstractComponentName = abstractMachineInfo
					.getAbstractMachine().getComponentName();
			IEvent concreteEvent = concreteEventInfo.getEvent();
			
			List<FreeIdentifier> parameters = abstractEventInfo.getParameters();
			for (FreeIdentifier param : parameters) {
				IIdentifierSymbolInfo paramSymbolInfo = SymbolFactory
						.getInstance().makeImportedParameter(param.getName(),
								false, concreteEvent,
								EventBAttributes.EXTENDED_ATTRIBUTE,
								abstractComponentName);

				paramSymbolInfo.setType(param.getType());
				paramSymbolInfo.makeImmutable();
				identifierSymbolTable.putSymbolInfo(paramSymbolInfo);
				typeEnvironment.add(param);
			}

			if (target == null)
				return;

			ISCParameter[] scParameters = abstractEventInfo.getEvent()
					.getSCParameters();
			for (ISCParameter scParam : scParameters) {
				scParam.copy(target, null, null, false, monitor);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.core.tool.types.IModule#getModuleType()
	 */
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		abstractMachineInfo = (IAbstractMachineInfo) repository
				.getState(IAbstractMachineInfo.STATE_TYPE);
		concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		typeEnvironment = repository.getTypeEnvironment();
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		abstractMachineInfo = null;
		concreteEventInfo = null;
		identifierSymbolTable = null;
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

}
