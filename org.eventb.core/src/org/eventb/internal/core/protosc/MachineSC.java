/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.builder.IAutomaticTool;
import org.rodinp.core.builder.IExtractor;
import org.rodinp.core.builder.IGraph;

/**
 * @author halstefa
 *
 */
public class MachineSC extends CommonSC implements IAutomaticTool, IExtractor {

//	private IInterrupt interrupt;
	private IProgressMonitor monitor;

	private IMachineFile machine;
	private ISCMachineFile scMachine;
	
	private MachineRuleBase ruleBase;
	
	private MachineCache machineCache;
	private HashMap<String, Predicate> invariantPredicateMap;
	private HashMap<String, Predicate> theoremPredicateMap;
	private HashMap<String, Predicate> guardPredicateMap;
	private HashMap<String, Assignment> actionSubstititionMap;

	public void init(
			@SuppressWarnings("hiding") IMachineFile machine, 
			@SuppressWarnings("hiding") ISCMachineFile scMachine, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws RodinDBException {
		this.monitor = monitor;
		this.machine = machine;
		this.scMachine = scMachine;
		this.ruleBase = new MachineRuleBase();
		machineCache = new MachineCache(machine, this);
		invariantPredicateMap = new HashMap<String, Predicate>(machineCache.getInvariants().length * 4 / 3 + 1);
		theoremPredicateMap = new HashMap<String, Predicate>(machineCache.getTheorems().length * 4 / 3 + 1);
		guardPredicateMap =  new HashMap<String, Predicate>(machineCache.getEvents().length * 20 / 3 + 1);
		actionSubstititionMap =  new HashMap<String, Assignment>(machineCache.getEvents().length * 20 / 3 + 1);
		problems.clear();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IProducer#run(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IInterrupt, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean run(
			IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		
		if(DEBUG)
			System.out.println(getClass().getName() + " running.");
		
		ISCMachineFile newSCMachine = (ISCMachineFile) RodinCore.create(file);
		IMachineFile machineIn = newSCMachine.getMachineFile();
		
		if (! machineIn.exists())
			MachineSC.makeError("Source machine does not exist.");
		
		init(machineIn, newSCMachine, monitor);
		runSC();
		return true;
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.builder.IProducer#clean(org.eclipse.core.resources.IFile, org.rodinp.core.builder.IInterrupt, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void clean(
			IFile file, 
			@SuppressWarnings("hiding") IProgressMonitor monitor) throws CoreException {
		file.delete(true, monitor);

	}
	
	public void extract(IFile file, IGraph graph) throws CoreException {
		IMachineFile machineIn = (IMachineFile) RodinCore.create(file);
		ISCMachineFile target = machineIn.getSCMachineFile();
		ISCContextFile seen = getSeenContext(machineIn);

		IPath inPath = machineIn.getPath();
		IPath targetPath = target.getPath();
		IPath seenPath = seen != null ? seen.getPath() : null;
		
		graph.addNode(targetPath, SCCore.MACHINE_SC_TOOL_ID);
		graph.putToolDependency(inPath, targetPath, SCCore.CONTEXT_SC_TOOL_ID, true);
		if (seenPath != null)
			graph.putUserDependency(inPath, seenPath, targetPath, SCCore.MACHINE_SEES_REL_ID, true);
		graph.updateGraph();
	}

	public void runSC() throws CoreException {
		commitVariables();
		commitInvariants();
		retractUntypedVariables();
		commitTheorems();
		commitEvents();

		// Create the resulting statically checked file atomically.
		RodinCore.run(
				new IWorkspaceRunnable() {
					public void run(IProgressMonitor saveMonitor) throws RodinDBException {
						createCheckedMachine();
					}
				}, monitor);
		
		issueProblems(machine);
	}

	private void commitVariables() throws RodinDBException {
		List<IMachineRule> rules = ruleBase.getVariableRules();
		for(IVariable variable : machineCache.getVariables()) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(variable, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				String elementName = variable.getElementName();
				machineCache.getNewVariables().put(elementName, variable);	
			}
		}
	}
	
	private void commitInvariants() throws RodinDBException {
		List<IMachineRule> rules = ruleBase.getInvariantRules();
		for(IInvariant invariant : machineCache.getInvariants()) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(invariant, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				SCParser parser = parseAndVerifyPredicate(invariant, machineCache.getTypeEnvironment());
				if(parser != null) {
					machineCache.getNewInvariants().add(invariant);	
					machineCache.setTypeEnvironment(parser.getTypeEnvironment());
					invariantPredicateMap.put(invariant.getElementName(), parser.getPredicate());
				}
			}
		}
	}
	
	private void commitTheorems() throws RodinDBException {
		List<IMachineRule> rules = ruleBase.getTheoremRules();
		for(ITheorem theorem : machineCache.getTheorems()) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(theorem, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				SCParser parser = parseAndVerifyPredicate(theorem, machineCache.getTypeEnvironment());
				if(parser != null) {
					machineCache.getNewTheorems().add(theorem);
					theoremPredicateMap.put(theorem.getElementName(), parser.getPredicate());
				}
			}
		}
	}
	
	private void retractUntypedVariables() {
		String[] variables = new String[machineCache.getNewVariables().size()];
		machineCache.getNewVariables().keySet().toArray(variables);
		for(String name : variables) {
			if(!machineCache.getTypeEnvironment().contains(machineCache.getVariableIdentMap().get(name))) {
				addProblem(machineCache.getNewVariables().get(name), "Variable does not have a type.", SCProblem.SEVERITY_ERROR);
				machineCache.getNewVariables().remove(name);
			}
		}
	}
	
	private void commitEvents() throws RodinDBException {
		List<IMachineRule> rules = ruleBase.getEventRules();
		for(IEvent event : machineCache.getEvents()) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(event, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				String name = event.getElementName();
				machineCache.getNewEvents().put(name, event);
				
				ArrayList<IGuard> committedGuards = machineCache.getNewGuards().get(name); 
				
				ArrayList<IAction> committedActions = machineCache.getNewActions().get(name);
				
				HashMap<String, IVariable> committedVariables = machineCache.getNewLocalVariables().get(name);
				
				commitLocalVariables(event, committedVariables);
				commitGuards(event, committedGuards);
				retractUntypedLocalVariables(event, committedVariables);
				commitActions(event, committedActions);
			}
		}
		if(!machineCache.getNewEvents().keySet().contains("INITIALISATION"))
			addProblem(machine, "MachineFile does not have an initialisation.", SCProblem.SEVERITY_ERROR);
	}
	
	private void commitLocalVariables(IEvent event, HashMap<String, IVariable> committedVariables) throws RodinDBException {
		List<IMachineRule> rules = ruleBase.getLocalVariableRules();
//		String name = event.getElementName();
		for(IVariable variable : machineCache.getVariables(event)) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(variable, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				String elementName = variable.getElementName();
				committedVariables.put(elementName, variable);	
			}
		}
	}
	
	private void retractUntypedLocalVariables(IEvent event, HashMap<String, IVariable> committedVariables) {
		String[] variables = new String[committedVariables.size()];
		committedVariables.keySet().toArray(variables);
		ITypeEnvironment typeEnvironment = machineCache.getLocalTypeEnvironment().get(event.getElementName());
		for(String name : variables) {
			if(!typeEnvironment.contains(machineCache.getLocalVariableIdentMap(event).get(name))) {
				addProblem(machineCache.getNewLocalVariables().get(event.getElementName()).get(name), "Variable does not have a type.", SCProblem.SEVERITY_ERROR);
				machineCache.getNewLocalVariables().get(event.getElementName()).remove(name);
			}
		}
	}
	
	private void commitGuards(IEvent event, ArrayList<IGuard> committedGuards) throws RodinDBException {
		ITypeEnvironment typeEnvironment = machineCache.getTypeEnvironment().clone();
		List<IMachineRule> rules = ruleBase.getGuardRules();
		String name = event.getElementName();
		for(IGuard guard : machineCache.getGuards(event)) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(guard, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				SCParser parser = parseAndVerifyPredicate(guard, typeEnvironment);
				if(parser != null) {
					committedGuards.add(guard);
					guardPredicateMap.put(name + "-" + guard.getElementName(), parser.getPredicate());
				}
			}
		}
		ITypeEnvironment localEnvironment = FormulaFactory.getDefault().makeTypeEnvironment();
		for(String vName : typeEnvironment.getNames())
			if(!machineCache.getTypeEnvironment().contains(vName))
				localEnvironment.addName(vName, typeEnvironment.getType(vName));
		machineCache.getLocalTypeEnvironment().put(name, localEnvironment);
	}
	
	private void commitActions(IEvent event, ArrayList<IAction> committedActions) throws RodinDBException {
		List<IMachineRule> rules = ruleBase.getActionRules();
		String name = event.getElementName();
		boolean isOrdinary = (!name.equals("INITIALISATION"));
		Set<String> vars = new HashSet<String>(machineCache.getNewVariables().keySet());
		for(IAction action : machineCache.getActions(event)) {
			boolean verified = true;
			for(IMachineRule rule : rules) {
				verified = rule.verify(action, machineCache, this);
				if(!verified)
					break;
			}
			if(verified) {
				ITypeEnvironment allTypeEnvironment = machineCache.getTypeEnvironment().clone();
				allTypeEnvironment.addAll(machineCache.getLocalTypeEnvironment().get(name));
				SCParser parser = parseAndVerifyAssignment(action, allTypeEnvironment, isOrdinary);
				if(parser != null) {
					committedActions.add(action);
					actionSubstititionMap.put(name + action.getContents(), parser.getAssignment());
					for(FreeIdentifier identifier : parser.getAssignment().getAssignedIdentifiers()) {
						vars.remove(identifier.getName());
					}
				}
			}
		}
		if(!isOrdinary && !vars.isEmpty()) {
			addProblem(event, "Not all variables are initialised. Missing: " + vars.toString(), SCProblem.SEVERITY_ERROR);
		}
	}
	
	protected SCParser parseAndVerifyPredicate(IInternalElement element, ITypeEnvironment typeEnvironment) {
		Collection<FreeIdentifier> declaredIdentifiers = new HashSet<FreeIdentifier>();
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getOldCarrierSets().keySet(), machineCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getOldConstants().keySet(), machineCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getOldConstants().keySet(), machineCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getNewVariables().keySet(), machineCache.getFactory()));
		if(element instanceof IGuard || element instanceof IAction)
			declaredIdentifiers.addAll(makeIdentifiers(machineCache.getLocalVariables((IEvent) element.getParent()).keySet(), machineCache.getFactory()));
		try {
			SCParser parser = new SCParser(typeEnvironment, declaredIdentifiers, machineCache.getFactory());
			if(parser.parsePredicate(element, this)) {
				
				FreeIdentifier[] freeIdentifiers = parser.getPredicate().getFreeIdentifiers();
				
				ArrayList<String> unboundList = new ArrayList<String>(freeIdentifiers.length);
				
				boolean allContained = true;
				for(FreeIdentifier identifier : freeIdentifiers) {
					boolean contained = false;
					String name = identifier.getName();
					if(element instanceof IInvariant || element instanceof ITheorem) {
						contained |= machineCache.getOldCarrierSets().containsKey(name);
						contained |= machineCache.getOldConstants().containsKey(name);
						contained |= machineCache.getOldVariables().containsKey(name);
						contained |= machineCache.getNewVariables().containsKey(name);
					} else if(element instanceof IGuard || element instanceof IAction) {
						contained |= machineCache.getOldCarrierSets().containsKey(name);
						contained |= machineCache.getOldConstants().containsKey(name);
						contained |= machineCache.getNewVariables().containsKey(name);
						contained |= machineCache.getLocalVariables(((IEvent) element.getParent())).containsKey(name);
					}
					allContained &= contained;
					if(!contained)
						unboundList.add(name);
				}
				if(allContained) {
					return parser;
				} else {
					assert unboundList.size() > 0;
					String result = unboundList.get(0);
					for(int i=1; i< unboundList.size(); i++) {
						result += "," + unboundList.get(i); //$NON-NLS-1$
					}
					addProblem(element, "Undeclared free identifiers in predicate:" + result, SCProblem.SEVERITY_ERROR);
				}
			}
		} catch (RodinDBException e) {
			logMessage(e, "Cannot access contents of element" + element.getElementName());
		}
		// in this case we cannot accept the axiom as well-formed
		return null;
	}
	
	protected SCParser parseAndVerifyAssignment(IInternalElement element, ITypeEnvironment typeEnvironment, boolean isOrdinaryEvent) {
		Collection<FreeIdentifier> declaredIdentifiers = new HashSet<FreeIdentifier>();
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getOldCarrierSets().keySet(), machineCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getOldConstants().keySet(), machineCache.getFactory()));
		declaredIdentifiers.addAll(makeIdentifiers(machineCache.getOldConstants().keySet(), machineCache.getFactory()));
		if(isOrdinaryEvent) {
			declaredIdentifiers.addAll(makeIdentifiers(machineCache.getNewVariables().keySet(), machineCache.getFactory()));
			declaredIdentifiers.addAll(makeIdentifiers(machineCache.getLocalVariables((IEvent) element.getParent()).keySet(), machineCache.getFactory()));
		}
		try {
			SCParser parser = new SCParser(typeEnvironment, declaredIdentifiers, machineCache.getFactory());
			if(parser.parseAssignment(element, this)) {
				
				Assignment assignment = parser.getAssignment();
				
				FreeIdentifier[] assignedIdentifiers = assignment.getAssignedIdentifiers();
				
				FreeIdentifier[] freeIdentifiers = assignment.getUsedIdentifiers();
				ArrayList<String> unboundList = new ArrayList<String>(freeIdentifiers.length);
				
				boolean allContained = true;
				for(FreeIdentifier identifier : freeIdentifiers) {
					boolean contained = false;
					String name = identifier.getName();
					contained |= machineCache.getOldCarrierSets().containsKey(name);
					contained |= machineCache.getOldConstants().containsKey(name);
					if(isOrdinaryEvent) {
						contained |= machineCache.getNewVariables().containsKey(name);
						contained |= machineCache.getLocalVariables(((IEvent) element.getParent())).containsKey(name);
					}
					allContained &= contained;
					if(!contained)
						unboundList.add(name);
				}
				if(!allContained) {
					assert unboundList.size() > 0;
					String result = unboundList.get(0);
					for(int i=1; i< unboundList.size(); i++) {
						result += "," + unboundList.get(i); //$NON-NLS-1$
					}
					addProblem(element, "Undeclared free identifiers on right hand side of assignment:" + result, SCProblem.SEVERITY_ERROR);
				}
				unboundList.clear();
				for(FreeIdentifier identifier : assignedIdentifiers) {
					if(!machineCache.getNewVariables().containsKey(identifier.getName()))
						unboundList.add(identifier.getName());
				}
				if(allContained && unboundList.isEmpty()) {
					return parser;
				} else if(!unboundList.isEmpty()) {
					String result = unboundList.get(0);
					for(int i=1; i< unboundList.size(); i++) {
						result += "," + unboundList.get(i); //$NON-NLS-1$
					}
					addProblem(element, "Undeclared free identifiers on left hand side of assignment:" + result, SCProblem.SEVERITY_ERROR);
				}
			}
		} catch (RodinDBException e) {
			logMessage(e, "Cannot access contents of element" + element.getElementName());
		}
		// in this case we cannot accept the axiom as well-formed
		return null;
	}
	
	void createCheckedMachine() throws RodinDBException {
		IRodinProject project = scMachine.getRodinProject();
		project.createRodinFile(scMachine.getElementName(), true, null);

		createDeclarations(scMachine, machineCache.getNewVariables().values(),
				machineCache.getVariableIdentMap());

//		ISCInvariantSet invariantSet = (ISCInvariantSet) scMachine.createInternalElement(ISCInvariantSet.ELEMENT_TYPE, "MODEL", null, monitor);
//		createOldFormulas(invariantSet, machineCache.getOldInvariants());

		ISCContextFile seenContext = machineCache.getSeenContext();
		if (seenContext != null) {
			ISCInternalContext internalContext = createInternalContext(seenContext);
			createDeclarations(
					internalContext,
					machineCache.getOldCarrierSets().values(),
					null);
			createDeclarations(
					internalContext,
					machineCache.getOldConstants().values(),
					null);
			createOldFormulas(internalContext, machineCache.getOldAxioms());
			createOldFormulas(internalContext, machineCache.getOldTheorems());
		}
		
		createNewFormulas(scMachine, machineCache.getNewInvariants());
		createNewFormulas(scMachine, machineCache.getNewTheorems());
		
		createEvents();
		
		scMachine.save(monitor, true);
	}
	
	private ISCInternalContext createInternalContext(ISCContextFile seenContext)
			throws RodinDBException {
		ISCInternalContext result = 
			(ISCInternalContext) scMachine.createInternalElement(
				ISCInternalContext.ELEMENT_TYPE,
				seenContext.getElementName(),
				null,
				monitor);
		result.setContents(seenContext.getHandleIdentifier());
		return result;
	}

	static ISCContextFile getSeenContext(IMachineFile machine)
			throws RodinDBException {
		ISeesContext[] sees = machine.getSeesClauses();
		if (sees.length == 0)
			return null;
		return sees[0].getSeenSCContext();
	}
	
	String getCorrespondingElementType(String type) {
		if(type.equals(ISCConstant.ELEMENT_TYPE))
			return ISCConstant.ELEMENT_TYPE;
		else if(type.equals(ISCCarrierSet.ELEMENT_TYPE))
			return ISCCarrierSet.ELEMENT_TYPE;
		else if(type.equals(ISCTheorem.ELEMENT_TYPE))
			return ISCTheorem.ELEMENT_TYPE;
		else if(type.equals(IVariable.ELEMENT_TYPE))
			return ISCVariable.ELEMENT_TYPE;
		else if(type.equals(IInvariant.ELEMENT_TYPE))
			return ISCInvariant.ELEMENT_TYPE;
		else if(type.equals(ITheorem.ELEMENT_TYPE))
			return ISCTheorem.ELEMENT_TYPE;
		else
			return "?";
	}
	
	private void createDeclarations(
			IInternalParent parent,
			Collection<? extends IInternalElement> elements,
			HashMap<String, String> identMap) throws RodinDBException {
		for(IInternalElement element : elements) {
			IInternalElement newElement = parent.createInternalElement(
					getCorrespondingElementType(element.getElementType()),
					element.getElementName(), 
					null, 
					monitor);
			// TODO: set origin attribute of new element
			newElement.setContents(machineCache.getTypeEnvironment().getType(element.getElementName()).toString());
//			IPOIdentifier identifier = (IPOIdentifier) scMachine.createInternalElement(IPOIdentifier.ELEMENT_TYPE, element.getElementName(), null, monitor);
//			String ident;
//			if(identMap == null)
//				ident = element.getElementName();
//			else
//				ident = identMap.get(element.getElementName());
//			identifier.setContents(machineCache.getTypeEnvironment().getType(ident).toString());
		}
	}
	
	private void createOldFormulas(IInternalParent parent, Collection<? extends IInternalElement> elements) throws RodinDBException {
		for(IInternalElement element : elements) {
			IInternalElement newElement = parent.createInternalElement(element.getElementType(), element.getElementName(), null, monitor);
			newElement.setContents(element.getContents());
			// TODO: set origin attribute of new element
		}
	}

	private void createNewFormulas(IInternalParent parent, Collection<? extends IInternalElement> elements) throws RodinDBException {
		for(IInternalElement element : elements) {
			IInternalElement newElement = parent.createInternalElement(
					getCorrespondingElementType(element.getElementType()), element.getElementName(), null, monitor);
			String newContents = (element.getElementType().equals(IInvariant.ELEMENT_TYPE)) ? 
					invariantPredicateMap.get(element.getElementName()).toString() :
					theoremPredicateMap.get(element.getElementName()).toString();
			newElement.setContents(newContents);
			// TODO: set origin attribute of new element
		}
	}
	
	private void createEvents() throws RodinDBException {
		for(IEvent event : machineCache.getNewEvents().values()) {
			ISCEvent newEvent = (ISCEvent) scMachine.createInternalElement(ISCEvent.ELEMENT_TYPE, event.getElementName(), null, monitor);
			for(IVariable variable : machineCache.getLocalVariables(event).values()) {
				ISCVariable newVariable = (ISCVariable) newEvent.createInternalElement(ISCVariable.ELEMENT_TYPE, variable.getElementName(), null, null);
				newVariable.setContents(machineCache.getLocalTypeEnvironment().get(event.getElementName()).getType(variable.getElementName()).toString());
//				IPOIdentifier identifier = (IPOIdentifier) newEvent.createInternalElement(IPOIdentifier.ELEMENT_TYPE, variable.getElementName(), null, monitor);
//				String ident = machineCache.getLocalVariableIdentMap(event).get(variable.getElementName());
//				identifier.setContents(machineCache.getLocalTypeEnvironment().get(event.getElementName()).getType(ident).toString());
			}
			for(IGuard guard : machineCache.getNewGuards().get(event.getElementName())) {
				ISCGuard newGuard = (ISCGuard) newEvent.createInternalElement(ISCGuard.ELEMENT_TYPE, guard.getElementName(), null, monitor);
				Predicate predicate = guardPredicateMap.get(event.getElementName() + "-" + guard.getElementName());
				newGuard.setContents(predicate.toString());
			}
			for(IAction action : machineCache.getNewActions().get(event.getElementName())) {
				ISCAction newAction = (ISCAction) newEvent.createInternalElement(ISCAction.ELEMENT_TYPE, action.getElementName(), null, monitor);
				newAction.setContents(actionSubstititionMap.get(event.getElementName() + action.getContents()).toString());
			}
		}
	}
	
}
