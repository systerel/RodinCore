/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer.tests.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * 
 *
 */
public class ModelContextTest extends ExplorerTest {

	protected static IContextRoot contextRoot;
	protected static ModelContext context;
	protected static IAxiom thm1;
	protected static IAxiom axm1;
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
	
	
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		//create a context
		contextRoot = createContext("c0");
		context =  new ModelContext(contextRoot);

		//add some elements to the context
		thm1 = createAxiomTheorem(contextRoot, "thm1");
		axm1 = createAxiom(contextRoot, "axm1");
		
		setUpProofObligations();
		
	}

	
	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		ModelController.removeProject(rodinProject);
		
	}

	@Test
	public void getModelParent() {
		//project needs to be processed, otherwise the parent can't be found
		ModelController.processProject(rodinProject);
		
		assertEquals(ModelController.getProject(rodinProject), context.getModelParent());
	}
	
	@Test
	public void processChildren() {
		context.processChildren();
		//check that the children have been transformed into ModelElements
		assertModel(axm1, context.getAxiom(axm1));
		assertModel(thm1, context.getAxiom(thm1));
	}

	@Test
	public void processPORoot() {
		context.processChildren();
		context.processPORoot();
		//check that proof obligations have been added to the model
		assertModelPOSequent(context.getProofObligations(), sequent1, sequent2);

		//check that the proof obligations have been added to the appropriate children
		assertModelPOSequent(context.getAxiom(thm1).getProofObligations(), sequent1);
		assertModelPOSequent(context.getAxiom(axm1).getProofObligations(), sequent1, sequent2);
	}

	@Test
	public void processPSRoot() {
		context.processChildren();
		//the POs need to be processed first.
		context.processPORoot();

		context.processPSRoot();
		//check that statuses haven been added to the proof obligations
		assertModelPSStatus(context.getProofObligations(), status1, status2);

	}
	
	@Test
	public void getRestMachines() throws RodinDBException {
		
		IContextRoot c1 = createContext("c1");
		ModelContext mc1=  new ModelContext(c1);

		IContextRoot c2 = createContext("c2");
		ModelContext mc2=  new ModelContext(c2);
		
		IContextRoot c3 = createContext("c3");
		ModelContext mc3=  new ModelContext(c3);

		IContextRoot c4 = createContext("c4");
		ModelContext mc4=  new ModelContext(c4);
		
		//add some refinements to the model.
		context.addExtendedByContext(mc1);
		context.addExtendedByContext(mc3);
		context.addExtendedByContext(mc4);
		
		//set the longest branch
		ArrayList<ModelContext> branch = new ArrayList<ModelContext>();
		branch.add(mc1);
		branch.add(mc2);
		context.setLongestBranch(branch);
		
		//check the rest machines
		assertArray(context.getRestContexts().toArray(), mc3, mc4);
		
	}
	
	private void setUpProofObligations() throws RodinDBException {
		//add some proof obligations to the context
		ipo = createIPORoot("c0");
		assertNotNull("ipo should be created successfully ", ipo);
		
		ips = createIPSRoot("c0");
		assertNotNull("ips should be created successfully ", ips);
		
		//add some sequents
		sequent1 = createSequent(ipo);
		status1 = createPSStatus(ips);

		source1 =  createPOSource(sequent1);
		source1.setSource(axm1, null);
		source2 =  createPOSource(sequent1);
		source2.setSource(thm1, null);

		
		sequent2 = createSequent(ipo);
		status2 = createPSStatus(ips);
		source3 =  createPOSource(sequent2);
		source3.setSource(axm1, null);
	}

	
}
