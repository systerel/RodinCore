/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEvent;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.EventRefinesInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventRefinesModule extends ProcessorModule {

	private ILabelSymbolTable labelSymbolTable;
	private IAbstractEventTable abstractEventTable;
	private IEventRefinesInfo eventRefinesInfo;
	private String eventLabel;
	
	private static String REFINES_NAME_PREFIX = "REF";

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IProcessorModule#process(org.rodinp.core.IRodinElement, org.rodinp.core.IInternalParent, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor)
			throws CoreException {

		if (target == null)
			return;
		
		IEventSymbolInfo symbolInfo = (IEventSymbolInfo) labelSymbolTable.getSymbolInfo(eventLabel);
		
		createRefinesClause(target, symbolInfo, monitor);
		
	}
	
	private void createRefinesClause(
			IInternalParent target, 
			IEventSymbolInfo symbolInfo, 
			IProgressMonitor monitor) throws RodinDBException {
		
		IEventRefinesInfo refinesInfo = symbolInfo.getRefinesInfo();
		
		List<IRefinesEvent> refines = refinesInfo.getRefinesEvents();
		
		if (refines.size() > 0) { // user specified refinements
		
			int index = 0;
			
			for (IRefinesEvent refinesEvent : refines) {
				
				String label = refinesEvent.getAbstractEventLabel(null);
				
				ISCEvent abstractEvent = abstractEventTable.getAbstractEventInfo(label).getEvent();
		
				index = createRefinesEvent(target, index, refinesEvent, abstractEvent, monitor);
			}
		} else if (refinesInfo.getAbstractEventInfos().size() > 0) { // inherited event or initialisation
			IAbstractEventInfo abstractEventInfo = refinesInfo.getAbstractEventInfos().get(0);
			
			createRefinesEvent(target, 0, 
					symbolInfo.getSourceElement(), abstractEventInfo.getEvent(), monitor);
		}
	}

	private int createRefinesEvent(
			IInternalParent target, 
			int index, 
			IRodinElement element, 
			ISCEvent abstractEvent, 
			IProgressMonitor monitor) throws RodinDBException {
		ISCRefinesEvent scRefinesEvent = (ISCRefinesEvent) target.createInternalElement(
				ISCRefinesEvent.ELEMENT_TYPE, 
				REFINES_NAME_PREFIX + index++, null, monitor);
		scRefinesEvent.setAbstractSCEvent(abstractEvent, null);
		scRefinesEvent.setSource(element, monitor);
		return index;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		
		IEvent event = (IEvent) element;
		
		eventLabel = event.getLabel(monitor);
		
		labelSymbolTable = (ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
		
		IEventSymbolInfo eventSymbolInfo = (IEventSymbolInfo) labelSymbolTable.getSymbolInfo(eventLabel);
		
		eventRefinesInfo = eventSymbolInfo.getRefinesInfo();
		
		if (eventRefinesInfo == null)
			eventRefinesInfo = new EventRefinesInfo(eventSymbolInfo, 0);
		
		repository.setState(eventRefinesInfo);
		
		abstractEventTable = (IAbstractEventTable) repository.getState(IAbstractEventTable.STATE_TYPE);
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(element, repository, monitor);
		labelSymbolTable = null;
		abstractEventTable = null;
	}
	
}
