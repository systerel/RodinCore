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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextFile;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAssignmentElement;
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
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.tests.EventBTest;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BasicSCTest extends EventBTest {
	
	public BasicSCTest() {
		super();
	}

	public BasicSCTest(String name) {
		super(name);
	}

	private static IConvergenceElement.Convergence getConvergence(ISCEvent event) throws RodinDBException {
		return event.getConvergence();
	}
	
	public static void isOrdinary(ISCEvent event) throws RodinDBException {
		assertEquals("event should be ordinary", 
				IConvergenceElement.Convergence.ORDINARY, getConvergence(event));
	}

	public static void isAnticipated(ISCEvent event) throws RodinDBException {
		assertEquals("event should be anticipated", 
				IConvergenceElement.Convergence.ANTICIPATED, getConvergence(event));
	}

	public static void isConvergent(ISCEvent event) throws RodinDBException {
		assertEquals("event should be convergent", 
				IConvergenceElement.Convergence.CONVERGENT, getConvergence(event));
	}

	public Set<String> getIdentifierNameSet(ISCIdentifierElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCIdentifierElement element : elements)
			names.add(element.getIdentifierString());
		return names;
	}

	public Set<String> getRefinedNameSet(ISCRefinesEvent[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCRefinesEvent element : elements)
			names.add(element.getAbstractSCEvent().getLabel());
		return names;
	}

	public Set<String> getLabelNameSet(ILabeledElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ILabeledElement element : elements)
			names.add(element.getLabel());
		return names;
	}

	public Hashtable<String, String> getActionTable(ISCAction[] elements) throws RodinDBException {
		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
		for (ISCAction action : elements)
			table.put(action.getLabel(), action.getAssignmentString());
		return table;
	}

	public Hashtable<String, String> getPredicateTable(ISCPredicateElement[] elements) throws RodinDBException {
		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
		for (ISCPredicateElement predicate : elements)
			table.put(((ILabeledElement) predicate).getLabel(), predicate.getPredicateString());
		return table;
	}

	public Hashtable<String, String> getAssignmentTable(ISCAssignmentElement[] elements) throws RodinDBException {
		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
		for (ISCAssignmentElement assignment : elements)
			table.put(((ILabeledElement) assignment).getLabel(), assignment.getAssignmentString());
		return table;
	}

	public Set<String> getSCPredicateSet(ISCPredicateElement[] elements) throws RodinDBException {
		HashSet<String> predicates = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCPredicateElement element : elements)
			predicates.add(element.getPredicateString());
		return predicates;
	}

	public Expression expressionFromString(String expression) {
		Expression ee = factory.parseExpression(expression).getParsedExpression();
		return ee;
	}
	
	public Predicate predicateFromString(String predicate) {
		Predicate pp = factory.parsePredicate(predicate).getParsedPredicate();
		return pp;
	}
	
	public Assignment assignmentFromString(String assignment) {
		Assignment aa = factory.parseAssignment(assignment).getParsedAssignment();
		return aa;
	}

	public Type typeFromString(String type) {
		Type tt = factory.parseType(type).getParsedType();
		return tt;
	}

	public void containsPredicates(
			String type, ITypeEnvironment environment, String[] labels, String[] strings, 
			ISCPredicateElement[] predicateElements) throws RodinDBException {
		assert labels.length == strings.length;
		assertEquals("wrong number [" + type + "]", strings.length, predicateElements.length);
		
		if (predicateElements.length == 0)
			return;
		
		Hashtable<String, String> table = getPredicateTable(predicateElements);
		
		for (int k=0; k<labels.length; k++) {
			String predicate = table.get(labels[k]);
			assertNotNull("should contain" + type + " " + labels[k], predicate);
			assertEquals("wrong " + type, 
					getNormalizedPredicate(strings[k], environment), 
					predicate);
		}
	}

	public void containsAssignments(
			String type, ITypeEnvironment environment, String[] labels, String[] strings, 
			ISCAssignmentElement[] assignmentElements) throws RodinDBException {
		assert labels.length == strings.length;
		Hashtable<String, String> table = getAssignmentTable(assignmentElements);
		for (int k=0; k<labels.length; k++) {
			String assignment = table.get(labels[k]);
			assertNotNull("should contain" + type + " " + labels[k], assignment);
			assertEquals("wrong " + type, 
					getNormalizedAssignment(strings[k], environment), 
					assignment);
		}
	}

	public void containsGuards(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCGuard[] guards = event.getSCGuards();
		
		containsPredicates("guard", environment, labels, strings, guards);
	}

	public void containsWitnesses(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCWitness[] witnesses = event.getSCWitnesses();
		
		containsPredicates("witness", environment, labels, strings, witnesses);
	}

	public void containsAxioms(ISCContext context, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCAxiom[] axioms = context.getSCAxioms();
		
		containsPredicates("axiom", environment, labels, strings, axioms);
	}

	public void containsTheorems(ISCContextFile file, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCTheorem[] theorems = file.getSCTheorems();
		
		containsTheorems(theorems, environment, labels, strings);
	}

	public void containsTheorems(ISCMachineFile file, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCTheorem[] theorems = file.getSCTheorems();
		
		containsTheorems(theorems, environment, labels, strings);
	}

	private void containsTheorems(ISCTheorem[] theorems, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		containsPredicates("theorem", environment, labels, strings, theorems);
	}

	public void containsInvariants(ISCMachineFile file, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCInvariant[] invariants = file.getSCInvariants();
		
		containsPredicates("invariant", environment, labels, strings, invariants);
	}

	public ISCInternalContext[] getInternalContexts(ISCContextFile file, int num) throws RodinDBException {
		ISCInternalContext[] contexts = file.getAbstractSCContexts();
		
		assertEquals("wrong number of internal contexts", num, contexts.length);
		return contexts;
	}

	public ISCEvent[] getSCEvents(ISCMachineFile file, String...strings) throws RodinDBException {
		ISCEvent[] events = file.getSCEvents();
		
		assertEquals("wrong number of events", strings.length, events.length);
		
		if (strings.length == 0)
			return events;
		
		Set<String> nameSet = getLabelNameSet(events);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
		
		return events;
	}

	public ISCInternalContext[] getInternalContexts(ISCMachineFile file, int num) throws RodinDBException {
		ISCInternalContext[] contexts = file.getSCSeenContexts();
		
		assertEquals("wrong number of internal contexts", num, contexts.length);
		return contexts;
	}

	public void containsConstants(ISCContext context, String... strings) throws RodinDBException {
		ISCConstant[] constants = context.getSCConstants();
		
		assertEquals("wrong number of constants", strings.length, constants.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(constants);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public void containsEvents(ISCMachineFile file, String... strings) throws RodinDBException {
		ISCEvent[] events = file.getSCEvents();
		
		assertEquals("wrong number of events", strings.length, events.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getLabelNameSet(events);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void containsActions(ISCEvent event, ITypeEnvironment environment, String[] actionLabels, String[] actions) throws RodinDBException {
		ISCAction[] acts =  event.getSCActions();
		
		assertEquals("wrong number of actions", actions.length, acts.length);
		
		if (actions.length == 0)
			return;
		
		containsAssignments("action", environment, actionLabels, actions, acts);
		
	}

	public void containsVariables(ISCEvent event, String... strings) throws RodinDBException {
		ISCVariable[] variables = event.getSCVariables();
		
		assertEquals("wrong number of variables", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void containsMarkers(IRodinFile rodinFile, boolean yes) throws CoreException {
		IFile file = rodinFile.getResource();
		IMarker[] markers = 
			file.findMarkers(RodinMarkerUtil.RODIN_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		
		if (yes)
			assertTrue("should contain markers", markers.length != 0);
		else
			assertEquals("should not contain markers", 0, markers.length);
	}

	public void refinesEvents(ISCEvent event, String... strings) throws RodinDBException {
		ISCRefinesEvent[] variables = event.getSCRefinesClauses();
		
		assertEquals("wrong number of refines clauses", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getRefinedNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public void containsVariables(ISCMachineFile file, String... strings) throws RodinDBException {
		ISCVariable[] variables = file.getSCVariables();
		
		assertEquals("wrong number of variables", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public void containsVariant(ISCMachineFile file, ITypeEnvironment environment, String... strings) throws RodinDBException {
		assert strings.length <= 1;
		ISCVariant[] variants = file.getSCVariants();
		
		
		
		assertEquals("wrong number of variants", strings.length, variants.length);
		
		if (strings.length == 0)
			return;
		
		String vs = variants[0].getExpressionString();
		String exp = getNormalizedExpression(strings[0], environment);
				
		assertEquals("wrong variant", exp, vs);
	}

	public void containsCarrierSets(ISCContext context, String... strings) throws RodinDBException {
		ISCCarrierSet[] sets = context.getSCCarrierSets();
		
		assertEquals("wrong number of constants", strings.length, sets.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(sets);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	// for generic tests:
	
	public ISCMachineFile getSCComponent(IMachineFile rodinFile) throws RodinDBException {
		return rodinFile.getSCMachineFile();
	}
	
	public ISCContextFile getSCComponent(IContextFile rodinFile) throws RodinDBException {
		return rodinFile.getSCContextFile();
	}
	
	public void containsIdents(ISCContextFile rodinFile, String...strings) throws RodinDBException {
		containsConstants(rodinFile, strings);
	}
	
	public void containsIdents(ISCMachineFile rodinFile, String...strings) throws RodinDBException {
		containsVariables(rodinFile, strings);
	}
	
	public void containsNonTheorems(ISCContextFile context, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		containsAxioms(context, environment, labels, strings);
	}

	public void containsNonTheorems(ISCMachineFile machine, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		containsInvariants(machine, environment, labels, strings);
	}

}
