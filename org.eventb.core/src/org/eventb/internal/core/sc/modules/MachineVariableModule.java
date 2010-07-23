/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.IVariable;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.state.SymbolFactory;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineVariableModule extends IdentifierModule {

	public static final IModuleType<MachineVariableModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".machineVariableModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		IRodinFile machineFile = (IRodinFile) element;
		IMachineRoot machineRoot = (IMachineRoot) machineFile.getRoot();

		IVariable[] variables = machineRoot.getVariables();

		if (variables.length == 0)
			return;

		monitor.subTask(Messages.bind(Messages.progress_MachineVariables));

		fetchSymbols(variables, target, repository, monitor);
	}

	@Override
	protected boolean insertIdentifierSymbol(IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {

		IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
				.getSymbolInfo(newSymbolInfo.getSymbol());

		if (symbolInfo != null) {
			if (symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE) {

				boolean concrete = symbolInfo
						.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE);

				if (!concrete) {
					boolean abstr = symbolInfo
							.getAttributeValue(EventBAttributes.ABSTRACT_ATTRIBUTE);
					if (abstr) {
						symbolInfo.setAttributeValue(
								EventBAttributes.CONCRETE_ATTRIBUTE, true);
						return true;
					} else {
						createProblemMarker(
								element,
								EventBAttributes.IDENTIFIER_ATTRIBUTE,
								GraphProblem.DisappearedVariableRedeclaredError,
								symbolInfo.getSymbol());
						return false;
					}
				}

			}

			newSymbolInfo.createConflictMarker(this);

			if (symbolInfo.hasError())
				return false; // do not produce too many error messages

			symbolInfo.createConflictMarker(this);

			if (symbolInfo.isMutable())
				symbolInfo.setError();

			return false;

		} else {

			identifierSymbolTable.putSymbolInfo(newSymbolInfo);
			newSymbolInfo.setAttributeValue(
					EventBAttributes.CONCRETE_ATTRIBUTE, true);
			newSymbolInfo.setAttributeValue(
					EventBAttributes.ABSTRACT_ATTRIBUTE, false);
			return true;
		}
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		IEventBRoot file = (IEventBRoot) element.getParent();
		return SymbolFactory.getInstance().makeLocalVariable(name, true,
				element, file.getComponentName());
	}

}
