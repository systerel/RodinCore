/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.explorer.tests.utils;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
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
import fr.systerel.internal.explorer.navigator.ExplorerUtils;
import fr.systerel.internal.explorer.navigator.actionProviders.Messages;

public class ExplorerUtilsTest extends ExplorerTest {

	private static IContextRoot c0;
	private static IPORoot c0IPO;
	private static IPSRoot c0IPS;
	private static IPOSequent sequent1;
	private static IPSStatus status1;
	private static IPOSequent sequent2;
	private static IPSStatus status2;
	private static IPOSequent sequent3;
	private static IPSStatus status3;
	private static IPOSequent sequent4;
	private static IPSStatus status4;
	private static IPOSource source1;
	private static IPOSource source2;
	private static IPOSource source3;
	private static IPOSource source4;
	private static IElementNode node;
	private static IAxiom axiom1;
	private static IAxiom thm1;
	private static IAxiom thm2;

	@Test
	public void testGetStatusesNoPO() throws Exception {

		final IMachineRoot machineRoot = createMachine("m0");
		final ModelMachine machine = new ModelMachine(machineRoot);

		final SubMonitor subMonitor = SubMonitor
				.convert(new NullProgressMonitor(),
						Messages.dialogs_replayingProofs, 10);

		assertEquals("Expected no statuses", Collections.emptySet(),
				ExplorerUtils
						.getStatuses(new Object[] { machine }, false, subMonitor));

	}

	@Test
	public void testGetStatuses() throws Exception {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		final Set<IPSStatus> statuses = new HashSet<IPSStatus>();
		statuses.addAll(asList(status1, status2, status3, status4));

		assertEquals("Unexpected statuses", statuses,
				ExplorerUtils.getStatuses(new Object[] { c0 }, false, null));

	}

	@Test
	public void testGetStatusesPendingOnly() throws Exception {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		final Set<IPSStatus> statuses = new HashSet<IPSStatus>();
		statuses.addAll(asList(status3, status4));

		assertEquals("Unexpected statuses", statuses,
				ExplorerUtils.getStatuses(new Object[] { c0 }, true, null));
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpContext();
		setUpContextPOs();

		ModelController.processProject(rodinProject);

		createNodes();

	}

	private void setUpContext() throws RodinDBException {
		// create a context
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		// create some elements in the context
		axiom1 = createAxiom(c0, "axiom1");
		thm1 = createAxiomTheorem(c0, "thm1");
		thm2 = createAxiomTheorem(c0, "thm2");
	}

	private void createNodes() {
		node = ModelController.getContext(c0).po_node;
		assertNotNull("the node should be created successfully ", node);
	}

	private void setUpContextPOs() throws RodinDBException {
		c0IPO = createIPORoot("c0");
		assertNotNull("c0IPO should be created successfully ", c0IPO);

		c0IPS = createIPSRoot("c0");
		assertNotNull("c0IPS should be created successfully ", c0IPS);

		sequent1 = createSequent(c0IPO);
		status1 = createPSStatus(c0IPS);
		status1.setConfidence(IConfidence.DISCHARGED_MAX, null);
		source1 = createPOSource(sequent1);
		source1.setSource(axiom1, null);

		sequent2 = createSequent(c0IPO);
		status2 = createPSStatus(c0IPS);
		status2.setConfidence(IConfidence.REVIEWED_MAX, null);
		source2 = createPOSource(sequent2);
		source2.setSource(axiom1, null);

		sequent3 = createSequent(c0IPO);
		status3 = createPSStatus(c0IPS);
		status3.setConfidence(IConfidence.PENDING, null);
		source3 = createPOSource(sequent3);
		source3.setSource(thm1, null);

		sequent4 = createSequent(c0IPO);
		status4 = createPSStatus(c0IPS);
		status4.setConfidence(IConfidence.UNATTEMPTED, null);
		source4 = createPOSource(sequent4);
		source4.setSource(thm2, null);

		c0IPO.getRodinFile().save(null, true);
		c0IPS.getRodinFile().save(null, true);
	}

}
