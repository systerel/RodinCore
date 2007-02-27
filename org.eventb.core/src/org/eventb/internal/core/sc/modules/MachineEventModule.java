/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IAbstractEventTable;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.AbstractEventInfo;
import org.eventb.internal.core.sc.AbstractEventTable;
import org.eventb.internal.core.sc.CurrentEvent;
import org.eventb.internal.core.sc.EventRefinesInfo;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.EventLabelSymbolTable;
import org.eventb.internal.core.sc.symbolTable.EventSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.rodinp.core.IAttributeType;
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
	
	public static final IModuleType<MachineEventModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public static int EVENT_LABEL_SYMTAB_SIZE = 47;
	public static int EVENT_IDENT_SYMTAB_SIZE = 29;

	IIdentifierSymbolTable identifierSymbolTable;
	
	FormulaFactory factory;
	
	ITypeEnvironment machineTypeEnvironment;
	
	IEvent[] events;
	
	private AbstractEventTable abstractEventTable;

	private static String EVENT_NAME_PREFIX = "EVT";
	
	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IMachineFile machineFile = (IMachineFile) element;
		
		if (events.length == 0)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_MachineEvents));
		
		IEventSymbolInfo[] symbolInfos = 
			fetchEvents(machineFile, repository, monitor);
		
		ISCEvent[] scEvents = new ISCEvent[events.length];
		
		preprocessEvents(machineFile, (ISCMachineFile) target, scEvents, symbolInfos, monitor);
				
		processEvents(scEvents, repository, symbolInfos, monitor);
				
	}

	private void preprocessEvents(
			IMachineFile machineFile,
			ISCMachineFile target, 
			ISCEvent[] scEvents, 
			IEventSymbolInfo[] symbolInfos, 
			IProgressMonitor monitor) throws CoreException {
		
		for (AbstractEventInfo abstractEventInfo : abstractEventTable.getAbstractEventInfos()) {
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
				
				IEventSymbolInfo eventSymbolInfo = abstractEventInfo.getImplicit();
				
				if (eventSymbolInfo != null)
					if (mergeSymbolInfos.size() > 0 || splitSymbolInfos.size() > 0) {
						createProblemMarker(
								eventSymbolInfo.getSourceElement(), 
								GraphProblem.EventInheritedMergeSplitError, 
								abstractEventInfo.getEventLabel());
						eventSymbolInfo.setError();
					}
			}
			
			abstractEventInfo.makeImmutable();
		}
		
		int index = 0;
		
		for (int i=0; i < events.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				if (symbolInfos[i].isInherited()) {
					scEvents[i] = copyAndPatchSCEvent(target, index++, symbolInfos[i], events[i], monitor);
				} else
					scEvents[i] = createSCEvent(target, index++, symbolInfos[i], events[i], monitor);
			} else
				scEvents[i] = null;
		}
		
	}

	private static String INHERITED_REFINES_NAME = "IREF";
	
	private ISCEvent copyAndPatchSCEvent(
			ISCMachineFile target, 
			int index, 
			IEventSymbolInfo info, 
			IEvent event, 
			IProgressMonitor monitor) throws RodinDBException {

		ISCEvent abstractSCEvent = 
			info.getRefinesInfo().getAbstractEventInfos().get(0).getEvent();
		
		String eventName = EVENT_NAME_PREFIX + index;

		abstractSCEvent.copy(target, null, eventName, false, monitor);
		ISCEvent scEvent = target.getSCEvent(eventName);
		scEvent.setSource(event, monitor);
		
		for (ISCRefinesEvent refinesEvent : scEvent.getSCRefinesClauses())
			refinesEvent.delete(true, monitor);
		
		ISCRefinesEvent refinesEvent = scEvent.getSCRefinesClause(INHERITED_REFINES_NAME);
		refinesEvent.create(null, monitor);
		refinesEvent.setAbstractSCEvent(abstractSCEvent, monitor);
		
		return scEvent;
	}

	private ISCEvent createSCEvent(
			ISCMachineFile target, 
			int index,
			IEventSymbolInfo symbolInfo,
			IEvent event,
			IProgressMonitor monitor) throws RodinDBException {
		ISCEvent scEvent = target.getSCEvent(EVENT_NAME_PREFIX + index);
		scEvent.create(null, monitor);
		scEvent.setLabel(symbolInfo.getSymbol(), monitor);
		scEvent.setSource(event, monitor);
		return scEvent;
	}

	void issueErrorMarkers(
			List<IEventSymbolInfo> symbolInfos, 
			AbstractEventInfo abstractEventInfo, 
			IRodinProblem problem) throws CoreException {
		String abstractEventLabel = abstractEventInfo.getEventLabel();
		for (IEventSymbolInfo symbolInfo : symbolInfos) {
			IEventRefinesInfo refinesInfo = symbolInfo.getRefinesInfo();
			
			issueRefinementErrorMarker(symbolInfo);
			
			for (IRefinesEvent refinesEvent : refinesInfo.getRefinesClauses())
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
			EventSymbolInfo symbolInfo, 
			IRefinesEvent[] refinesEvents, 
			IProgressMonitor monitor) throws CoreException {
		
		EventRefinesInfo refinesInfo = symbolInfo.getRefinesInfo() == null ?
				new EventRefinesInfo(refinesEvents.length) :
				(EventRefinesInfo) symbolInfo.getRefinesInfo();
		
		symbolInfo.setRefinesInfo(refinesInfo);
		
		boolean found = false;
		
		ArrayList<String> abstractLabels = (refinesEvents.length > 1) ? 
				new ArrayList<String>(refinesEvents.length) : 
				null;
		
		HashSet<String> typeErrors = (refinesEvents.length > 1) ?
				new HashSet<String>(37) :
				null;
		Hashtable<String, Type> types = (refinesEvents.length > 1) ?
				new Hashtable<String, Type>(37) :
				null;
				
		boolean firstAction = true;
		boolean actionError = false;
		Hashtable<String, String> actions = (refinesEvents.length > 1) ?
				new Hashtable<String, String>(43) :
				null;
				
		for (int i=0; i<refinesEvents.length; i++) {
			
			String label = refinesEvents[i].getAbstractEventLabel();
			
			// filter duplicates
			if (abstractLabels != null)
				if (abstractLabels.contains(label)) {
					createProblemMarker(
							refinesEvents[i],
							EventBAttributes.TARGET_ATTRIBUTE,
							GraphProblem.AbstractEventLabelConflictWarning, 
							label);
					continue;
				} else
					abstractLabels.add(label);
			
			if (label.equals(IEvent.INITIALISATION)) {
				createProblemMarker(
						refinesEvents[i],
						EventBAttributes.TARGET_ATTRIBUTE,
						GraphProblem.InitialisationRefinedError);
				issueRefinementErrorMarker(symbolInfo);
				continue;
			}
			
			AbstractEventInfo abstractEventInfo = 
				getAbstractEventInfoForLabel(
						symbolInfo, 
						label, 
						refinesEvents[i], 
						EventBAttributes.TARGET_ATTRIBUTE);
			
			if (abstractEventInfo == null)
				continue;
			
			if (symbolInfo.getSymbol().equals(abstractEventInfo.getEventLabel()))
				found = true;
			
			checkForLocalVariableTypeErrors(
					symbolInfo, 
					typeErrors, 
					types, 
					abstractEventInfo);
			
			if (actions != null && !actionError)
				if (firstAction) {
					for (ISCAction action : abstractEventInfo.getEvent().getSCActions()) {
						actions.put(action.getLabel(), action.getAssignmentString());
					}
					firstAction = false;
				} else {
					actionError = checkAbstractActionAccordance(
							symbolInfo, 
							actions, 
							actionError, 
							abstractEventInfo);
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
		
		refinesInfo.makeImmutable();
		
		return found;
	}

	private boolean checkAbstractActionAccordance(IEventSymbolInfo symbolInfo, Hashtable<String, String> actions, boolean actionError, IAbstractEventInfo abstractEventInfo) throws RodinDBException, CoreException {
		ISCAction[] scActions = abstractEventInfo.getEvent().getSCActions();
		boolean ok = scActions.length == actions.size();
		if (ok)
			for (ISCAction action : scActions) {
				String assignment = actions.get(action.getLabel());
				if (assignment != null 
						&& assignment.equals(action.getAssignmentString()))
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
		return actionError;
	}

	private void checkForLocalVariableTypeErrors(IEventSymbolInfo symbolInfo, HashSet<String> typeErrors, Hashtable<String, Type> types, IAbstractEventInfo abstractEventInfo) throws RodinDBException, CoreException {
		if (types != null)
			for (FreeIdentifier identifier : abstractEventInfo.getVariables()) {
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
	}

	private AbstractEventInfo getAbstractEventInfoForLabel(
			IEventSymbolInfo symbolInfo, 
			String label, 
			IInternalElement element,
			IAttributeType attributeType) throws CoreException {
		AbstractEventInfo abstractEventInfo = abstractEventTable.getAbstractEventInfo(label);

		if (abstractEventInfo == null) {
			if (attributeType == null)
				createProblemMarker(
						element,
						GraphProblem.AbstractEventNotFoundError);
			else
				createProblemMarker(
						element,
						attributeType,
						GraphProblem.AbstractEventNotFoundError);
			abstractEventInfo = null;
			createProblemMarker(
					element,
					attributeType,
					GraphProblem.EventRefinementError);
			issueRefinementErrorMarker(symbolInfo);
		}
		return abstractEventInfo;
	}

	private void processEvents(
			ISCEvent[] scEvents, 
			ISCStateRepository repository, 
			IEventSymbolInfo[] symbolInfos, 
			IProgressMonitor monitor) throws CoreException {
		
		for (int i=0; i < events.length; i++) {
			
			if (symbolInfos[i] != null && !symbolInfos[i].isInherited()) { // inherited events are only copied, not processed!
				
				repository.setState(new CurrentEvent(events[i], symbolInfos[i]));
			
				repository.setState(
						new StackedIdentifierSymbolTable(
								identifierSymbolTable, 
								EVENT_IDENT_SYMTAB_SIZE, 
								factory));
			
				repository.setState(
						new EventLabelSymbolTable(EVENT_LABEL_SYMTAB_SIZE));
			
				ITypeEnvironment eventTypeEnvironment = factory.makeTypeEnvironment();
				eventTypeEnvironment.addAll(machineTypeEnvironment);
				addPostValues(eventTypeEnvironment);
				repository.setTypeEnvironment(eventTypeEnvironment);
			
				initProcessorModules(events[i], repository, null);
			
				processModules(events[i], scEvents[i], repository, monitor);
			
				endProcessorModules(events[i], repository, null);
			}
			
			monitor.worked(1);
		}
		
	}

	private IEventSymbolInfo[] fetchEvents(
			IMachineFile machineFile, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		String machineName = machineFile.getElementName();
		
		initFilterModules(repository, null);
		
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
				fetchRefinement(machineFile, event, (EventSymbolInfo) symbolInfos[i], true, monitor);
			} else {
				fetchRefinement(machineFile, event, (EventSymbolInfo) symbolInfos[i], false, monitor);
			}
			
			if (!filterModules(event, repository, null)) {
				symbolInfos[i].setError();
				continue;
			}
			
		}
		
		if (init == null || init.hasError())
			createProblemMarker(
					machineFile,
					GraphProblem.MachineWithoutInitialisationError,
					machineName);
		
		endFilterModules(repository, null);
		
		return symbolInfos;
	}
	
	private void fetchRefinement(
			IMachineFile machineFile,
			IEvent event, 
			EventSymbolInfo symbolInfo, 
			boolean isInit, 
			IProgressMonitor monitor) throws RodinDBException, CoreException {
		boolean inherited = event.isInherited();
		
		if (isInit && !inherited) {
			if (machineFile.getRefinesClauses().length != 0)
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
				if (abstractEventInfo != null)
					createProblemMarker(
							machineFile,
							GraphProblem.InconsistentEventLabelWarning,
							symbolInfo.getSymbol());
			}
		}
	}

	private void makeImplicitRefinement(IEvent event, EventSymbolInfo symbolInfo) throws CoreException {
		AbstractEventInfo eventInfo = 
			getAbstractEventInfoForLabel(symbolInfo, symbolInfo.getSymbol(), event, null);
		
		if (eventInfo == null)
			symbolInfo.setError();
		else {
			eventInfo.setImplicit(symbolInfo);
			EventRefinesInfo refinesInfo = new EventRefinesInfo(1);
			symbolInfo.setRefinesInfo(refinesInfo);
			refinesInfo.addAbstractEventInfo(eventInfo);
			refinesInfo.makeImmutable();
		}
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
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		IMachineFile machineFile = (IMachineFile) element;
		
		events = machineFile.getEvents();
		
		abstractEventTable =
			(AbstractEventTable) repository.getState(IAbstractEventTable.STATE_TYPE);
		
		identifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		factory = repository.getFormulaFactory();
		
		machineTypeEnvironment = repository.getTypeEnvironment();
				
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ProcessorModule#endModule(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(
			IRodinElement element, 
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		repository.setState(identifierSymbolTable);
		repository.setTypeEnvironment(machineTypeEnvironment);
		identifierSymbolTable = null;
		factory = null;
		machineTypeEnvironment = null;
		events = null;
		abstractEventTable = null;
		super.endModule(element, repository, monitor);
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.LabeledElementModule#getLabelSymbolTableFromRepository(org.eventb.core.sc.IStateRepository)
	 */
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(
			String symbol, ILabeledElement element, String component) throws CoreException {
		return new EventSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
	}

}
