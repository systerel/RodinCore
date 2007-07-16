/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests.pm;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
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
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
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
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
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
import org.rodinp.core.RodinMarkerUtil;

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
		final IProject project = rodinProject.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs= project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER,
				true,
				IResource.DEPTH_INFINITE
		);
		if (buildPbs.length != 0) {
			for (IMarker marker: buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			fail("Build produced build problems, see console");
		}
		checkPSFiles();
	}

	private void checkPSFiles() throws RodinDBException {
		IRodinFile[] files = rodinProject.getRodinFiles(); 
		for (IRodinFile file: files) {
			if (file instanceof IPSFile) {
				checkPSFile((IPSFile) file);
			}
		}
	}

	private void checkPSFile(IPSFile file) throws RodinDBException {
		for (IPSStatus psStatus: file.getStatuses()) {
			final IPOSequent poSequent = psStatus.getPOSequent();
			assertEquals("PS file not in sync with PO file",
					poSequent.getPOStamp(), psStatus.getPOStamp());
		}
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
		final IContextFile result = (IContextFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected IMachineFile createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		final IMachineFile result = (IMachineFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	public static void addAxioms(
			IContextFile rodinFile, 
			String[] names, 
			String[] axioms) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IAxiom axiom = rodinFile.getAxiom(getUniqueName());
			axiom.create(null, null);
			axiom.setPredicateString(axioms[i], null);
			axiom.setLabel(names[i], null);
		}
	}
		
	public static void addCarrierSets(IContextFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			ICarrierSet set = rodinFile.getCarrierSet(getUniqueName());
			set.create(null, null);
			set.setIdentifierString(name, null);
		}
			
	}
	
	public static void addConstants(IContextFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			IConstant constant = rodinFile.getConstant(getUniqueName());
			constant.create(null, null);
			constant.setIdentifierString(name, null);
		}
	}

	public static void addEventRefines(IEvent event, String name) throws RodinDBException {
		IRefinesEvent refines = event.getRefinesClause(getUniqueName());
		refines.create(null, null);
		refines.setAbstractEventLabel(name, null);
	}

	public static void addEventWitnesses(IEvent event, String[] labels, String[] predicates) throws RodinDBException {
		assert labels.length == predicates.length;
		for (int i=0; i<labels.length; i++) {
			IWitness witness = event.getWitness(getUniqueName());
			witness.create(null, null);
			witness.setLabel(labels[i], null);
			witness.setPredicateString(predicates[i], null);
		}
	}

	public static IEvent addEvent(IMachineFile rodinFile, 
				String name,
				String[] vars,
				String[] guardNames,
				String[] guards,
				String[] actionNames,
				String[] actions
	) throws RodinDBException {
		IEvent event = rodinFile.getEvent(getUniqueName());
		event.create(null, null);
		event.setLabel(name, null);
		event.setInherited(false, null);
		event.setConvergence(IConvergenceElement.Convergence.ORDINARY, null);
		for(int i=0; i<vars.length; i++) {
			IVariable variable = event.getVariable(getUniqueName());
			variable.create(null, null);
			variable.setIdentifierString(vars[i], null);
			
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = event.getGuard(getUniqueName());
			guard.create(null, null);
			guard.setPredicateString(guards[i], null);
			guard.setLabel(guardNames[i], null);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = event.getAction(getUniqueName());
			action.create(null, null);
			action.setAssignmentString(actions[j], null);
			action.setLabel(actionNames[j], null);
		}
		return event;
	}
	
	public static IEvent addEvent(IMachineFile rodinFile, 
			String name) throws RodinDBException {
		return addEvent(rodinFile, name, makeSList(), makeSList(), makeSList(), makeSList(), makeSList());
	}
	
	public static IEvent addInheritedEvent(IMachineFile rodinFile, 
			String name) throws RodinDBException {
		IEvent event = rodinFile.getEvent(getUniqueName());
		event.create(null, null);
		event.setLabel(name, null);
		event.setInherited(true, null);
		event.setConvergence(IConvergenceElement.Convergence.ORDINARY, null);
		return event;
		
	}
	
	private static void setConvergence(IEvent event, 
			IConvergenceElement.Convergence convergence) throws RodinDBException {
		event.setConvergence(convergence, null);
	}
	
	private static IConvergenceElement.Convergence getConvergence(ISCEvent event) throws RodinDBException {
		return event.getConvergence();
	}
	
	public static void setOrdinary(IEvent event) throws RodinDBException {
		setConvergence(event, IConvergenceElement.Convergence.ORDINARY);
	}

	public static void setAnticipated(IEvent event) throws RodinDBException {
		setConvergence(event, IConvergenceElement.Convergence.ANTICIPATED);
	}

	public static void setConvergent(IEvent event) throws RodinDBException {
		setConvergence(event, IConvergenceElement.Convergence.CONVERGENT);
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

	public static void addInvariants(IMachineFile rodinFile, String[] names, String[] invariants) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInvariant invariant = rodinFile.getInvariant(getUniqueName());
			invariant.create(null, null);
			invariant.setPredicateString(invariants[i], null);
			invariant.setLabel(names[i], null);
		}
	}

	public static void addVariant(IMachineFile rodinFile, String expression) throws RodinDBException {
		IVariant variant = rodinFile.getVariant(getUniqueName());
		variant.create(null, null);
		variant.setExpressionString(expression, null);
	}

	public static void addMachineSees(IMachineFile rodinFile, String name) throws RodinDBException {
		ISeesContext sees = rodinFile.getSeesClause(getUniqueName());
		sees.create(null, null);
		sees.setSeenContextName(name, null);
	}

	public static void addMachineRefines(IMachineFile rodinFile, String name) throws RodinDBException {
		IRefinesMachine refines = rodinFile.getRefinesClause(getUniqueName());
		refines.create(null, null);
		refines.setAbstractMachineName(name, null);
	}

	public static void addContextExtends(IContextFile rodinFile, String name) throws RodinDBException {
		IExtendsContext extendsContext = rodinFile.getExtendsClause(getUniqueName());
		extendsContext.create(null, null);
		extendsContext.setAbstractContextName(name, null);
	}

	public static void addTheorems(IMachineFile rodinFile, String[] names, String[] theorems) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = rodinFile.getTheorem(getUniqueName());
			theorem.create(null, null);
			theorem.setPredicateString(theorems[i], null);
			theorem.setLabel(names[i], null);
		}
	}

	public static void addTheorems(IContextFile rodinFile, String[] names, String[] theorems) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = rodinFile.getTheorem(getUniqueName());
			theorem.create(null, null);
			theorem.setPredicateString(theorems[i], null);
			theorem.setLabel(names[i], null);
		}
	}

	public static void addVariables(IMachineFile rodinFile, String... names) throws RodinDBException {
		for(String name : names) {
			IVariable variable = rodinFile.getVariable(getUniqueName());
			variable.create(null, null);
			variable.setIdentifierString(name, null);
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

	protected String getNormalizedExpression(String input, ITypeEnvironment environment) {
		Expression expr = factory.parseExpression(input).getParsedExpression();
		expr.typeCheck(environment);
		assertTrue(expr.isTypeChecked());
		return expr.toStringWithTypes();
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
		rodinProject = RodinCore.valueOf(project);
		
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
		ISCInternalContext[] contexts = file.getSCSeenContexts();
		
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
	
	protected void containsMarkers(IRodinFile rodinFile, boolean yes) throws CoreException {
		IFile file = rodinFile.getResource();
		IMarker[] markers = 
			file.findMarkers(RodinMarkerUtil.RODIN_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		
		if (yes)
			assertTrue("should contain markers", markers.length != 0);
		else
			assertEquals("should not contain markers", 0, markers.length);
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

	protected void containsVariant(ISCMachineFile file, ITypeEnvironment environment, String... strings) throws RodinDBException {
		assert strings.length <= 1;
		ISCVariant[] variants = file.getSCVariants();
		
		
		
		assertEquals("wrong number of variants", strings.length, variants.length);
		
		if (strings.length == 0)
			return;
		
		String vs = variants[0].getExpressionString();
		String exp = getNormalizedExpression(strings[0], environment);
				
		assertEquals("wrong variant", exp, vs);
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
