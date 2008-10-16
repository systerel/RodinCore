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
package org.eventb.core.indexer.tests;

import static org.eventb.core.indexer.tests.OccUtils.*;
import static org.eventb.core.indexer.tests.ResourceUtils.*;

import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.indexer.MachineIndexer;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexerTests extends EventBIndexerTests {

	// TODO factorize recurrent processing
	// TODO factorize files

	private static IDeclaration getDeclVar(IMachineFile machine,
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

	private static final String VAR_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
			+ "<org.eventb.core.variable name=\"internal_element1\" org.eventb.core.identifier=\"var1\"/>"
			+ "</org.eventb.core.machineFile>";

	public void testDeclaration() throws Exception {

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(machine, INTERNAL_ELEMENT1,
				VAR1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertDeclarations(declVar1);
	}

	/**
	 * @throws Exception
	 */
	public void testOccurrence() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IOccurrence occDecl = makeDecl(machine);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(var1, occDecl);
	}

	/**
	 * @throws Exception
	 */
	public void testOccurrenceOtherThanDecl() throws Exception {
		final String VAR_1DECL_1REF_INV = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable name=\"internal_element1\" org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant name=\"internal_element1\" org.eventb.core.label=\"inv1\" org.eventb.core.predicate=\"var1 &gt; 1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_INV);

		final IInvariant invariant = machine.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(invariant, 0, 4);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	/**
	 * @throws Exception
	 */
	public void testDoubleOccurrenceSameElement() throws Exception {
		final String VAR_1DECL_2OCC_SAME_INV = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable name=\"internal_element1\" org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant name=\"internal_element1\" org.eventb.core.label=\"inv1\" org.eventb.core.predicate=\"var1 ≥ var1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_2OCC_SAME_INV);

		final IInvariant invariant = machine.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occRef1 = makeRefPred(invariant, 0, 4);
		final IOccurrence occRef2 = makeRefPred(invariant, 7, 11);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, occRef1, occRef2);
	}

	/**
	 * @throws Exception
	 */
	public void testExportLocal() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(machine, INTERNAL_ELEMENT1,
				VAR1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertExports(declVar1);
	}

	private static final String EMPTY_MACHINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\"/>";
	
	/**
	 * @throws Exception
	 */
	public void testDoNotExportDisappearingVar() throws Exception {

		final IMachineFile exporter = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(exporter, INTERNAL_ELEMENT1,
				VAR1);

		final IMachineFile importer = createMachine(project, MCH_BARE_NAME,
				EMPTY_MACHINE);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertEmptyExports();
	}
	
	private static final String CST_1DECL_SET_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_SET_1DECL);

		final ICarrierSet set1 = context.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final IDeclaration declSet1 = newDecl(set1, "set1");
		final IDeclaration declCst1 = newDecl(cst1, "cst1");

		final IMachineFile importer = createMachine(project, MCH_BARE_NAME,
				EMPTY_MACHINE);

		final ToolkitStub tk = new ToolkitStub(importer, declSet1, declCst1);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertExports(declSet1, declCst1);
	}

	private static final String VAR_1REF_INV = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
		final IMachineFile exporter = createMachine(project, EXPORTER,
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(exporter, INTERNAL_ELEMENT1,
				VAR1);

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1REF_INV);

		final IInvariant invariant = importer.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occVar1 = makeRefPred(invariant, 0, 4);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(declVar1.getElement(), occVar1);
	}

	public void testImportedRedeclaration() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				VAR_1DECL);
		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1DECL);

		final IOccurrence occDecl = makeRef(importer);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(varExp, occDecl);
	}

	/**
	 * @throws Exception
	 */
	public void testUnknownElement() throws Exception {
		final IMachineFile independent = createMachine(project, "independent",
				VAR_1DECL);
		final IDeclaration declVar1 = getDeclVar(independent,
				INTERNAL_ELEMENT1, VAR1);

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1REF_INV);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertEmptyOccurrences(declVar1.getElement());
	}

	/**
	 * @throws Exception
	 */
	public void testTwoImportsSameName() throws Exception {
		final IMachineFile exporter1 = createMachine(project, "exporter1",
				VAR_1DECL);
		final IDeclaration declVarExp1 = getDeclVar(exporter1,
				INTERNAL_ELEMENT1, VAR1);

		final IMachineFile exporter2 = createMachine(project, "exporter2",
				VAR_1DECL);
		final IDeclaration declVarExp2 = getDeclVar(exporter2,
				INTERNAL_ELEMENT1, VAR1);

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1REF_INV);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExp1,
				declVarExp2);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertEmptyOccurrences(declVarExp1.getElement());
		tk.assertEmptyOccurrences(declVarExp2.getElement());
	}

	public void testRefConstantAndCarrierSet() throws Exception {
		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_SET_1DECL);

		final ICarrierSet set1 = context.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final IDeclaration declSet1 = newDecl(set1, "set1");
		final IDeclaration declCst1 = newDecl(cst1, "cst1");

		final String CST_1REF_SET_1REF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				CST_1REF_SET_1REF);

		final IInvariant invariant = machine.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence refCst1 = makeRefPred(invariant, 0, 4);
		final IOccurrence refSet1 = makeRefPred(invariant, 7, 11);

		final ToolkitStub tk = new ToolkitStub(machine, declCst1, declSet1);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(set1, refSet1);
		tk.assertOccurrences(cst1, refCst1);
	}

	public void testRefTheorem() throws Exception {
		final String VAR_1DECL_1REF_THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_THM);

		final ITheorem theorem = machine.getTheorem(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefPred(theorem, 0, 4);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	public void testRefVariant() throws Exception {
		final String VAR_1DECL_1REF_VRT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_VRT);

		final IVariant variant = machine.getVariant(INTERNAL_ELEMENT1);
		final IOccurrence occRef = makeRefExpr(variant, 5, 9);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	private static final String EVT_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile"
			+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
			+ "		version=\"3\">"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\""
			+ "		org.eventb.core.extended=\"false\""
			+ "		org.eventb.core.label=\"evt1\"/>"
			+ "</org.eventb.core.machineFile>";

	public void testEventDeclAndExport() throws Exception {

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				EVT_1DECL);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);

		final IDeclaration declEvt1 = OccUtils.newDecl(event, event.getLabel());

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertDeclarations(declEvt1);
		tk.assertExports(declEvt1);
	}

	public void testEventRefInRefinesClauses() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				EVT_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp = newDecl(eventExp, eventExp.getLabel());

		final String EVT_1REF_REFINES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile"
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"3\">"
				+ "<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.target=\"exporter\"/>"
				+ "<org.eventb.core.event"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.convergence=\"0\" org.eventb.core.extended=\"true\" org.eventb.core.label=\"evt1\">"
				+ "		<org.eventb.core.refinesEvent"
				+ "				name=\"internal_element1\""
				+ "				org.eventb.core.target=\"evt1\"/>"
				+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

		final IMachineFile importer = createMachine(project, IMPORTER,
				EVT_1REF_REFINES);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IRefinesEvent refinesImp = eventImp
				.getRefinesClause(INTERNAL_ELEMENT1);
		final IOccurrence refEventImp = makeRefTarget(refinesImp);

		final ToolkitStub tk = new ToolkitStub(importer, declEventExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertEmptyOccurrences(eventImp);
		tk.assertOccurrences(eventExp, refEventImp);
	}

	private static final String PRM_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile"
			+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
			+ "		version=\"3\">"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\""
			+ "		org.eventb.core.extended=\"false\""
			+ "		org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.parameter"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.identifier=\"prm1\"/>"
			+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

	private static final String PRM_1DECL_1REF_GRD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<org.eventb.core.machineFile"
			+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
			+ "		version=\"3\">"
			+ "<org.eventb.core.refinesMachine"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.target=\"exporter\"/>"
			+ "<org.eventb.core.event"
			+ "		name=\"internal_element1\""
			+ "		org.eventb.core.convergence=\"0\""
			+ "		org.eventb.core.extended=\"true\""
			+ "		org.eventb.core.label=\"evt1\">"
			+ "		<org.eventb.core.refinesEvent"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.target=\"evt1\"/>"
			+ "		<org.eventb.core.parameter"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.identifier=\"prm1\"/>"
			+ "		<org.eventb.core.guard"
			+ "				name=\"internal_element1\""
			+ "				org.eventb.core.label=\"grd1\""
			+ "				org.eventb.core.predicate=\"prm1 = 1\"/>"
			+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

	public void testEventParamDeclAndExport() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				PRM_1DECL);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);
		final IParameter prm1 = event.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrm1 = newDecl(prm1, prm1.getIdentifierString());

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertDeclarations(IParameter.ELEMENT_TYPE, declPrm1);
		tk.assertExports(IParameter.ELEMENT_TYPE, declPrm1);
	}

	public void testEventParamRef() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				PRM_1DECL_1REF_GRD);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);
		final IParameter prm1 = event.getParameter(INTERNAL_ELEMENT1);

		final IGuard guard = event.getGuard(INTERNAL_ELEMENT1);
		final IOccurrence refPrm1 = makeRefPred(guard, 0, 4);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(prm1, refPrm1);
	}

	public void testEventParamAbstractRefInExtendedDecl() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				PRM_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp = newDecl(eventExp, eventExp.getLabel());

		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp = newDecl(prmExp, prmExp
				.getIdentifierString());

		final String PRM_1DECL_EXTENDED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile"
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"3\">"
				+ "	<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.target=\"exporter\"/>"
				+ "<org.eventb.core.event"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.convergence=\"0\""
				+ "		org.eventb.core.extended=\"true\""
				+ "		org.eventb.core.label=\"evt1\">"
				+ "		<org.eventb.core.refinesEvent"
				+ "				name=\"internal_element1\""
				+ "				org.eventb.core.target=\"evt1\"/>"
				+ "		<org.eventb.core.parameter"
				+ "				name=\"internal_element1\""
				+ "				org.eventb.core.identifier=\"prm1\"/>"
				+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

		final IMachineFile importer = createMachine(project, IMPORTER,
				PRM_1DECL_EXTENDED);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IParameter paramImp = eventImp.getParameter(INTERNAL_ELEMENT1);
		final IOccurrence refParamImp = makeRefIdent(paramImp);

		final ToolkitStub tk = new ToolkitStub(importer, declEventExp,
				declPrmExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(prmExp, refParamImp);
	}

	public void testEventParamAbstractRefInExtendedWit() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				PRM_1DECL);

		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEventExp = newDecl(eventExp, eventExp.getLabel());
		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp = newDecl(prmExp, prmExp
				.getIdentifierString());

		final String PRM_2REF_LBL_PRED_WIT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
				+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

		final IMachineFile importer = createMachine(project, IMPORTER,
				PRM_2REF_LBL_PRED_WIT);

		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);

		final IWitness witness = eventImp.getWitness(INTERNAL_ELEMENT1);
		final IOccurrence refLblWitImp = makeRefLabel(witness);
		final IOccurrence refPredWitImp = makeRefPred(witness, 0, 4);

		final ToolkitStub tk = new ToolkitStub(importer, declEventExp,
				declPrmExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(prmExp, refLblWitImp, refPredWitImp);
	}

	public void testEventVarModifInAction() throws Exception {

		final String VAR_1DECL_1REF_ACT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile "
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"3\">"
				+ "<org.eventb.core.event "
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

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_ACT);

		final IEvent event = machine.getEvent(INTERNAL_ELEMENT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeModifAssign(action, 0, 4);

		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, occRef);
	}

	public void testEventVarRedeclared() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_1DECL_1REF_ACT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1DECL_1REF_ACT);
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occModif = makeModifAssign(action, 0, 4);

		final IOccurrence refVarExp = makeRef(importer);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);
		tk.assertOccurrencesOtherThanDecl(varImp, occModif);
		tk.assertOccurrences(varExp, refVarExp);
	}

	public void testEventVarNotRedeclaredModifInAction() throws Exception {
		// var1 disappears
		final IMachineFile exporter = createMachine(project, EXPORTER,
				VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_1REF_ACT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
				+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1REF_ACT);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		// NOTE: event-b compliant behavior should be:
		// tk.assertEmptyOccurrences(var1);
		// but visibility is not managed by the indexer

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeModifAssign(action, 0, 4);

		tk.assertOccurrences(varExp, occRef);
	}

	public void testEventVarRefInWitness()
			throws Exception {
		// var1 disappears
		final IMachineFile exporter = createMachine(project, EXPORTER,
				VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_2REF_PRIMED_WIT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
				+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_2REF_PRIMED_WIT);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);

		final IWitness witness = event.getWitness(INTERNAL_ELEMENT1);

		final IOccurrence occRefLblWit = makeRefLabel(witness);
		final IOccurrence occRefPredWit = makeRefPred(witness, 0, 5);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(varExp, occRefLblWit, occRefPredWit);
	}

	public void testEventVarRedeclaredRefInGuard() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = newDecl(varExp, VAR1);

		final String VAR_1DECL_1REF_GRD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
				+ "</org.eventb.core.event>" + "</org.eventb.core.machineFile>";

		final IMachineFile importer = createMachine(project, IMPORTER,
				VAR_1DECL_1REF_GRD);

		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IGuard guard = event.getGuard(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeRefPred(guard, 7, 11);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExp);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(varImp, occRef);
	}

	public void testPrioritiesParamVsAbsParam() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				PRM_1DECL);
	
		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEvExp = newDecl(eventExp, EVT1);
	
		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp = newDecl(prmExp, PRM1);
	
		final IMachineFile importer = createMachine(project, IMPORTER,
				PRM_1DECL_1REF_GRD);
	
		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
		final IParameter prmImp = eventImp.getParameter(INTERNAL_ELEMENT1);
		final IGuard grdImp = eventImp.getGuard(INTERNAL_ELEMENT1);
		final IOccurrence grdRef = makeRefPred(grdImp, 0, 4);
	
		final ToolkitStub tk = new ToolkitStub(importer, declPrmExp, declEvExp);
	
		final MachineIndexer indexer = new MachineIndexer();
	
		indexer.index(tk);
	
		tk.assertOccurrences(prmImp, grdRef);
	}

	public void testPrioritiesAbsParamVsLocalVar() throws Exception {
		final IMachineFile exporter = createMachine(project, EXPORTER,
				PRM_1DECL);
	
		final IEvent eventExp = exporter.getEvent(INTERNAL_ELEMENT1);
		final IDeclaration declEvExp = newDecl(eventExp, EVT1);
		
		final IParameter prmExp = eventExp.getParameter(INTERNAL_ELEMENT1);
		final IDeclaration declPrmExp = newDecl(prmExp, prmExp
				.getIdentifierString());
	
		final String VARPRM_1DECL_PRM_2REF_LBL_PRED_WIT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
	
		final IMachineFile importer = createMachine(project, IMPORTER,
				VARPRM_1DECL_PRM_2REF_LBL_PRED_WIT);
	
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
	
		final IEvent eventImp = importer.getEvent(INTERNAL_ELEMENT1);
	
		final IWitness witness = eventImp.getWitness(INTERNAL_ELEMENT1);
		final IOccurrence refLblWitImp = makeRefLabel(witness);
		final IOccurrence refPredWitImp = makeRefPred(witness, 0, 4);
	
		final ToolkitStub tk = new ToolkitStub(importer, declEvExp, declPrmExp);
	
		final MachineIndexer indexer = new MachineIndexer();
	
		indexer.index(tk);
	
		tk.assertOccurrencesOtherThanDecl(varImp);
		tk.assertOccurrences(prmExp, refLblWitImp, refPredWitImp);
	}

	public void testPrioritiesLocalVarVsImport() throws Exception {
		final String CST_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "		<org.eventb.core.constant"
				+ "				name=\"internal_element1\""
				+ "				org.eventb.core.identifier=\"cst1\"/>"
				+ "</org.eventb.core.contextFile>";
	
		final IContextFile exporter = createContext(project, EXPORTER,
				CST_1DECL);
	
		final IConstant cstExp = exporter.getConstant(INTERNAL_ELEMENT1);
		final IDeclaration declCstExp = newDecl(cstExp, cstExp
				.getIdentifierString());
	
		final String VARCST_1DECL_1REF_INV = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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
	
		final IMachineFile importer = createMachine(project, IMPORTER,
				VARCST_1DECL_1REF_INV);
	
		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);
		final IInvariant invImp = importer.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence refInvImp = makeRefPred(invImp, 0, 4);
	
		final ToolkitStub tk = new ToolkitStub(importer, declCstExp);
	
		final MachineIndexer indexer = new MachineIndexer();
	
		indexer.index(tk);
	
		tk.assertEmptyOccurrences(cstExp);
		tk.assertOccurrencesOtherThanDecl(varImp, refInvImp);
	}

	public void testBadFileType() throws Exception {
		final String CST_1DECL_1REF_THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1REF_THM);

		final ToolkitStub tk = new ToolkitStub(context);

		final MachineIndexer indexer = new MachineIndexer();

		try {
			indexer.index(tk);
			fail("IllegalArgumentException expected");
		} catch(IllegalArgumentException e) {
			// OK
		}
	}

	/**
	 * @throws Exception
	 */
	public void testMalformedXML() throws Exception {
		// missing closing " after internal_element1 in variable node
		final String MALFORMED_MACHINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable name=\"internal_element1 org.eventb.core.identifier=\"var1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				MALFORMED_MACHINE);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

	/**
	 * @throws Exception
	 */
	public void testMissingAttribute() throws Exception {
		final String VAR_1DECL_NO_IDENT_ATT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable name=\"internal_element1\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_NO_IDENT_ATT);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

	/**
	 * @throws Exception
	 */
	public void testDoesNotParse() throws Exception {
		final String VAR_1DECL_1INV_DOES_NOT_PARSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\">"
				+ "<org.eventb.core.variable name=\"internal_element1\" org.eventb.core.identifier=\"var1\"/>"
				+ "<org.eventb.core.invariant name=\"internal_element1\" org.eventb.core.label=\"inv1\" org.eventb.core.predicate=\"∃ s · var1 &lt; 1 ∧ ¬\"/>"
				+ "</org.eventb.core.machineFile>";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1INV_DOES_NOT_PARSE);

		final ToolkitStub tk = new ToolkitStub(machine);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

}
