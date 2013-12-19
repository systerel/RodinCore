/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineContextClosureModule extends SCProcessorModule {

	public static final IModuleType<MachineContextClosureModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineContextClosureModule"); //$NON-NLS-1$

	private static final String CSEES_NAME_PREFIX = "CSEES";

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private IAbstractMachineInfo abstractMachineInfo;

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

		ISCMachineRoot scMachRoot = (ISCMachineRoot) target;
		
		if (abstractMachineInfo.getAbstractMachine() == null)
			return;

		ISCInternalContext[] abstractContexts = abstractMachineInfo
				.getAbstractMachine().getSCSeenContexts();

		if (abstractContexts.length == 0)
			return;

		ContextPointerArray contextPointerArray = (ContextPointerArray) repository
				.getState(IContextPointerArray.STATE_TYPE);

		List<ISCContext> validContexts = contextPointerArray.getValidContexts();

		HashSet<String> validContextNames = new HashSet<String>(validContexts
				.size() * 4 / 3 + 1);
		for (ISCContext context : validContexts) {
			validContextNames.add(context.getComponentName());
		}

		int count = 0;
		for (ISCInternalContext context : abstractContexts) {
			String name = context.getComponentName();
			if (validContextNames.contains(name))
				continue;
			else {

				createProblemMarker(abstractMachineInfo.getRefinesClause(),
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.ContextOnlyInAbstractMachineWarning,
						context.getComponentName());

				
				// repair
				copySeesClause(scMachRoot, abstractMachineInfo
						.getAbstractMachine(), context, count++);
				context.copy(scMachRoot, null, null, false, null);

			}
		}

	}

	// Copy the sees clause from the abstraction if it introduces directly the
	// context, otherwise don't add any sees clause: the context is seen
	// indirectly.
	private void copySeesClause(ISCMachineRoot scMachine,
			ISCMachineRoot scAbsMachFile, ISCInternalContext scContext,
			int count) throws CoreException {

		final String ctxName = scContext.getElementName();
		for (ISCSeesContext clause : scAbsMachFile.getSCSeesClauses()) {
			if (ctxName.equals(clause.getSeenSCContext().getComponentName())) {
				final String name = CSEES_NAME_PREFIX + count;
				clause.copy(scMachine, null, name, false, null);
			}
		}
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		abstractMachineInfo = (IAbstractMachineInfo) repository
				.getState(IAbstractMachineInfo.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		abstractMachineInfo = null;
		super.endModule(element, repository, monitor);
	}

}
