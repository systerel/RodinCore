/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
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
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IConcreteEventInfo;
import org.eventb.core.sc.state.IConcreteEventTable;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineEventCommitRefinesModule extends SCProcessorModule {

	public static final IModuleType<MachineEventCommitRefinesModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineEventCommitRefinesModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private ILabelSymbolTable labelSymbolTable;
	private IAbstractEventTable abstractEventTable;
	private IConcreteEventInfo concreteEventInfo;
	private IConcreteEventTable concreteEventTable;
	private String eventLabel;

	private static String REFINES_NAME_PREFIX = "REF";

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

		if (target == null)
			return;

		ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(eventLabel);

		createRefinesClause((ISCEvent) target, monitor);

		if (symbolInfo.getAttributeValue(EventBAttributes.EXTENDED_ATTRIBUTE)
				&& concreteEventInfo.getAbstractEventInfos().size() > 0) {
			boolean accurate = concreteEventInfo.getAbstractEventInfos().get(0)
					.getEvent().isAccurate();
			if (!accurate) {
				concreteEventInfo.setNotAccurate();
			}
		}
	}

	private void createRefinesClause(ISCEvent target, IProgressMonitor monitor)
			throws CoreException {

		List<IRefinesEvent> refines = concreteEventInfo.getRefinesClauses();

		if (refines.size() > 0) { // user specified refinements

			int index = 0;

			for (IRefinesEvent refinesEvent : refines) {

				String label = refinesEvent.getAbstractEventLabel();

				ISCEvent abstractEvent = abstractEventTable
						.getAbstractEventInfo(label).getEvent();

				index = createRefinesEvent(target, index, refinesEvent,
						abstractEvent, monitor);
			}
		} else if (concreteEventInfo.getAbstractEventInfos().size() > 0) { // implicit
			// refinement
			IAbstractEventInfo abstractEventInfo = concreteEventInfo
					.getAbstractEventInfos().get(0);

			createRefinesEvent(target, 0, concreteEventInfo.getEvent(),
					abstractEventInfo.getEvent(), monitor);
		}
	}

	private int createRefinesEvent(ISCEvent target, int index,
			IRodinElement element, ISCEvent abstractEvent,
			IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesEvent scRefinesEvent = target
				.getSCRefinesClause(REFINES_NAME_PREFIX + index++);
		scRefinesEvent.create(null, monitor);
		scRefinesEvent.setAbstractSCEvent(abstractEvent, null);
		scRefinesEvent.setSource(element, monitor);
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);

		IEvent event = (IEvent) element;

		eventLabel = event.getLabel();

		labelSymbolTable = (ILabelSymbolTable) repository
				.getState(IMachineLabelSymbolTable.STATE_TYPE);

		concreteEventTable = (IConcreteEventTable) repository
				.getState(IConcreteEventTable.STATE_TYPE);

		concreteEventInfo = concreteEventTable.getConcreteEventInfo(eventLabel);

		repository.setState(concreteEventInfo);

		abstractEventTable = (IAbstractEventTable) repository
				.getState(IAbstractEventTable.STATE_TYPE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement
	 * , org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		labelSymbolTable = null;
		concreteEventTable = null;
		concreteEventInfo = null;
		abstractEventTable = null;
	}

}
