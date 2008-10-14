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

import java.util.List;

import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.core.indexer.MachineIndexer;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.index.IDeclaration;
import org.rodinp.core.index.IOccurrence;

/**
 * @author Nicolas Beauger
 * 
 */
public class MachineIndexerTests extends EventBIndexerTests {

	// TODO test indexing cancellation

	private static IRodinProject project;
	private static final String VAR1 = "var1";

	private static IDeclaration getDeclVar(IMachineFile machine,
			String varIntName, String varName) throws RodinDBException {
		final IVariable var = machine.getVariable(varIntName);

		return makeDecl(var, varName);
	}

	/**
	 * @param name
	 */
	public MachineIndexerTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		project = createRodinProject("P");
	}

	protected void tearDown() throws Exception {
		deleteProject("P");
		super.tearDown();
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
		final List<IDeclaration> expected = makeDeclList(declVar1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertDeclarations(expected);
	}

	/**
	 * @throws Exception
	 */
	public void testOccurrence() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IOccurrence occDecl = makeDecl(machine);

		final List<IOccurrence> expected = makeOccList(occDecl);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(var1, expected);
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

		final List<IOccurrence> expected = makeOccList(occRef);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, expected);
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

		final List<IOccurrence> expected = makeOccList(occRef1, occRef2);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, expected);
	}

	/**
	 * @throws Exception
	 */
	public void testExportLocal() throws Exception {
		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(machine, INTERNAL_ELEMENT1,
				VAR1);
		final List<IDeclaration> expected = makeDeclList(declVar1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertExports(expected);
	}

	/**
	 * @throws Exception
	 */
	public void testDoNotExportImportedNoDecl() throws Exception {

		final String EMPTY_MACHINE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"3\"/>";

		final IMachineFile exporter = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(exporter, INTERNAL_ELEMENT1,
				VAR1);
		final List<IDeclaration> declVar1List = makeDeclList(declVar1);

		final IMachineFile importer = createMachine(project, MCH_BARE_NAME,
				EMPTY_MACHINE);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1List, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertExports(EMPTY_DECL);
	}

	// private static final String CST_1REF_AXM = "<?xml version=\"1.0\"
	// encoding=\"UTF-8\"?>"
	// + "<org.eventb.core.machineFile
	// org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
	// + "<org.eventb.core.invariant name=\"internal_element1\"
	// org.eventb.core.comment=\"\" org.eventb.core.label=\"axm1\"
	// org.eventb.core.predicate=\"1 &lt; var1\"/>"
	// + "</org.eventb.core.machineFile>";

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
		final IMachineFile exporter = createMachine(project, "exporter",
				VAR_1DECL);

		final IDeclaration declVar1 = getDeclVar(exporter, INTERNAL_ELEMENT1,
				VAR1);
		final List<IDeclaration> declVar1List = makeDeclList(declVar1);

		final IMachineFile importer = createMachine(project, "importer",
				VAR_1REF_INV);

		final IInvariant invariant = importer.getInvariant(INTERNAL_ELEMENT1);
		final IOccurrence occVar1 = makeRefPred(invariant, 0, 4);
		final List<IOccurrence> expected = makeOccList(occVar1);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1List, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(declVar1.getElement(), expected);
	}

	public void testImportedRedeclaration() throws Exception {
		final IMachineFile exporter = createMachine(project, "exporter",
				VAR_1DECL);
		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);

		final IMachineFile importer = createMachine(project, "importer",
				VAR_1DECL);

		final IVariable varImp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarImp = makeDecl(varImp, VAR1);

		final List<IDeclaration> declVarImpList = makeDeclList(declVarImp);

		final IOccurrence occDecl = makeRef(importer);
		final List<IOccurrence> expected = makeOccList(occDecl);

		final ToolkitStub tk = new ToolkitStub(importer, declVarImpList, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(varExp, expected);

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

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

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

		final List<IDeclaration> declList = makeDeclList(declVarExp1,
				declVarExp2);

		final IMachineFile importer = createMachine(project, "importer",
				VAR_1REF_INV);

		final ToolkitStub tk = new ToolkitStub(importer, declList, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertEmptyOccurrences(declVarExp1.getElement());
		tk.assertEmptyOccurrences(declVarExp2.getElement());
	}

	public void testRefConstantAndCarrierSet() throws Exception {
		final String CST_1DECL_SET_1DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
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

		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_SET_1DECL);

		final ICarrierSet set1 = context.getCarrierSet(INTERNAL_ELEMENT1);
		final IConstant cst1 = context.getConstant(INTERNAL_ELEMENT1);

		final IDeclaration declSet1 = makeDecl(set1, "set1");
		final IDeclaration declCst1 = makeDecl(cst1, "cst1");

		final List<IDeclaration> declCst1Set1List = makeDeclList(declCst1,
				declSet1);

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
		final List<IOccurrence> refCst1List = makeOccList(refCst1);
		final IOccurrence refSet1 = makeRefPred(invariant, 7, 11);
		final List<IOccurrence> refSet1List = makeOccList(refSet1);

		final ToolkitStub tk = new ToolkitStub(machine, declCst1Set1List, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(set1, refSet1List);
		tk.assertOccurrences(cst1, refCst1List);
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

		final List<IOccurrence> expected = makeOccList(occRef);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, expected);
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

		final List<IOccurrence> expected = makeOccList(occRef);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, expected);
	}

	public void testEvent_Var_1Decl_1Ref_Act() throws Exception {

		final String VAR_1DECL_1REF_ACT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile "
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"3\">"
				+ "<org.eventb.core.event "
				+ "		name=\"internal_evt1\""
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

		final String INTERNAL_EVT1 = "internal_evt1";

		final IMachineFile machine = createMachine(project, MCH_BARE_NAME,
				VAR_1DECL_1REF_ACT);

		final IEvent event = machine.getEvent(INTERNAL_EVT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeRefAssign(action, 0, 4);

		final List<IOccurrence> expected = makeOccList(occRef);
		final IVariable var1 = machine.getVariable(INTERNAL_ELEMENT1);

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(var1, expected);
	}

	public void testEvent_Var_1DeclAbs_1Decl_1Ref_Act() throws Exception {
		final IMachineFile exporter = createMachine(project, "exporter",
				VAR_1DECL);

		final IVariable var1Exp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 = makeDecl(var1Exp, VAR1);
		final List<IDeclaration> declVar1ExpList = makeDeclList(declVar1);

		final String VAR_1DECL_1REF_ACT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.machineFile "
				+ "		org.eventb.core.configuration=\"org.eventb.core.fwd\""
				+ "		version=\"3\">"
				+ "<org.eventb.core.refinesMachine"
				+ "		name=\"internal_element1\""
				+ "		org.eventb.core.target=\"exporter\"/>"
				+ "<org.eventb.core.event"
				+ "		name=\"internal_evt1\""
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

		final String INTERNAL_EVT1 = "internal_evt1";

		final IMachineFile importer = createMachine(project, "importer",
				VAR_1DECL_1REF_ACT);
		final IVariable var1Imp = importer.getVariable(INTERNAL_ELEMENT1);

		final IEvent event = importer.getEvent(INTERNAL_EVT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeRefAssign(action, 0, 4);

		final List<IOccurrence> expected = makeOccList(occRef);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1ExpList, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);
		tk.assertOccurrencesOtherThanDecl(var1Imp, expected);
	}

	public void testEvent_Var_1DeclAbs_1Ref_Act() throws Exception {
		// var1 disappears
		final IMachineFile exporter = createMachine(project, "exporter",
				VAR_1DECL);

		final IVariable var1 = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 = makeDecl(var1, VAR1);
		final List<IDeclaration> declVar1List = makeDeclList(declVar1);

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

		final IMachineFile importer = createMachine(project, "importer",
				VAR_1REF_ACT);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1List, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);
		
		// NOTE: event-b compliant behavior should be:
		// tk.assertEmptyOccurrences(var1);
		// but visibility is not managed by the indexer
		
		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IAction action = event.getAction(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeRefAssign(action, 0, 4);

		final List<IOccurrence> expected = makeOccList(occRef);
	
		tk.assertOccurrences(var1, expected);
	}

	public void testEvent_Var_1DeclAbs_2RefPrimed_Lbl_Pred_Wit()
			throws Exception {
		// var1 disappears
		final IMachineFile exporter = createMachine(project, "exporter",
				VAR_1DECL);

		final IVariable var1 = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVar1 = makeDecl(var1, VAR1);
		final List<IDeclaration> declVar1List = makeDeclList(declVar1);

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

		final IMachineFile importer = createMachine(project, "importer",
				VAR_2REF_PRIMED_WIT);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);

		final IWitness witness = event.getWitness(INTERNAL_ELEMENT1);

		final IOccurrence occRefLblWit = makeRefLabel(witness);
		final IOccurrence occRefPredWit = makeRefPred(witness, 0, 5);

		final List<IOccurrence> expected = makeOccList(occRefLblWit,
				occRefPredWit);

		final ToolkitStub tk = new ToolkitStub(importer, declVar1List, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrences(var1, expected);
	}

	// TODO test event parameters

//	public void testPrioritiesParamVsLocalVar() throws Exception {
//		fail("not implemented yet");
//	}
//
//	public void testPrioritiesParamVsImport() throws Exception {
//		fail("not implemented yet");
//	}
//
//	public void testPrioritiesLocalVarVsImport() throws Exception {
//		fail("not implemented yet");
//	}

	public void testEvent_Var_1DeclAbs_1Decl_1Ref_Grd() throws Exception {
		final IMachineFile exporter = createMachine(project, "exporter",
				VAR_1DECL);

		final IVariable varExp = exporter.getVariable(INTERNAL_ELEMENT1);
		final IDeclaration declVarExp = makeDecl(varExp, VAR1);

		final List<IDeclaration> declVarExpList = makeDeclList(declVarExp);

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

		final IMachineFile importer = createMachine(project, "importer",
				VAR_1DECL_1REF_GRD);

		final IVariable varImp = importer.getVariable(INTERNAL_ELEMENT1);

		final IEvent event = importer.getEvent(INTERNAL_ELEMENT1);
		final IGuard guard = event.getGuard(INTERNAL_ELEMENT1);

		final IOccurrence occRef = makeRefPred(guard, 7, 11);

		final List<IOccurrence> expected = makeOccList(occRef);

		final ToolkitStub tk = new ToolkitStub(importer, declVarExpList, null);

		final MachineIndexer indexer = new MachineIndexer();

		indexer.index(tk);

		tk.assertOccurrencesOtherThanDecl(varImp, expected);

	}

	public void testBadFileType() throws Exception {
		final String CST_1DECL_1REF_THM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<org.eventb.core.contextFile org.eventb.core.configuration=\"org.eventb.core.fwd\" version=\"1\">"
				+ "<org.eventb.core.constant name=\"internal_element1\" org.eventb.core.identifier=\"cst1\"/>"
				+ "<org.eventb.core.theorem name=\"internal_element1\" org.eventb.core.label=\"thm1\" org.eventb.core.predicate=\"∀i·i∈ℕ ⇒ cst1 = i\"/>"
				+ "</org.eventb.core.contextFile>";

		final IContextFile context = createContext(project, CTX_BARE_NAME,
				CST_1DECL_1REF_THM);

		final ToolkitStub tk = new ToolkitStub(context, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.index(tk);
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

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

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

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

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

		final ToolkitStub tk = new ToolkitStub(machine, EMPTY_DECL, null);

		final MachineIndexer indexer = new MachineIndexer();

		// should not throw an exception
		indexer.index(tk);
	}

}
