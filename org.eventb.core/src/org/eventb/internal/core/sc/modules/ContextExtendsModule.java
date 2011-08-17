/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
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
import org.eventb.core.IContextRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IContextAccuracyInfo;
import org.eventb.core.sc.state.IContextPointerArray;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.ContextPointerArray;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class ContextExtendsModule extends ContextPointerModule {

	public static final IModuleType<ContextExtendsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID + ".contextExtendsModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected ContextPointerArray contextPointerArray;

	private IContextAccuracyInfo accuracyInfo;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);

	
		final IRodinFile contextFile = (IRodinFile) element;
		final IContextRoot root = (IContextRoot) contextFile.getRoot();
		
		
		IExtendsContext[] extendsContexts = root.getExtendsClauses();

//		ISCContextFile[] contextFiles = new ISCContextFile[extendsContexts.length];
		IRodinFile[] contextFiles = new IRodinFile[extendsContexts.length];

		for (int i = 0; i < extendsContexts.length; i++) {
			if (extendsContexts[i].hasAbstractContextName()) {
				contextFiles[i] = extendsContexts[i].getAbstractSCContext().getRodinFile();
				if (!contextFiles[i].exists()) {
					createProblemMarker(extendsContexts[i],
							EventBAttributes.TARGET_ATTRIBUTE,
							GraphProblem.AbstractContextNotFoundError,
							extendsContexts[i].getAbstractContextName());
					contextFiles[i] = null;
				} else if (!((ISCContextRoot) contextFiles[i].getRoot()).hasConfiguration()) {
					createProblemMarker(
							extendsContexts[i],
							EventBAttributes.TARGET_ATTRIBUTE,
							GraphProblem.AbstractContextWithoutConfigurationError,
							extendsContexts[i].getAbstractContextName());
					contextFiles[i] = null;
				}
			} else {
				createProblemMarker(extendsContexts[i],
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.AbstractContextNameUndefError);
			}
		}

		contextPointerArray = new ContextPointerArray(
				IContextPointerArray.PointerType.EXTENDS_POINTER,
				extendsContexts, contextFiles);
		repository.setState(contextPointerArray);

		accuracyInfo = (IContextAccuracyInfo) repository
				.getState(IContextAccuracyInfo.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		contextPointerArray = null;
		accuracyInfo = null;
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		// we need to do everything up to this point
		// produce a define repository state

		if (contextPointerArray.size() == 0) {
			contextPointerArray.makeImmutable();
			return; // nothing to do
		}

		monitor.subTask(Messages.bind(Messages.progress_ContextExtends));

		boolean accurate = fetchSCContexts(contextPointerArray, monitor);

		contextPointerArray.makeImmutable();

		accurate &= createExtendsClauses((ISCContextRoot) target);

		if (!accurate)
			accuracyInfo.setNotAccurate();

		createInternalContexts(target, contextPointerArray.getValidContexts(),
				repository, null);

	}

	@Override
	protected ISCInternalContext getSCInternalContext(IInternalElement target,
			String elementName) {
		ISCContextRoot root = (ISCContextRoot) target;
		return root.getSCInternalContext(elementName);
	}

	private boolean createExtendsClauses(final ISCContextRoot scCtxRoot)
			throws RodinDBException {
		boolean accurate = true;

		final int size = contextPointerArray.size();
		for (int i = 0; i < size; ++i) {
			final IRodinFile scSeenContext = contextPointerArray
					.getSCContextFile(i);
			if (scSeenContext == null || contextPointerArray.hasError(i)) {
				accurate = false;
			} else {
				final ISCExtendsContext scExtends = scCtxRoot.createChild(
						ISCExtendsContext.ELEMENT_TYPE, null, null);

				scExtends.setAbstractSCContext((ISCContextRoot) scSeenContext
						.getRoot(), null);

				final IInternalElement source = contextPointerArray
						.getContextPointer(i);
				scExtends.setSource(source, null);
			}
		}
		return accurate;
	}

	@Override
	protected IRodinProblem getRedundantContextWarning() {
		return GraphProblem.AbstractContextRedundantWarning;
	}

}
