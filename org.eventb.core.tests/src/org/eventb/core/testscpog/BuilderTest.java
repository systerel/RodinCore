/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.testscpog;

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
import org.eventb.core.IContext;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachine;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.ISCAxiomSet;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachine;
import org.eventb.core.ISCTheoremSet;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISees;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.basis.POIdentifier;
import org.eventb.core.basis.SCCarrierSet;
import org.eventb.core.basis.SCConstant;
import org.eventb.core.basis.SCVariable;
import org.eventb.internal.core.pom.AutoProver;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Abstract class for builder tests.
 * 
 * @author Laurent Voisin
 */
public abstract class BuilderTest extends TestCase {
	
	protected FormulaFactory factory = FormulaFactory.getDefault();

	public static void addAxioms(IRodinFile rodinFile, String[] names, String[] axioms, String bag) throws RodinDBException {
		IInternalParent element = rodinFile;
		if(bag != null) {
			element = element.createInternalElement(ISCAxiomSet.ELEMENT_TYPE, bag, null, null);
		}
		for(int i=0; i<names.length; i++) {
			IAxiom axiom = (IAxiom) element.createInternalElement(IAxiom.ELEMENT_TYPE, names[i], null, null);
			axiom.setContents(axioms[i]);
		}
	}
	
	public static void addCarrierSets(IRodinFile rodinFile, String[] names) throws RodinDBException {
		for(String name : names)
			rodinFile.createInternalElement(ICarrierSet.ELEMENT_TYPE, name, null, null);
	}
	
	public static void addConstants(IRodinFile rodinFile, String[] names) throws RodinDBException {
		for(String name : names)
			rodinFile.createInternalElement(IConstant.ELEMENT_TYPE, name, null, null);
	}

	public static void addEvent(IRodinFile rodinFile, 
				String name,
				String[] vars,
				String[] guardNames,
				String[] guards,
				String[] actions
	) throws RodinDBException {
		IEvent event = (IEvent) rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, name, null, null);
		for(int i=0; i<vars.length; i++) {
			event.createInternalElement(IVariable.ELEMENT_TYPE, vars[i], null, null);
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = (IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, guardNames[i], null, null);
			guard.setContents(guards[i]);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, "", null, null);
			action.setContents(actions[j]);
		}
	}

	public static void addIdentifiers(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			POIdentifier identifier = (POIdentifier) rodinFile.createInternalElement(POIdentifier.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}

	public static void addInvariants(IRodinFile rodinFile, String[] names, String[] invariants) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInvariant invariant = (IInvariant) rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, names[i], null, null);
			invariant.setContents(invariants[i]);
		}
	}

	public static ISCAxiomSet addOldAxioms(IRodinFile file, String name) throws RodinDBException {
		return (ISCAxiomSet) file.createInternalElement(ISCAxiomSet.ELEMENT_TYPE, name, null, null);
	}

	public static ISCTheoremSet addOldTheorems(IRodinFile file, String name) throws RodinDBException {
		return (ISCTheoremSet) file.createInternalElement(ISCTheoremSet.ELEMENT_TYPE, name, null, null);
	}

	public static void addSCCarrierSets(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			SCCarrierSet identifier = (SCCarrierSet) rodinFile.createInternalElement(ISCCarrierSet.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}

	public static void addSCConstants(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
			for(int i=0; i<names.length; i++) {
				SCConstant identifier = (SCConstant) rodinFile.createInternalElement(ISCConstant.ELEMENT_TYPE, names[i], null, null);
				identifier.setContents(types[i]);
			}
		}

	public static void addSCEvent(IRodinFile rodinFile, 
				String name,
				String[] vars,
				String[] guardNames,
				String[] guards,
				String[] actions,
				String[] types
	) throws RodinDBException {
		ISCEvent event = (ISCEvent) rodinFile.createInternalElement(ISCEvent.ELEMENT_TYPE, name, null, null);
		for(int i=0; i<vars.length; i++) {
			ISCVariable variable = (ISCVariable) event.createInternalElement(ISCVariable.ELEMENT_TYPE, vars[i], null, null);
			variable.setContents(types[i]);
		}
		for(int i=0; i<guards.length; i++) {
			IGuard guard = (IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, guardNames[i], null, null);
			guard.setContents(guards[i]);
		}
		for(int j=0; j<actions.length; j++) {
			IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, "ACTION", null, null);
			action.setContents(actions[j]);
		}
	}

	public static void addSCVariables(IRodinFile rodinFile, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			SCVariable identifier = (SCVariable) rodinFile.createInternalElement(ISCVariable.ELEMENT_TYPE, names[i], null, null);
			identifier.setContents(types[i]);
		}
	}

	//	public static void addEvent(IRodinFile rodinFile, 
//			String name,
//			String[] vars,
//			String[] guardNames,
//			String[] guards,
//			String[] actions,
//			
//) throws RodinDBException {
//	IEvent event = (IEvent) rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, name, null, null);
//	for(int i=0; i<vars.length; i++) {
//		event.createInternalElement(IVariable.ELEMENT_TYPE, vars[i], null, null);
//	}
//	for(int i=0; i<guards.length; i++) {
//		IGuard guard = (IGuard) event.createInternalElement(IGuard.ELEMENT_TYPE, guardNames[i], null, null);
//		guard.setContents(guards[i]);
//	}
//	for(int j=0; j<actions.length; j++) {
//		IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, "ACTION", null, null);
//		action.setContents(actions[j]);
//	}
//}
//
	public static void addSees(IRodinFile rodinFile, String name) throws RodinDBException {
		ISees sees = (ISees) rodinFile.createInternalElement(ISees.ELEMENT_TYPE, null, null, null);
		sees.setContents(name);
	}

	public static void addTheorems(IRodinFile rodinFile, String[] names, String[] theorems, String bag) throws RodinDBException {
		IInternalParent element = rodinFile;
		if(bag != null) {
			element = element.createInternalElement(ISCTheoremSet.ELEMENT_TYPE, bag, null, null);
		}
		for(int i=0; i<names.length; i++) {
			ITheorem theorem = (ITheorem) element.createInternalElement(ITheorem.ELEMENT_TYPE, names[i], null, null);
			theorem.setContents(theorems[i]);
		}
	}

	public static void addVariables(IRodinFile rodinFile, String[] names) throws RodinDBException {
		for(String name : names)
			rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
	}

	public static String[] makeList(String...strings) {
		return strings;
	}

	protected IRodinProject rodinProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
	}
	
	protected IContext createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		return (IContext) rodinProject.createRodinFile(fileName, true, null);
	}

	protected IMachine createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		return (IMachine) rodinProject.createRodinFile(fileName, true, null);
	}

	protected IPOFile createPOFile(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		return (IPOFile) rodinProject.createRodinFile(fileName, true, null);
	}

	protected ISCContext createSCContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCContextFileName(bareName);
		return (ISCContext) rodinProject.createRodinFile(fileName, true, null);
	}

	protected ISCMachine createSCMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCMachineFileName(bareName);
		return (ISCMachine) rodinProject.createRodinFile(fileName, true, null);
	}

	protected String getSourceContents(IPOSequent poSequent, int sourceIdx) throws RodinDBException {
		IRodinElement element = getSourceElement(poSequent, sourceIdx);
		return ((IInternalElement) element).getContents();
	}
	
	protected IRodinElement getSourceElement(IPOSequent poSequent, int sourceIdx) throws RodinDBException {
		IPOSource[] sources = poSequent.getDescription().getSources();
		String memento = sources[sourceIdx].getSourceHandleIdentifier();
		return RodinCore.create(memento);
	}
	
	protected String getSourceName(IPOSequent poSequent, int sourceIdx) throws RodinDBException {
		IRodinElement element = getSourceElement(poSequent, sourceIdx);
		return element.getElementName();
	}
	
	protected void runBuilder() throws CoreException {
		rodinProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	protected IPOFile runPOG(ISCContext context) throws CoreException {
		runBuilder();
		return context.getPOFile();
	}

	protected IPOFile runPOG(ISCMachine machine) throws CoreException {
		runBuilder();
		return machine.getPOFile();
	}
	
	protected ISCContext runSC(IContext context) throws CoreException {
		runBuilder();
		return context.getSCContext();
	}
	
	protected ISCMachine runSC(IMachine machine) throws CoreException {
		runBuilder();
		return machine.getSCMachine();
	}
	
	protected Predicate predicateFromString(String predicate) {
		Predicate pp = factory.parsePredicate(predicate).getParsedPredicate();
		return pp;
	}
	
	protected Assignment assignmentFromString(String assignment) {
		Assignment aa = factory.parseAssignment(assignment).getParsedAssignment();
		return aa;
	}
	
	protected Predicate rewriteGoal(ITypeEnvironment typeEnv, String predicate, String substitution) {
		Predicate goal1 = predicateFromString(predicate);
		goal1.typeCheck(typeEnv);
		Assignment goalass1 = assignmentFromString(substitution);
		goalass1.typeCheck(typeEnv);
		goal1 = goal1.applyAssignment((BecomesEqualTo) goalass1, factory);
		return goal1;
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

}
