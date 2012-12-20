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
package org.eventb.core.tests.indexers;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.eventb.core.tests.ResourceUtils.INTERNAL_ELEMENT1;
import static org.eventb.core.tests.ResourceUtils.INTERNAL_ELEMENT2;
import static org.eventb.core.tests.indexers.ListAssert.assertSameElements;
import static org.eventb.core.tests.indexers.OccUtils.makeDecl;
import static org.eventb.core.tests.indexers.OccUtils.makeOccList;
import static org.eventb.core.tests.indexers.OccUtils.makeRedeclIdent;
import static org.eventb.core.tests.indexers.OccUtils.makeRedeclTarget;
import static org.eventb.core.tests.indexers.OccUtils.makeRefLabel;
import static org.eventb.core.tests.indexers.OccUtils.makeRefPred;
import static org.eventb.core.tests.indexers.OccUtils.newDecl;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.internal.core.indexers.EventPropagator;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.location.IInternalLocation;

/**
 * @author Nicolas Beauger
 * 
 */
public class IntegrationTests extends EventBIndexerTests {

	private static final String C1 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "		<org.eventb.core.carrierSet"
					+ "			name=\"internal_element1\""
					+ "			org.eventb.core.identifier=\"set1\"/>"
					+ "</org.eventb.core.contextFile>";

	private static final String C2 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
					+ "	<org.eventb.core.extendsContext"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.target=\"C1\"/>"
					+ "	<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst2\"/>"
					+ "	<org.eventb.core.axiom"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.label=\"axm1\""
					+ "		org.eventb.core.predicate=\"cst2 ∈ set1\""
					+ " 	org.eventb.core.theorem=\"false\"/>"
					+ "</org.eventb.core.contextFile>";

	private static final String C2_NO_EXTENDS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "	<org.eventb.core.constant"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.identifier=\"cst2\"/>"
			+ "	<org.eventb.core.axiom"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.label=\"axm1\""
			+ "		org.eventb.core.predicate=\"cst2 ∈ set1\""
			+ " 	org.eventb.core.theorem=\"false\"/>"
			+ "</org.eventb.core.contextFile>";

	private static final String C3 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
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
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
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
					+ " 		org.eventb.core.theorem=\"false\"/>"
					+ "		<org.eventb.core.invariant"
					+ "			name=\"internal_element2\""
					+ "			org.eventb.core.label=\"thm1\""
					+ "			org.eventb.core.predicate=\"cst2 ∈ set3\""
					+ " 		org.eventb.core.theorem=\"true\"/>"
					+ "</org.eventb.core.machineFile>";

	private static final String M2 =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"5\">"
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
					+ "			org.eventb.core.predicate=\"var2 ∈ set3\""
					+ " 		org.eventb.core.theorem=\"false\"/>"
					+ "		<org.eventb.core.invariant"
					+ "			name=\"internal_element2\""
					+ "			org.eventb.core.comment=\"\""
					+ "			org.eventb.core.label=\"thm1\""
					+ "			org.eventb.core.predicate=\"set1 ⊆ set1\""
					+ " 		org.eventb.core.theorem=\"true\"/>"
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

	@Before
	public void turnOnIndexing() throws Exception {
		enableIndexing();
	}

	@Test
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
		final IInvariant thmM1 = m1.getInvariant(INTERNAL_ELEMENT2);
		final IInvariant thmM2 = m2.getInvariant(INTERNAL_ELEMENT2);
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
		final IDeclaration declEvtM2 = query.getDeclaration(evtM2);

		final IOccurrence set1Decl = makeDecl(set1, declSet1);
		final IOccurrence evtM2Decl = makeDecl(evtM2, declEvtM2);
		final IOccurrence refSet1AxmC2 = makeRefPred(axmC2, 7, 11, declSet1);
		final IOccurrence refSet1InvM1 = makeRefPred(invM1, 7, 11, declSet1);
		final IOccurrence refSet1ThmM2_1 = makeRefPred(thmM2, 0, 4, declSet1);
		final IOccurrence refSet1ThmM2_2 = makeRefPred(thmM2, 7, 11, declSet1);

		// set1 must be indexed in machines because imports from C2
		// and C3 are identical
		final List<IOccurrence> expSet1 =
				makeOccList(set1Decl, refSet1AxmC2, refSet1InvM1, refSet1ThmM2_1,
						refSet1ThmM2_2);

		final IOccurrence declC2 = makeDecl(cst2C2, declCst2C2);
		final IOccurrence refCst2AxmC2 =
				makeRefPred(c2.getAxiom(INTERNAL_ELEMENT1), 0, 4, declCst2C2);

		// cst2 from C2 must not be referenced in M1 because imports in C2 and
		// C3 are
		// different
		final List<IOccurrence> expCst2C2 = makeOccList(declC2, refCst2AxmC2);

		final IOccurrence declC3Set3 = makeDecl(set3, declSet3);
		final IOccurrence refSet3ThmM1 = makeRefPred(thmM1, 7, 11, declSet3);
		final IOccurrence refSet3InvM2 = makeRefPred(invM2, 7, 11, declSet3);
		final List<IOccurrence> expSet3 =
				makeOccList(declC3Set3, refSet3ThmM1, refSet3InvM2);

		// cst2 from C3 must not be referenced in machines either
		final IOccurrence declC3Cst2 = makeDecl(cst2C3, declCst2C3);
		final List<IOccurrence> expCst2C3 = makeOccList(declC3Cst2);

		final IOccurrence declM1 = makeDecl(var1, declVar1);
		final IOccurrence refVar1InvM1 = makeRefPred(invM1, 0, 4, declVar1);
		final IOccurrence refVar1LblWitM2 = makeRefLabel(witM2, declVar1);
		final IOccurrence refVar1PredWitM2 = makeRefPred(witM2, 0, 4, declVar1);

		final List<IOccurrence> expVar1 =
				makeOccList(declM1, refVar1InvM1, refVar1LblWitM2,
						refVar1PredWitM2);

		final IOccurrence declM2 = makeDecl(var2, declVar2);
		final IOccurrence refVar2InvM2 = makeRefPred(invM2, 0, 4, declVar2);

		final List<IOccurrence> expVar2 = makeOccList(declM2, refVar2InvM2);

		final List<IOccurrence> expEvtM2 = makeOccList(evtM2Decl);

		final Set<IOccurrence> occSet1 = query.getOccurrences(declSet1);
		final Set<IOccurrence> occCst2C2 = query.getOccurrences(declCst2C2);
		final Set<IOccurrence> occSet3 = query.getOccurrences(declSet3);
		final Set<IOccurrence> occCst2C3 = query.getOccurrences(declCst2C3);
		final Set<IOccurrence> occVar1 = query.getOccurrences(declVar1);
		final Set<IOccurrence> occVar2 = query.getOccurrences(declVar2);
		final Set<IOccurrence> occEvtM2 = query.getOccurrences(declEvtM2);

		assertSameElements(expSet1, occSet1, "occ set1");
		assertSameElements(expCst2C2, occCst2C2, "occ cst2C2");
		assertSameElements(expSet3, occSet3, "occ set3");
		assertSameElements(expCst2C3, occCst2C3, "occ cst2C3");
		assertSameElements(expVar1, occVar1, "occ var1");
		assertSameElements(expVar2, occVar2, "occ var2");
		assertSameElements(expEvtM2, occEvtM2, "occ evtM2");
	}

	@Test
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
			final IOccurrence declMySetC1 = makeDecl(mySet, declMySet);
			final List<IOccurrence> expMySet = makeOccList(declMySetC1);
			assertSameElements(expMySet, occsMySet, "occurrences of mySet");
		}
	}

	@Test
	public void testIndexQueryWikiExampleHandle() throws Exception {
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);
		final IVariable var = m2.getVariable(INTERNAL_ELEMENT1);

		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();

		final IDeclaration declVar = query.getDeclaration(var);
		
		final String userDefinedName = declVar.getName();
		
		assertEquals(userDefinedName, "var2");
		final IInvariant invM2 = m2.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence declM2 = makeDecl(var, declVar);
		final IOccurrence refVar2InvM2 = makeRefPred(invM2, 0, 4, declVar);
		final List<IOccurrence> expVar2 = makeOccList(declM2, refVar2InvM2);
	
		final Set<IOccurrence> occurrences = query.getOccurrences(declVar);
		
		assertSameElements(expVar2, occurrences, "occ var2");

	}
	
	
	@Test
	public void testIndexQueryPropagate() throws Exception {
		final IMachineRoot m2 = ResourceUtils.createMachine(rodinProject, "M2",
				M2);
		final IEvent evt1 = m2.getEvent(INTERNAL_ELEMENT1);
		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();

		final IDeclaration declEvt1 = query.getDeclaration(evt1);
		assertNotNull(declEvt1);

		final Set<IDeclaration> declarations = query.getDeclarations(m2
				.getRodinFile());
		query.filterType(declarations, IEvent.ELEMENT_TYPE);
		query.filterName(declarations, "evt1");
		Assert.isTrue(declarations.size() == 1);
		final IDeclaration declaration = declarations.iterator().next();
		final Set<IOccurrence> occurrences = query.getOccurrences(declaration,
				EventPropagator.getDefault());

		assertEquals(declEvt1, declaration);
		assertSameElements(asList(makeDecl(evt1, declEvt1)), occurrences,
				"propagated occurrences of evt1");
	}

	@Test
	public void testOnlyExtendsChange() throws Exception {
		final IContextRoot c1 = ResourceUtils.createContext(rodinProject, "C1",
				C1);
		final IContextRoot c2 = ResourceUtils.createContext(rodinProject, "C2",
				C2_NO_EXTENDS);

		final IIndexQuery query = RodinCore.makeIndexQuery();
		final ICarrierSet set1 = c1.getCarrierSet(INTERNAL_ELEMENT1);

		query.waitUpToDate();

		final IDeclaration declsSet1 = query.getDeclaration(set1);
		final Set<IOccurrence> occsSet1 = query.getOccurrences(declsSet1);
		assertEquals(1, occsSet1.size());

		final IExtendsContext extCls = c2.getExtendsClause(INTERNAL_ELEMENT1);
		extCls.create(null, null);
		extCls.setAbstractContextName("C1", null);
		c2.getRodinFile().save(null, true);

		query.waitUpToDate();

		final Set<IOccurrence> occsSet1After = query
				.getOccurrences(declsSet1);
		assertEquals(2, occsSet1After.size());

	}

	@Test
	public void testEventPropagation() throws Exception {
		final IMachineRoot exporter = ResourceUtils.createMachine(rodinProject,
				EXPORTER, EVT_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp = newDecl(eventExp, eventExp.getLabel());
		final IOccurrence eventExpDecl = makeDecl(eventExp, declEventExp);

		final IMachineRoot importer = ResourceUtils.createMachine(rodinProject,
				IMPORTER, EVT_1REF_REFINES);
		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventImp = newDecl(eventImp, eventImp.getLabel());
		final IOccurrence eventImpDecl = makeDecl(eventImp, declEventImp);

		final IRefinesEvent refinesClause = eventImp
				.getRefinesClause(INTERNAL_ELEMENT1);
		final IOccurrence eventExpRedeclInRefines = makeRedeclTarget(
				refinesClause, declEventExp);

		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration declEventProp = query.getDeclaration(eventExp);
		assertNotNull("declaration expected for " + eventExp, declEventProp);
		final Set<IOccurrence> occsEventProp = query.getOccurrences(declEventProp,
				EventBPlugin.getEventPropagator());
		final List<IOccurrence> expected = asList(eventExpDecl,
				eventExpRedeclInRefines, eventImpDecl);
		assertSameElements(expected, occsEventProp,
				"propagated event occurrences");

	}

	protected static final String PRM_2DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<org.eventb.core.machineFile"
		+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
		+ "		version=\"5\">"
		+ "<org.eventb.core.event"
		+ "		name=\"internal_element1\""
		+ "		org.eventb.core.convergence=\"0\""
		+ "		org.eventb.core.extended=\"false\""
		+ "		org.eventb.core.label=\"evt1\">"
		+ "		<org.eventb.core.parameter"
		+ "				name=\"internal_element1\""
		+ "				org.eventb.core.identifier=\"prm1\"/>"
		+ "		<org.eventb.core.parameter"
		+ "				name=\"internal_element2\""
		+ "				org.eventb.core.identifier=\"prm2\"/>"
		+ "</org.eventb.core.event>"
		+ "<org.eventb.core.event"
		+ "		name=\"internal_element2\""
		+ "		org.eventb.core.convergence=\"0\""
		+ "		org.eventb.core.extended=\"false\""
		+ "		org.eventb.core.label=\"evt2\">"
		+ "		<org.eventb.core.parameter"
		+ "				name=\"internal_element3\""
		+ "				org.eventb.core.identifier=\"prm1\"/>"
		+ "		<org.eventb.core.parameter"
		+ "				name=\"internal_element4\""
		+ "				org.eventb.core.identifier=\"prm2\"/>"
		+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";
	
	@Test
	public void testIndexQueryWikiPrmFilterLocation() throws Exception {
		final IMachineRoot importer = ResourceUtils.createMachine(rodinProject, "M",
				PRM_2DECL);
		final IRodinFile file = importer.getRodinFile();
		final IEvent evt1 = importer.getEvent(INTERNAL_ELEMENT1);
		final IParameter prm1 = evt1.getParameter(INTERNAL_ELEMENT1);
		final IParameter prm2 = evt1.getParameter(INTERNAL_ELEMENT2);
		
		final IDeclaration declPrm1 = newDecl(prm1, prm1.getIdentifierString());
		final IDeclaration declPrm2 = newDecl(prm2, prm2.getIdentifierString());
		final List<IDeclaration> expected = asList(declPrm1, declPrm2);

		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();

		final Set<IDeclaration> declarations = query.getDeclarations(file);
		query.filterType(declarations, IParameter.ELEMENT_TYPE);
		final Set<IOccurrence> occurrences = query.getOccurrences(declarations);
		query.filterKind(occurrences, EventBPlugin.DECLARATION);
		
		final IInternalLocation evt1Location = RodinCore.getInternalLocation(evt1);
		query.filterLocation(occurrences, evt1Location);

		final Set<IDeclaration> paramsOfEvt1 = query
				.getDeclarations(occurrences);

		assertSameElements(expected, paramsOfEvt1, "params of evt1");
	}

	@Test
	public void testIdentifierPropagation() throws Exception {
		final IMachineRoot exporter = ResourceUtils.createMachine(rodinProject,
				EXPORTER, VAR_1DECL_1REF_INV);

		final IVariable var = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(var, var
				.getIdentifierString());
		final IOccurrence varExpDecl = makeDecl(var, declVarExp);
		final IInvariant invExp = exporter.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence varExpRefInInvExp = makeRefPred(invExp, 0, 4,
				declVarExp);

		final String VAR_1DECL_1REF_REFINES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile"
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"5\">"
				+ "<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.target=\"exporter\"/>"
				+ "<org.eventb.core.variable"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.label=\"inv1\""
				+ "		org.eventb.core.predicate=\"var1 = 1\""
				+ " 	org.eventb.core.theorem=\"false\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer = ResourceUtils.createMachine(rodinProject,
				IMPORTER, VAR_1DECL_1REF_REFINES);
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarImp = newDecl(varImp, varImp
				.getIdentifierString());
		final IOccurrence varImpDecl = makeDecl(varImp, declVarImp);
		final IOccurrence varExpRedeclInVarImpIdent = makeRedeclIdent(varImp,
				declVarExp);
		final IInvariant invImp = importer.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence varImpRefInInvImp = makeRefPred(invImp, 0, 4,
				declVarImp);

		final IIndexQuery query = RodinCore.makeIndexQuery();
		query.waitUpToDate();
		final IDeclaration declaration = query.getDeclaration(var);
		assertNotNull("declaration expected for " + var, declaration);
		final Set<IOccurrence> propagatedOccs = query.getOccurrences(
				declaration, EventBPlugin.getIdentifierPropagator());
		final List<IOccurrence> expected = asList(varExpDecl,
				varExpRefInInvExp, varExpRedeclInVarImpIdent, varImpDecl,
				varImpRefInInvImp);
		assertSameElements(expected, propagatedOccs,
				"propagated event occurrences");

	}

}
