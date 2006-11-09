/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IProcessorModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.state.ITypingState;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.CurrentEvent;
import org.eventb.internal.core.sc.EventRefinesInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.TypingState;
import org.eventb.internal.core.sc.symbolTable.EventLabelSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
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
	
	FormulaFactory factory;
	
	ITypingState typingState;
	
	IEvent[] events;
	
	private IAbstractEventTable abstractEventTable;

	private static String EVENT_NAME_PREFIX = "EVT";
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;
		
		if (events.length == 0)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_MachineEvents));
		
		IEventSymbolInfo[] symbolInfos = 
			fetchEvents(machineFile, repository, monitor);
		
		ISCEvent[] scEvents = new ISCEvent[events.length];
		
		preprocessEvents(machineFile, target, scEvents, symbolInfos, monitor);
				
		processEvents(scEvents, repository, monitor);
				
	}

	private void preprocessEvents(
			IMachineFile machineFile,
			IInternalParent target, 
			ISCEvent[] scEvents, 
			IEventSymbolInfo[] symbolInfos, 
			IProgressMonitor monitor) throws CoreException {
		
		for (IAbstractEventInfo abstractEventInfo : abstractEventTable) {
			if (!abstractEventInfo.isRefined())
				createProblemMarker(
						machineFile, 
						GraphProblem.AbstractEventNotRefinedError, 
						abstractEventInfo.getEventLabel());
			else {
				List<IEventSymbolInfo> mergeSymbolInfos = abstractEventInfo.getMergeSymbolInfos();
				List<IEventSymbolInfo> splitSymbolInfos = abstractEventInfo.getSplitSymbolInfos();
				if (mergeSymbolInfos.size() > 0 && splitSymbolInfos.size() > 0) {
					issueErrorMarkers(
							mergeSymbolInfos, 
							abstractEventInfo, 
							GraphProblem.EventMergeSplitError);
					issueErrorMarkers(
							splitSymbolInfos, 
							abstractEventInfo, 
							GraphProblem.EventMergeSplitError);
				} else if (mergeSymbolInfos.size() > 1) {
					issueErrorMarkers(
							mergeSymbolInfos, 
							abstractEventInfo, 
							GraphProblem.EventMergeMergeError);
				}
				
				IEventSymbolInfo eventSymbolInfo = abstractEventInfo.getInherited();
				
				if (eventSymbolInfo != null)
					if (mergeSymbolInfos.size() > 0 || splitSymbolInfos.size() > 0) {
//						issueErrorMarkers(
//								mergeSymbolInfos, 
//								abstractEventInfo, 
//								Messages.scuser_EventInheritedMergeSplitConflict);
//						issueErrorMarkers(
//								splitSymbolInfos, 
//								abstractEventInfo, 
//								Messages.scuser_EventInheritedMergeSplitConflict);
						createProblemMarker(
								eventSymbolInfo.getSourceElement(), 
								GraphProblem.EventInheritedMergeSplitError, 
								abstractEventInfo.getEventLabel());
						eventSymbolInfo.setError();
					}
			}
		}
		
		int index = 0;
		
		for (int i=0; i < events.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scEvents[i] = createSCEvent(target, index++, symbolInfos[i], events[i], monitor);
			} else
				scEvents[i] = null;
		}
		
	}

	void issueErrorMarkers(
			List<IEventSymbolInfo> symbolInfos, 
			IAbstractEventInfo abstractEventInfo, 
			IRodinProblem problem) throws CoreException {
		String abstractEventLabel = abstractEventInfo.getEventLabel();
		for (IEventSymbolInfo symbolInfo : symbolInfos) {
			IEventRefinesInfo refinesInfo = symbolInfo.getRefinesInfo();
			
			issueRefinementErrorMarker(symbolInfo);
			
			for (IRefinesEvent refinesEvent : refinesInfo.getRefinesEvents())
				if (refinesEvent.getAbstractEventLabel().equals(abstractEventLabel))
					createProblemMarker(
							refinesEvent, 
							problem,
							abstractEventLabel);
		}
		abstractEventInfo.setRefineError(true);
	}

	private void issueRefinementErrorMarker(IEventSymbolInfo symbolInfo) throws CoreException {
		if (!symbolInfo.hasError())
			createProblemMarker(
					symbolInfo.getSourceElement(), 
					GraphProblem.EventRefinementError);
		symbolInfo.setError();
	}
	
	private boolean fetchRefineData(
			IEventSymbolInfo symbolInfo, 
			IRefinesEvent[] refinesEvents, 
			IProgressMonitor monitor) throws CoreException {
		
		IEventRefinesInfo refinesInfo = symbolInfo.getRefinesInfo() == null ?
				new EventRefinesInfo(symbolInfo, refinesEvents.length) :
				symbolInfo.getRefinesInfo();
		
		symbolInfo.setRefinesInfo(refinesInfo);
		
		boolean found = false;
		
		HashSet<String> abstractLabels = (refinesEvents.length > 1) ? 
				new HashSet<String>(refinesEvents.length * 4 / 3 + 1) : 
				null;
		
		HashSet<String> typeErrors = (refinesEvents.length > 1) ?
				new HashSet<String>(37) :
				null;
		Hashtable<String, Type> types = (refinesEvents.length > 1) ?
				new Hashtable<String, Type>(37) :
				null;
				
		boolean firstAction = true;
		boolean actionError = false;
		HashSet<String> actions = (refinesEvents.length > 1) ?
				new HashSet<String>(43) :
				null;
				
		for (int i=0; i<refinesEvents.length; i++) {
			
			String label = refinesEvents[i].getAbstractEventLabel();
			
			// filter duplicates
			if (abstractLabels != null)
				if (abstractLabels.contains(label)) {
					createProblemMarker(
							refinesEvents[i],
							EventBAttributes.REFINES_ATTRIBUTE,
							GraphProblem.AbstractEventLabelConflictWarning, 
							label);
					continue;
				} else
					abstractLabels.add(label);
			
			if (label.equals(IEvent.INITIALISATION)) {
				createProblemMarker(
						refinesEvents[i],
						EventBAttributes.REFINES_ATTRIBUTE,
						GraphProblem.InitialisationRefinedError);
				issueRefinementErrorMarker(symbolInfo);
				continue;
			}
			
			IAbstractEventInfo abstractEventInfo = 
				getAbstractEventInfoForLabel(
						symbolInfo, 
						label, 
						refinesEvents[i], 
						EventBAttributes.REFINES_ATTRIBUTE);
			
			if (abstractEventInfo == null)
				continue;
			
			if (symbolInfo.getSymbol().equals(abstractEventInfo.getEventLabel()))
				found = true;
			
			if (types != null)
				for (FreeIdentifier identifier : abstractEventInfo.getIdentifiers()) {
					String name = identifier.getName();
					Type newType = identifier.getType();
					Type type = types.put(name, newType);
					if (type == null || type.equals(newType))
						continue;
					if (typeErrors.add(name)) {
						createProblemMarker(
								symbolInfo.getSourceElement(),
								GraphProblem.EventMergeVariableTypeError,
								name);
						symbolInfo.setError();
					}
				}
			
			if (actions != null && !actionError)
				if (firstAction) {
					for (ISCAction action : abstractEventInfo.getEvent().getSCActions()) {
						actions.add(action.getAssignmentString());
					}
					firstAction = false;
				} else {
					ISCAction[] scActions = abstractEventInfo.getEvent().getSCActions();
					boolean ok = scActions.length == actions.size();
					if (ok)
						for (ISCAction action : scActions) {
							if (actions.contains(action.getAssignmentString()))
								continue;
							ok = false;
							break;
						}
					if (!ok) {
						createProblemMarker(
								symbolInfo.getSourceElement(),
								GraphProblem.EventMergeActionError);
						actionError = true;
						symbolInfo.setError();
					}
				}
			
			refinesInfo.addAbstractEventInfo(abstractEventInfo);
			refinesInfo.addRefinesEvent(refinesEvents[i]);
			
			// this is a pretty rough distinction. But it should be sufficient in practice.
			if (refinesEvents.length == 1) {
				abstractEventInfo.addSplitSymbolInfo(symbolInfo);
			} else {
				abstractEventInfo.addMergeSymbolInfo(symbolInfo);
			}
		}
		
		return found;
	}

	private IAbstractEventInfo getAbstractEventInfoForLabel(
			IEventSymbolInfo symbolInfo, 
			String label, 
			IInternalElement element,
			String attributeId) throws CoreException {
		IAbstractEventInfo abstractEventInfo = abstractEventTable.getAbstractEventInfo(label);

		if (abstractEventInfo == null || abstractEventInfo.isForbidden()) {
			if (attributeId == null)
				createProblemMarker(
						element,
						GraphProblem.AbstractEventNotFoundError);
			else
				createProblemMarker(
						element,
						attributeId,
						GraphProblem.AbstractEventNotFoundError);
			abstractEventInfo = null;
			createProblemMarker(
					element,
					attributeId,
					GraphProblem.EventRefinementError);
			issueRefinementErrorMarker(symbolInfo);
		}
		return abstractEventInfo;
	}

	private void processEvents(
			ISCEvent[] scEvents, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		for (int i=0; i < events.length; i++) {
			
			repository.setState(new CurrentEvent(events[i]));
			
			repository.setState(
					new StackedIdentifierSymbolTable(
							identifierSymbolTable, 
							EVENT_IDENT_SYMTAB_SIZE, 
							factory));
			
			repository.setState(
					new EventLabelSymbolTable(EVENT_LABEL_SYMTAB_SIZE));
			
			ITypeEnvironment eventTypeEnvironment = factory.makeTypeEnvironment();
			eventTypeEnvironment.addAll(typingState.getTypeEnvironment());
			addPostValues(eventTypeEnvironment);
			repository.setState(new TypingState(eventTypeEnvironment));
			
			initProcessorModules(events[i], processorModules, repository, null);
			
			processModules(processorModules, events[i], scEvents[i], repository, monitor);
			
			endProcessorModules(events[i], processorModules, repository, null);
			
			monitor.worked(1);
		}
		
	}

	private IEventSymbolInfo[] fetchEvents(
			IMachineFile machineFile, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		
		String machineName = machineFile.getElementName();
		
		initAcceptorModules(acceptorModules, repository, null);
		
		IEventSymbolInfo[] symbolInfos = new IEventSymbolInfo[events.length];
		
		IEventSymbolInfo init = null;

		for (int i=0; i < events.length; i++) {
			
			IEvent event = events[i];
			
			symbolInfos[i] = 
				(IEventSymbolInfo) fetchLabel(event, machineName, null);
			
			if (symbolInfos[i] == null)
				continue;
			
			if (symbolInfos[i].getSymbol().equals(IEvent.INITIALISATION)) {
				init = symbolInfos[i];
				fetchRefinement(machineFile, event, symbolInfos[i], true, monitor);
			} else {
				fetchRefinement(machineFile, event, symbolInfos[i], false, monitor);
			}
			
			if (!acceptModules(acceptorModules, event, repository, null)) {
				symbolInfos[i].setError();
				continue;
			}
			
		}
		
		if (init == null || init.hasError())
			createProblemMarker(
					machineFile,
					GraphProblem.MachineWithoutInitialisationError,
					machineName);
		
		endAcceptorModules(acceptorModules, repository, null);
		
		return symbolInfos;
	}
	
	private void fetchRefinement(
			IMachineFile machineFile,
			IEvent event, 
			IEventSymbolInfo symbolInfo, 
			boolean isInit, 
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		boolean inherited = event.isInherited(monitor);
		
		if (isInit && !inherited) {
			if (machineFile.getRefinesClause() != null)
				makeImplicitRefinement(event, symbolInfo);
		}
		
		if (inherited) {
			symbolInfo.setInherited();
			
			makeImplicitRefinement(event, symbolInfo);
		} else {
			boolean found = fetchRefineData(symbolInfo, event.getRefinesClauses(), monitor);
			if (!found && !isInit) {
				IAbstractEventInfo abstractEventInfo =
					abstractEventTable.getAbstractEventInfo(symbolInfo.getSymbol());
				if (abstractEventInfo != null && !abstractEventInfo.isForbidden())
					createProblemMarker(
							machineFile,
							GraphProblem.InconsistentEventLabelWarning,
							symbolInfo.getSymbol());
			}
		}
	}

	private void makeImplicitRefinement(IEvent event, IEventSymbolInfo symbolInfo) throws CoreException {
		IAbstractEventInfo eventInfo = 
			getAbstractEventInfoForLabel(symbolInfo, symbolInfo.getSymbol(), event, null);
		
		if (eventInfo == null)
			symbolInfo.setError();
		else {
			eventInfo.setInherited(symbolInfo);
			IEventRefinesInfo refinesInfo = new EventRefinesInfo(symbolInfo, 1);
			symbolInfo.setRefinesInfo(refinesInfo);
			refinesInfo.addAbstractEventInfo(eventInfo);
		}
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
	
	private void addPostValues(ITypeEnvironment typeEnvironment) {
		for (ISymbolInfo symbolInfo : identifierSymbolTable)
			if (symbolInfo instanceof IVariableSymbolInfo){
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				if (variableSymbolInfo.isVisible() 
						&& !variableSymbolInfo.hasError() 
						&& !variableSymbolInfo.isForbidden()) {
					FreeIdentifier identifier = 
						factory.makeFreeIdentifier(
								variableSymbolInfo.getSymbol(), null, 
								variableSymbolInfo.getType()).withPrime(factory);
					typeEnvironment.addName(identifier.getName(), identifier.getType());
				}
		}
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
		IMachineFile machineFile = (IMachineFile) element;
		
		events = machineFile.getEvents();
		
		abstractEventTable =
			(IAbstractEventTable) repository.getState(IAbstractEventTable.STATE_TYPE);
		
		identifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		factory = repository.getFormulaFactory();
		
		typingState = 
			(ITypingState) repository.getState(ITypingState.STATE_TYPE);
				
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		repository.setState(identifierSymbolTable);
		repository.setState(typingState);
		identifierSymbolTable = null;
		factory = null;
		typingState = null;
		events = null;
		abstractEventTable = null;
		super.endModule(element, repository, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			IStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}
	
}
