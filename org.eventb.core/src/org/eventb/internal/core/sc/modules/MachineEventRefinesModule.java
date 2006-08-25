/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.sc.IAbstractEventInfo;
import org.eventb.core.sc.IAbstractEventTable;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IRefinedEventTable;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.RefinedEventTable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventRefinesModule extends ProcessorModule {

	private IRefinesEvent[] refinesEvents;
	private IRefinedEventTable refinedEventTable;
	private IAbstractEventTable abstractEventTable;
	
	private static String REFINES_NAME_PREFIX = "REF";

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor)
			throws CoreException {
		
		if (refinesEvents.length == 0)
			return;
		
		IAbstractEventInfo abstractEventInfo =
			abstractEventTable.getAbstractEventInfo(
					refinesEvents[0].getAbstractEventName());
		
		if (abstractEventInfo == null) {
			issueMarker(IMarkerDisplay.SEVERITY_ERROR, refinesEvents[0], 
					Messages.scuser_AbstractEventNotFound);
			return;
		}
		
		createRefinesClause(target, 0, refinesEvents[0], abstractEventInfo.getEvent(), monitor);
		
		refinedEventTable.addAbstractEventInfo(abstractEventInfo);

	}
	
	private void createRefinesClause(
			IInternalParent target, 
			int index,
			IRefinesEvent refinesEvent, 
			ISCEvent event,
			IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesEvent scRefinesEvent = (ISCRefinesEvent) target.createInternalElement(
				ISCRefinesEvent.ELEMENT_TYPE, 
				REFINES_NAME_PREFIX + index, null, monitor);
		scRefinesEvent.setAbstractSCEvent(event);
		scRefinesEvent.setSource(refinesEvent, monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		IEvent event = (IEvent) element;
		
		refinesEvents = event.getRefinesClauses();
		
		if (refinesEvents.length > 1)
			issueMarker(
					IMarkerDisplay.SEVERITY_WARNING, 
					event, 
					Messages.scuser_OnlyOneEventRefinesClauseProblem);
		
		refinedEventTable =
			new RefinedEventTable(refinesEvents.length);
		
		repository.setState(refinedEventTable);

		abstractEventTable =
			(IAbstractEventTable) repository.getState(IAbstractEventTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		refinesEvents = null;
		refinedEventTable = null;
		abstractEventTable = null;
	}
	
}
