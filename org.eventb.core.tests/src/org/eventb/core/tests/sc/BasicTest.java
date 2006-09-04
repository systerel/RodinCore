/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests.sc;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCWitness;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BasicTest extends TestCase {
	
	protected FormulaFactory factory = FormulaFactory.getDefault();

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	private static int alloc;
	
	private static String getUniqueName() {
		if (alloc == Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		return "x" + alloc++;
	}

	public BasicTest() {
		super();
	}

	public BasicTest(String name) {
		super(name);
	}

	protected IRodinProject rodinProject;

	protected ITypeEnvironment emptyEnv = factory.makeTypeEnvironment();

	protected void runBuilder() throws CoreException {
		rodinProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	protected ISCContextFile runSC(IContextFile context) throws CoreException {
		runBuilder();
		return context.getSCContextFile();
	}

	protected ISCMachineFile runSC(IMachineFile machine) throws CoreException {
		runBuilder();
		return machine.getSCMachineFile();
	}

	protected IContextFile createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		return (IContextFile) rodinProject.createRodinFile(fileName, true, null);
	}

//	protected ISCContextFile createSCContext(String bareName) throws RodinDBException {
//		final String fileName = EventBPlugin.getSCContextFileName(bareName);
//		return (ISCContextFile) rodinProject.createRodinFile(fileName, true, null);
//	}

	protected IMachineFile createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		return (IMachineFile) rodinProject.createRodinFile(fileName, true, null);
	}

//	protected ISCMachineFile createSCMachine(String bareName) throws RodinDBException {
//		final String fileName = EventBPlugin.getSCMachineFileName(bareName);
//		return (ISCMachineFile) rodinProject.createRodinFile(fileName, true, null);
//	}

	public static void addAxioms(
			IRodinFile rodinFile, 
			String[] names, 
			String[] axioms) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IAxiom axiom = 
				(IAxiom) rodinFile.createInternalElement(IAxiom.ELEMENT_TYPE, 
					getUniqueName(), null, null);
			axiom.setPredicateString(axioms[i]);
			axiom.setLabel(names[i], null);
		}
	}
	
//	public static void addSCAxioms(
//			IRodinFile rodinFile, 
//			String[] names, 
//			Predicate[] axioms, 
//			IInternalParent parent) throws RodinDBException {
//		IInternalParent element = null;
//		if(parent == null) 
//			element = rodinFile;
//		else
//			element = parent;
//		for(int i=0; i<names.length; i++) {
//			ISCAxiom axiom = 
//				(ISCAxiom) element.createInternalElement(ISCAxiom.ELEMENT_TYPE, 
//						getUniqueName(), null, null);
//			axiom.setPredicate(axioms[i]);
//			axiom.setLabel(names[i], null);
//		}
//	}
	
	public static void addCarrierSets(IRodinFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			ICarrierSet set = 
				(ICarrierSet) rodinFile.createInternalElement(ICarrierSet.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			set.setIdentifierString(name);
		}
			
	}
	
	public static void addConstants(IRodinFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			IConstant constant = 
				(IConstant) rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			constant.setIdentifierString(name);
		}
	}

	public static void addEventRefines(IEvent event, String refName, String name) throws RodinDBException {
		IRefinesEvent refines = (IRefinesEvent) event.createInternalElement(IRefinesEvent.ELEMENT_TYPE, refName, null, null);
		refines.setAbstractEventName(name);
	}

	public static void addEventWitnesses(IEvent event, String[] labels, String[] predicates) throws RodinDBException {
		assert labels.length == predicates.length;
		for (int i=0; i<labels.length; i++) {
			IWitness witness = (IWitness) event.createInternalElement(IWitness.ELEMENT_TYPE, getUniqueName(), null, null);
			witness.setLabel(labels[i], null);
			witness.setPredicateString(predicates[i]);
		}
	}

	public static IEvent addEvent(IRodinFile rodinFile, 
				String name,
				String[] vars,
				String[] guardNames,
				String[] guards,
				String[] actionNames,
				String[] actions
	) throws RodinDBException {
		IEvent event = 
			(IEvent) rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, 
					getUniqueName(), null, null);
		event.setLabel(name, null);
		for(int i=0; i<vars.length; i++) {
			IVariable variable = 
				(IVariable) event.createInternalElement(IVariable.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			variable.setIdentifierString(vars[i]);
			
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = 
				(IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			guard.setPredicateString(guards[i]);
			guard.setLabel(guardNames[i], null);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = 
				(IAction) event.createInternalElement(IAction.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			action.setAssignmentString(actions[j]);
			action.setLabel(actionNames[j], null);
		}
		return event;
	}

	public static void addInvariants(IRodinFile rodinFile, String[] names, String[] invariants) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInvariant invariant = 
				(IInvariant) rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			invariant.setPredicateString(invariants[i]);
			invariant.setLabel(names[i], null);
		}
	}

//	public static void addSCInvariants(IRodinFile rodinFile, String[] names, Predicate[] invariants) throws RodinDBException {
//		for(int i=0; i<names.length; i++) {
//			ISCInvariant invariant = 
//				(ISCInvariant) rodinFile.createInternalElement(ISCInvariant.ELEMENT_TYPE, 
//						getUniqueName(), null, null);
//			invariant.setPredicate(invariants[i]);
//			invariant.setLabel(names[i], null);
//		}
//	}
//
//	public static ISCInternalContext addSCInternalContext(IRodinFile file, String name) throws RodinDBException {
//		return (ISCInternalContext) file.createInternalElement(ISCInternalContext.ELEMENT_TYPE, name, null, null);
//	}
//
//	public static void addSCCarrierSets(IRodinFile rodinFile, String[] names, Type[] types, ISCInternalContext internalContext) throws RodinDBException {
//		IInternalParent parent = null;
//		if(internalContext == null)
//			parent = rodinFile;
//		else
//			parent = internalContext;
//		for(int i=0; i<names.length; i++) {
//			SCCarrierSet identifier = 
//				(SCCarrierSet) parent.createInternalElement(ISCCarrierSet.ELEMENT_TYPE, 
//						names[i], null, null);
//			identifier.setType(types[i]);
//		}
//	}
//
//	public static void addSCConstants(IRodinFile rodinFile, String[] names, Type[] types, ISCInternalContext internalContext) throws RodinDBException {
//		IInternalParent parent = null;
//		if(internalContext == null)
//			parent = rodinFile;
//		else
//			parent = internalContext;
//		for(int i=0; i<names.length; i++) {
//			SCConstant identifier = 
//				(SCConstant) parent.createInternalElement(ISCConstant.ELEMENT_TYPE, 
//						names[i], null, null);
//			identifier.setType(types[i]);
//		}
//	}
//
//	public static void addSCEvent(IRodinFile rodinFile, 
//				String name,
//				String[] vars,
//				Type[] types,
//				String[] guardNames,
//				Predicate[] guards,
//				String[] actionNames,
//				Assignment[] actions
//	) throws RodinDBException {
//		ISCEvent event = 
//			(ISCEvent) rodinFile.createInternalElement(ISCEvent.ELEMENT_TYPE, 
//					getUniqueName(), null, null);
//		event.setLabel(name, null);
//		for(int i=0; i<vars.length; i++) {
//			ISCVariable variable = 
//				(ISCVariable) event.createInternalElement(ISCVariable.ELEMENT_TYPE, 
//						vars[i], null, null);
//			variable.setType(types[i]);
//		}
//		for(int i=0; i<guards.length; i++) {
//			ISCGuard guard = 
//				(ISCGuard) event.createInternalElement(ISCGuard.ELEMENT_TYPE, 
//						getUniqueName(), null, null);
//			guard.setPredicate(guards[i]);
//			guard.setLabel(guardNames[i], null);
//		}
//		for(int j=0; j<actions.length; j++) {
//			ISCAction action = 
//				(ISCAction) event.createInternalElement(ISCAction.ELEMENT_TYPE, 
//						getUniqueName(), null, null);
//			action.setAssignment(actions[j]);
//			action.setLabel(actionNames[j], null);
//		}
//	}
//
//	public static void addSCVariables(IRodinFile rodinFile, String[] names, Type[] types) throws RodinDBException {
//		for(int i=0; i<names.length; i++) {
//			SCVariable identifier = 
//				(SCVariable) rodinFile.createInternalElement(ISCVariable.ELEMENT_TYPE, 
//						names[i], null, null);
//			identifier.setType(types[i]);
//		}
//	}

	public static void addMachineSees(IRodinFile rodinFile, String refName, String name) throws RodinDBException {
		ISeesContext sees = (ISeesContext) rodinFile.createInternalElement(ISeesContext.ELEMENT_TYPE, refName, null, null);
		sees.setSeenContextName(name);
	}

	public static void addMachineRefines(IRodinFile rodinFile, String refName, String name) throws RodinDBException {
		IRefinesMachine refines = (IRefinesMachine) rodinFile.createInternalElement(IRefinesMachine.ELEMENT_TYPE, refName, null, null);
		refines.setAbstractMachineName(name);
	}

	public static void addContextExtends(IRodinFile rodinFile, String refName, String name) throws RodinDBException {
		IExtendsContext extendsContext = (IExtendsContext) rodinFile.createInternalElement(IExtendsContext.ELEMENT_TYPE, refName, null, null);
		extendsContext.setAbstractContextName(name);
	}

	public static void addTheorems(IRodinFile rodinFile, String[] names, String[] theorems) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = 
				(ITheorem) rodinFile.createInternalElement(ITheorem.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			theorem.setPredicateString(theorems[i]);
			theorem.setLabel(names[i], null);
		}
	}

//	public static void addSCTheorems(IRodinFile rodinFile, String[] names, Predicate[] theorems, IInternalParent parent) throws RodinDBException {
//		IInternalParent element = null;
//		if(parent == null) 
//			element = rodinFile;
//		else
//			element = parent;
//		for(int i=0; i<names.length; i++) {
//			ISCTheorem theorem = 
//				(ISCTheorem) element.createInternalElement(ISCTheorem.ELEMENT_TYPE, 
//						getUniqueName(), null, null);
//			theorem.setPredicate(theorems[i]);
//			theorem.setLabel(names[i], null);
//		}
//	}

	public static void addVariables(IRodinFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			IVariable variable = 
				(IVariable) rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, 
						getUniqueName(), null, null);
			variable.setIdentifierString(name);
		}
	}

	public static String[] makeSList(String...strings) {
		return strings;
	}

	public static Type[] makeTList(Type...types) {
		return types;
	}
	
	public Set<String> getIdentifierNameSet(ISCIdentifierElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCIdentifierElement element : elements)
			names.add(element.getIdentifierName());
		return names;
	}

	public Set<String> getRefinedNameSet(ISCRefinesEvent[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCRefinesEvent element : elements)
			names.add(element.getAbstractSCEvent().getLabel(null));
		return names;
	}

	public Set<String> getLabelNameSet(ILabeledElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ILabeledElement element : elements)
			names.add(element.getLabel(null));
		return names;
	}

	public Hashtable<String, String> getActionTable(ISCAction[] elements) throws RodinDBException {
		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
		for (ISCAction action : elements)
			table.put(action.getLabel(null), action.getAssignmentString());
		return table;
	}

	public Hashtable<String, String> getPredicateTable(ISCPredicateElement[] elements) throws RodinDBException {
		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
		for (ISCPredicateElement predicate : elements)
			table.put(((ILabeledElement) predicate).getLabel(null), predicate.getPredicateString());
		return table;
	}

	public Set<String> getSCPredicateSet(ISCPredicateElement[] elements) throws RodinDBException {
		HashSet<String> predicates = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCPredicateElement element : elements)
			predicates.add(element.getPredicateString());
		return predicates;
	}

	protected Expression expressionFromString(String expression) {
		Expression ee = factory.parseExpression(expression).getParsedExpression();
		return ee;
	}
	
	protected Predicate predicateFromString(String predicate) {
		Predicate pp = factory.parsePredicate(predicate).getParsedPredicate();
		return pp;
	}
	
	protected Assignment assignmentFromString(String assignment) {
		Assignment aa = factory.parseAssignment(assignment).getParsedAssignment();
		return aa;
	}

	protected Type typeFromString(String type) {
		Type tt = factory.parseType(type).getParsedType();
		return tt;
	}

	protected String getNormalizedPredicate(String input, ITypeEnvironment environment) {
		Predicate pred = factory.parsePredicate(input).getParsedPredicate();
		pred.typeCheck(environment);
		assertTrue(pred.isTypeChecked());
		return pred.toStringWithTypes();
	}
	
	protected String getNormalizedAssignment(String input, ITypeEnvironment environment) {
		Assignment assn = factory.parseAssignment(input).getParsedAssignment();
		assn.typeCheck(environment);
		assertTrue(assn.isTypeChecked());
		return assn.toStringWithTypes();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		// Create a new project
		IProject project = workspace.getRoot().getProject("P");
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		rodinProject = RodinCore.create(project);
		
		// Turn off automatic provers
		AutoProver.disable();
	}
	
	protected void tearDown() throws Exception {
		rodinProject.getProject().delete(true, true, null);
		super.tearDown();
	}

	protected void numberOfAxioms(ISCContextFile file, int num) throws RodinDBException {
		ISCAxiom[] axioms = file.getSCAxioms();
		
		assertEquals("wrong number of axioms", num, axioms.length);
	}

	protected void numberOfInvariants(ISCMachineFile file, int num) throws RodinDBException {
		ISCInvariant[] invariants = file.getSCInvariants();
		
		assertEquals("wrong number of invariants", num, invariants.length);
	}

	protected void containsGuards(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCGuard[] guards = event.getSCGuards();
		
		assertEquals("wrong number of guards", strings.length, guards.length);
		
		if (strings.length == 0)
			return;
		
		Hashtable<String, String> table = getPredicateTable(guards);
		tableContainsPredicates("guard", environment, labels, strings, table);
	}

	protected void containsWitnesses(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCWitness[] witnesses = event.getSCWitnesses();
		
		assertEquals("wrong number of witnesses", strings.length, witnesses.length);
		
		if (strings.length == 0)
			return;
		
		Hashtable<String, String> table = getPredicateTable(witnesses);
		tableContainsPredicates("witness", environment, labels, strings, table);
	}

	protected void containsAxioms(ISCContext context, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCAxiom[] axioms = context.getSCAxioms();
		
		assertEquals("wrong number of axioms", strings.length, axioms.length);
		
		if (strings.length == 0)
			return;
		
		Hashtable<String, String> table = getPredicateTable(axioms);
		tableContainsPredicates("axiom", environment, labels, strings, table);
	}

	protected void containsTheorems(ISCContextFile file, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCTheorem[] theorems = file.getSCTheorems();
		
		containsTheorems(theorems, environment, labels, strings);
	}

	protected void containsTheorems(ISCMachineFile file, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCTheorem[] theorems = file.getSCTheorems();
		
		containsTheorems(theorems, environment, labels, strings);
	}

	private void containsTheorems(ISCTheorem[] theorems, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		assertEquals("wrong number of theorems", strings.length, theorems.length);
		
		if (strings.length == 0)
			return;
		
		Hashtable<String, String> table = getPredicateTable(theorems);
		tableContainsPredicates("theorem", environment, labels, strings, table);
	}

	protected void containsInvariants(ISCMachineFile file, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCInvariant[] invariants = file.getSCInvariants();
		
		assertEquals("wrong number of invariant", strings.length, invariants.length);
		
		if (strings.length == 0)
			return;
		
		Hashtable<String, String> table = getPredicateTable(invariants);
		tableContainsPredicates("invariant", environment, labels, strings, table);
	}

	protected ISCInternalContext[] getInternalContexts(ISCContextFile file, int num) throws RodinDBException {
		ISCInternalContext[] contexts = file.getAbstractSCContexts();
		
		assertEquals("wrong number of internal contexts", num, contexts.length);
		return contexts;
	}

	protected ISCEvent[] getSCEvents(ISCMachineFile file, String...strings) throws RodinDBException {
		ISCEvent[] events = file.getSCEvents();
		
		assertEquals("wrong number of events", strings.length, events.length);
		
		if (strings.length == 0)
			return events;
		
		Set<String> nameSet = getLabelNameSet(events);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
		
		return events;
	}

	protected ISCInternalContext[] getInternalContexts(ISCMachineFile file, int num) throws RodinDBException {
		ISCInternalContext[] contexts = file.getSCInternalContexts();
		
		assertEquals("wrong number of internal contexts", num, contexts.length);
		return contexts;
	}

	protected void containsConstants(ISCContext context, String... strings) throws RodinDBException {
		ISCConstant[] constants = context.getSCConstants();
		
		assertEquals("wrong number of constants", strings.length, constants.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(constants);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	protected void containsEvents(ISCMachineFile file, String... strings) throws RodinDBException {
		ISCEvent[] events = file.getSCEvents();
		
		assertEquals("wrong number of events", strings.length, events.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getLabelNameSet(events);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	protected void containsActions(ISCEvent event, ITypeEnvironment environment, String[] actionNames, String[] actions) throws RodinDBException {
		ISCAction[] acts =  event.getSCActions();
		
		assertEquals("wrong number of actions", actions.length, acts.length);
		
		if (actions.length == 0)
			return;
		
		Hashtable<String, String> table = getActionTable(acts);
		
		tableContainsAssignments("action", environment, actionNames, actions, table);
	}

	private void tableContainsAssignments(String type, ITypeEnvironment environment, String[] labels, String[] strings, Hashtable<String, String> table) {
		for (int i=0; i<strings.length; i++) {
			String action = table.get(labels[i]);
			String naction = getNormalizedAssignment(strings[i], environment);
			assertNotNull("should contain" + type + " " + labels[i], action);
			assertEquals("should be" + type + " " + strings[i], naction, action);
		}
	}
			
	private void tableContainsPredicates(String type, ITypeEnvironment environment, String[] labels, String[] strings, Hashtable<String, String> table) {
		for (int i=0; i<strings.length; i++) {
			String action = table.get(labels[i]);
			String naction = getNormalizedPredicate(strings[i], environment);
			assertNotNull("should contain" + type + " " + labels[i], action);
			assertEquals("should be" + type + " " + strings[i], naction, action);
		}
	}
			
	protected void containsVariables(ISCEvent event, String... strings) throws RodinDBException {
		ISCVariable[] variables = event.getSCVariables();
		
		assertEquals("wrong number of variables", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	protected void refinesEvents(ISCEvent event, String... strings) throws RodinDBException {
		ISCRefinesEvent[] variables = event.getSCRefinesClauses();
		
		assertEquals("wrong number of refines clauses", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getRefinedNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	protected void containsVariables(ISCMachineFile file, String... strings) throws RodinDBException {
		ISCVariable[] variables = file.getSCVariables();
		
		assertEquals("wrong number of variables", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	protected void containsCarrierSets(ISCContext context, String... strings) throws RodinDBException {
		ISCCarrierSet[] sets = context.getSCCarrierSets();
		
		assertEquals("wrong number of constants", strings.length, sets.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(sets);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}


}
