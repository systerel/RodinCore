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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IIdentifierSymbolTable;
import org.eventb.core.sc.ILabelSymbolTable;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ITypingState;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.LabelSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventModule extends LabeledElementModule {
	
	public static int EVENT_LABEL_SYMTAB_SIZE = 47;
	public static int EVENT_IDENT_SYMTAB_SIZE = 29;

	public static final String MACHINE_EVENT_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventAcceptor";

	public static final String MACHINE_EVENT_PROCESSOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventProcessor";

	private IAcceptorModule[] acceptorModules;
	
	private IProcessorModule[] processorModules;

	public MachineEventModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		acceptorModules = manager.getAcceptorModules(MACHINE_EVENT_ACCEPTOR);
		processorModules = manager.getProcessorModules(MACHINE_EVENT_PROCESSOR);
	}
	
	IIdentifierSymbolTable identifierSymbolTable;
	
	ILabelSymbolTable labelSymbolTable;
	
	FormulaFactory factory;
	
	ITypingState typingState;
	
	ITypeEnvironment typeEnvironment;

	private static String EVENT_NAME_PREFIX = "EVT";
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;
		
		IEvent[] events = machineFile.getEvents();
		
		if (events.length == 0)
			return;
		
		IEventSymbolInfo[] symbolInfos = 
			fetchEvents(machineFile, events, repository, monitor);
		
		ISCEvent[] scEvents = new ISCEvent[events.length];
		
		int index = 0;
		
		for (int i=0; i < events.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scEvents[i] = createSCEvent(target, index++, symbolInfos[i], events[i], monitor);
			} else
				scEvents[i] = null;
		}
				
		processEvents(events, scEvents, repository, monitor);
				
	}

	private void processEvents(
			IEvent[] events, 
			ISCEvent[] scEvents, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		for (int i=0; i < events.length; i++) {
			
			repository.setState(
					new StackedIdentifierSymbolTable(
							identifierSymbolTable, 
							EVENT_IDENT_SYMTAB_SIZE, 
							factory));
			
			repository.setState(
					new LabelSymbolTable(EVENT_LABEL_SYMTAB_SIZE));
			
			ITypeEnvironment eventTypeEnvironment = factory.makeTypeEnvironment();
			eventTypeEnvironment.addAll(typeEnvironment);
			typingState.setTypeEnvironment(eventTypeEnvironment);
			
			initProcessorModules(events[i], processorModules, repository, monitor);
			
			processModules(processorModules, events[i], scEvents[i], repository, monitor);
			
			endProcessorModules(events[i], processorModules, repository, monitor);
		}
		
	}

	private IEventSymbolInfo[] fetchEvents(
			IMachineFile machineFile, 
			IEvent[] events, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		String machineName = machineFile.getElementName();
		
		initAcceptorModules(acceptorModules, repository, monitor);
		
		IEventSymbolInfo[] symbolInfos = new IEventSymbolInfo[events.length];

		for (int i=0; i < events.length; i++) {
			
			IEvent event = events[i];
			
			symbolInfos[i] = 
				(IEventSymbolInfo) fetchLabel(event, labelSymbolTable, machineName, monitor);
			
			if (symbolInfos[i] != null && !acceptModules(acceptorModules, event, repository, monitor)) {
				symbolInfos[i].setError();
			}
			
		}
		
		endAcceptorModules(acceptorModules, repository, monitor);
		
		return symbolInfos;
	}
	
	private ISCEvent createSCEvent(
			IInternalParent target, 
			int index,
			IEventSymbolInfo symbolInfo,
			IEvent event,
			IProgressMonitor monitor) throws RodinDBException {
		ISCEvent scEvent =
			(ISCEvent) target.createInternalElement(
					ISCEvent.ELEMENT_TYPE, EVENT_NAME_PREFIX + index, null, monitor);
		scEvent.setLabel(symbolInfo.getSymbol(), monitor);
		scEvent.setSource(event, monitor);
		return scEvent;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#initModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IRodinElement element, IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		labelSymbolTable =
			(ILabelSymbolTable) repository.getState(ILabelSymbolTable.STATE_TYPE);
		
		factory = repository.getFormulaFactory();
		
		typingState = 
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
		
		typeEnvironment = typingState.getTypeEnvironment();

	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		repository.setState(identifierSymbolTable);
		repository.setState(labelSymbolTable);
		typingState.setTypeEnvironment(typeEnvironment);
		identifierSymbolTable = null;
		labelSymbolTable = null;
		factory = null;
		typingState = null;
		typeEnvironment = null;
	}
	
}
