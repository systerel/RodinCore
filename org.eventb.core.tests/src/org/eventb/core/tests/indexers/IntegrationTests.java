/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.indexers;

import static java.util.Arrays.asList;
import static org.eventb.core.tests.indexers.ListAssert.assertSameElements;
import static org.eventb.core.tests.indexers.OccUtils.*;
import static org.eventb.core.tests.indexers.ResourceUtils.INTERNAL_ELEMENT1;

import java.util.List;
import java.util.Set;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.internal.core.debug.DebugHelpers;

/**
 * @author Nicolas Beauger
 * 
 */
public class IntegrationTests extends EventBIndexerTests {

	private static final String C1 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
					+ "		<org.eventb.core.carrierSet"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.identifier=\"set1\"/>"
					+ "</org.eventb.core.contextFile>";

	private static final String C2 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
					+ "	<org.eventb.core.extendsContext"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.target=\"C1\"/>"
					+ "	<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst2\"/>"
					+ "	<org.eventb.core.axiom"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"axm1\""
					+ "		org.eventb.core.predicate=\"cst2 ∈ set1\"/>"
					+ "</org.eventb.core.contextFile>";

	private static final String C2_NO_EXTENDS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
			+ "	<org.eventb.core.constant"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.identifier=\"cst2\"/>"
			+ "	<org.eventb.core.axiom"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.label=\"axm1\""
			+ "		org.eventb.core.predicate=\"cst2 ∈ set1\"/>"
			+ "</org.eventb.core.contextFile>";

	private static final String C3 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
					+ "		<org.eventb.core.extendsContext"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.target=\"C1\"/>"
					+ "		<org.eventb.core.carrierSet"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.identifier=\"set3\"/>"
					+ "		<org.eventb.core.constant"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.identifier=\"cst2\"/>"
					+ "</org.eventb.core.contextFile>";

	private static final String M1 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "		<org.eventb.core.seesContext"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.target=\"C2\"/>"
					+ "		<org.eventb.core.seesContext"
					+ "			name=\"internal_element2\""
					+ "			org.eventb.core.target=\"C3\"/>"
					+ "		<org.eventb.core.variable"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.identifier=\"var1\"/>"
					+ "		<org.eventb.core.invariant"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.label=\"inv1\""
					+ "			org.eventb.core.predicate=\"var1 ∈ set1\"/>"
					+ "		<org.eventb.core.theorem"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.label=\"thm1\""
					+ "			org.eventb.core.predicate=\"cst2 ∈ set3\"/>"
					+ "</org.eventb.core.machineFile>";

	private static final String M2 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "		<org.eventb.core.refinesMachine"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.target=\"M1\"/>"
					+ "		<org.eventb.core.variable"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.identifier=\"var2\"/>"
					+ "		<org.eventb.core.invariant"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.comment=\"\""
					+ "			org.eventb.core.label=\"inv1\""
					+ "			org.eventb.core.predicate=\"var2 ∈ set3\"/>"
					+ "		<org.eventb.core.theorem"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.comment=\"\""
					+ "			org.eventb.core.label=\"thm1\""
					+ "			org.eventb.core.predicate=\"set1 ⊆ set1\"/>"
					+ "		<org.eventb.core.event"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.convergence=\"0\""
					+ "			org.eventb.core.extended=\"false\""
					+ "			org.eventb.core.label=\"evt1\">"
					+ "			<org.eventb.core.witness"
					+ "				name=\"internal_element1\""
					+ "				org.eventb.core.label=\"var1\""
					+ "				org.eventb.core.predicate=\"var1 = 1\"/>"
					+ "		</org.eventb.core.event>"
					+ "</org.eventb.core.machineFile>";

	/**
	 * @param name
	 */
	public IntegrationTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		DebugHelpers.enableIndexing();
	}

	protected void tearDown() throws Exception {
		DebugHelpers.disableIndexing();
		super.tearDown();
	}

	public void testIntegration() throws Exception {

		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2", M2);
		final IContextRoot c3 = ResourceUtils.createContext(rodinProject, "C3", C3);
		final IMachineRoot m1 = ResourceUtils.createMachine(rodinProject, "M1", M1);
		final IContextRoot c1 = ResourceUtils.createContext(rodinProject, "C1", C1);
		final IContextRoot c2 = ResourceUtils.createContext(rodinProject, "C2", C2);

		final ICarrierSet set1 = c1.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst2C2 = c2.getConstant(INTERNAL_ELEMENT1);
		final ICarrierSet set3 = c3.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst2C3 = c3.getConstant(INTERNAL_ELEMENT1);
		final IVariable var1 = m1.getVariable(INTERNAL_ELEMENT1);
		final IVariable var2 = m2.getVariable(INTERNAL_ELEMENT1);

		final IAxiom axmC2 = c2.getAxiom(INTERNAL_ELEMENT1);
		final IInvariant invM1 = m1.getInvariant(INTERNAL_ELEMENT1);
		final IInvariant invM2 = m2.getInvariant(INTERNAL_ELEMENT1);
		final ITheorem thmM1 = m1.getTheorem(INTERNAL_ELEMENT1);
		final ITheorem thmM2 = m2.getTheorem(INTERNAL_ELEMENT1);
		final IEvent evtM2 = m2.getEvent(INTERNAL_ELEMENT1);
		final IWitness witM2 = evtM2.getWitness(INTERNAL_ELEMENT1);

		final IIndexQuery query = RodinCore.makeIndexQuery();

		// Thread.sleep(800);
		query.waitUpToDate();

		final IDeclaration declSet1 = query.getDeclaration(set1);
		final IDeclaration declCst2C2 = query.getDeclaration(cst2C2);
		final IDeclaration declSet3 = query.getDeclaration(set3);
		final IDeclaration declCst2C3 = query.getDeclaration(cst2C3);
		final IDeclaration declVar1 = query.getDeclaration(var1);
		final IDeclaration declVar2 = query.getDeclaration(var2);

		final IOccurrence declC1 = makeDecl(c1, declSet1);
		final IOccurrence refSet1AxmC2 = makeRefPred(axmC2, 7, 11, declSet1);
		final IOccurrence refSet1InvM1 = makeRefPred(invM1, 7, 11, declSet1);
		final IOccurrence refSet1ThmM2_1 = makeRefPred(thmM2, 0, 4, declSet1);
		final IOccurrence refSet1ThmM2_2 = makeRefPred(thmM2, 7, 11, declSet1);

		// set1 must be indexed in machines because imports from C2
		// and C3 are identical
		final List<IOccurrence> expSet1 =
				makeOccList(declC1, refSet1AxmC2, refSet1InvM1, refSet1ThmM2_1,
						refSet1ThmM2_2);

		final IOccurrence declC2 = makeDecl(c2, declCst2C2);
		final IOccurrence refCst2AxmC2 =
				makeRefPred(c2.getAxiom(INTERNAL_ELEMENT1), 0, 4, declCst2C2);

		// cst2 from C2 must not be referenced in M1 because imports in C2 and
		// C3 are
		// different
		final List<IOccurrence> expCst2C2 = makeOccList(declC2, refCst2AxmC2);

		final IOccurrence declC3Set3 = makeDecl(c3, declSet3);
		final IOccurrence refSet3ThmM1 = makeRefPred(thmM1, 7, 11, declSet3);
		final IOccurrence refSet3InvM2 = makeRefPred(invM2, 7, 11, declSet3);
		final List<IOccurrence> expSet3 =
				makeOccList(declC3Set3, refSet3ThmM1, refSet3InvM2);

		// cst2 from C3 must not be referenced in machines either
		final IOccurrence declC3Cst2 = makeDecl(c3, declCst2C3);
		final List<IOccurrence> expCst2C3 = makeOccList(declC3Cst2);

		final IOccurrence declM1 = makeDecl(m1, declVar1);
		final IOccurrence refVar1InvM1 = makeRefPred(invM1, 0, 4, declVar1);
		final IOccurrence refVar1LblWitM2 = makeRefLabel(witM2, declVar1);
		final IOccurrence refVar1PredWitM2 = makeRefPred(witM2, 0, 4, declVar1);

		final List<IOccurrence> expVar1 =
				makeOccList(declM1, refVar1InvM1, refVar1LblWitM2,
						refVar1PredWitM2);

		final IOccurrence declM2 = makeDecl(m2, declVar2);
		final IOccurrence refVar2InvM2 = makeRefPred(invM2, 0, 4, declVar2);

		final List<IOccurrence> expVar2 = makeOccList(declM2, refVar2InvM2);

		final Set<IOccurrence> occSet1 = query.getOccurrences(declSet1);
		final Set<IOccurrence> occCst2C2 = query.getOccurrences(declCst2C2);
		final Set<IOccurrence> occSet3 = query.getOccurrences(declSet3);
		final Set<IOccurrence> occCst2C3 = query.getOccurrences(declCst2C3);
		final Set<IOccurrence> occVar1 = query.getOccurrences(declVar1);
		final Set<IOccurrence> occVar2 = query.getOccurrences(declVar2);

		assertSameElements(expSet1, occSet1, "occ set1");
		assertSameElements(expCst2C2, occCst2C2, "occ cst2C2");
		assertSameElements(expSet3, occSet3, "occ set3");
		assertSameElements(expCst2C3, occCst2C3, "occ cst2C3");
		assertSameElements(expVar1, occVar1, "occ var1");
		assertSameElements(expVar2, occVar2, "occ var2");
	}

	public void testIndexQueryJavadocExample() throws Exception {
		final IContextRoot c1 = ResourceUtils.createContext(rodinProject, "C1",
				C1);
		final ICarrierSet mySet = c1.getCarrierSet(INTERNAL_ELEMENT1);
		final IRodinFile myFile = c1.getRodinFile();

		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration declMySet = query.getDeclaration(mySet);
		if (declMySet != null) { // the element is indexed
			final Set<IOccurrence> occsMySet = query.getOccurrences(declMySet);
			query.filterFile(occsMySet, myFile);
			// process occurrences of mySet in myFile
			final IOccurrence declMySetC1 = makeDecl(c1, declMySet);
			final List<IOccurrence> expMySet = makeOccList(declMySetC1);
			assertSameElements(expMySet, occsMySet, "occurrences of mySet");
		}
	}

	public void testOnlyExtendsChange() throws Exception {
		final IContextRoot c1 = ResourceUtils.createContext(rodinProject, "C1",
				C1);
		final IContextRoot c2 = ResourceUtils.createContext(rodinProject, "C2",
				C2_NO_EXTENDS);

		final IIndexQuery requester = RodinCore.makeIndexQuery();
		final ICarrierSet set1 = c1.getCarrierSet(INTERNAL_ELEMENT1);

		requester.waitUpToDate();

		final IDeclaration declsSet1 = requester.getDeclaration(set1);
		final Set<IOccurrence> occsSet1 = requester.getOccurrences(declsSet1);
		assertEquals(1, occsSet1.size());

		final IExtendsContext extCls = c2.getExtendsClause(INTERNAL_ELEMENT1);
		extCls.create(null, null);
		extCls.setAbstractContextName("C1", null);
		c2.getRodinFile().save(null, true);

		requester.waitUpToDate();

		final Set<IOccurrence> occsSet1After = requester
				.getOccurrences(declsSet1);
		assertEquals(2, occsSet1After.size());

	}

	public void testEventPropagation() throws Exception {
		final IMachineRoot exporter = ResourceUtils.createMachine(rodinProject,
				EXPORTER, EVT_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp = newDecl(eventExp, eventExp.getLabel());
		final IOccurrence eventExpDecl = makeDecl(exporter, declEventExp);

		final IMachineRoot importer = ResourceUtils.createMachine(rodinProject,
				IMPORTER, EVT_1REF_REFINES);
		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventImp = newDecl(eventImp, eventImp.getLabel());
		final IOccurrence eventImpDecl = makeDecl(importer, declEventImp);

		final IRefinesEvent refinesClause = eventImp
				.getRefinesClause(INTERNAL_ELEMENT1);
		final IOccurrence eventExpRefInRefines = makeRefTarget(refinesClause,
				declEventExp);

		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration declEventProp = query.getDeclaration(eventExp);
		assertNotNull("declaration expected for " + eventExp, declEventProp);
		final Set<IOccurrence> occsEventProp = query.getOccurrences(declEventProp,
				EventBPlugin.getEventPropagator());
		final List<IOccurrence> expected = asList(eventExpDecl,
				eventExpRefInRefines, eventImpDecl);
		assertSameElements(expected, occsEventProp,
				"propagated event occurrences");

	}

	public void testIdentifierPropagation() throws Exception {
		final IMachineRoot exporter = ResourceUtils.createMachine(rodinProject,
				EXPORTER, VAR_1DECL_1REF_INV);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, varExp
				.getIdentifierString());
		final IOccurrence varExpDecl = makeDecl(exporter, declVarExp);
		final IInvariant invExp = exporter.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence varExpRefInInvExp = makeRefPred(invExp, 0, 4, declVarExp);

		final String VAR_1DECL_1REF_REFINES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile"
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"3\">"
				+ "<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.target=\"exporter\"/>"
				+ "<org.eventb.core.variable"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"inv1\""
				+ "		org.eventb.core.predicate=\"var1 = 1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer = ResourceUtils.createMachine(rodinProject,
				IMPORTER, VAR_1DECL_1REF_REFINES);
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarImp = newDecl(varImp, varImp
				.getIdentifierString());
		final IOccurrence varImpDecl = makeDecl(importer, declVarImp);
		final IOccurrence varExpRefInVarImpIdent = makeRefIdent(varImp,
				declVarExp);
		final IInvariant invImp = importer.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence varImpRefInInvImp = makeRefPred(invImp, 0, 4, declVarImp);

		
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration declVarProp = query.getDeclaration(varExp);
		assertNotNull("declaration expected for " + varExp, declVarProp);
		final Set<IOccurrence> occsEventProp = query.getOccurrences(declVarProp,
				EventBPlugin.getIdentifierPropagator());
		final List<IOccurrence> expected = asList(varExpDecl, varExpRefInInvExp,
				varExpRefInVarImpIdent, varImpDecl, varImpRefInInvImp);
		assertSameElements(expected, occsEventProp,
				"propagated event occurrences");

	}

}
