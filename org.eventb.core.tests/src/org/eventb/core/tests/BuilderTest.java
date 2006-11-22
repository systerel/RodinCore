/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ISCMachineFile;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.pom.AutoProver;
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

	protected IRodinProject rodinProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
	}
	
	protected IContextFile createContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getContextFileName(bareName);
		IContextFile result = (IContextFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected IMachineFile createMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getMachineFileName(bareName);
		IMachineFile result = (IMachineFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected IPOFile createPOFile(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getPOFileName(bareName);
		IPOFile result = (IPOFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected ISCContextFile createSCContext(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCContextFileName(bareName);
		ISCContextFile result = (ISCContextFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}

	protected ISCMachineFile createSCMachine(String bareName) throws RodinDBException {
		final String fileName = EventBPlugin.getSCMachineFileName(bareName);
		ISCMachineFile result = (ISCMachineFile) rodinProject.getRodinFile(fileName);
		result.create(true, null);
		return result;
	}
	
	protected void runBuilder() throws CoreException {
		rodinProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	protected IPOFile runPOG(ISCContextFile context) throws CoreException {
		runBuilder();
		return context.getContextFile().getPOFile();
	}

	protected IPOFile runPOG(ISCMachineFile machine) throws CoreException {
		runBuilder();
		return machine.getMachineFile().getPOFile();
	}
	
	protected ISCContextFile runSC(IContextFile context) throws CoreException {
		runBuilder();
		return context.getSCContextFile();
	}
	
	protected ISCMachineFile runSC(IMachineFile machine) throws CoreException {
		runBuilder();
		return machine.getSCMachineFile();
	}
	
	protected Predicate predicateFromString(String predicate) {
		Predicate pp = factory.parsePredicate(predicate).getParsedPredicate();
		return pp;
	}
	
	protected String getNormalizedPredicate(String input) {
		Predicate pred = factory.parsePredicate(input).getParsedPredicate();
		pred.typeCheck(factory.makeTypeEnvironment());
		assertTrue(pred.isTypeChecked());
		return pred.toStringWithTypes();
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

	/**
	 * Creates a new type environment from the given strings. The given strings
	 * are alternatively an identifier name and its type.
	 * 
	 * @param strings
	 *            an even number of strings
	 * @return a new type environment
	 */
	public ITypeEnvironment mTypeEnvironment(String... strings) {
		// even number of strings
		assert (strings.length & 1) == 0;
		final ITypeEnvironment result = factory.makeTypeEnvironment();
		for (int i = 0; i < strings.length; i += 2) {
			final String name = strings[i];
			final String typeString = strings[i+1];
			final IParseResult pResult = factory.parseType(typeString);
			assertTrue("Parsing type failed for " + typeString,
					pResult.isSuccess());
			final Type type = pResult.getParsedType(); 
			result.addName(name, type);
		}
		return result;
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

}
