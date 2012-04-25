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
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.rodinp.core.RodinMarkerUtil.RODIN_PROBLEM_MARKER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAccuracyElement;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IDerivedPredicateElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCAssignmentElement;
import org.eventb.core.ISCAxiom;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCExtendsContext;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCSeesContext;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.EventBTest;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 */
public abstract class BasicSCTest extends EventBTest {
	
	@Override
	protected void runBuilder() throws CoreException {
		super.runBuilder();
		for (IEventBRoot root : sourceRoots)
			assertTrue("ill-formed markers", GraphProblemTest.check(root));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sourceRoots.clear();
	}

	@Override
	protected void tearDown() throws Exception {
		sourceRoots.clear();
		super.tearDown();
	}

	private final List<IEventBRoot> sourceRoots = new ArrayList<IEventBRoot>();
	
	@Override
	protected IContextRoot createContext(String bareName) throws RodinDBException {
		IContextRoot root = super.createContext(bareName);
		sourceRoots.add(root);
		addRoot(root.getSCContextRoot());
		return root;
	}

	@Override
	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		IMachineRoot root = super.createMachine(bareName);
		sourceRoots.add(root);
		addRoot(root.getSCMachineRoot());
		return root;
	}

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

	private Set<String> getIdentifierNameSet(ISCIdentifierElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCIdentifierElement element : elements)
			if (element != null)
				names.add(element.getIdentifierString());
		return names;
	}

	private Set<String> getRefinedNameSet(ISCRefinesEvent[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCRefinesEvent element : elements)
			names.add(element.getAbstractSCEvent().getLabel());
		return names;
	}

	private Set<String> getSeenNameSet(ISCSeesContext[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCSeesContext element : elements)
			names.add(element.getSeenSCContext().getComponentName());
		return names;
	}

	private Set<String> getExtendedNameSet(ISCExtendsContext[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCExtendsContext element : elements)
			names.add(element.getAbstractSCContext().getComponentName());
		return names;
	}

	private Set<String> getContextNameSet(ISCContext[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ISCContext element : elements)
			names.add(element.getElementName());
		return names;
	}

	private Set<String> getLabelNameSet(ILabeledElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(ILabeledElement element : elements)
			names.add(element.getLabel());
		return names;
	}

//	private Hashtable<String, String> getActionTable(ISCAction[] elements) throws RodinDBException {
//		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
//		for (ISCAction action : elements)
//			table.put(action.getLabel(), action.getAssignmentString());
//		return table;
//	}

//	private Hashtable<String, String> getPredicateTable(ISCPredicateElement[] elements, boolean[] derived) throws RodinDBException {
//		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
//		for (ISCPredicateElement predicate : elements)
//			table.put(((ILabeledElement) predicate).getLabel(), predicate.getPredicateString());
//		return table;
//	}
//
	private Hashtable<String, String> getAssignmentTable(ISCAssignmentElement[] elements) throws RodinDBException {
		Hashtable<String, String> table = new Hashtable<String, String>(elements.length * 4 / 3 + 1);
		for (ISCAssignmentElement assignment : elements)
			table.put(((ILabeledElement) assignment).getLabel(), assignment.getAssignmentString());
		return table;
	}

//	private Set<String> getSCPredicateSet(ISCPredicateElement[] elements) throws RodinDBException {
//		HashSet<String> predicates = new HashSet<String>(elements.length * 4 / 3 + 1);
//		for(ISCPredicateElement element : elements)
//			predicates.add(element.getPredicateString());
//		return predicates;
//	}

//	private Expression expressionFromString(String expression) {
//		Expression ee = factory.parseExpression(expression).getParsedExpression();
//		return ee;
//	}
//	
//	private Predicate predicateFromString(String predicate) {
//		Predicate pp = factory.parsePredicate(predicate).getParsedPredicate();
//		return pp;
//	}
//	
//	private Assignment assignmentFromString(String assignment) {
//		Assignment aa = factory.parseAssignment(assignment).getParsedAssignment();
//		return aa;
//	}
//
//	private Type typeFromString(String type) {
//		Type tt = factory.parseType(type).getParsedType();
//		return tt;
//	}

	private void containsPredicates(
			String type, ITypeEnvironment environment, String[] labels, String[] strings, 
			boolean[] derived, ISCPredicateElement[] predicateElements) throws RodinDBException {
		assert labels.length == strings.length;
		assertEquals("wrong number [" + type + "]", strings.length, predicateElements.length);
		
		if (predicateElements.length == 0)
			return;
		
		List<String> labelList = getLabelList(predicateElements);
		
		for (int k=0; k<labels.length; k++) {
			int index = labelList.indexOf(labels[k]);
			assertTrue("should contain " + type + " " + labels[k], index != -1);
			String predicate = predicateElements[index].getPredicateString();
			assertEquals("wrong " + type, 
					getNormalizedPredicate(strings[k], environment), 
					predicate);
			if (derived != null) {
				IDerivedPredicateElement derivedElement = (IDerivedPredicateElement) predicateElements[k];
				assertEquals("should " + (derived[k] ? "" : "not") + " be a theorem ", derived[k], derivedElement.isTheorem());
			}
		}
	}

	private List<String> getLabelList(ISCPredicateElement[] predicateElements) throws RodinDBException {
		List<String> list = new ArrayList<String>(predicateElements.length);
		for (ISCPredicateElement element : predicateElements) {
			ILabeledElement labeledElement = (ILabeledElement) element;
			list.add(labeledElement.getLabel());
		}
		return list;
	}

	private void containsAssignments(
			String type, ITypeEnvironment environment, String[] labels, String[] strings, 
			ISCAssignmentElement[] assignmentElements) throws RodinDBException {
		assert labels.length == strings.length;
		Hashtable<String, String> table = getAssignmentTable(assignmentElements);
		for (int k=0; k<labels.length; k++) {
			String assignment = table.get(labels[k]);
			assertNotNull("should contain " + type + " " + labels[k], assignment);
			assertEquals("wrong " + type, 
					getNormalizedAssignment(strings[k], environment), 
					assignment);
		}
	}

	public void containsGuards(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		containsGuards(event, environment, labels, strings, null);
	}
	
	public void containsGuards(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException {
		ISCGuard[] guards = event.getSCGuards();
		
		containsPredicates("guard", environment, labels, strings, derived, guards);
	}

	public void containsWitnesses(ISCEvent event, ITypeEnvironment environment, String[] labels, String[] strings) throws RodinDBException {
		ISCWitness[] witnesses = event.getSCWitnesses();
		
		containsPredicates("witness", environment, labels, strings, null, witnesses);
	}

	public void containsAxioms(ISCContext context, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException {
		ISCAxiom[] axioms = context.getSCAxioms();
		
		containsPredicates("axiom", environment, labels, strings, derived, axioms);
	}

	public void containsInvariants(ISCMachineRoot root, ITypeEnvironment environment, String[] labels, String[] strings, boolean... derived) throws RodinDBException {
		ISCInvariant[] invariants = root.getSCInvariants();
		
		containsPredicates("invariant", environment, labels, strings, derived, invariants);
	}
	
	public ISCInternalContext[] getInternalContexts(ISCContextRoot root, int num) throws RodinDBException {
		final ISCInternalContext[] contexts = root.getAbstractSCContexts();
		checkInternalContexts(num, contexts);
		return contexts;
	}

	private void checkInternalContexts(int num, ISCInternalContext[] contexts)
			throws RodinDBException {

		assertEquals("wrong number of internal contexts", num, contexts.length);

		// Ensure that there is no nested internal context
		for (final ISCInternalContext iCtx: contexts) {
			containsNoContexts(iCtx);
		}
	}

	public ISCEvent getSCEvent(ISCMachineRoot root, String label) throws RodinDBException {
		for (ISCEvent event: root.getSCEvents()) {
			if (label.equals(event.getLabel())) {
				return event;
			}
		}
		fail("No event labelled " + label + " in " + root.getRodinFile());
		return null;
	}

	public ISCEvent[] getSCEvents(ISCMachineRoot root, String...strings) throws RodinDBException {
		ISCEvent[] events = root.getSCEvents();
		
		assertEquals("wrong number of events", strings.length, events.length);
		
		if (strings.length == 0)
			return events;
		
		Set<String> nameSet = getLabelNameSet(events);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
		
		return events;
	}

	public ISCInternalContext[] getInternalContexts(ISCMachineRoot root, int num) throws RodinDBException {
		final ISCInternalContext[] contexts = root.getSCSeenContexts();
		checkInternalContexts(num, contexts);
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

	public void containsEvents(ISCMachineRoot root, String... strings) throws RodinDBException {
		ISCEvent[] events = root.getSCEvents();
		
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

	public void containsParameters(ISCEvent event, String... strings) throws RodinDBException {
		ISCParameter[] parameters = event.getSCParameters();
		
		assertEquals("wrong number of variables", strings.length, parameters.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(parameters);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void containsMarkers(IInternalElement element, boolean yes)
			throws CoreException {
		final IFile file = element.getResource();
		final IMarker[] markers = file.findMarkers(RODIN_PROBLEM_MARKER, true,
				DEPTH_INFINITE);

		if (yes) {
			assertTrue("Should contain markers", markers.length != 0);
		} else if (markers.length != 0) {
			final StringBuilder sb = new StringBuilder();
			sb.append("Unexpected markers found on element " + element + ":");
			for (final IMarker marker : markers) {
				sb.append("\n\t");
				sb.append(marker.getAttribute(MESSAGE));
			}
			fail(sb.toString());
		}
	}
	
	public void hasMarker(IRodinElement element, IAttributeType attrType) throws Exception {
		hasMarker(element, attrType, null);
	}

	public void hasMarker(IRodinElement element) throws Exception {
		hasMarker(element, null);
	}

	public void hasNotMarker(IRodinElement element, IRodinProblem problem) throws Exception {
		IRodinFile file = (IRodinFile) element.getOpenable();
		IMarker[] markers = 
			file.getResource().findMarkers(
					RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
					true, 
					IResource.DEPTH_INFINITE);
		for (IMarker marker : markers) {
			IRodinElement elem = RodinMarkerUtil.getElement(marker);
			if (elem != null && elem.equals(element))
				if (problem == null || problem.getErrorCode().equals(RodinMarkerUtil.getErrorCode(marker)))
					fail("surplus problem marker on element");
		}
	}

	public void hasNotMarker(IRodinElement element) throws Exception {
		hasNotMarker(element, null);
	}

	public void hasMarker(IRodinElement element, IAttributeType attrType, IRodinProblem problem, String... args) throws Exception {
		IRodinFile file = (IRodinFile) element.getOpenable();
		IMarker[] markers = 
			file.getResource().findMarkers(
					RodinMarkerUtil.RODIN_PROBLEM_MARKER, 
					true, 
					IResource.DEPTH_INFINITE);
		for (IMarker marker : markers) {
			IRodinElement elem = RodinMarkerUtil.getInternalElement(marker);
			if (elem != null && elem.equals(element)) {
				if (attrType != null) {
					IAttributeType attributeType = RodinMarkerUtil.getAttributeType(marker);
					assertEquals("problem not attached to attribute", attrType, attributeType);
				}
				if (problem == null)
					return;
				if (problem.getErrorCode().equals(RodinMarkerUtil.getErrorCode(marker))) {
					String[] pargs = RodinMarkerUtil.getArguments(marker);
					assertEquals(args.length, pargs.length);
					for (int i=0; i<args.length; i++) {
						assertEquals(args[i], pargs[i]);
					}
					return;
				}
			}
		}
		fail("problem marker missing from element" +
				((attrType != null) ? " (attribute: " + attrType.getId() + ")" : ""));
	}
	
	public void isNotAccurate(IAccuracyElement element) throws RodinDBException {
		boolean acc = element.isAccurate();
		
		assertEquals("element is accurate", false, acc);
	}
	
	public void isAccurate(IAccuracyElement element) throws RodinDBException {
		boolean acc = element.isAccurate();
		
		assertEquals("element is not accurate", true, acc);
	}

	public void refinesEvents(ISCEvent event, String... strings) throws RodinDBException {
		ISCRefinesEvent[] refines = event.getSCRefinesClauses();
		
		assertEquals("wrong number of refines clauses", strings.length, refines.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getRefinedNameSet(refines);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public void extendsContexts(ISCContextRoot root, String... names) throws RodinDBException {
		ISCExtendsContext[] scExtends = root.getSCExtendsClauses();
		
		assertEquals("wrong number of extends clauses", names.length, scExtends.length);
		
		if (names.length == 0)
			return;
		
		Set<String> nameSet = getExtendedNameSet(scExtends);
		for (String name : names)
			assertTrue("should contain " + name, nameSet.contains(name));
	}

	public void containsContexts(ISCContextRoot root, String... names) throws RodinDBException {
		ISCInternalContext[] contexts = root.getAbstractSCContexts();
		
		assertEquals("wrong number of internal contexts", names.length, contexts.length);
		
		if (names.length == 0)
			return;
		
		Set<String> nameSet = getContextNameSet(contexts);
		for (String name : names)
			assertTrue("should contain " + name, nameSet.contains(name));
	}

	private void containsNoContexts(ISCInternalContext scContext)
			throws RodinDBException {
		final ISCInternalContext[] children = scContext
				.getChildrenOfType(ISCInternalContext.ELEMENT_TYPE);

		assertEquals("Should not contain any internal contexts", 0,
				children.length);
	}
	
	public void seesContexts(ISCMachineRoot scMachine, String... names) throws RodinDBException {
		ISCSeesContext[] sees = scMachine.getSCSeesClauses();
		
		assertEquals("wrong number of sees clauses", names.length, sees.length);
		
		if (names.length == 0)
			return;
		
		Set<String> nameSet = getSeenNameSet(sees);
		for (String name : names)
			assertTrue("should contain " + name, nameSet.contains(name));
	}

	public void containsContexts(ISCMachineRoot root, String... names) throws RodinDBException {
		ISCInternalContext[] contexts = root.getSCSeenContexts();
		
		assertEquals("wrong number of internal contexts", names.length, contexts.length);
		
		if (names.length == 0)
			return;
		
		Set<String> nameSet = getContextNameSet(contexts);
		for (String name : names)
			assertTrue("should contain " + name, nameSet.contains(name));
	}

	public void forbiddenVariables(ISCMachineRoot root, String... strings) throws RodinDBException {
		ISCVariable[] variables = root.getSCVariables();
		
		for (int i=0; i<variables.length; i++) {
			if (variables[i].isConcrete())
				variables[i] = null;
		}
		
		Set<String> nameSet = getIdentifierNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public void containsVariables(ISCMachineRoot root, String... strings) throws RodinDBException {
		ISCVariable[] variables = root.getSCVariables();
		
		assertEquals("wrong number of variables", strings.length, variables.length);
		
		if (strings.length == 0)
			return;
		
		Set<String> nameSet = getIdentifierNameSet(variables);
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void containsVariant(ISCMachineRoot root, ITypeEnvironment environment, String... strings) throws RodinDBException {
		assert strings.length <= 1;
		ISCVariant[] variants = root.getSCVariants();
		
		
		
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

}
