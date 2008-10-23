/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.tests.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IWitness;
import org.junit.Before;
import org.junit.Test;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.tests.ExplorerTest;

/**
 * 
 *
 */
public class ModelMachineTest extends ExplorerTest {

	protected static IMachineRoot machineRoot;
	protected static ModelMachine machine;
	protected static IInvariant inv1;
	protected static ITheorem thm1;
	protected static IEvent evt1;
	protected static IEvent evt2;
	protected static IPORoot ipo;
	protected static IPSRoot ips;
	protected static IPOSequent sequent1;
	protected static IPOSequent sequent2;
	protected static IPOSequent sequent3;
	protected static IPOSequent sequent4;
	protected static IPOSequent sequent5;
	protected static IPSStatus status1;
	protected static IPSStatus status2;
	protected static IPSStatus status3;
	protected static IPSStatus status4;
	protected static IPSStatus status5;
	protected static IPOSource source1;
	protected static IPOSource source2;
	protected static IPOSource source3;
	protected static IPOSource source4;
	protected static IPOSource source5;
	protected static IPOSource source6;
	protected static IAction action1;
	protected static IGuard guard1;
	protected static IWitness witness1;
	
	
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		//create a machine
		machineRoot = createMachine("m0");
		machine =  new ModelMachine(machineRoot);

		//add some elements to the machine
		inv1 = createInvariant(machineRoot, "inv1");
		thm1 = createTheorem(machineRoot, "thm1");
		evt1 = createEvent(machineRoot, "evt1");
		
		//create an event with guards, witness and action
		evt2 = createEvent(machineRoot, "evt2");
		guard1 = createGuard(evt2, "guard1");
		action1 = createAction(evt2, "action1");
		witness1 = createWitness(evt2, "witness1");
		
		//add some proof obligations to the machine
		ipo = createIPORoot("m0");
		assertNotNull("m0IPO should be created successfully ", ipo);
		
		ips = createIPSRoot("m0");
		assertNotNull("m0IPS should be created successfully ", ips);
		
		//add some sequents
		sequent1 = createSequent(ipo, "sequent1");
		status1 = createPSStatus(ips, "sequent1");

		source1 =  createPOSource(sequent1, "source1");
		source1.setSource(inv1, null);
		source2 =  createPOSource(sequent1, "source2");
		source2.setSource(evt1, null);

		
		sequent2 = createSequent(ipo, "sequent2");
		status2 = createPSStatus(ips, "sequent2");
		source3 =  createPOSource(sequent2, "source3");
		source3.setSource(guard1, null);
		
		sequent3 = createSequent(ipo, "sequent3");
		status3 = createPSStatus(ips, "sequent3");
		source4 =  createPOSource(sequent3, "source4");
		source4.setSource(action1, null);

		sequent4 = createSequent(ipo, "sequent4");
		status4 = createPSStatus(ips, "sequent4");
		source5 =  createPOSource(sequent4, "source5");
		source5.setSource(witness1, null);

		sequent5 = createSequent(ipo, "sequent5");
		status5 = createPSStatus(ips, "sequent5");
		source6 =  createPOSource(sequent5, "source6");
		source6.setSource(thm1, null);
	}
	

	@Test
	public void getModelParent() {
		//project needs to be processed, otherwise the parent can't be found
		ModelController.processProject(rodinProject);
		
		assertEquals(ModelController.getProject(rodinProject), machine.getModelParent());
	}
	
	@Test
	public void processChildren() {
		machine.processChildren();
		//check that the children have been transformed into ModelElements
		assertModel(evt1, machine.getEvent(evt1));
		assertModel(inv1, machine.getInvariant(inv1));
		assertModel(thm1, machine.getTheorem(thm1));
	}

	@Test
	public void processPORoot() {
		machine.processChildren();
		machine.processPORoot();
		//check that proof obligations have been added to the model
		assertModelPOSequent(machine.getProofObligations(), sequent3, sequent4, sequent1, sequent2, sequent5);

		//check that the proof obligations have been added to the appropriate children
		assertModelPOSequent(machine.getInvariant(inv1).getProofObligations(), sequent1);
		assertModelPOSequent(machine.getEvent(evt1).getProofObligations(), sequent1);
		assertModelPOSequent(machine.getTheorem(thm1).getProofObligations(), sequent5);
		assertModelPOSequent(machine.getEvent(evt2).getProofObligations(), sequent3, sequent4, sequent2);
	}

	@Test
	public void processPSRoot() {
		machine.processChildren();
		//the POs need to be processed first.
		machine.processPORoot();

		machine.processPSRoot();
		//check that statuses haven been added to the proof obligations
		assertModelPSStatus(machine.getProofObligations(), status3, status4, status1, status2, status5);

	}
	
}
