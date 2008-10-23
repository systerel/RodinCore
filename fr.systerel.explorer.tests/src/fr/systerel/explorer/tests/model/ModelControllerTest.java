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

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
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
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.seqprover.IConfidence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.masterDetails.statistics.Statistics;
import fr.systerel.explorer.model.ModelAxiom;
import fr.systerel.explorer.model.ModelContext;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelEvent;
import fr.systerel.explorer.model.ModelInvariant;
import fr.systerel.explorer.model.ModelMachine;
import fr.systerel.explorer.model.ModelProject;
import fr.systerel.explorer.model.ModelTheorem;
import fr.systerel.explorer.navigator.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;

/**
 * 
 *
 */
public class ModelControllerTest extends ExplorerTest {
	
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
	public void processDelta() throws RodinDBException {
		//process the project
		ModelController.processProject(rodinProject);
		project = ModelController.getProject(rodinProject);

		//subscribe for changes
		RodinCore.addElementChangedListener(listener);
		
		//change some stuff in the database
		IMachineRoot m5 = createMachine("m5");
		
		System.out.println(deltas.size());
		//process the deltas
		
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
}
