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

package fr.systerel.explorer.tests.statistics;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelMachine;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.statistics.AggregateStatistics;
import fr.systerel.internal.explorer.statistics.Statistics;

/**
 * 
 *
 */
public class AggregateStatisticsTest extends ExplorerTest {

	protected static IMachineRoot m0;
	protected static IContextRoot c0;
	protected static IElementNode po_node_mach;
	protected static IElementNode axiom_node;
	protected static IElementNode inv_node;
	protected static IElementNode event_node;
	protected static IElementNode po_node_ctx;
	protected static IInvariant inv1;
	protected static IInvariant inv2;
	protected static IEvent event1;
	protected static IEvent event2;
	protected static IAxiom axiom1;
	protected static IAxiom thm1;
	protected static IInvariant thm2;
	protected static IPORoot m0IPO;
	protected static IPSRoot m0IPS;
	protected static IPORoot c0IPO;
	protected static IPSRoot c0IPS;
	protected static IPOSequent sequent2;
	protected static IPOSequent sequent3;
	protected static IPOSequent sequent4;
	protected static IPOSequent sequent5;
	protected static IPOSequent sequent6;
	protected static IPOSequent sequent7;
	protected static IPOSequent sequent8;
	protected static IPSStatus status2;
	protected static IPSStatus status3;
	protected static IPSStatus status4;
	protected static IPSStatus status5;
	protected static IPSStatus status6;
	protected static IPSStatus status7;
	protected static IPSStatus status8;
	protected static IPOSource source1;
	protected static IPOSource source2;
	protected static IPOSource source3;
	protected static IPOSource source4;
	protected static IPOSource source5;
	protected static IPOSource source6;
	protected static IPOSource source7;
	protected static IPOSource source8;
	protected static IPOSource source9;
	protected static IPOSource source10;
	protected static ModelProject project;
	protected static ModelMachine mach;
	protected static ModelContext ctx;
	protected static IRodinProject rodinProject2;
	protected static ModelProject project2;
	protected static IMachineRoot m1;
	protected static IPORoot m1IPO;
	protected static IPSRoot m1IPS;
	protected static IInvariant inv3;
	protected static IPOSequent sequent9;
	protected static IPSStatus status9;
	protected static IPOSource source11;
	protected static ModelMachine mach2;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		setUpMachine();
		
		setUpMachinePOs();
		
		setUpContext();

		setUpContextPOs();
		
		processProject();
		
		setUpNodes();
		
		setUpSecondProject();
	}


	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		deleteProject("P2");
		ModelController.removeProject(rodinProject2);
		
	}

	@Test
	public void equals() {
		Statistics[] input1 =  {new Statistics(mach), new Statistics(ctx)};
		AggregateStatistics stats1 = new AggregateStatistics(input1);
		Statistics[] input2 =  {new Statistics(mach), new Statistics(ctx)};
		AggregateStatistics stats2 = new AggregateStatistics(input2);
		
		assertTrue(stats1.equals(stats2));
		
	}

	@Test
	public void getTotalProject() {
		Statistics stats1 = new Statistics(project);
		Statistics stats2 = new Statistics(project2);
		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(8, stats.getTotal());
	}
	
	@Test
	public void getTotalMachines() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(mach2);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(6, stats.getTotal());
	}

	@Test
	public void getTotalContextMachine() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(ctx);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(7, stats.getTotal());
	}

	@Test
	public void getTotalPOnodes() {
		Statistics stats1 = new Statistics(po_node_ctx);
		Statistics stats2 = new Statistics(po_node_mach);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(stats1.getTotal() + stats2.getTotal(), stats.getTotal());
	}

	@Test
	public void getTotalInvariantEventNode() {
		Statistics stats1 = new Statistics(inv_node);
		Statistics stats2 = new Statistics(event_node);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(5, stats.getTotal());
	}
	

	@Test
	public void getTotalElements() {
		Statistics stats1 = new Statistics(ModelController.getInvariant(inv1));
		Statistics stats2 = new Statistics(ModelController.getEvent(event1));
		Statistics stats3 = new Statistics(ModelController.getAxiom(axiom1));
		Statistics stats4 = new Statistics(ModelController.getAxiom(thm1));
		Statistics stats5 = new Statistics(ModelController.getInvariant(inv3));
		Statistics[] st = {stats1, stats2, stats3, stats4, stats5};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(7, stats.getTotal());
	}

	
	@Test
	public void getManualProject() {
		Statistics stats1 = new Statistics(project);
		Statistics stats2 = new Statistics(project2);
		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getManual());
	}
	
	@Test
	public void getManualMachines() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(mach2);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualContextMachine() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(ctx);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getManual());
	}

	@Test
	public void getManualPOnodes() {
		Statistics stats1 = new Statistics(po_node_ctx);
		Statistics stats2 = new Statistics(po_node_mach);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getManual());
	}

	@Test
	public void getManualInvariantEventNode() {
		Statistics stats1 = new Statistics(inv_node);
		Statistics stats2 = new Statistics(event_node);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getManual());
	}
	

	@Test
	public void getManualElements() {
		Statistics stats1 = new Statistics(ModelController.getInvariant(inv1));
		Statistics stats2 = new Statistics(ModelController.getEvent(event1));
		Statistics stats3 = new Statistics(ModelController.getAxiom(axiom1));
		Statistics stats4 = new Statistics(ModelController.getAxiom(thm1));
		Statistics stats5 = new Statistics(ModelController.getInvariant(inv3));

		Statistics[] st = {stats1, stats2, stats3, stats4, stats5};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getManual());
	}

	@Test
	public void getAutoProject() {
		Statistics stats1 = new Statistics(project);
		Statistics stats2 = new Statistics(project2);
		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getAuto());
	}
	
	@Test
	public void getAutoMachines() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(mach2);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoContextMachine() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(ctx);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoPOnodes() {
		Statistics stats1 = new Statistics(po_node_ctx);
		Statistics stats2 = new Statistics(po_node_mach);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoInvariantEventNode() {
		Statistics stats1 = new Statistics(inv_node);
		Statistics stats2 = new Statistics(event_node);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getAuto());
	}
	

	@Test
	public void getAutoElements() {
		Statistics stats1 = new Statistics(ModelController.getInvariant(inv1));
		Statistics stats2 = new Statistics(ModelController.getEvent(event1));
		Statistics stats3 = new Statistics(ModelController.getAxiom(axiom1));
		Statistics stats4 = new Statistics(ModelController.getAxiom(thm1));
		Statistics stats5 = new Statistics(ModelController.getInvariant(inv3));

		Statistics[] st = {stats1, stats2, stats3, stats4, stats5};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getReviewedProject() {
		Statistics stats1 = new Statistics(project);
		Statistics stats2 = new Statistics(project2);
		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getReviewed());
	}
	
	@Test
	public void getReviewedMachines() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(mach2);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getReviewed());
	}

	@Test
	public void getReviewedContextMachine() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(ctx);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getReviewed());
	}

	@Test
	public void getReviewedPOnodes() {
		Statistics stats1 = new Statistics(po_node_ctx);
		Statistics stats2 = new Statistics(po_node_mach);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getReviewed());
	}

	@Test
	public void getReviewedInvariantEventNode() {
		Statistics stats1 = new Statistics(inv_node);
		Statistics stats2 = new Statistics(event_node);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getReviewed());
	}
	

	@Test
	public void getReviewedElements() {
		Statistics stats1 = new Statistics(ModelController.getInvariant(inv1));
		Statistics stats2 = new Statistics(ModelController.getEvent(event1));
		Statistics stats3 = new Statistics(ModelController.getAxiom(axiom1));
		Statistics stats4 = new Statistics(ModelController.getAxiom(thm1));
		Statistics stats5 = new Statistics(ModelController.getInvariant(inv3));

		Statistics[] st = {stats1, stats2, stats3, stats4, stats5};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getReviewed());
	}
	
	@Test
	public void getUndischargedRestProject() {
		Statistics stats1 = new Statistics(project);
		Statistics stats2 = new Statistics(project2);
		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(3, stats.getUndischargedRest());
	}
	
	@Test
	public void getUndischargedRestMachines() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(mach2);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedRestContextMachine() {
		Statistics stats1 = new Statistics(mach);
		Statistics stats2 = new Statistics(ctx);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedRestPOnodes() {
		Statistics stats1 = new Statistics(po_node_ctx);
		Statistics stats2 = new Statistics(po_node_mach);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(2, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedRestInvariantEventNode() {
		Statistics stats1 = new Statistics(inv_node);
		Statistics stats2 = new Statistics(event_node);

		Statistics[] st = {stats1, stats2};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(1, stats.getUndischargedRest());
	}
	

	@Test
	public void getUndischargedRestElements() {
		Statistics stats1 = new Statistics(ModelController.getInvariant(inv1));
		Statistics stats2 = new Statistics(ModelController.getEvent(event1));
		Statistics stats3 = new Statistics(ModelController.getAxiom(axiom1));
		Statistics stats4 = new Statistics(ModelController.getAxiom(thm1));
		Statistics stats5 = new Statistics(ModelController.getInvariant(inv3));
		Statistics[] st = {stats1, stats2, stats3, stats4, stats5};
		AggregateStatistics stats = new AggregateStatistics(st);
		
		assertEquals(3, stats.getUndischargedRest());
	}
	
	/**
	 * sets up a second project 
	 * @throws CoreException 
	 */
	protected void setUpSecondProject() throws CoreException {
		//create a second project
		rodinProject2 = createRodinProject("P2");

		// create a machine
		m1 = createMachine("m1", rodinProject2);
		assertNotNull("m1 should be created successfully ", m1);
		
		// create some elements in the machine
		inv3 = createInvariant(m1, "inv3");
		
		// create proof obligations for the machine
		m1IPO = createIPORoot("m1", rodinProject2);
		assertNotNull("m1IPO should be created successfully ", m1IPO);
		
		m1IPS = createIPSRoot("m1", rodinProject2);
		assertNotNull("m1IPS should be created successfully ", m1IPS);
		
		//create an undischarged po
		sequent9 = createSequent(m1IPO);
		status9 = createPSStatus(m1IPS);
		status9.setConfidence(IConfidence.PENDING, null);

		source11 =  createPOSource(sequent9);
		source11.setSource(inv3, null);
		
		ModelController.processProject(rodinProject2);
		project2 = ModelController.getProject(rodinProject2);
		
		mach2 = ModelController.getMachine(m1);
		mach2.processPORoot();
		mach2.processPSRoot();
		
	}
	
	private void setUpContext() throws RodinDBException {
		// create a context
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		// create some elements in the context
		axiom1 = createAxiom(c0, "axiom1");
		thm1 =  createAxiomTheorem(c0, "thm1");
	}


	private void setUpMachine() throws RodinDBException {
		// create a machine
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		
		// create some elements in the machine
		inv1 = createInvariant(m0, "inv1");
		inv2 = createInvariant(m0, "inv2");
		event1 = createEvent(m0, "event1");
		event2 = createEvent(m0, "event2");
		thm2 =  createInvariantTheorem(m0, "thm2");
	}


	private void setUpMachinePOs() throws RodinDBException {
		// create proof obligations for the machine
		m0IPO = createIPORoot("m0");
		assertNotNull("m0IPO should be created successfully ", m0IPO);
		
		m0IPS = createIPSRoot("m0");
		assertNotNull("m0IPS should be created successfully ", m0IPS);
		
		//create an undischarged po
		sequent2 = createSequent(m0IPO);
		status2 = createPSStatus(m0IPS);
		status2.setConfidence(IConfidence.PENDING, null);

		source3 =  createPOSource(sequent2);
		source3.setSource(inv1, null);
		source9 =  createPOSource(sequent2);
		source9.setSource(event2, null);

		//create a reviewed po
		sequent3 = createSequent(m0IPO);
		status3 = createPSStatus(m0IPS);
		status3.setConfidence(IConfidence.REVIEWED_MAX, null);

		source4 =  createPOSource(sequent3);
		source4.setSource(inv1, null);
		
		//create a manually discharged po
		sequent4 = createSequent(m0IPO);
		status4 = createPSStatus(m0IPS);
		status4.setConfidence(IConfidence.DISCHARGED_MAX, null);
		status4.setHasManualProof(true, null);

		source5 =  createPOSource(sequent4);
		source5.setSource(event1, null);

		// create a auto. discharged po
		sequent5 = createSequent(m0IPO);
		status5 = createPSStatus(m0IPS);
		status5.setConfidence(IConfidence.DISCHARGED_MAX, null);

		source7 =  createPOSource(sequent5);
		source7.setSource(inv1, null);
		source8 =  createPOSource(sequent5);
		source8.setSource(thm2, null);

		//create a reviewed po
		sequent8 = createSequent(m0IPO);
		status8 = createPSStatus(m0IPS);
		status8.setConfidence(IConfidence.REVIEWED_MAX, null);

		source10 =  createPOSource(sequent8);
		source10.setSource(inv2, null);
	}


	private void setUpContextPOs() throws RodinDBException {
		c0IPO = createIPORoot("c0");
		assertNotNull("c0IPO should be created successfully ", c0IPO);
		c0IPS = createIPSRoot("c0");
		assertNotNull("c0IPS should be created successfully ", c0IPS);

		//create an undischarged po
		sequent6 = createSequent(c0IPO);
		status6 = createPSStatus(c0IPS);
		status6.setConfidence(IConfidence.PENDING, null);

		source2 =  createPOSource(sequent6);
		source2.setSource(axiom1, null);
		
		//create a manually discharged po
		sequent7 = createSequent(c0IPO);
		status7 = createPSStatus(c0IPS);
		status7.setConfidence(IConfidence.DISCHARGED_MAX, null);
		status7.setHasManualProof(true, null);

		source1 =  createPOSource(sequent7);
		source1.setSource(thm1, null);
	}


	private void setUpNodes() {
		po_node_mach = mach.po_node;
		inv_node = mach.invariant_node;
		event_node = mach.event_node;
		
		axiom_node = ctx.axiom_node;
		po_node_ctx = ctx.po_node;
	}


	private void processProject() {
		ModelController.processProject(rodinProject);
		project = ModelController.getProject(rodinProject);
		mach = ModelController.getMachine(m0);
		ctx = ModelController.getContext(c0);
		mach.processPORoot();
		mach.processPSRoot();
		ctx.processPORoot();
		ctx.processPSRoot();
	}
	
	
}
