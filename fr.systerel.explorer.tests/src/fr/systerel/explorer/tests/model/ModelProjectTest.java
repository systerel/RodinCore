/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer.tests.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelMachine;
import fr.systerel.internal.explorer.model.ModelProject;

/**
 * 
 *
 */
public class ModelProjectTest extends ExplorerTest {
	
	protected static ModelProject project;
	protected static IMachineRoot m0;
	protected static IMachineRoot m1;
	protected static IMachineRoot m2;
	protected static IMachineRoot m3;
	protected static IMachineRoot m4;
	protected static IContextRoot c0;
	protected static IContextRoot c1;
	protected static IContextRoot c2;
	protected static IContextRoot c3;
	protected static IContextRoot c4;
	
	
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpContexts();
		setUpMachines();

		project =  new ModelProject(rodinProject);
		assertNotNull("The ModelProject should be created successfully.", project);

	}
	

	@Test
	public void processContextSimple() {
		project.processContext(c0);
		assertEquals(project.getContext(c0).getInternalContext(), c0);
	}

	
	@Test
	public void processContextExtends() {
		project.processContext(c1);
		ModelContext mc0 = project.getContext(c0);
		ModelContext mc1 = project.getContext(c1);
		// c0 should get processed too
		assertEquals(mc0.getInternalContext(), c0);
		assertEquals(mc1.getInternalContext(), c1);
		// check that the dependencies have been taken into the model
		assertArray(mc1.getExtendsContexts().toArray(), mc0);
		assertArray(mc0.getExtendedByContexts().toArray(), mc1);		
	}
	
	@Test
	public void processContextCycle() throws RodinDBException {
		//introduce a cycle in context extends
		//now c0 extends c1 and c1 extends c0
		createExtendsContextClause(c0, c1);
		
		//process the contexts
		project.processContext(c0);
		ModelContext mc0 = project.getContext(c0);
		ModelContext mc1 = project.getContext(c1);
		
		//both contexts should have been taken into the model
		assertEquals(mc0.getInternalContext(), c0);
		assertEquals(mc1.getInternalContext(), c1);
		
		// there should be no extends cycle in the model
		assertFalse(mc0.getExtendsContexts().contains(mc1) && mc1.getExtendsContexts().contains(mc0));		
		assertFalse(mc0.getExtendedByContexts().contains(mc1) && mc1.getExtendedByContexts().contains(mc0));		
	}

	@Test
	public void processContextLongCycle() throws RodinDBException {
		//introduce a cycle in context extends
		//now c2 extends c1, c1 extends c0 and c0 extends c2.
		createExtendsContextClause(c0, c2);
		
		//process the contexts
		project.processContext(c0);
		ModelContext mc0 = project.getContext(c0);
		ModelContext mc1 = project.getContext(c1);
		ModelContext mc2 = project.getContext(c2);
		
		//all contexts should have been taken into the model
		assertEquals(mc0.getInternalContext(), c0);
		assertEquals(mc1.getInternalContext(), c1);
		assertEquals(mc2.getInternalContext(), c2);
		
		// there should be no extends cycle in the model
		assertFalse(mc0.getExtendsContexts().contains(mc2) && mc1.getExtendsContexts().contains(mc0)
				&& mc2.getExtendsContexts().contains(mc1));		
		assertFalse(mc0.getExtendedByContexts().contains(mc2) && mc1.getExtendedByContexts().contains(mc0)
				&& mc2.getExtendedByContexts().contains(mc1));		
	}
	
	
	@Test
	public void processMachineSimple() {
		project.processMachine(m0);
		assertEquals(project.getMachine(m0).getInternalMachine(), m0);
	}

	
	@Test
	public void processMachineRefines() {
		project.processMachine(m1);
		ModelMachine mm0 = project.getMachine(m0);
		ModelMachine mm1 = project.getMachine(m1);
		// m0 should get processed too
		assertEquals(mm0.getInternalMachine(), m0);
		assertEquals(mm1.getInternalMachine(), m1);
		// check that the dependencies have been taken into the model
		assertArray(mm1.getRefinesMachines().toArray(), mm0);
		assertArray(mm0.getRefinedByMachines().toArray(), mm1);		
	}

	@Test
	public void processMachineSees() {
		project.processMachine(m0);
		ModelMachine mm0 = project.getMachine(m0);
		ModelContext mc0 = project.getContext(c0);
		// c0 should get processed too
		assertEquals(mm0.getInternalMachine(), m0);
		assertEquals(mc0.getInternalContext(), c0);
		// check that the dependencies have been taken into the model
		assertArray(mm0.getSeesContexts().toArray(), mc0);
		assertArray(mc0.getSeenByMachines().toArray(), mm0);		
	}

	@Test
	public void processMachineCycle() throws RodinDBException {
		//introduce a cycle in machine refinement
		//now m0 refines m1 and m1 refines m0
		createRefinesMachineClause(m0, m1);
		
		//process the machines
		project.processMachine(m0);
		ModelMachine mm0 = project.getMachine(m0);
		ModelMachine mm1 = project.getMachine(m1);
		
		//both machines should have been taken into the model
		assertEquals(mm0.getInternalMachine(), m0);
		assertEquals(mm1.getInternalMachine(), m1);
		
		// there should be no refinement cycle in the model
		assertFalse(mm0.getRefinesMachines().contains(mm1) && mm1.getRefinesMachines().contains(mm0));		
		assertFalse(mm0.getRefinedByMachines().contains(mm1) && mm1.getRefinedByMachines().contains(mm0));		
	}

	@Test
	public void processMachineLongCycle() throws RodinDBException {
		//introduce a cycle in machine refinement
		//now m2 refines m1, m1 refines m0 and m0 refines m2
		createRefinesMachineClause(m0, m2);
		
		//process the machines
		project.processMachine(m0);
		ModelMachine mm0 = project.getMachine(m0);
		ModelMachine mm1 = project.getMachine(m1);
		ModelMachine mm2 = project.getMachine(m2);
		
		//all machines should have been taken into the model
		assertEquals(mm0.getInternalMachine(), m0);
		assertEquals(mm1.getInternalMachine(), m1);
		assertEquals(mm2.getInternalMachine(), m2);
		
		// there should be no refinement cycle in the model
		assertFalse(mm0.getRefinesMachines().contains(mm2) && mm1.getRefinesMachines().contains(mm0)
				&&mm2.getRefinesMachines().contains(mm1));		
		assertFalse(mm0.getRefinedByMachines().contains(mm2) && mm1.getRefinedByMachines().contains(mm0)
				 && mm2.getRefinedByMachines().contains(mm1));		
	}
	
	@Test
	public void calculateMachineBranches() {
		//process all machines
		project.processMachine(m0);
		project.processMachine(m1);
		project.processMachine(m2);
		project.processMachine(m3);
		project.processMachine(m4);
		
		//calculate the branches
		project.calculateMachineBranches();
		
		ModelMachine mm0 = project.getMachine(m0);
		ModelMachine mm1 = project.getMachine(m1);
		ModelMachine mm2 = project.getMachine(m2);
		ModelMachine mm3 = project.getMachine(m3);
		ModelMachine mm4 = project.getMachine(m4);
		
		//check the results
		assertArray(mm0.getLongestBranch().toArray(), mm0, mm1, mm2);
		assertArray(mm1.getLongestBranch().toArray(), mm1, mm2);
		assertArray(mm2.getLongestBranch().toArray(), mm2);
		assertArray(mm3.getLongestBranch().toArray(), mm3);
		assertArray(mm4.getLongestBranch().toArray(), mm4);
	}
	
	@Test
	public void calculateContextBranches() {
		//process all contexts
		project.processContext(c0);
		project.processContext(c1);
		project.processContext(c2);
		project.processContext(c3);
		project.processContext(c4);
		
		//calculate the branches
		project.calculateContextBranches();
		
		ModelContext mc0 = project.getContext(c0);
		ModelContext mc1 = project.getContext(c1);
		ModelContext mc2 = project.getContext(c2);
		ModelContext mc3 = project.getContext(c3);
		ModelContext mc4 = project.getContext(c4);
		
		//check the results
		assertArray(mc0.getLongestBranch().toArray(), mc0, mc1, mc2);
		assertArray(mc1.getLongestBranch().toArray(), mc1, mc2);
		assertArray(mc2.getLongestBranch().toArray(), mc2);
		assertArray(mc3.getLongestBranch().toArray(), mc3);
		assertArray(mc4.getLongestBranch().toArray(), mc4);
	}
	
	@Test
	public void getRootMachines() {
		//process all machines
		project.processMachine(m0);
		project.processMachine(m1);
		project.processMachine(m2);
		project.processMachine(m3);
		project.processMachine(m4);
		
		//calculate the branches
		project.calculateMachineBranches();
		
		ModelMachine mm0 = project.getMachine(m0);
		ModelMachine mm1 = project.getMachine(m1);
		ModelMachine mm2 = project.getMachine(m2);
		ModelMachine mm4 = project.getMachine(m4);
	
		assertArray(project.getRootMachines(), mm4, mm0, mm1, mm2);
		
	}
	
	
	@Test
	public void getRootContexts() {
		//process all contexts
		project.processContext(c0);
		project.processContext(c1);
		project.processContext(c2);
		project.processContext(c3);
		project.processContext(c4);
		
		//calculate the branches
		project.calculateContextBranches();
		
		ModelContext mc0 = project.getContext(c0);
		ModelContext mc1 = project.getContext(c1);
		ModelContext mc2 = project.getContext(c2);
		ModelContext mc4 = project.getContext(c4);
	
		assertArray(project.getRootContexts(), mc4, mc0, mc1, mc2);
	}
	
	@Test
	public void removeMachineDependenciesRefines() {
		//process machines
		project.processMachine(m1);
		
		ModelMachine mm0 = project.getMachine(m0);
		ModelMachine mm1 = project.getMachine(m1);

		// check that the dependencies have been taken into the model
		assertArray(mm1.getRefinesMachines().toArray(), mm0);
		assertArray(mm0.getRefinedByMachines().toArray(), mm1);		
		
		//remove the dependencies of m1
		project.removeMachineDependencies(mm1);
		
		//check that the dependencies have been taken away.
		assertArray(mm0.getRefinedByMachines().toArray());
		
	}

	@Test
	public void removeMachineDependenciesSees() {
		//process machines
		project.processMachine(m0);
		
		ModelContext mc0 = project.getContext(c0);
		ModelMachine mm0 = project.getMachine(m0);

		// check that the dependencies have been taken into the model
		assertArray(mm0.getSeesContexts().toArray(), mc0);
		assertArray(mc0.getSeenByMachines().toArray(), mm0);		
		
		//remove the dependencies of m0
		project.removeMachineDependencies(mm0);
		
		//check that the dependencies have been taken away.
		assertArray(mc0.getSeenByMachines().toArray());		
		
	}

	
	@Test
	public void removeContextDependencies() {
		//process context
		project.processContext(c1);
		
		ModelContext mc0 = project.getContext(c0);
		ModelContext mc1 = project.getContext(c1);

		// check that the dependencies have been taken into the model
		assertArray(mc0.getExtendedByContexts().toArray(), mc1);
		assertArray(mc1.getExtendsContexts().toArray(), mc0);		
		
		//remove the dependencies of c1
		project.removeContextDependencies(mc1);
		
		//check that the dependencies have been taken away.
		assertArray(mc0.getExtendedByContexts().toArray());		
		
	}

	
	
	protected void setUpContexts() throws RodinDBException {
		//create some contexts
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		c1 = createContext("c1");
		assertNotNull("c1 should be created successfully ", c1);

		c2 = createContext("c2");
		assertNotNull("c2 should be created successfully ", c2);

		c3 = createContext("c3");
		assertNotNull("c3 should be created successfully ", c3);

		c4 = createContext("c4");
		assertNotNull("c4 should be created successfully ", c4);
		
		//create dependencies between the contexts.
		createExtendsContextClause(c1, c0);
		createExtendsContextClause(c2, c1);
		createExtendsContextClause(c3, c0);
		
	}

	
	protected void setUpMachines() throws RodinDBException {
		//create some machines
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);

		m1 = createMachine("m1");
		assertNotNull("m1 should be created successfully ", m1);

		m2 = createMachine("m2");
		assertNotNull("m2 should be created successfully ", m2);

		m3 = createMachine("m3");
		assertNotNull("m3 should be created successfully ", m3);

		m4 = createMachine("m4");
		assertNotNull("m4 should be created successfully ", m4);
		
		//create dependencies between machines
		createRefinesMachineClause(m1, m0);
		createRefinesMachineClause(m2, m1);
		createRefinesMachineClause(m3, m0);
		
		//create a sees clause
		createSeesContextClause(m0, c0);
		
	}
}
