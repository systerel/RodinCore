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
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.DeltaProcessor;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProject;

/**
 * 
 *
 */
public class DeltaProcessorTest extends ExplorerTest {
	
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
	public void processDeltaAddMachine() throws RodinDBException {
		setUpSubscription();
		
		//add a machine
		createMachine("m5");
		
		//there should be one delta.
		assertEquals(1, deltas.size());
		//process the delta
		DeltaProcessor processor =  new DeltaProcessor(deltas.get(0));

		//the project should get refreshed.
		assertArray(processor.getToRefresh().toArray(), rodinProject);
	}

	@Test
	public void processDeltaRemoveMachine() throws RodinDBException {
		
		setUpSubscription();
		
		//remove a machine
		m4.getRodinFile().delete(true, null);
		
		//there should be one delta.
		assertEquals(1, deltas.size());
		
		//process the delta
		DeltaProcessor processor =  new DeltaProcessor(deltas.get(0));
		//the project should get refreshed.
		assertArray(processor.getToRefresh().toArray(), rodinProject);
		//the machine should be removed from the model
		assertArray(processor.getToRemove().toArray(), m4);
	}
	
	@Test
	public void processDeltaAddContext() throws RodinDBException {
		setUpSubscription();

		//add a context
		createContext("c5");
		
		//there should be one delta.
		assertEquals(1, deltas.size());
		//process the delta
		DeltaProcessor processor =  new DeltaProcessor(deltas.get(0));
		//the project should get refreshed.
		assertArray(processor.getToRefresh().toArray(), rodinProject);
	}

	@Test
	public void processDeltaRemoveContext() throws RodinDBException {
		
		setUpSubscription();
		
		//remove a context
		c4.getRodinFile().delete(true, null);
		
		//there should be one delta.
		assertEquals(1, deltas.size());
		
		//process the delta
		DeltaProcessor processor =  new DeltaProcessor(deltas.get(0));
		//the project should get refreshed.
		assertArray(processor.getToRefresh().toArray(), rodinProject);
		//the context should be removed from the model
		assertArray(processor.getToRemove().toArray(), c4);
	}
	
	@Test
	public void processDeltaAddProject() throws CoreException {
		setUpSubscription();

		//add a project
		createProject("newProject");
		
		//there should be one delta.
		assertEquals(1, deltas.size());
		//process the delta
		DeltaProcessor processor =  new DeltaProcessor(deltas.get(0));
		//everything should be refreshed, this is done when the RodinDB is added to refresh.
		assertArray(processor.getToRefresh().toArray(), RodinCore.getRodinDB());
	}

	@Test
	public void processDeltaRemoveProject() throws CoreException {
		
		setUpSubscription();
		
		//remove a project
		rodinProject.getProject().delete(true, null);
		
		//there should be one delta.
		assertEquals(1, deltas.size());
		
		//process the delta
		DeltaProcessor processor =  new DeltaProcessor(deltas.get(0));
		//everything should be refreshed, this is done when the RodinDB is added to refresh.
		assertArray(processor.getToRefresh().toArray(), RodinCore.getRodinDB());
		//the project should be removed from the model
		assertArray(processor.getToRemove().toArray(), rodinProject);
	}

	@Test
	public void processDeltaAddInvariant() throws CoreException {
		setUpSubscription();

		//add an invariant
		createInvariant(m0, "inv1");
		
		//there should be some deltas.
		assertTrue(deltas.size() >0);
		
		ArrayList<IRodinElement> toRefresh = new ArrayList<IRodinElement>();
		
		//process the deltas
		for (IRodinElementDelta delta : deltas) {
			DeltaProcessor processor =  new DeltaProcessor(delta);
			toRefresh.addAll(processor.getToRefresh());
		}
		//the parent machine should get refreshed
		assertTrue(toRefresh.contains(m0));
	}

	@Test
	public void processDeltaRemoveInvariant() throws CoreException {
		
		setUpSubscription();
		
		//add an invariant (to remove it afterwards)
		IInvariant inv = createInvariant(m0, "inv1");
		
		//clear the deltas that were created while adding the invariant
		deltas.clear();
		
		//remove the invariant
		inv.delete(true, null);
		
		//there should be some deltas.
		assertTrue(deltas.size() >0);
		
		ArrayList<IRodinElement> toRefresh = new ArrayList<IRodinElement>();
		//process the deltas
		for (IRodinElementDelta delta : deltas) {
			DeltaProcessor processor =  new DeltaProcessor(delta);
			toRefresh.addAll(processor.getToRefresh());
		}
		//the parent machine should get refreshed
		assertArray(toRefresh.toArray(), m0);
	}

	@Test
	public void processDeltaAddPOSequent() throws CoreException {
		setUpSubscription();

		// create a PORoot
		 IPORoot ipo = createIPORoot("m0");
		
		//clear the deltas that were created while adding the root
		deltas.clear();
		 
		//add a sequent
		createSequent(ipo);
		
		//there should be some deltas.
		assertTrue(deltas.size() >0);
		
		//process the deltas
		ArrayList<IRodinElement> toRefresh = new ArrayList<IRodinElement>();
		for (IRodinElementDelta delta : deltas) {
			DeltaProcessor processor =  new DeltaProcessor(delta);
			toRefresh.addAll(processor.getToRefresh());
		}
		//the parent PORoot should get refreshed
		assertArray(toRefresh.toArray(), ipo);
	}
	
	@Test
	public void processDeltaAddPSStatus() throws CoreException {
		
		setUpSubscription();

		// create a PSRoot
		 IPSRoot ips = createIPSRoot("mo");
		
		//clear the deltas that were created while adding the root
		deltas.clear();
		 
		//add a status
		createPSStatus(ips);
		
		//there should be some deltas.
		assertTrue(deltas.size() >0);
		
		//process the deltas
		ArrayList<IRodinElement> toRefresh = new ArrayList<IRodinElement>();
		for (IRodinElementDelta delta : deltas) {
			DeltaProcessor processor =  new DeltaProcessor(delta);
			toRefresh.addAll(processor.getToRefresh());
		}
		//the parent PSRoot should get refreshed
		assertArray(toRefresh.toArray(), ips);
	}

	@Test
	public void processDeltaChangePSStatus() throws CoreException {
		
		setUpSubscription();

		// create a PSRoot
		 IPSRoot ips = createIPSRoot("mo");
		
		//add a status
		IPSStatus status = createPSStatus(ips);

		//set the confidence
		status.setConfidence(IConfidence.UNATTEMPTED, null);

		//clear the deltas that were created before
		deltas.clear();
		
		//change the confidence
		status.setConfidence(IConfidence.DISCHARGED_MAX, null);
		 
		
		//there should be some deltas.
		assertTrue(deltas.size() >0);
		
		//process the deltas
		ArrayList<IRodinElement> toRefresh = new ArrayList<IRodinElement>();
		for (IRodinElementDelta delta : deltas) {
			DeltaProcessor processor =  new DeltaProcessor(delta);
			toRefresh.addAll(processor.getToRefresh());
		}
		//the parent PSRoot should get refreshed
		assertArray(toRefresh.toArray(), ips);
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
		createRefinesMachineClause(m1, m0, "refines1");
		assertTrue(m1.getRefinesClause("refines1").exists());
		createRefinesMachineClause(m2, m1, "refines2");
		assertTrue(m2.getRefinesClause("refines2").exists());
		createRefinesMachineClause(m3, m0, "refines3");
		assertTrue(m3.getRefinesClause("refines3").exists());
		
		//create a sees clause
		createSeesContextClause(m0, c0);
		
	}
	
	protected void setUpSubscription() {
		//process the project
		ModelController.processProject(rodinProject);
		project = ModelController.getProject(rodinProject);

		
		//subscribe for changes
		RodinCore.addElementChangedListener(listener);
		
	}
}
