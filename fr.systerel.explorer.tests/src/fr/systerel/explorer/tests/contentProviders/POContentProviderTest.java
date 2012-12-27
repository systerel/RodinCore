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
package fr.systerel.explorer.tests.contentProviders;

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
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import fr.systerel.explorer.IElementNode;
import fr.systerel.explorer.tests.ExplorerTest;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.contentProviders.POContentProvider;

/**
 * 
 *
 */
public class POContentProviderTest extends ExplorerTest {

	private static POContentProvider contentProvider;
	protected static IContextRoot c0;
	protected static IMachineRoot m0;
	protected static IPORoot c0IPO;
	protected static IPSRoot c0IPS;
	protected static IPORoot m0IPO;
	protected static IPSRoot m0IPS;
	protected static IPOSequent sequent1;
	protected static IPSStatus status1;
	protected static IPOSequent sequent2;
	protected static IPSStatus status2;
	protected static IPOSequent sequent3;
	protected static IPSStatus status3;
	protected static IPOSequent sequent4;
	protected static IPSStatus status4;
	protected static IPOSequent sequent5;
	protected static IPSStatus status5;
	protected static IPOSequent sequent6;
	protected static IPSStatus status6;
	protected static IPOSequent sequent7;
	protected static IPSStatus status7;
	protected static IPOSequent sequent8;
	protected static IPSStatus status8;
	protected static IPOSource source1;
	protected static IPOSource source2;
	protected static IPOSource source3;
	protected static IPOSource source4;
	protected static IPOSource source5;
	protected static IPOSource source6;
	protected static IPOSource source7;
	protected static IPOSource source8;
	protected static IElementNode node;
	protected static IElementNode node2;
	protected static IAxiom axiom1;
	protected static IInvariant inv1;
	protected static IEvent event1;
	protected static IEvent event2;
	protected static IAxiom thm1;
	protected static IInvariant thm2;
	
	

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contentProvider = new POContentProvider();
		
		setUpContext();
		setUpContextPOs();
		
		setUpMachine();
		setUpMachinePOs();
		
		ModelController.processProject(rodinProject);
		
		createNodes();

		
	}

	private void setUpContext() throws RodinDBException {
		//create a context
		c0 = createContext("c0");
		assertNotNull("c0 should be created successfully ", c0);

		//create some elements in the context
		axiom1 = createAxiom(c0, "axiom1");
		thm1 = createAxiomTheorem(c0, "thm1");
	}

	private void setUpMachine() throws RodinDBException {
		// create a machine
		m0 = createMachine("m0");
		assertNotNull("m0 should be created successfully ", m0);
		
		// create some elements in the machine
		inv1 = createInvariant(m0, "inv1");
		event1 = createEvent(m0, "event1");
		thm2 =  createInvariantTheorem(m0, "thm2");
	}

	private void createNodes() {
		node = ModelController.getContext(c0).po_node;
		assertNotNull("the node should be created successfully ", node);
		node2 = ModelController.getMachine(m0).po_node;
		assertNotNull("the node should be created successfully ", node2);
	}

	private void setUpMachinePOs() throws RodinDBException {
		m0IPO = createIPORoot("m0");
		assertNotNull("m0IPO should be created successfully ", m0IPO);
		
		m0IPS = createIPSRoot("m0");
		assertNotNull("m0IPS should be created successfully ", m0IPS);
		
		sequent3 = createSequent(m0IPO);
		status3 = createPSStatus(m0IPS);

		source4 =  createPOSource(sequent3);
		source4.setSource(inv1, null);
	
		sequent4 = createSequent(m0IPO);
		status4 = createPSStatus(m0IPS);

		source5 =  createPOSource(sequent4);
		source5.setSource(event1, null);

		sequent5 = createSequent(m0IPO);
		status5 = createPSStatus(m0IPS);

		source6 =  createPOSource(sequent5);
		source6.setSource(event1, null);
		source7 =  createPOSource(sequent5);
		source7.setSource(inv1, null);
		source8 =  createPOSource(sequent5);
		source8.setSource(thm2, null);
	}

	private void setUpContextPOs() throws RodinDBException {
		c0IPO = createIPORoot("c0");
		assertNotNull("c0IPO should be created successfully ", c0IPO);
		
		c0IPS = createIPSRoot("c0");
		assertNotNull("c0IPS should be created successfully ", c0IPS);
		
		sequent1 = createSequent(c0IPO);
		status1 = createPSStatus(c0IPS);

		source1 =  createPOSource(sequent1);
		source1.setSource(axiom1, null);

		sequent2 = createSequent(c0IPO);
		status2 = createPSStatus(c0IPS);

		source2 =  createPOSource(sequent2);
		source2.setSource(axiom1, null);

		source3 =  createPOSource(sequent2);
		source3.setSource(thm1, null);
	}
	
	@Test
	public void getChildrenContext() throws RodinDBException {
		assertArray(contentProvider.getChildren(c0), node);
	}

	@Test
	public void getChildrenMachine() throws RodinDBException {
		assertArray(contentProvider.getChildren(m0), node2);
	}
	
	@Test
	public void getChildrenPOnodeContext() throws RodinDBException {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertArray(contentProvider.getChildren(node), status1, status2);
	}

	@Test
	public void getChildrenPOnodeMachine() throws RodinDBException {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getChildren(node2), status3, status4, status5);
	}
	
	@Test
	public void getChildrenAxiom() throws RodinDBException {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertArray(contentProvider.getChildren(axiom1), status1, status2);
	}

	@Test
	public void getChildrenEvent() throws RodinDBException {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getChildren(event1), status4, status5);
	}

	
	@Test
	public void getChildrenInvariant() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getChildren(inv1), status3, status5);
	}

	@Test
	public void getChildrenTheoremContext() throws RodinDBException {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertArray(contentProvider.getChildren(thm1), status2);
	}
	
	@Test
	public void getChildrenTheoremMachine() throws RodinDBException {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getChildren(thm2), status5);
	}
	
	@Test
	public void getParent() throws RodinDBException {
		//the getParent function is not really implemented in POContentProvider,
		//since POs can have more than one parent. It always returns null.
	}

	@Test
	public void hasChildrenContext() {
		assertTrue(contentProvider.hasChildren(c0));
	}

	@Test
	public void hasChildrenContextPOnode() {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertTrue(contentProvider.hasChildren(node));
	}

	@Test
	public void hasChildrenMachinePOnode() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertTrue(contentProvider.hasChildren(node2));
	}
	
	
	@Test
	public void hasChildrenAxiom() {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertTrue(contentProvider.hasChildren(axiom1));
	}

	@Test
	public void hasChildrenEvent() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertTrue(contentProvider.hasChildren(event1));
	}
	
	@Test
	public void hasChildrenInvariant() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertTrue(contentProvider.hasChildren(inv1));
	}
	
	@Test
	public void hasChildrenTheoremContext() {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertTrue(contentProvider.hasChildren(thm1));
	}

	@Test
	public void hasChildrenTheoremMachine() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertTrue(contentProvider.hasChildren(thm2));
	}
	
	@Test
	public void getElementsContext() {
		assertArray(contentProvider.getElements(c0), node);
	}

	@Test
	public void getElementsContextPOnode() {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertArray(contentProvider.getElements(node), status1, status2);
	}

	@Test
	public void getElementsMachinePOnode() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getElements(node2), status3, status4, status5);
	}
	
	@Test
	public void getElementsAxiom() {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertArray(contentProvider.getElements(axiom1), status1, status2);
	}

	@Test
	public void getElementsEvent() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getElements(event1), status4, status5);
	}
	
	@Test
	public void getElementsInvariant() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getElements(inv1), status3, status5);
	}
	
	@Test
	public void getElementsTheoremContext() {
		ModelController.getContext(c0).processPORoot();
		ModelController.getContext(c0).processPSRoot();
		assertArray(contentProvider.getElements(thm1), status2);
	}

	@Test
	public void getElementsTheoremMachine() {
		ModelController.getMachine(m0).processPORoot();
		ModelController.getMachine(m0).processPSRoot();
		assertArray(contentProvider.getElements(thm2), status5);
	}

}
