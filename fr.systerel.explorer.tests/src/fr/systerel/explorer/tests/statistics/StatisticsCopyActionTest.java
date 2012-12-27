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
package fr.systerel.explorer.tests.statistics;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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

import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelMachine;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.statistics.IStatistics;
import fr.systerel.internal.explorer.statistics.Statistics;
import fr.systerel.internal.explorer.statistics.StatisticsCopyAction;

/**
 * 
 *
 */
public class StatisticsCopyActionTest extends ExplorerTest {

	protected static IMachineRoot m0;
	protected static IContextRoot c0;
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
	protected static IStatistics stats1;
	protected static IStatistics stats2;
	protected static IStatistics stats3;
	protected static StatisticsCopyAction actionNoLabel;
	protected static StatisticsCopyAction actionLabel;
	
	
	

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		setUpMachine();
		setUpMachinePOs();
		
		setUpContext();
		setUpContextPOs();
		
		processProject();
		
		setUpStatistics();
				
		actionLabel = new StatisticsCopyAction(true);
		actionNoLabel = new StatisticsCopyAction(false);
	}

	

	@Test
	public void buildCopyStringSimpleNoLabel() {
		IStatistics[] input = {stats1};
		String result = actionNoLabel.buildCopyString(input);
		String expected = "7	1	2	2	2" +System.getProperty("line.separator");
		assertEquals(expected, result);
	}

	@Test
	public void buildCopyStringNoLabel() {
		IStatistics[] input = {stats1, stats2, stats3};
		String result = actionNoLabel.buildCopyString(input);
		String expected = "7	1	2	2	2" +System.getProperty("line.separator")
		 +"5	1	1	2	1" +System.getProperty("line.separator")
		 +"2	0	1	0	1" +System.getProperty("line.separator");
		assertEquals(expected, result);
	}


	@Test
	public void buildCopyStringSimpleLabel() {
		IStatistics[] input = {stats1};
		String result = actionLabel.buildCopyString(input);
		String expected = "P	7	1	2	2	2" +System.getProperty("line.separator");
		assertEquals(expected, result);
	}

	@Test
	public void buildCopyStringLabel() {
		IStatistics[] input = {stats1, stats2, stats3};
		String result = actionLabel.buildCopyString(input);
		String expected = "P	7	1	2	2	2" +System.getProperty("line.separator")
		 +"m0	5	1	1	2	1" +System.getProperty("line.separator")
		 +"c0	2	0	1	0	1" +System.getProperty("line.separator");
		assertEquals(expected, result);
	}
	

	private void setUpStatistics() {
		//create some statistics
		stats1 = new Statistics(project);
		assertEquals(7, stats1.getTotal());
		assertEquals(1, stats1.getAuto());
		assertEquals(2, stats1.getManual());
		assertEquals(2, stats1.getReviewed());
		assertEquals(2, stats1.getUndischargedRest());
		assertEquals("P", stats1.getParentLabel());
		stats2 = new Statistics(mach);
		assertEquals(5, stats2.getTotal());
		assertEquals(1, stats2.getAuto());
		assertEquals(1, stats2.getManual());
		assertEquals(2, stats2.getReviewed());
		assertEquals(1, stats2.getUndischargedRest());
		assertEquals("m0", stats2.getParentLabel());
		stats3 = new Statistics(ctx);
		assertEquals(2, stats3.getTotal());
		assertEquals(0, stats3.getAuto());
		assertEquals(1, stats3.getManual());
		assertEquals(0, stats3.getReviewed());
		assertEquals(1, stats3.getUndischargedRest());
		assertEquals("c0", stats3.getParentLabel());
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
