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
package org.eventb.core.tests.autocompletion;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAxiom;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.tests.BuilderTest;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.autocompletion.AutoCompletion;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.RodinCore;
import org.rodinp.core.location.IAttributeLocation;
/**
 * @author Nicolas Beauger
 * 
 */
public class AutoCompletionTests extends BuilderTest {

	private static final String INTERNAL_ELEMENT1 = "internal_element1";

	private static final String INTERNAL_WIT1 = "internal_wit1";

	private static final String INTERNAL_PRM2 = "internal_prm2";

	private static final String INTERNAL_EVT1 = "internal_evt1";

	private static final String INTERNAL_1 = "internal_1";

	private static final String INTERNAL_INV1 = "internal_inv1";

	private static final String INTERNAL_THM1 = "internal_thm1";

	private static final String C1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "<org.eventb.core.constant"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.identifier=\"cst1\"/>"
			+ "<org.eventb.core.axiom"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.label=\"axm1\""
			+ "		org.eventb.core.predicate=\" = 2\""
			+ "		org.eventb.core.theorem=\"false\"/>"
			+ "</org.eventb.core.contextFile>";

	private static final String C2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "<org.eventb.core.extendsContext"
			+ "		name=\"internal_1\""
			+ "		org.eventb.core.target=\"C1\"/>"
			+ "<org.eventb.core.carrierSet"
			+ " 	name=\"internal_set1\" org.eventb.core.identifier=\"set1\"/>"
			+ "<org.eventb.core.axiom"
			+ " 	name=\"internal_thm1\""
			+ " 	org.eventb.core.label=\"thm1\""
			+ " 	org.eventb.core.predicate=\" ∈ \""
			+ "		org.eventb.core.theorem=\"true\"/>"
			+ "</org.eventb.core.contextFile>";

	private static final String M1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
			+ "<org.eventb.core.seesContext"
			+ "		name=\"internal_1\" org.eventb.core.target=\"C2\"/>"
			+ "<org.eventb.core.variable"
			+ " 	name=\"internal_var1\""
			+ " 	org.eventb.core.identifier=\"varM1\"/>"
			+ "<org.eventb.core.invariant"
			+ " 	name=\"internal_inv1\""
			+ " 	org.eventb.core.label=\"inv1\""
			+ " 	org.eventb.core.predicate=\"varM1 ∈ set1\""
			+ "		org.eventb.core.theorem=\"false\"/>"
			+ "<org.eventb.core.variant"
			+ " 	name=\"internal_1\""
			+ " 	org.eventb.core.expression=\"set1 ∪ {varM1}\"/>"
			+ "<org.eventb.core.event"
			+ " 	name=\"internal_evt1\""
			+ " 	org.eventb.core.convergence=\"0\""
			+ " 	org.eventb.core.extended=\"false\""
			+ " 	org.eventb.core.label=\"evtM1\">"
			+ "		<org.eventb.core.parameter"
			+ " 		name=\"internal_prm1\""
			+ " 		org.eventb.core.identifier=\"prmM1\"/>"
			+ "		<org.eventb.core.guard"
			+ " 		name=\"internal_grd1\""
			+ " 		org.eventb.core.label=\"grd1\""
			+ " 		org.eventb.core.predicate=\"prmM1 ∈ set1\""
			+ "			org.eventb.core.theorem=\"false\"/>"
			+ "		<org.eventb.core.action"
			+ " 		name=\"internal_act1\""
			+ " 		org.eventb.core.assignment=\"varM1 :∣ ⊤\""
			+ " 		org.eventb.core.label=\"act1\"/>"
			+ "</org.eventb.core.event>" 
			+ "<org.eventb.core.event"
			+ " 	name=\"internal_evt2\""
			+ " 	org.eventb.core.convergence=\"0\""
			+ " 	org.eventb.core.extended=\"false\""
			+ " 	org.eventb.core.label=\"evtM1_2\">"
			+ "		<org.eventb.core.parameter"
			+ " 		name=\"internal_prm2\""
			+ " 		org.eventb.core.identifier=\"prmM1\"/>"
			+ "		<org.eventb.core.guard"
			+ " 		name=\"internal_grd2\""
			+ " 		org.eventb.core.label=\"grd1\""
			+ " 		org.eventb.core.predicate=\"prmM1 ∈ set1\""
			+ "			org.eventb.core.theorem=\"false\"/>"
			+ "		<org.eventb.core.action"
			+ " 		name=\"internal_act2\""
			+ " 		org.eventb.core.assignment=\"varM1 ≔ prmM1\""
			+ " 		org.eventb.core.label=\"act1\"/>"
			+ "</org.eventb.core.event>" 
			+ "</org.eventb.core.machineFile>";

	private static final String M2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
			+ "<org.eventb.core.refinesMachine"
			+ " 	name=\"internal_1\""
			+ " 	org.eventb.core.target=\"M1\"/>"
			+ "<org.eventb.core.seesContext"
			+ " 	name=\"internal_1\""
			+ " 	org.eventb.core.target=\"C2\"/>"
			+ "<org.eventb.core.variable"
			+ " 	name=\"internal_var1\""
			+ " 	org.eventb.core.identifier=\"varM2\"/>"
			+ "<org.eventb.core.invariant"
			+ " 	name=\"internal_inv1\""
			+ " 	org.eventb.core.label=\"inv1\""
			+ " 	org.eventb.core.predicate=\"\""
			+ "		org.eventb.core.theorem=\"false\"/>"
			+ "<org.eventb.core.event"
			+ " 	name=\"internal_evt1\""
			+ " 	org.eventb.core.convergence=\"0\""
			+ " 	org.eventb.core.extended=\"true\""
			+ " 	org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.refinesEvent"
			+ " 		name=\"internal_1\""
			+ " 		org.eventb.core.target=\"evtM1\"/>"
			+ "		<org.eventb.core.parameter"
			+ " 			name=\"internal_prm2\""
			+ " 			org.eventb.core.identifier=\"prmM2\"/>"
			+ "		<org.eventb.core.guard"
			+ " 			name=\"internal_grd2\""
			+ " 			org.eventb.core.label=\"grd2\""
			+ " 			org.eventb.core.predicate=\"prmM2 = prmM1 \""
			+ "				org.eventb.core.theorem=\"false\"/>"
			+ "		<org.eventb.core.witness"
			+ " 		name=\"internal_wit1\""
			+ " 		org.eventb.core.label=\"varM1\""
			+ " 		org.eventb.core.predicate=\"varM1 = prmM1\"/>"
			+ "		<org.eventb.core.action"
			+ " 		name=\"internal_act2\""
			+ " 		org.eventb.core.assignment=\"varM2 ≔ cst2\""
			+ " 		org.eventb.core.label=\"act2\"/>"
			+ "</org.eventb.core.event>"
			+ "<org.eventb.core.event"
			+ " 	name=\"internal_evt2\""
			+ " 	org.eventb.core.convergence=\"0\""
			+ " 	org.eventb.core.extended=\"false\""
			+ " 	org.eventb.core.label=\"evtM2_2\">"
			+ "		<org.eventb.core.parameter"
			+ " 		name=\"internal_prm1\""
			+ " 		org.eventb.core.identifier=\"prmM2_2\"/>"
			+ "</org.eventb.core.event>"
			+ "<org.eventb.core.event"
			+ " 	name=\"internal_evt3\""
			+ " 	org.eventb.core.convergence=\"0\""
			+ " 	org.eventb.core.extended=\"false\""
			+ " 	org.eventb.core.label=\"evtM2_3\">"
			+ "		<org.eventb.core.refinesEvent"
			+ " 		name=\"internal_3\""
			+ " 		org.eventb.core.target=\"evtM1_2\"/>"
			+ "		<org.eventb.core.witness"
			+ " 		name=\"internal_wit3\""
			+ " 		org.eventb.core.label=\"\""
			+ " 		org.eventb.core.predicate=\"\"/>"
			+ "</org.eventb.core.event>"
			+ "</org.eventb.core.machineFile>";

	private static final String M3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
		+ "<org.eventb.core.refinesMachine"
		+ " 	name=\"internal_1\""
		+ " 	org.eventb.core.target=\"M2\"/>"
		+ "<org.eventb.core.seesContext"
		+ " 	name=\"internal_1\""
		+ " 	org.eventb.core.target=\"C2\"/>"
		+ "<org.eventb.core.variable"
		+ " 	name=\"internal_var1\""
		+ " 	org.eventb.core.identifier=\"varM3\"/>"
		+ "<org.eventb.core.invariant"
		+ " 	name=\"internal_inv1\""
		+ " 	org.eventb.core.label=\"inv1\""
		+ " 	org.eventb.core.predicate=\"\""
		+ "		org.eventb.core.theorem=\"false\"/>"
		+ "</org.eventb.core.machineFile>";
	
	@Before
	public void turnOnIndexing() throws Exception {
		enableIndexing();
	}

	private void doTest(IAttributeLocation location, String... expected) {
		final Set<String> expSet = new HashSet<String>(asList(expected));
		final Set<String> completions = AutoCompletion
				.getProposals(location, true);
		assertEquals("bad completions", expSet, completions);
	}

	@Test
	public void testCtxLocalCstInAxm() throws Exception {
		final IContextRoot c1 = ResourceUtils.createContext(rodinProject,
				"Ctx", C1);
		final IAxiom axiom = c1.getAxiom(INTERNAL_ELEMENT1);
		final IAttributeLocation axiomPred = RodinCore.getInternalLocation(
				axiom, PREDICATE_ATTRIBUTE);
		doTest(axiomPred, "cst1");
	}

	@Test
	public void testCtxAbstractSetInThm() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		final IContextRoot c2 = ResourceUtils.createContext(rodinProject, "C2",
				C2);
		final IAxiom theorem = c2.getAxiom(INTERNAL_THM1);
		final IAttributeLocation axiomPred = RodinCore.getInternalLocation(
				theorem, PREDICATE_ATTRIBUTE);
		doTest(axiomPred, "cst1", "set1");
	}

	@Test
	public void testMchAbstractSetCstLocalVarInInvariant() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		final IMachineRoot m1 = ResourceUtils.createMachine(rodinProject, "M1",
				M1);
		final IInvariant invariant = m1.getInvariant(INTERNAL_INV1);
		final IAttributeLocation invPred = RodinCore.getInternalLocation(
				invariant, PREDICATE_ATTRIBUTE);
		doTest(invPred, "cst1", "set1", "varM1");
	}

	@Test
	public void testMchAbstractSetCstLocalVarInVariant() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		final IMachineRoot m1 = ResourceUtils.createMachine(rodinProject, "M1",
				M1);
		final IVariant variant = m1.getVariant(INTERNAL_1);
		final IAttributeLocation vrtPred = RodinCore.getInternalLocation(
				variant, EventBAttributes.EXPRESSION_ATTRIBUTE);
		doTest(vrtPred, "cst1", "set1", "varM1");
	}

	@Test
	public void testMchAbstractVarInInvariant() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);

		final IInvariant invariant = m2.getInvariant(INTERNAL_INV1);
		final IAttributeLocation invPred = RodinCore.getInternalLocation(
				invariant, PREDICATE_ATTRIBUTE);
		doTest(invPred, "cst1", "set1", "varM1", "varM2");
	}

	@Test
	public void testMchAbstractVarInInvariant2() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		ResourceUtils.createMachine(rodinProject, "M2", M2);
		final IMachineRoot m3 = ResourceUtils.createMachine(rodinProject, "M3",
				M3);

		final IInvariant invariant = m3.getInvariant(INTERNAL_INV1);
		final IAttributeLocation invPred = RodinCore.getInternalLocation(
				invariant, PREDICATE_ATTRIBUTE);
		doTest(invPred, "cst1", "set1", "varM2", "varM3");
	}

	@Test
	public void testEvtParamInGuard() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);

		final IEvent evt1 = m2.getEvent(INTERNAL_EVT1);
		final IGuard guard = evt1.getGuard(INTERNAL_PRM2);
		final IAttributeLocation grdPred = RodinCore.getInternalLocation(guard,
				PREDICATE_ATTRIBUTE);
		// includes prmM1 from extended event
		doTest(grdPred, "cst1", "prmM1", "prmM2", "set1", "varM2");

	}

	@Test
	public void testEvtAbstractPrmPrimedVarInWitLabel() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);

		final IEvent evt1 = m2.getEvent(INTERNAL_EVT1);

		final IWitness witness = evt1.getWitness(INTERNAL_WIT1);
		final IAttributeLocation witLabel = RodinCore.getInternalLocation(
				witness, LABEL_ATTRIBUTE);
		// "prmM1" does not disappear because evt1 is extended
		doTest(witLabel, "varM1'");
	}

	@Test
	public void testEvtInWitPredicate() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);

		final IEvent evt1 = m2.getEvent(INTERNAL_EVT1);

		final IWitness witness = evt1.getWitness(INTERNAL_WIT1);
		final IAttributeLocation witPred = RodinCore.getInternalLocation(
				witness, PREDICATE_ATTRIBUTE);
		doTest(witPred, "cst1", "prmM1", "prmM2", "set1", "varM1", "varM1'", "varM2");
	}
	
	@Test
	public void testRemoveNonDeterministicallyAssignedVars() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);

		final IEvent evt3 = m2.getEvent("internal_evt3");

		final IWitness witness = evt3.getWitness("internal_wit3");
		final IAttributeLocation witLabel = RodinCore.getInternalLocation(
				witness, LABEL_ATTRIBUTE);

		// varM1 is deterministically assigned in abstract event
		// so varM1' must not appear in the completions
		// but but evt3 is not extended so prmM1 disappears
		doTest(witLabel, "prmM1");
	}
	
	@Test
	public void testEventLabel() throws Exception {
		ResourceUtils.createContext(rodinProject, "C1", C1);
		ResourceUtils.createContext(rodinProject, "C2", C2);
		ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);

		final IEvent evt3 = m2.getEvent("internal_evt3");

		final IAttributeLocation evtLabel = RodinCore.getInternalLocation(
				evt3, LABEL_ATTRIBUTE);

		doTest(evtLabel, "evtM1", "evtM1_2");
	}
}