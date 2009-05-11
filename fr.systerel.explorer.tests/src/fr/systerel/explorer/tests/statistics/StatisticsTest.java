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
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelAxiom;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelEvent;
import fr.systerel.internal.explorer.model.ModelInvariant;
import fr.systerel.internal.explorer.model.ModelMachine;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.statistics.Statistics;

/**
 * 
 *
 */
public class StatisticsTest extends ExplorerTest {

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
		
	}



	@Test
	public void equals() {
		Statistics stats1 = new Statistics(project);
		Statistics stats2 = new Statistics(project);
		
		assertTrue(stats1.equals(stats2));
	}

	@Test
	public void getParentLabelProject() {
		Statistics stats1 = new Statistics(project);
		
		assertEquals("P", stats1.getParentLabel());
	}

	@Test
	public void getParentLabelMachine() {
		Statistics stats1 = new Statistics(mach);
		
		assertEquals("m0", stats1.getParentLabel());
	}
	
	@Test
	public void getParentLabelContext() {
		Statistics stats1 = new Statistics(ctx);
		
		assertEquals("c0", stats1.getParentLabel());
	}

	@Test
	public void getParentLabelInvariant() throws RodinDBException {
		ModelInvariant inv = ModelController.getInvariant(inv1);
		Statistics stats = new Statistics(inv);

		assertEquals("inv1", stats.getParentLabel());
	}

	@Test
	public void getParentLabelAxiomNode() {
		Statistics stats1 = new Statistics(axiom_node);
		
		assertEquals("Axioms", stats1.getParentLabel());
	}
	
	
	@Test
	public void getTotalProject() {
		Statistics stats = new Statistics(project);
		
		assertEquals(7, stats.getTotal());
	}
	
	@Test
	public void getTotalMachine() {
		Statistics stats = new Statistics(mach);
		
		assertEquals(5, stats.getTotal());
	}

	@Test
	public void getTotalContext() {
		Statistics stats = new Statistics(ctx);
		
		assertEquals(2, stats.getTotal());
	}

	@Test
	public void getTotalPOnodeMachine() {
		Statistics stats = new Statistics(po_node_mach);
		
		assertEquals(5, stats.getTotal());
	}

	@Test
	public void getTotalPOnodeContext() {
		Statistics stats = new Statistics(po_node_ctx);
		
		assertEquals(2, stats.getTotal());
	}


	@Test
	public void getTotalInvariantNode() {
		Statistics stats = new Statistics(inv_node);
		
		assertEquals(4, stats.getTotal());
	}

	@Test
	public void getTotalEventNode() {
		Statistics stats = new Statistics(event_node);
		
		assertEquals(2, stats.getTotal());
	}
	
	@Test
	public void getTotalAxiomNode() {
		Statistics stats = new Statistics(axiom_node);
		
		assertEquals(2, stats.getTotal());
	}

	@Test
	public void getTotalTheoremMachine() {
		ModelInvariant thm = ModelController.getInvariant(thm2);
		Statistics stats = new Statistics(thm);
		
		assertEquals(1, stats.getTotal());
	}

	@Test
	public void getTotalTheoremContext() {
		ModelAxiom thm = ModelController.getAxiom(thm1);
		Statistics stats = new Statistics(thm);
		
		assertEquals(1, stats.getTotal());
	}

	@Test
	public void getTotalInvariant() {
		ModelInvariant inv = ModelController.getInvariant(inv1);
		Statistics stats = new Statistics(inv);
		
		assertEquals(3, stats.getTotal());
	}

	@Test
	public void getTotalEvent() {
		ModelEvent event = ModelController.getEvent(event1);
		Statistics stats = new Statistics(event);
		
		assertEquals(1, stats.getTotal());
	}
	
	@Test
	public void getTotalAxiom() {
		ModelAxiom axiom = ModelController.getAxiom(axiom1);
		Statistics stats = new Statistics(axiom);
		
		assertEquals(1, stats.getTotal());
	}
	
	@Test
	public void getManualProject() {
		Statistics stats = new Statistics(project);
		
		assertEquals(2, stats.getManual());
	}
	
	@Test
	public void getManualMachine() {
		Statistics stats = new Statistics(mach);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualContext() {
		Statistics stats = new Statistics(ctx);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualPOnodeMachine() {
		Statistics stats = new Statistics(po_node_mach);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualPOnodeContext() {
		Statistics stats = new Statistics(po_node_ctx);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualInvariantNode() {
		Statistics stats = new Statistics(inv_node);
		
		assertEquals(0, stats.getManual());
	}

	@Test
	public void getManualEventNode() {
		Statistics stats = new Statistics(event_node);
		
		assertEquals(1, stats.getManual());
	}
	
	@Test
	public void getManualAxiomNode() {
		Statistics stats = new Statistics(axiom_node);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualTheoremMachine() {
		ModelInvariant thm = ModelController.getInvariant(thm2);
		Statistics stats = new Statistics(thm);
		
		assertEquals(0, stats.getManual());
	}

	@Test
	public void getManualTheoremContext() {
		ModelAxiom thm = ModelController.getAxiom(thm1);
		Statistics stats = new Statistics(thm);
		
		assertEquals(1, stats.getManual());
	}

	@Test
	public void getManualInvariant() {
		ModelInvariant inv = ModelController.getInvariant(inv1);
		Statistics stats = new Statistics(inv);
		
		assertEquals(0, stats.getManual());
	}

	@Test
	public void getManualEvent() {
		ModelEvent event = ModelController.getEvent(event1);
		Statistics stats = new Statistics(event);
		
		assertEquals(1, stats.getManual());
	}
	
	@Test
	public void getManualAxiom() {
		ModelAxiom axiom = ModelController.getAxiom(axiom1);
		Statistics stats = new Statistics(axiom);
		
		assertEquals(0, stats.getManual());
	}
	
	@Test
	public void getAutoProject() {
		Statistics stats = new Statistics(project);
		
		assertEquals(1, stats.getAuto());
	}
	
	@Test
	public void getAutoMachine() {
		Statistics stats = new Statistics(mach);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoContext() {
		Statistics stats = new Statistics(ctx);
		
		assertEquals(0, stats.getAuto());
	}

	@Test
	public void getAutoPOnodeMachine() {
		Statistics stats = new Statistics(po_node_mach);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoPOnodeContext() {
		Statistics stats = new Statistics(po_node_ctx);
		
		assertEquals(0, stats.getAuto());
	}

	@Test
	public void getAutoInvariantNode() {
		Statistics stats = new Statistics(inv_node);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoEventNode() {
		Statistics stats = new Statistics(event_node);
		
		assertEquals(0, stats.getAuto());
	}
	
	@Test
	public void getAutoAxiomNode() {
		Statistics stats = new Statistics(axiom_node);
		
		assertEquals(0, stats.getAuto());
	}

	@Test
	public void getAutoTheoremMachine() {
		ModelInvariant thm = ModelController.getInvariant(thm2);
		Statistics stats = new Statistics(thm);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoTheoremContext() {
		ModelAxiom thm = ModelController.getAxiom(thm1);
		Statistics stats = new Statistics(thm);
		
		assertEquals(0, stats.getAuto());
	}

	@Test
	public void getAutoInvariant() {
		ModelInvariant inv = ModelController.getInvariant(inv1);
		Statistics stats = new Statistics(inv);
		
		assertEquals(1, stats.getAuto());
	}

	@Test
	public void getAutoEvent() {
		ModelEvent event = ModelController.getEvent(event1);
		Statistics stats = new Statistics(event);
		
		assertEquals(0, stats.getAuto());
	}
	
	@Test
	public void getAutoAxiom() {
		ModelAxiom axiom = ModelController.getAxiom(axiom1);
		Statistics stats = new Statistics(axiom);
		
		assertEquals(0, stats.getAuto());
	}
	
	@Test
	public void getReviewedProject() {
		Statistics stats = new Statistics(project);
		
		assertEquals(2, stats.getReviewed());
	}
	
	@Test
	public void getReviewedMachine() {
		Statistics stats = new Statistics(mach);
		
		assertEquals(2, stats.getReviewed());
	}

	@Test
	public void getReviewedContext() {
		Statistics stats = new Statistics(ctx);
		
		assertEquals(0, stats.getReviewed());
	}

	@Test
	public void getReviewedPOnodeMachine() {
		Statistics stats = new Statistics(po_node_mach);
		
		assertEquals(2, stats.getReviewed());
	}

	@Test
	public void getReviewedPOnodeContext() {
		Statistics stats = new Statistics(po_node_ctx);
		
		assertEquals(0, stats.getReviewed());
	}

	@Test
	public void getReviewedInvariantNode() {
		Statistics stats = new Statistics(inv_node);
		
		assertEquals(2, stats.getReviewed());
	}

	@Test
	public void getReviewedEventNode() {
		Statistics stats = new Statistics(event_node);
		
		assertEquals(0, stats.getReviewed());
	}
	
	@Test
	public void getReviewedAxiomNode() {
		Statistics stats = new Statistics(axiom_node);
		
		assertEquals(0, stats.getReviewed());
	}

	@Test
	public void getReviewedTheoremMachine() {
		ModelInvariant thm = ModelController.getInvariant(thm2);
		Statistics stats = new Statistics(thm);
		
		assertEquals(0, stats.getReviewed());
	}

	@Test
	public void getReviewedTheoremContext() {
		ModelAxiom thm = ModelController.getAxiom(thm1);
		Statistics stats = new Statistics(thm);
		
		assertEquals(0, stats.getReviewed());
	}

	@Test
	public void getReviewedInvariant() {
		ModelInvariant inv = ModelController.getInvariant(inv1);
		Statistics stats = new Statistics(inv);
		
		assertEquals(1, stats.getReviewed());
	}

	@Test
	public void getReviewedEvent() {
		ModelEvent event = ModelController.getEvent(event1);
		Statistics stats = new Statistics(event);
		
		assertEquals(0, stats.getReviewed());
	}
	
	@Test
	public void getReviewedAxiom() {
		ModelAxiom axiom = ModelController.getAxiom(axiom1);
		Statistics stats = new Statistics(axiom);
		
		assertEquals(0, stats.getReviewed());
	}

	@Test
	public void getUndischargedProject() {
		Statistics stats = new Statistics(project);
		
		assertEquals(2, stats.getUndischargedRest());
	}
	
	@Test
	public void getUndischargedMachine() {
		Statistics stats = new Statistics(mach);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedContext() {
		Statistics stats = new Statistics(ctx);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedPOnodeMachine() {
		Statistics stats = new Statistics(po_node_mach);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedPOnodeContext() {
		Statistics stats = new Statistics(po_node_ctx);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedInvariantNode() {
		Statistics stats = new Statistics(inv_node);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedEventNode() {
		Statistics stats = new Statistics(event_node);
		
		assertEquals(1, stats.getUndischargedRest());
	}
	
	@Test
	public void getUndischargedAxiomNode() {
		Statistics stats = new Statistics(axiom_node);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedTheoremMachine() {
		ModelInvariant thm = ModelController.getInvariant(thm2);
		Statistics stats = new Statistics(thm);
		
		assertEquals(0, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedTheoremContext() {
		ModelAxiom thm = ModelController.getAxiom(thm1);
		Statistics stats = new Statistics(thm);
		
		assertEquals(0, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedInvariant() {
		ModelInvariant inv = ModelController.getInvariant(inv1);
		Statistics stats = new Statistics(inv);
		
		assertEquals(1, stats.getUndischargedRest());
	}

	@Test
	public void getUndischargedEvent() {
		ModelEvent event = ModelController.getEvent(event1);
		Statistics stats = new Statistics(event);
		
		assertEquals(0, stats.getUndischargedRest());
	}
	
	@Test
	public void getUndischargedAxiom() {
		ModelAxiom axiom = ModelController.getAxiom(axiom1);
		Statistics stats = new Statistics(axiom);
		
		assertEquals(1, stats.getUndischargedRest());
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


	private void setUpContextPOs() throws RodinDBException {
		// create proof obligations for the context
		c0IPO = createIPORoot("c0");
		assertNotNull("c0IPO should be created successfully ", c0IPO);
		c0IPS = createIPSRoot("c0");
		assertNotNull("c0IPS should be created successfully ", c0IPS);

		//create an undischarged po
		sequent6 = createSequent(c0IPO, "sequent6");
		status6 = createPSStatus(c0IPS, "sequent6");
		status6.setConfidence(IConfidence.PENDING, null);

		source2 =  createPOSource(sequent6, "source2");
		source2.setSource(axiom1, null);
		
		//create a manually discharged po
		sequent7 = createSequent(c0IPO, "sequent7");
		status7 = createPSStatus(c0IPS, "sequent7");
		status7.setConfidence(IConfidence.DISCHARGED_MAX, null);
		status7.setHasManualProof(true, null);

		source1 =  createPOSource(sequent7, "source1");
		source1.setSource(thm1, null);
	}


	private void setUpContext() throws RodinDBException {
		// create a context
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		// create some elements in the context
		axiom1 = createAxiom(c0, "axiom1");
		thm1 =  createAxiomTheorem(c0, "thm1");
	}


	private void setUpMachinePOs() throws RodinDBException {
		// create proof obligations for the machine
		m0IPO = createIPORoot("m0");
		assertNotNull("m0IPO should be created successfully ", m0IPO);
		
		m0IPS = createIPSRoot("m0");
		assertNotNull("m0IPS should be created successfully ", m0IPS);
		
		//create an undischarged po
		sequent2 = createSequent(m0IPO, "sequent2");
		status2 = createPSStatus(m0IPS, "sequent2");
		status2.setConfidence(IConfidence.PENDING, null);

		source3 =  createPOSource(sequent2, "source3");
		source3.setSource(inv1, null);
		source9 =  createPOSource(sequent2, "source9");
		source9.setSource(event2, null);

		//create a reviewed po
		sequent3 = createSequent(m0IPO, "sequent3");
		status3 = createPSStatus(m0IPS, "sequent3");
		status3.setConfidence(IConfidence.REVIEWED_MAX, null);

		source4 =  createPOSource(sequent3, "source4");
		source4.setSource(inv1, null);
		
		//create a manually discharged po
		sequent4 = createSequent(m0IPO, "sequent4");
		status4 = createPSStatus(m0IPS, "sequent4");
		status4.setConfidence(IConfidence.DISCHARGED_MAX, null);
		status4.setHasManualProof(true, null);

		source5 =  createPOSource(sequent4, "source5");
		source5.setSource(event1, null);

		// create a auto. discharged po
		sequent5 = createSequent(m0IPO, "sequent5");
		status5 = createPSStatus(m0IPS, "sequent5");
		status5.setConfidence(IConfidence.DISCHARGED_MAX, null);

		source7 =  createPOSource(sequent5, "source7");
		source7.setSource(inv1, null);
		source8 =  createPOSource(sequent5, "source8");
		source8.setSource(thm2, null);

		//create a reviewed po
		sequent8 = createSequent(m0IPO, "sequent8");
		status8 = createPSStatus(m0IPS, "sequent8");
		status8.setConfidence(IConfidence.REVIEWED_MAX, null);

		source10 =  createPOSource(sequent8, "source10");
		source10.setSource(inv2, null);
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
	
	
}
