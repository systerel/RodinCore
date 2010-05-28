/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
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

import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBRoot;
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
import fr.systerel.internal.explorer.model.ModelPOContainer;
import fr.systerel.internal.explorer.model.ModelProject;
import fr.systerel.internal.explorer.statistics.AggregateStatistics;
import fr.systerel.internal.explorer.statistics.IStatistics;
import fr.systerel.internal.explorer.statistics.Statistics;
import fr.systerel.internal.explorer.statistics.StatisticsDetailsContentProvider;

/**
 * 
 *
 */
public class StatisticsDetailsContentProviderTest extends ExplorerTest {

	private static StatisticsDetailsContentProvider contentProvider;
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
	protected static IPOSource source2;
	protected static IPOSource source3;
	protected static IPOSource source4;
	protected static IPOSource source5;
	protected static IPOSource source6;
	protected static IPOSource source7;
	protected static IPOSource source9;
	protected static IPOSource source10;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new StatisticsDetailsContentProvider();
		
		setUpMachine();
		setUpMachinePOs();
		
		setUpContext();
		setUpContextPOs();
		
		ModelController.processProject(rodinProject);
		
		setUpNodes();
		
	}


	@Test
	public void getElementsProject() {
		final Object[] input = { rodinProject.getProject() };
		final Object[] output = contentProvider.getElements(input);
		final ModelMachine mach = ModelController.getMachine(m0);
		final ModelContext ctx = ModelController.getContext(c0);
		final ModelProject prj = ModelController.getProject(rodinProject);

		final Statistics s1 = new Statistics(prj);
		final Statistics s2 = new Statistics(ctx);
		final Statistics s3 = new Statistics(mach);
		final Statistics[] agg = { s1 };

		assertArray(output, new AggregateStatistics(agg), s2, s3);

	}

	@Test
	public void getElementsMachine() {
		processRoot(m0);
		final Object[] input = { m0 };
		final Object[] output = contentProvider.getElements(input);
		final ModelMachine mm0 = ModelController.getMachine(m0);
		final ModelInvariant i1 = ModelController.getInvariant(inv1);
		final ModelInvariant i2 = ModelController.getInvariant(inv2);
		final ModelEvent e1 = ModelController.getEvent(event1);
		final ModelEvent e2 = ModelController.getEvent(event2);

		final Statistics s1 = new Statistics(mm0);
		final Statistics s2 = new Statistics(i1);
		final Statistics s3 = new Statistics(i2);
		final Statistics s4 = new Statistics(e1);
		final Statistics s5 = new Statistics(e2);

		final IStatistics[] agg = { s1 };
		assertArray(output, new AggregateStatistics(agg), s2, s3, s4, s5);
	}

	@Test
	public void getElementsContext() {
		processRoot(c0);
		final Object[] input = { c0 };
		final Object[] output = contentProvider.getElements(input);
		final ModelContext mc0 = ModelController.getContext(c0);
		final ModelAxiom a1 = ModelController.getAxiom(axiom1);

		final Statistics s1 = new Statistics(mc0);
		final Statistics s2 = new Statistics(a1);
		final Statistics[] agg = { s1 };

		assertArray(output, new AggregateStatistics(agg), s2);

	}
	
	@Test
	public void getElementsPOnodeMachine() {
		processRoot(m0);
		final Object[] input = { po_node_mach };
		final Object[] output = contentProvider.getElements(input);
		final ModelInvariant i1 = ModelController.getInvariant(inv1);
		final ModelInvariant i2 = ModelController.getInvariant(inv2);
		final ModelEvent e1 = ModelController.getEvent(event1);
		final ModelEvent e2 = ModelController.getEvent(event2);

		final Statistics s1 = new Statistics(po_node_mach);
		final Statistics s2 = new Statistics(i1);
		final Statistics s3 = new Statistics(i2);
		final Statistics s4 = new Statistics(e1);
		final Statistics s5 = new Statistics(e2);

		final Statistics[] agg = { s1 };
		assertArray(output, new AggregateStatistics(agg), s2, s3, s4, s5);
	}

	@Test
	public void getElementsPOnodeContext() {
		processRoot(c0);
		final Object[] input = { po_node_ctx };
		final Object[] output = contentProvider.getElements(input);
		final ModelAxiom a1 = ModelController.getAxiom(axiom1);
		final Statistics s1 = new Statistics(po_node_ctx);
		final Statistics s2 = new Statistics(a1);

		final Statistics[] agg = { s1 };
		assertArray(output, new AggregateStatistics(agg), s2);

	}
	
	@Test
	public void getElementsEventNode() {
		processRoot(m0);
		final Object[] input = { event_node };
		final Object[] output = contentProvider.getElements(input);
		final ModelEvent e1 = ModelController.getEvent(event1);
		final ModelEvent e2 = ModelController.getEvent(event2);

		final Statistics s1 = new Statistics(event_node);
		final Statistics s2 = new Statistics(e1);
		final Statistics s3 = new Statistics(e2);

		final Statistics[] agg = { s1 };
		assertArray(output, new AggregateStatistics(agg), s2, s3);

	}


	@Test
	public void getElementsInvariantNode() {
		processRoot(m0);
		final Object[] input = { inv_node };
		final Object[] output = contentProvider.getElements(input);
		final ModelInvariant i1 = ModelController.getInvariant(inv1);
		final ModelInvariant i2 = ModelController.getInvariant(inv2);

		final Statistics s1 = new Statistics(inv_node);
		final Statistics s2 = new Statistics(i1);
		final Statistics s3 = new Statistics(i2);

		final Statistics[] agg = { s1 };
		assertArray(output, new AggregateStatistics(agg), s2, s3);

	}

	@Test
	public void getElementsAxiomNode() {
		// get statistics for an axiom node
		processRoot(c0);
		final Object[] input = { axiom_node };
		final Object[] output = contentProvider.getElements(input);
		final ModelAxiom a1 = ModelController.getAxiom(axiom1);
		final Statistics s1 = new Statistics(axiom_node);
		final Statistics s2 = new Statistics(a1);

		final Statistics[] agg = { s1 };
		assertArray(output, new AggregateStatistics(agg), s2);

	}


	@Test
	public void getElementsComboNodes() {
		processRoot(m0);
		final Object[] input = { inv_node, event_node };
		final Statistics is1 = new Statistics(inv_node);
		final Statistics es1 = new Statistics(event_node);
		final IStatistics[] agg = { is1, es1 };
		final Object[] output = contentProvider.getElements(input);
		assertArray(output, new AggregateStatistics(agg), is1, es1);

	}

	@Test
	public void getElementsComboInvariantEvent() {
		// get statistics for an invariant and an event
		processRoot(m0);
		final Object[] input = { inv1, event2 };
		final Object[] output = contentProvider.getElements(input);
		final ModelInvariant i1 = ModelController.getInvariant(inv1);
		final ModelEvent e2 = ModelController.getEvent(event2);

		final Statistics s1 = new Statistics(i1);
		final Statistics s2 = new Statistics(e2);
		final IStatistics[] agg = { s1, s2 };

		assertArray(output, new AggregateStatistics(agg), s1, s2);

	}

	private void processRoot(IEventBRoot root) {
		if (root instanceof IMachineRoot) {
			final IMachineRoot ebMachine = (IMachineRoot) root;
			final ModelMachine machine = ModelController.getMachine(ebMachine);
			machine.processPORoot();
			machine.processPSRoot();
		}
		if (root instanceof IContextRoot) {
			final IContextRoot ebContext = (IContextRoot) root;
			final ModelContext context = ModelController.getContext(ebContext);
			context.processPORoot();
			context.processPSRoot();
		}
	}

	@Test
	public void getElementsComboMachinecontext() {
		// get statistics for a machine and a context
		final Object[] input = { c0, m0 };
		final Object[] output = contentProvider.getElements(input);
		final ModelContext cont0 = (ModelController.getContext(c0));
		final ModelMachine mach0 = (ModelController.getMachine(m0));

		final Statistics s1 = new Statistics(cont0);
		final Statistics s2 = new Statistics(mach0);
		final IStatistics[] agg = { s1, s2 };
		final AggregateStatistics aggregate = new AggregateStatistics(agg);

		assertArray(output, aggregate, s1, s2);
	}
	

	private void setUpNodes() {
		po_node_mach = ModelController.getMachine(m0).po_node;
		assertNotNull("the node should be created successfully ", po_node_mach);
		inv_node = ModelController.getMachine(m0).invariant_node;
		event_node = ModelController.getMachine(m0).event_node;
		
		axiom_node = ModelController.getContext(c0).axiom_node;
		po_node_ctx = ModelController.getContext(c0).po_node;
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

	}


	private void setUpContext() throws RodinDBException {
		// create a context
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		// create some elements in the context
		axiom1 = createAxiom(c0, "axiom1");
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
