/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class MachineCache extends Cache<IMachine> {

	private HashMap<String, IVariable> olderVariables; // variables that were present before the abstraction
	private HashMap<String, IVariable> oldVariables; // the collection of variables present in the abstraction
	private HashSet<String> variableConflictSet = new HashSet<String>(3);
	private HashMap<String, String> variableIdentMap = new HashMap<String, String>(3);

	private HashMap<String, ISCCarrierSet> oldCarrierSets; // the collection of carrier sets of the seen contexts
	private HashMap<String, ISCConstant> oldConstants; // the collection of constants of the seen contexts
	private ArrayList<IAxiom> oldAxioms; // the axioms of the abstractions

	private ArrayList<IAxiom> oldInvariants; // the axioms of the abstractions
	private HashSet<String> invariantConflictSet = new HashSet<String>(3);
	private HashMap<String, String> invariantIdentMap = new HashMap<String, String>(3);

	private ArrayList<ITheorem> oldTheorems; // the theorems of the abstractions and seen contexts
	private HashSet<String> theoremConflictSet = new HashSet<String>(3);
	private HashMap<String, String> theoremIdentMap = new HashMap<String, String>(3);

	private final HashMap<String, IVariable> newVariables; // the collection of the variables of the machine
	private final ArrayList<IInvariant> newInvariants; // the new axioms of the context
	private final ArrayList<ITheorem> newTheorems; // the new theorems of the context
	
	private HashSet<String> eventConflictSet = new HashSet<String>(3);
	private HashMap<String, String> eventIdentMap = new HashMap<String, String>(3);
	private final HashMap<String,IEvent> newEvents; // the new events
	private final HashMap<String,ArrayList<IGuard>> newGuards; // the guards of the new events
	private final HashMap<String,ArrayList<IAction>> newActions; // the guards of the new events
	
	private HashMap<String,HashSet<String>> guardConflictSet;
	private HashMap<String,HashMap<String, String>> guardIdentMap = new HashMap<String, HashMap<String, String>>(3);
	private HashMap<String,HashSet<String>> localVariableConflictSet;
	private HashMap<String,HashMap<String, String>> localVariableIdentMap = new HashMap<String, HashMap<String, String>>(3);
	private final HashMap<String, HashMap<String, IVariable>> newLocalVariables; // the collection of the variables of the event
	private final HashMap<String, ITypeEnvironment> localTypeEnvironment;

	public MachineCache(IMachine machine, ISCProblemList problemList) throws RodinDBException {
		super(machine);
		
		variableConflictSet = new HashSet<String>(3);
		variableIdentMap = new HashMap<String, String>(getVariables().length * 4 / 3 + 1);
		parseNames(getVariables(), variableIdentMap, variableConflictSet, problemList);
		
		invariantConflictSet = new HashSet<String>(3);
		invariantIdentMap = new HashMap<String, String>(getInvariants().length * 4 / 3 + 1);
		parseNames(getInvariants(), invariantIdentMap, invariantConflictSet, problemList);
		
		theoremConflictSet = new HashSet<String>(3);
		theoremIdentMap = new HashMap<String, String>(getTheorems().length * 4 / 3 + 1);
		parseNames(getTheorems(), theoremIdentMap, theoremConflictSet, problemList);

		eventConflictSet = new HashSet<String>(3);
		eventIdentMap = new HashMap<String, String>(getEvents().length * 4 / 3 + 1);
		parseNames(getEvents(), eventIdentMap, eventConflictSet, problemList);

		newTheorems = new ArrayList<ITheorem>(getTheorems().length);
		newVariables = new HashMap<String, IVariable>(getVariables().length * 4 / 3 + 1);
		newInvariants = new ArrayList<IInvariant>(getInvariants().length);
		newEvents = new HashMap<String,IEvent>(getEvents().length * 4 / 3 + 1);
		newGuards = new HashMap<String,ArrayList<IGuard>>(getEvents().length * 4 / 3 + 1);
		newActions = new HashMap<String,ArrayList<IAction>>(getEvents().length * 4 / 3 + 1);
		newLocalVariables = new HashMap<String, HashMap<String, IVariable>>(getEvents().length * 4 / 3 + 1);
		guardConflictSet = new HashMap<String,HashSet<String>>();
		localVariableConflictSet = new HashMap<String,HashSet<String>>();
		localTypeEnvironment = new HashMap<String, ITypeEnvironment>(getEvents().length * 4 / 3 + 1);
		for(IEvent event : getEvents()) {
			String name = event.getElementName();
			
			HashSet<String> gdConflictSet = new HashSet<String>(3);
			guardConflictSet.put(name, gdConflictSet);
			HashMap<String, String> gdIdentMap = new HashMap<String, String>(5);
			guardIdentMap.put(name, gdIdentMap);
			parseNames(event.getGuards(), gdIdentMap, gdConflictSet, problemList);
			
			HashSet<String> lvConflictSet = new HashSet<String>(3);
			localVariableConflictSet.put(name, lvConflictSet);
			HashMap<String, String> lvIdentMap = new HashMap<String, String>(5);
			localVariableIdentMap.put(name, lvIdentMap);
			parseNames(getVariables(event), lvIdentMap, lvConflictSet, problemList);
			
//			newLocalVariables.put(name, new HashMap<String, IVariable>());
			
			ArrayList<IGuard> nGuards = new ArrayList<IGuard>(getGuards(event).length);
			newGuards.put(name, nGuards);
			
			ArrayList<IAction> nActions = new ArrayList<IAction>(getActions(event).length);
			newActions.put(name, nActions);
			
			HashMap<String, IVariable> nVariables = new HashMap<String, IVariable>(getVariables(event).length * 4 / 3 + 1);
			newLocalVariables.put(name, nVariables);
			
			localTypeEnvironment.put(name, getFactory().makeTypeEnvironment());
		}
		
		oldVariables = new HashMap<String, IVariable>();
		
		oldInvariants = new ArrayList<IAxiom>();
		
		getSeenContext();
	}		
	
	private void getOldTypeEnvironment(ISCContext context) throws RodinDBException {
		for(ISCCarrierSet identifier : context.getSCCarrierSets()) {
			String name = identifier.getElementName();
			String type = identifier.getContents();
			IParseResult result = getFactory().parseType(type);
			assert result.isSuccess();
			getTypeEnvironment().addName(name, result.getParsedType());
		}
		for(ISCConstant identifier : context.getSCConstants()) {
			String name = identifier.getElementName();
			String type = identifier.getContents();
			IParseResult result = getFactory().parseType(type);
			assert result.isSuccess();
			getTypeEnvironment().addName(name, result.getParsedType());
		}
	}
	
	private void getSeenContext() throws RodinDBException {
		oldCarrierSets = new HashMap<String, ISCCarrierSet>();
		oldConstants = new HashMap<String, ISCConstant>();
		oldAxioms = new ArrayList<IAxiom>();
		oldTheorems = new ArrayList<ITheorem>();
		
		ISCContext[] sees = file.getSeenContexts();
		if (sees.length == 0)
			return;
		ISCContext context = sees[0];
		if (! context.exists())
			return;
		
		getOldTypeEnvironment(context);
		
		for(ISCCarrierSet carrierSet : context.getSCCarrierSets()) {
			oldCarrierSets.put(carrierSet.getElementName(), carrierSet);
		}
		
		for(ISCConstant constant : context.getSCConstants()) {
			oldConstants.put(constant.getElementName(), constant);
		}
		
		oldAxioms = new ArrayList<IAxiom>(Arrays.asList(context.getAxioms()));
		oldTheorems = new ArrayList<ITheorem>(Arrays.asList(context.getTheorems()));
	}
	
	private IVariable[] variables = null;
	public IVariable[] getVariables() throws RodinDBException {
		if(variables == null) {
			variables = file.getVariables();
		}
		return variables;
	}
	
	private ITheorem[] theorems = null;
	public ITheorem[] getTheorems() throws RodinDBException {
		if(theorems == null) {
			theorems = file.getTheorems();
		}
		return theorems;
	}

	private IInvariant[] invariants = null;
	public IInvariant[] getInvariants() throws RodinDBException {
		if(invariants == null) {
			invariants = file.getInvariants();
		}
		return invariants;
	}

	private IEvent[] events = null;
	public IEvent[] getEvents() throws RodinDBException {
		if(events == null) {
			events = file.getEvents();
		}
		return events;
	}

	private HashMap<String, IVariable[]> localVariables = null;
	public IVariable[] getVariables(IEvent event) throws RodinDBException {
		if(localVariables == null) {
			localVariables = new HashMap<String, IVariable[]>(getEvents().length * 4 / 3 + 1);
		}
		String name = event.getElementName();
		IVariable[] vr = localVariables.get(name);
		if(vr == null) {
			vr = event.getVariables();
			localVariables.put(name, vr);
		}
		return vr;
	}

	private HashMap<String, IGuard[]> guards = null;
	public IGuard[] getGuards(IEvent event) throws RodinDBException {
		if(guards == null) {
			guards = new HashMap<String, IGuard[]>(getEvents().length * 4 / 3 + 1);
		}
		String name = event.getElementName();
		IGuard[] gd = guards.get(name);
		if(gd == null) {
			gd = event.getGuards();
			guards.put(name, gd);
		}
		return gd;
	}

	private HashMap<String, IAction[]> actions = null;
	public IAction[] getActions(IEvent event) throws RodinDBException {
		if(actions == null) {
			actions = new HashMap<String, IAction[]>(getEvents().length * 4 / 3 + 1);
		}
		String name = event.getElementName();
		IAction[] act = actions.get(name);
		if(act == null) {
			act = event.getActions();
			actions.put(name, act);
		}
		return act;
	}

	/**
	 * @return Returns the newVariables.
	 */
	public HashMap<String, IVariable> getNewVariables() {
		return newVariables;
	}
	
	/**
	 * @return Returns the variableConflictSet.
	 */
	public HashSet<String> getVariableConflictSet() {
		return variableConflictSet;
	}
	
	/**
	 * @return Returns the variableIdentMap.
	 */
	public HashMap<String, String> getVariableIdentMap() {
		return variableIdentMap;
	}

	/**
	 * @return Returns the eventConflictSet.
	 */
	public HashSet<String> getEventConflictSet() {
		return eventConflictSet;
	}

	/**
	 * @return Returns the eventIdentMap.
	 */
	public HashMap<String, String> getEventIdentMap() {
		return eventIdentMap;
	}

	/**
	 * @return Returns the invariantConflictSet.
	 */
	public HashSet<String> getInvariantConflictSet() {
		return invariantConflictSet;
	}

	/**
	 * @return Returns the invariantIdentMap.
	 */
	public HashMap<String, String> getInvariantIdentMap() {
		return invariantIdentMap;
	}

	/**
	 * @return Returns the newInvariants.
	 */
	public ArrayList<IInvariant> getNewInvariants() {
		return newInvariants;
	}

	/**
	 * @return Returns the newTheorems.
	 */
	public ArrayList<ITheorem> getNewTheorems() {
		return newTheorems;
	}

	/**
	 * @return Returns the oldAxioms.
	 */
	public ArrayList<IAxiom> getOldAxioms() {
		return oldAxioms;
	}

	/**
	 * @return Returns the olderVariables.
	 */
	public HashMap<String, IVariable> getOlderVariables() {
		return olderVariables;
	}

	/**
	 * @return Returns the oldInvariants.
	 */
	public ArrayList<IAxiom> getOldInvariants() {
		return oldInvariants;
	}

	/**
	 * @return Returns the oldTheorems.
	 */
	public ArrayList<ITheorem> getOldTheorems() {
		return oldTheorems;
	}

	/**
	 * @return Returns the oldVariables.
	 */
	public HashMap<String, IVariable> getOldVariables() {
		return oldVariables;
	}

	/**
	 * @return Returns the theoremConflictSet.
	 */
	public HashSet<String> getTheoremConflictSet() {
		return theoremConflictSet;
	}

	/**
	 * @return Returns the theoremIdentMap.
	 */
	public HashMap<String, String> getTheoremIdentMap() {
		return theoremIdentMap;
	}

	/**
	 * @return Returns the oldCarrierSets.
	 */
	public HashMap<String, ISCCarrierSet> getOldCarrierSets() {
		return oldCarrierSets;
	}

	/**
	 * @return Returns the oldConstants.
	 */
	public HashMap<String, ISCConstant> getOldConstants() {
		return oldConstants;
	}

	/**
	 * @return Returns the newEvents.
	 */
	public HashMap<String, IEvent> getNewEvents() {
		return newEvents;
	}

	/**
	 * @return Returns the newLocalVariables.
	 */
	public HashMap<String, IVariable> getLocalVariables(IEvent event) {
		return newLocalVariables.get(event.getElementName());
	}

	/**
	 * @return Returns the newActions.
	 */
	public HashMap<String, ArrayList<IAction>> getNewActions() {
		return newActions;
	}

	/**
	 * @return Returns the newGuards.
	 */
	public HashMap<String, ArrayList<IGuard>> getNewGuards() {
		return newGuards;
	}

	/**
	 * @return Returns the newLocalVariables.
	 */
	public HashMap<String, HashMap<String, IVariable>> getNewLocalVariables() {
		return newLocalVariables;
	}

	/**
	 * @return Returns the localVariableIdentMap.
	 */
	public HashMap<String, String> getLocalVariableIdentMap(IEvent event) {
		return localVariableIdentMap.get(event.getElementName());
	}

	/**
	 * @return Returns the localTypeEnvironment.
	 */
	public HashMap<String, ITypeEnvironment> getLocalTypeEnvironment() {
		return localTypeEnvironment;
	}

	/**
	 * @return Returns the localVariableConflictSet.
	 */
	public HashSet<String> getLocalVariableConflictSet(IEvent event) {
		return localVariableConflictSet.get(event.getElementName());
	}

	/**
	 * @return Returns the guardConflictSet.
	 */
	public HashSet<String> getGuardConflictSet(IEvent event) {
		return guardConflictSet.get(event.getElementName());
	}
	

}
