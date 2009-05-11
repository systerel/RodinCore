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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.widgets.TableColumn;
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
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelMachine;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.statistics.IStatistics;
import fr.systerel.internal.explorer.statistics.Statistics;
import fr.systerel.internal.explorer.statistics.StatisticsDetailsComparator;

/**
 * 
 *
 */
public class StatisticsDetailsComparatorTest extends ExplorerTest {
	
	protected static IMachineRoot m0;
	protected static IContextRoot c0;
	protected static IElementNode po_node_mach;
	protected static IElementNode axiom_node;
	protected static IElementNode inv_node;
	protected static IElementNode event_node;
	protected static IElementNode thm_node_mach;
	protected static IElementNode po_node_ctx;
	protected static IElementNode thm_node_ctx;
	protected static IInvariant inv1;
	protected static IInvariant inv2;
	protected static IEvent event1;
	protected static IEvent event2;
	protected static IAxiom axiom1;
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
	protected static IStatistics stats1;
	protected static IStatistics stats2;
	protected static ModelProject project;
	protected static ModelMachine mach;
	protected static TableColumn column;
	
	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setUpStatistics();
		
		
	}
	
	@Test
	public void compareNameAscend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.NAME;
		
		assertTrue(comparator.compare(stats1, stats2) >0);
		
	}
 	

	@Test
	public void compareTotalAscend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.TOTAL;
	
		assertTrue(comparator.compare(stats1, stats2) >0);
	}
	
	@Test
	public void compareAutoAscend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.AUTO;
	
		assertTrue(comparator.compare(stats1, stats2) >0);
	}

	@Test
	public void compareManualAscend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.MANUAL;
	
		assertTrue(comparator.compare(stats1, stats2) <0);
	}
	
	@Test
	public void compareReviewedAscend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.REVIEWED;
	
		assertTrue(comparator.compare(stats1, stats2) >0);
	}

	@Test
	public void compareUndischargedRestAscend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.UNDISCHARGED;
	
		assertTrue(comparator.compare(stats1, stats2) ==0);
	}

	@Test
	public void compareNameDescend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.NAME;
		comparator.setOrder(!StatisticsDetailsComparator.ASCENDING);
		
		assertTrue(comparator.compare(stats1, stats2) <0);
		
	}

	@Test
	public void compareTotalDescend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.TOTAL;
		comparator.setOrder(!StatisticsDetailsComparator.ASCENDING);
	
		assertTrue(comparator.compare(stats1, stats2) <0);
	}
	
	@Test
	public void compareAutoDescend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.AUTO;
	
		comparator.setOrder(!StatisticsDetailsComparator.ASCENDING);
		
		assertTrue(comparator.compare(stats1, stats2) <0);
	}

	@Test
	public void compareManualDescend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.MANUAL;
	
		comparator.setOrder(!StatisticsDetailsComparator.ASCENDING);
		
		assertTrue(comparator.compare(stats1, stats2) >0);
	}
	
	@Test
	public void compareReviewedDescend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.REVIEWED;
	
		comparator.setOrder(!StatisticsDetailsComparator.ASCENDING);
		
		assertTrue(comparator.compare(stats1, stats2) <0);
	}

	@Test
	public void compareUndischargedRestDescend() {
		StatisticsDetailsComparator comparator = StatisticsDetailsComparator.UNDISCHARGED;
	
		comparator.setOrder(!StatisticsDetailsComparator.ASCENDING);
		
		assertTrue(comparator.compare(stats1, stats2) ==0);
	}
	
	
	protected void setUpStatistics() throws RodinDBException {
		setUpMachine();
		setUpMachinePOs();
		
		processProject();
		
		inv_node = mach.invariant_node;
		event_node = mach.event_node;
		
		//create statistics
		stats1 = new Statistics(inv_node);
		stats2 = new Statistics(event_node);
		
		assertEquals( "Invariants", stats1.getParentLabel());
		assertEquals("Events", stats2.getParentLabel());
		assertEquals(4, stats1.getTotal());
		assertEquals(2, stats2.getTotal());
		assertEquals(1, stats1.getAuto());
		assertEquals(0, stats2.getAuto());
		assertEquals(0, stats1.getManual());
		assertEquals(1, stats2.getManual());
		assertEquals(2, stats1.getReviewed());
		assertEquals(0, stats2.getReviewed());
		assertEquals(1, stats1.getUndischargedRest());
		assertEquals(1, stats2.getUndischargedRest());
				
	}

	private void processProject() {
		ModelController.processProject(rodinProject);
		project = ModelController.getProject(rodinProject);
		mach = ModelController.getMachine(m0);
		mach.processPORoot();
		mach.processPSRoot();
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
	}
}
