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

import static org.eventb.core.tests.indexers.OccUtils.*;
import static org.eventb.core.tests.indexers.ResourceUtils.*;

import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.core.indexers.MachineIndexer;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IOccurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexerTests extends EventBIndexerTests {

	private static IDeclaration getDeclVar(IMachineRoot machine,
			String varIntName, String varName) throws RodinDBException {
		final IVariable var = machine.getVariable(varIntName);

		return newDecl(var, varName);
	}

	/**
	 * @param name
	 */
	public MachineIndexerTests(String name) {
		super(name);
	}

	public void testDeclaration() throws Exception {

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL);

		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarations(declVar1);
	}

	/**
	 * @throws Exception
	 */
	public void testRefDeclaration() throws Exception {
		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);
		final IOccurrence occDecl = makeDecl(machine, declVar1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(var1, occDecl);
	}

	/**
	 * @throws Exception
	 */
	public void testOccurrenceOtherThanDecl() throws Exception {
		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL_1REF_INV);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);

		final IInvariant invariant = machine.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(invariant, 0, 4, declVar1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	/**
	 * @throws Exception
	 */
	public void testDoubleOccurrenceSameElement() throws Exception {
		final String VAR_1DECL_2OCC_SAME_INV =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "<org.eventb.core.invariant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"inv1\""
						+ "		org.eventb.core.predicate=\"var1 ≥ var1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL_2OCC_SAME_INV);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);

		final IInvariant invariant = machine.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occRef1 = makeRefPred(invariant, 0, 4, declVar1);
		final IOccurrence occRef2 = makeRefPred(invariant, 7, 11, declVar1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(var1, occRef1, occRef2);
	}

	/**
	 * @throws Exception
	 */
	public void testExportLocal() throws Exception {
		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL);

		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertExports(declVar1);
	}

	/**
	 * @throws Exception
	 */
	public void testDoNotExportDisappearingVar() throws Exception {

		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL);

		final IDeclaration declVar1 =
				getDeclVar(exporter, INTERNAL_ELEMENT1, VAR1);

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, EMPTY_MACHINE);

		final BridgeStub tk = new BridgeStub(importer, declVar1);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertEmptyExports();
	}

	private static final String CST_1DECL_SET_1DECL =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.contextFile"
					+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
					+ "		version=\"1\">"
					+ "<org.eventb.core.carrierSet"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"set1\"/>"
					+ "<org.eventb.core.constant"
					+ "		name=\"internal_element1\""
					+ "		org.eventb.core.identifier=\"cst1\"/>"
					+ "</org.eventb.core.contextFile>";

	public void testExportConstantsAndCarrierSets() throws Exception {
		final IContextRoot context =
			ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_SET_1DECL);

		final ICarrierSet set1 = context.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final IDeclaration declSet1 = newDecl(set1, "set1");
		final IDeclaration declCst1 = newDecl(cst1, "cst1");

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, EMPTY_MACHINE);

		final BridgeStub tk = new BridgeStub(importer, declSet1, declCst1);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertExports(declSet1, declCst1);
	}

	private static final String VAR_1REF_INV =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<org.eventb.core.machineFile"
					+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
					+ "		version=\"3\">"
					+ "		<org.eventb.core.invariant "
					+ "				name=\"internal_element1\""
					+ "				org.eventb.core.label=\"inv1\""
					+ "				org.eventb.core.predicate=\"var1 &gt; 1\"/>"
					+ "</org.eventb.core.machineFile>";

	/**
	 * @throws Exception
	 */
	public void testImportedOccurrence() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, VAR_1DECL);

		final IDeclaration declVar1 =
				getDeclVar(exporter, INTERNAL_ELEMENT1, VAR1);

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_1REF_INV);

		final IInvariant invariant = importer.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occVar1 = makeRefPred(invariant, 0, 4, declVar1);

		final BridgeStub tk = new BridgeStub(importer, declVar1);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(declVar1.getElement(), occVar1);
	}

	public void testImportedRedeclaration() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, VAR_1DECL);
		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_1DECL);
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		
		final IOccurrence occDecl = makeRefIdent(varImp, declVarExp);

		final BridgeStub tk = new BridgeStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(varExp, occDecl);
	}

	/**
	 * @throws Exception
	 */
	public void testUnknownElement() throws Exception {
		final IMachineRoot independent =
				ResourceUtils.createMachine(rodinProject, "independent", VAR_1DECL);
		final IDeclaration declVar1 =
				getDeclVar(independent, INTERNAL_ELEMENT1, VAR1);

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1REF_INV);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertEmptyOccurrences(declVar1.getElement());
	}

	/**
	 * @throws Exception
	 */
	public void testTwoImportsSameName() throws Exception {
		final IMachineRoot exporter1 =
				ResourceUtils.createMachine(rodinProject, "exporter1", VAR_1DECL);
		final IDeclaration declVarExp1 =
				getDeclVar(exporter1, INTERNAL_ELEMENT1, VAR1);

		final IMachineRoot exporter2 =
				ResourceUtils.createMachine(rodinProject, "exporter2", VAR_1DECL);
		final IDeclaration declVarExp2 =
				getDeclVar(exporter2, INTERNAL_ELEMENT1, VAR1);

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_1REF_INV);

		final BridgeStub tk =
				new BridgeStub(importer, declVarExp1, declVarExp2);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertEmptyOccurrences(declVarExp1.getElement());
		tk.assertEmptyOccurrences(declVarExp2.getElement());
	}

	public void testRefConstantAndCarrierSet() throws Exception {
		final IContextRoot context =
			ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_SET_1DECL);

		final ICarrierSet set1 = context.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final IDeclaration declSet1 = newDecl(set1, "set1");
		final IDeclaration declCst1 = newDecl(cst1, "cst1");

		final String CST_1REF_SET_1REF =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.seesContext"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"context\"/>"
						+ "<org.eventb.core.invariant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"inv1\""
						+ "		org.eventb.core.predicate=\"cst1 ∈ set1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, CST_1REF_SET_1REF);

		final IInvariant invariant = machine.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence refCst1 = makeRefPred(invariant, 0, 4, declCst1);
		final IOccurrence refSet1 = makeRefPred(invariant, 7, 11, declSet1);

		final BridgeStub tk = new BridgeStub(machine, declCst1, declSet1);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(set1, refSet1);
		tk.assertOccurrences(cst1, refCst1);
	}

	public void testRefTheorem() throws Exception {
		final String VAR_1DECL_1REF_THM =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "<org.eventb.core.theorem"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"thm1\""
						+ "		org.eventb.core.predicate=\"var1 = 1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL_1REF_THM);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);

		final ITheorem theorem = machine.getTheorem(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(theorem, 0, 4, declVar1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	public void testRefVariant() throws Exception {
		final String VAR_1DECL_1REF_VRT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "<org.eventb.core.variant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.expression=\"10 + var1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL_1REF_VRT);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 =
				getDeclVar(machine, INTERNAL_ELEMENT1, VAR1);

		final IVariant variant = machine.getVariant(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefExpr(variant, 5, 9, declVar1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	public void testEventDeclAndExport() throws Exception {

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, EVT_1DECL);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);

		final IDeclaration declEvt1 = newDecl(event, event.getLabel());
		final IOccurrence occEventDecl = makeDecl(machine, declEvt1);
		
		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarations(declEvt1);
		tk.assertOccurrences(event, occEventDecl);
		tk.assertExports(declEvt1);
	}

	public void testEventRefInRefinesClauses() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, EVT_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp =
				newDecl(eventExp, eventExp.getLabel());

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, EVT_1REF_REFINES);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventImp =
			newDecl(eventImp, eventImp.getLabel());
		final IOccurrence eventImpDecl = makeDecl(importer, declEventImp);
		
	
		final IRefinesEvent refinesImp =
				eventImp.getRefinesClause(INTERNAL_ELEMENT1);
		final IOccurrence refEventExpInImp = makeRefTarget(refinesImp, declEventExp);

		final BridgeStub tk = new BridgeStub(importer, declEventExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(eventImp, eventImpDecl);
		tk.assertOccurrences(eventExp, refEventExpInImp);
	}

	public void testEventParamDeclAndExport() throws Exception {
		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, PRM_1DECL);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);
		final IParameter prm1 = event.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrm1 = newDecl(prm1, prm1.getIdentifierString());
		final IOccurrence prm1Decl = makeDecl(event, declPrm1);
		
		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertDeclarations(IParameter.ELEMENT_TYPE, declPrm1);
		tk.assertOccurrences(prm1, prm1Decl);
		tk.assertExports(IParameter.ELEMENT_TYPE, declPrm1);
	}

	public void testEventParamRef() throws Exception {
		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, PRM_1DECL_1REF_GRD);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);
		final IParameter prm1 = event.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrm1 = newDecl(prm1, PRM1);

		final IGuard guard = event.getGuard(INTERNAL_ELEMENT1);
		final IOccurrence refPrm1 = makeRefPred(guard, 0, 4, declPrm1);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(prm1, refPrm1);
	}

	public void testEventParamAbstractRefInExtendedDecl() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, PRM_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp =
				newDecl(eventExp, eventExp.getLabel());

		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp =
				newDecl(prmExp, prmExp.getIdentifierString());

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, PRM_1DECL_1REF_GRD);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IParameter paramImp = eventImp.getParameter(INTERNAL_ELEMENT1);
		final IOccurrence refParamImp = makeRefIdent(paramImp, declPrmExp);

		final BridgeStub tk =
				new BridgeStub(importer, declEventExp, declPrmExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(prmExp, refParamImp);
	}

	public void testEventParamAbstractRefInExtendedWit() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, PRM_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp =
				newDecl(eventExp, eventExp.getLabel());
		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp =
				newDecl(prmExp, prmExp.getIdentifierString());

		final String PRM_2REF_LBL_PRED_WIT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.refinesMachine"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.event"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.convergence=\"0\""
						+ "		org.eventb.core.extended=\"true\" org.eventb.core.label=\"evt1\">"
						+ "		<org.eventb.core.refinesEvent"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.target=\"evt1\"/>"
						+ "		<org.eventb.core.witness"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.label=\"prm1\""
						+ "				org.eventb.core.predicate=\"prm1 = 0\"/>"
						+ "</org.eventb.core.event>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, PRM_2REF_LBL_PRED_WIT);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);

		final IWitness witness = eventImp.getWitness(INTERNAL_ELEMENT1);
		final IOccurrence refLblWitImp = makeRefLabel(witness, declPrmExp);
		final IOccurrence refPredWitImp =
				makeRefPred(witness, 0, 4, declPrmExp);

		final BridgeStub tk =
				new BridgeStub(importer, declEventExp, declPrmExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(prmExp, refLblWitImp, refPredWitImp);
	}

	public void testEventVarRedeclared() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_1DECL_1REF_ACT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile "
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.refinesMachine"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.event"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.convergence=\"0\""
						+ "		org.eventb.core.extended=\"false\""
						+ "		org.eventb.core.label=\"INITIALISATION\">"
						+ "		<org.eventb.core.action"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.assignment=\"var1 ≔ 1\""
						+ "				org.eventb.core.label=\"act1\"/>"
						+ "</org.eventb.core.event>"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_1DECL_1REF_ACT);
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarImp = newDecl(varImp, VAR1);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occModif = makeModifAssign(action, 0, 4, declVarImp);

		final IOccurrence refVarExp = makeRefIdent(varImp, declVarExp);

		final BridgeStub tk = new BridgeStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));
		tk.assertOccurrencesOtherThanDecl(varImp, occModif);
		tk.assertOccurrences(varExp, refVarExp);
	}

	public void testEventVarNotRedeclaredModifInAction() throws Exception {
		// var1 disappears
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_1REF_ACT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.refinesMachine"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.event name=\"internal_element1\""
						+ "		org.eventb.core.convergence=\"0\""
						+ "		org.eventb.core.extended=\"false\""
						+ "		org.eventb.core.label=\"evt1\">"
						+ "		<org.eventb.core.action "
						+ "				name=\"internal_element1\" "
						+ "				org.eventb.core.assignment=\"var1 ≔ 1\""
						+ "				org.eventb.core.label=\"act1\"/>"
						+ "</org.eventb.core.event>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_1REF_ACT);

		final BridgeStub tk = new BridgeStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		// NOTE: event-b compliant behavior should be:
		// tk.assertEmptyOccurrences(var1);
		// but visibility is not managed by the indexer

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeModifAssign(action, 0, 4, declVarExp);

		tk.assertOccurrences(varExp, occRef);
	}

	public void testEventVarRefInWitness() throws Exception {
		// var1 disappears
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_2REF_PRIMED_WIT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.refinesMachine"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.event"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.convergence=\"0\""
						+ "		org.eventb.core.extended=\"false\""
						+ "		org.eventb.core.label=\"evt1\">"
						+ "		<org.eventb.core.witness"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.label=\"var1'\""
						+ "				org.eventb.core.predicate=\"var1' = 1\"/>"
						+ "</org.eventb.core.event>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_2REF_PRIMED_WIT);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);

		final IWitness witness = event.getWitness(INTERNAL_ELEMENT1);

		final IOccurrence occRefLblWit = makeRefLabel(witness, declVarExp);
		final IOccurrence occRefPredWit =
				makeRefPred(witness, 0, 5, declVarExp);

		final BridgeStub tk = new BridgeStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(varExp, occRefLblWit, occRefPredWit);
	}

	public void testEventVarRedeclaredRefInGuard() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_1DECL_1REF_GRD =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.refinesMachine"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "<org.eventb.core.event"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.convergence=\"0\""
						+ "		org.eventb.core.extended=\"false\""
						+ "		org.eventb.core.label=\"evt1\">"
						+ "		<org.eventb.core.parameter"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.identifier=\"prm1\"/>"
						+ "		<org.eventb.core.guard"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.label=\"grd1\""
						+ "				org.eventb.core.predicate=\"prm1 = var1\"/>"
						+ "</org.eventb.core.event>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VAR_1DECL_1REF_GRD);

		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarImp = newDecl(varImp, VAR1);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IGuard guard = event.getGuard(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeRefPred(guard, 7, 11, declVarImp);

		final BridgeStub tk = new BridgeStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(varImp, occRef);
	}

	public void testPrioritiesParamVsAbsParam() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, PRM_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEvExp = newDecl(eventExp, EVT1);

		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp = newDecl(prmExp, PRM1);

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, PRM_1DECL_1REF_GRD);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IParameter prmImp = eventImp.getParameter(INTERNAL_ELEMENT1);
		final IGuard grdImp = eventImp.getGuard(INTERNAL_ELEMENT1);
		
		final IDeclaration declPrmImp = newDecl(prmImp, PRM1);
		
		final IOccurrence grdRef = makeRefPred(grdImp, 0, 4, declPrmImp);
		final IOccurrence prmImpDecl = makeDecl(eventImp, declPrmImp);

		final BridgeStub tk = new BridgeStub(importer, declPrmExp, declEvExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrences(prmImp, prmImpDecl, grdRef);
	}

	public void testPrioritiesAbsParamVsLocalVar() throws Exception {
		final IMachineRoot exporter =
				ResourceUtils.createMachine(rodinProject, EXPORTER, PRM_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEvExp = newDecl(eventExp, EVT1);

		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp =
				newDecl(prmExp, prmExp.getIdentifierString());

		final String VARPRM_1DECL_PRM_2REF_LBL_PRED_WIT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile"
						+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
						+ "		version=\"3\">"
						+ "<org.eventb.core.refinesMachine"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"prm1\"/>"
						+ "<org.eventb.core.event"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.convergence=\"0\""
						+ "		org.eventb.core.extended=\"true\""
						+ "		org.eventb.core.label=\"evt1\">"
						+ "		<org.eventb.core.refinesEvent"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.target=\"evt1\"/>"
						+ "		<org.eventb.core.witness"
						+ "				name=\"internal_element1\""
						+ "				org.eventb.core.label=\"prm1\""
						+ "				org.eventb.core.predicate=\"prm1 = 0\"/>"
						+ "	</org.eventb.core.event>"
						+ "	</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER,
						VARPRM_1DECL_PRM_2REF_LBL_PRED_WIT);

		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);

		final IWitness witness = eventImp.getWitness(INTERNAL_ELEMENT1);
		final IOccurrence refLblWitImp = makeRefLabel(witness, declPrmExp);
		final IOccurrence refPredWitImp =
				makeRefPred(witness, 0, 4, declPrmExp);

		final BridgeStub tk = new BridgeStub(importer, declEvExp, declPrmExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertOccurrencesOtherThanDecl(varImp);
		tk.assertOccurrences(prmExp, refLblWitImp, refPredWitImp);
	}

	public void testPrioritiesLocalVarVsImport() throws Exception {
		final IContextRoot exporter =
			ResourceUtils.createContext(rodinProject, EXPORTER, CST_1DECL);

		final IConstant cstExp = exporter.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCstExp =
				newDecl(cstExp, cstExp.getIdentifierString());

		final String VARCST_1DECL_1REF_INV =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.seesContext"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.target=\"exporter\"/>"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"cst1\"/>"
						+ "	<org.eventb.core.invariant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"inv1\""
						+ "		org.eventb.core.predicate=\"cst1 = 0\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot importer =
				ResourceUtils.createMachine(rodinProject, IMPORTER, VARCST_1DECL_1REF_INV);

		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		final IInvariant invImp = importer.getInvariant(INTERNAL_ELEMENT1);
		final IDeclaration declVarImp = newDecl(varImp, CST1);
		final IOccurrence refInvImp = makeRefPred(invImp, 0, 4, declVarImp);

		final BridgeStub tk = new BridgeStub(importer, declCstExp);

		final MachineIndexer indexer = new MachineIndexer();

		assertTrue(indexer.index(tk));

		tk.assertEmptyOccurrences(cstExp);
		tk.assertOccurrencesOtherThanDecl(varImp, refInvImp);
	}

	public void testBadFileType() throws Exception {
		final String CST_1DECL_1REF_THM =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
						+ "<org.eventb.core.constant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"cst1\"/>"
						+ "<org.eventb.core.theorem"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"thm1\""
						+ "		org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\"/>"
						+ "</org.eventb.core.contextFile>";

		final IContextRoot context =
				ResourceUtils.createContext(rodinProject, CTX_BARE_NAME, CST_1DECL_1REF_THM);

		final BridgeStub tk = new BridgeStub(context);

		final MachineIndexer indexer = new MachineIndexer();

		try {
			assertTrue(indexer.index(tk));
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	public void testMalformedXML() throws Exception {
		// missing closing " after internal_element1 in variable node
		final String MALFORMED_MACHINE =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1"
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, MALFORMED_MACHINE);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		assertFalse(indexer.index(tk));
	}

	/**
	 * @throws Exception
	 */
	public void testMissingAttribute() throws Exception {
		final String VAR_1DECL_NO_IDENT_ATT =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME, VAR_1DECL_NO_IDENT_ATT);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		assertTrue(indexer.index(tk));
	}

	/**
	 * @throws Exception
	 */
	public void testDoesNotParse() throws Exception {
		final String VAR_1DECL_INV_DOES_NOT_PARSE =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
						+ "<org.eventb.core.variable"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.identifier=\"var1\"/>"
						+ "<org.eventb.core.invariant"
						+ "		name=\"internal_element1\""
						+ "		org.eventb.core.label=\"inv1\""
						+ "		org.eventb.core.predicate=\"∃ s · var1 &lt; 1 ∧ ¬\"/>"
						+ "</org.eventb.core.machineFile>";

		final IMachineRoot machine =
				ResourceUtils.createMachine(rodinProject, MCH_BARE_NAME,
						VAR_1DECL_INV_DOES_NOT_PARSE);

		final BridgeStub tk = new BridgeStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		assertTrue(indexer.index(tk));
	}

}
