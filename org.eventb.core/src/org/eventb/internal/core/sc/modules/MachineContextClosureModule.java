/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - refactor code for better readability
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.ContextOnlyInAbstractMachineWarning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Copies the contexts seen in the abstract machine into the concrete machine if
 * they are not already seen there (issuing a warning in this case).
 * 
 * We use context names to determine whether a context is already seen or not,
 * which is justified because the name space of contexts is the Rodin project.
 * 
 * @author Stefan Hallerstede
 */
public class MachineContextClosureModule extends SCProcessorModule {

	public static final IModuleType<MachineContextClosureModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineContextClosureModule"); //$NON-NLS-1$

	private static final String CSEES_NAME_PREFIX = "CSEES"; //$NON-NLS-1$
	private static final ISCInternalContext[] NO_CONTEXTS = {};

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private IAbstractMachineInfo abstractMachineInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		final ISCMachineRoot scMachRoot = (ISCMachineRoot) target;
		final ISCInternalContext[] abstractContexts = getAbstractContexts();
		if (abstractContexts.length == 0) {
			return;
		}

		final Set<String> validContextNames = getValidContextNames(repository);
		int count = 0;
		for (final ISCInternalContext context : abstractContexts) {
			final String name = context.getComponentName();
			if (validContextNames.contains(name)) {
				continue;
			}
			// repair
			context.copy(scMachRoot, null, null, false, null);

			final boolean added = copySeesClause(scMachRoot,
					abstractMachineInfo.getAbstractMachine(), name, count++);
			if (added) {
				createProblemMarker(abstractMachineInfo.getRefinesClause(),
						TARGET_ATTRIBUTE, ContextOnlyInAbstractMachineWarning,
						name);
			}
		}
	}

	// Returns the abstract contexts seen by the abstract SC machine
	private ISCInternalContext[] getAbstractContexts() throws RodinDBException {
		final ISCMachineRoot absMach = abstractMachineInfo.getAbstractMachine();
		return absMach == null ? NO_CONTEXTS : absMach.getSCSeenContexts();
	}

	// Returns the names of the contexts already seen by the concrete machine
	private Set<String> getValidContextNames(ISCStateRepository repository)
			throws CoreException {
		final ContextPointerArray cpa = (ContextPointerArray) repository
				.getState(IContextPointerArray.STATE_TYPE);
		final List<ISCContext> validContexts = cpa.getValidContexts();
		final Set<String> result = new HashSet<String>(
				validContexts.size() * 4 / 3 + 1);
		for (final ISCContext context : validContexts) {
			result.add(context.getComponentName());
		}
		return result;
	}

	/*
	 * Copy the sees clause from the abstraction if it introduces directly the
	 * context, otherwise don't add any sees clause: the context is seen
	 * indirectly.  Returns whether a clause was added.
	 */
	private boolean copySeesClause(ISCMachineRoot scMachine,
			ISCMachineRoot scAbsMachFile, String ctxName, int count)
			throws CoreException {

		for (final ISCSeesContext clause : scAbsMachFile.getSCSeesClauses()) {
			if (ctxName.equals(clause.getSeenSCContext().getComponentName())) {
				final String name = CSEES_NAME_PREFIX + count;
				clause.copy(scMachine, null, name, false, null);
				return true;
			}
		}
		return false;
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
