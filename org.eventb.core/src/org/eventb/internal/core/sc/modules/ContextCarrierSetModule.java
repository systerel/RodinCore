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
import org.eventb.core.EventBPlugin;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
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
public class ContextCarrierSetModule extends IdentifierModule {

	public static final IModuleType<ContextCarrierSetModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".contextCarrierSetModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement
	 * , org.rodinp.core.IInternalElement, org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		
		IRodinFile contextFile = (IRodinFile) element;
		IContextRoot root = (IContextRoot) contextFile.getRoot();
		
		ICarrierSet[] carrierSets = root.getCarrierSets();

		if (carrierSets.length == 0)
			return;

		monitor.subTask(Messages.bind(Messages.progress_ContextCarrierSets));

		fetchSymbols(carrierSets, target, repository, monitor);
	}

	@Override
	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			ITypeEnvironmentBuilder environment) throws CoreException {
		environment.addGivenSet(newSymbolInfo.getSymbol());

		newSymbolInfo.setType(environment.getType(newSymbolInfo.getSymbol()));
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		IEventBRoot context = (IEventBRoot) element.getParent();
		return SymbolFactory.getInstance().makeLocalCarrierSet(name, true,
				element, context.getComponentName());
	}

}
