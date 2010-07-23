/*******************************************************************************
 * Copyright (c) 2008, 2009 University of Southampton and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCEvent;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractMachineInfo;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class MachineEventCopyLabeledElementsModule extends
		SCProcessorModule {

	private IAbstractMachineInfo abstractMachineInfo;
	protected IConcreteEventInfo concreteEventInfo;
	private IEventLabelSymbolTable labelSymbolTable;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {

		if (copyNeeded()) {

			ILabelSymbolInfo symbolInfo = concreteEventInfo.getSymbolInfo();

			if (symbolInfo.hasAttribute(EventBAttributes.EXTENDED_ATTRIBUTE)
					&& symbolInfo
							.getAttributeValue(EventBAttributes.EXTENDED_ATTRIBUTE)
					&& concreteEventInfo.getAbstractEventInfos().size() > 0) {

				IAbstractEventInfo abstractEventInfo = concreteEventInfo
						.getAbstractEventInfos().get(0);
				ISCEvent scEvent = abstractEventInfo.getEvent();

				ILabeledElement[] scElements = getSCElements(scEvent);

				String abstractMachineName = abstractMachineInfo
						.getAbstractMachine().getComponentName();
				IEvent concreteEvent = concreteEventInfo.getEvent();

				for (ILabeledElement scElement : scElements) {
					String label = scElement.getLabel();
					ILabelSymbolInfo newSymbolInfo = makeLabelSymbolInfo(label,
							concreteEvent, abstractMachineName);
					labelSymbolTable.putSymbolInfo(newSymbolInfo);
				}

				if (target == null)
					return;

				for (ILabeledElement scElement : scElements) {
					scElement.copy(target, null, null, false, monitor);
				}
			}
		}
	}

	protected abstract boolean copyNeeded();

	protected abstract ILabelSymbolInfo makeLabelSymbolInfo(String label,
			IEvent event, String component);

	protected abstract ILabeledElement[] getSCElements(ISCEvent scEvent)
			throws RodinDBException;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		concreteEventInfo = (IConcreteEventInfo) repository
				.getState(IConcreteEventInfo.STATE_TYPE);
		labelSymbolTable = (IEventLabelSymbolTable) repository
				.getState(IEventLabelSymbolTable.STATE_TYPE);
		abstractMachineInfo = (IAbstractMachineInfo) repository
				.getState(IAbstractMachineInfo.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		concreteEventInfo = null;
		labelSymbolTable = null;
		abstractMachineInfo = null;
		super.endModule(element, repository, monitor);
	}

}
