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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;

/**
 * 
 *
 */
public class ModelControllerTest extends ExplorerTest {
	
	protected static ModelProject project;
	protected static ModelController controller;
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
	protected static ArrayList<IRodinElementDelta> deltas = new  ArrayList<IRodinElementDelta>();
	
	protected static IElementChangedListener listener =  new IElementChangedListener() {

		public void elementChanged(ElementChangedEvent event) {
			deltas.add(event.getDelta());
			
		}
		
	};
	
	
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpContexts();
		setUpMachines();

	}
	
	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		//make sure the listener is always removed
		RodinCore.removeElementChangedListener(listener);
		deltas.clear();
		ModelController.removeProject(rodinProject);
		
	}

	@Test
	public void processProject() {
		ModelController.processProject(rodinProject);
		project = ModelController.getProject(rodinProject);
		
		//check that the project and its machines and contexts exists as model components
		assertNotNull(project);
		assertNotNull(project.getMachine(m0));
		assertNotNull(project.getMachine(m1));
		assertNotNull(project.getMachine(m2));
		assertNotNull(project.getMachine(m3));
		assertNotNull(project.getMachine(m4));
		assertNotNull(project.getContext(c0));
		assertNotNull(project.getContext(c1));
		assertNotNull(project.getContext(c2));
		assertNotNull(project.getContext(c3));
		assertNotNull(project.getContext(c4));
	}

	
	@Test
	public void refreshModelAddMachine() throws RodinDBException {
		setUpSubscription();
		IMachineRoot m5 =  createMachine("m5");
		//make sure that a ModelMachine has been created for the added machine.
		assertNotNull(ModelController.getMachine(m5));
	}

	@Test
	public void refreshModelAddContext() throws RodinDBException {
		setUpSubscription();
		IContextRoot c5 =  createContext("c5");
		//make sure that a ModelContext has been created for the added context.
		assertNotNull(ModelController.getContext(c5));
	}
	
	@Test
	public void refreshModelAddInvariant() throws RodinDBException {
		setUpSubscription();
		IInvariant inv =  createInvariant(m0, "inv");
		//make sure that a ModelInvariant has been created for the added invariant.
		assertNotNull(ModelController.getInvariant(inv));
	}

	@Test
	public void refreshModelRemoveInvariant() throws RodinDBException {
		IInvariant inv =  createInvariant(m0, "inv");
		setUpSubscription();

		//remove the invariant
		inv.delete(true, null);
		//make sure that the invariant has been removed from the model
		assertNull(ModelController.getInvariant(inv));
	}

	@Test
	public void refreshModelAddPOSequent() throws CoreException {
		// create a PORoot
		IPORoot ipo = createIPORoot("m0");

		setUpSubscription();
		//process the root
		ModelController.getMachine(m0).processPORoot();
			 
		//add a sequent
		createSequent(ipo, "sequent");
		
		//make sure that a ModelProofObligation has been created for the added sequent.
		assertEquals(1, ModelController.getMachine(m0).getProofObligations().length);
	}
	
	@Test
	public void refreshModelAddPSStatus() throws CoreException {
		// create a PORoot and a sequent
		IPORoot ipo = createIPORoot("m0");
		createSequent(ipo, "sequent");
		
		//create a PSRoot
		IPSRoot ips = createIPSRoot("m0");
		
		setUpSubscription();
		//process the roots
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
			 
		//add a status
		IPSStatus status = createPSStatus(ips, "sequent");
		//make sure that a ModelProofObligation has been created for the added status.
		assertNotNull(ModelController.getModelPO(status).getIPSStatus());
	}

	@Test
	public void refreshModelChangePSStatus() throws CoreException {
		// create a PORoot and a sequent
		IPORoot ipo = createIPORoot("m0");
		createSequent(ipo, "sequent");
		
		//create a PSRoot
		IPSRoot ips = createIPSRoot("m0");
		//add a status
		IPSStatus status = createPSStatus(ips, "sequent");
		status.setConfidence(IConfidence.PENDING, null);
		
		setUpSubscription();
		//process the roots
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
			 
		status.setConfidence(IConfidence.DISCHARGED_MAX, null);
		
		//make sure the PO is discharged now
		assertTrue(ModelController.getModelPO(status).isDischarged());
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
		createExtendsContextClause(c1, c0, "extend1");
		assertTrue(c1.getExtendsClause("extend1").exists());
		createExtendsContextClause(c2, c1, "extend2");
		assertTrue(c2.getExtendsClause("extend2").exists());
		createExtendsContextClause(c3, c0, "extend3");
		assertTrue(c3.getExtendsClause("extend3").exists());
		
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
		createRefinesMachineClause(m1, m0, "refines1");
		assertTrue(m1.getRefinesClause("refines1").exists());
		createRefinesMachineClause(m2, m1, "refines2");
		assertTrue(m2.getRefinesClause("refines2").exists());
		createRefinesMachineClause(m3, m0, "refines3");
		assertTrue(m3.getRefinesClause("refines3").exists());
		
		//create a sees clause
		createSeesContextClause(m0, c0, "sees1");
		assertTrue(m0.getSeesClause("sees1").exists());
		
	}
	
	protected void setUpSubscription() {
		//process the project
		ModelController.processProject(rodinProject);
		project = ModelController.getProject(rodinProject);

		controller = ModelController.getInstance();
		
		//subscribe for changes
		RodinCore.addElementChangedListener(listener);
		
	}
}
