/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - ensure that all AST problems are reported
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.IVariant.DEFAULT_LABEL;
import static org.eventb.core.sc.GraphProblem.ConvergentEventNoVariantWarning;
import static org.eventb.core.sc.GraphProblem.EventLabelConflictError;
import static org.eventb.core.sc.GraphProblem.InitialisationNotOrdinaryWarning;
import static org.eventb.core.sc.GraphProblem.InvalidVariantTypeError;
import static org.eventb.core.sc.GraphProblem.InvariantLabelConflictWarning;
import static org.eventb.core.sc.GraphProblem.NoConvergentEventButVariantWarning;
import static org.eventb.core.sc.GraphProblem.UndeclaredFreeIdentifierError;
import static org.eventb.core.sc.GraphProblem.VariantFreeIdentifierError;
import static org.eventb.core.sc.GraphProblem.VariantLabelConflictError;
import static org.eventb.core.sc.GraphProblem.VariantLabelConflictWarning;
import static org.eventb.core.sc.ParseProblem.LexerError;
import static org.eventb.core.sc.ParseProblem.SyntaxError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.core.ast.ITypeEnvironment;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 * 
 */
public class TestVariant extends BasicSCTestWithFwdConfig {

	/**
	 * create an integer variant
	 */
	@Test
	public void testVariant_00() throws Exception {
		IMachineRoot mac = createMachine("mac");

		setConvergent(addEvent(mac, "evt"));
		addInitialisation(mac);
		addVariant(mac, "1");

		saveRodinFileOf(mac);
		
		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariant(file, emptyEnv, DEFAULT_LABEL, "1");
	}

	/**
	 * create a set variant
	 */
	@Test
	public void testVariant_01() throws Exception {
		IMachineRoot mac = createMachine("mac");

		setConvergent(addEvent(mac, "evt"));
		addInitialisation(mac);
		addVariant(mac, "{TRUE}");

		saveRodinFileOf(mac);
		
		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariant(file, emptyEnv, DEFAULT_LABEL, "{TRUE}");
	}

	/**
	 * create an integer variant containing a variable
	 */
	@Test
	public void testVariant_02() throws Exception {
		IMachineRoot mac = createMachine("mac");

		setConvergent(addEvent(mac, "evt"));
		addInitialisation(mac, "V1");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addVariant(mac, "V1");

		saveRodinFileOf(mac);
		
		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, DEFAULT_LABEL, "V1");
	}

	/**
	 * variants must be of type integer or POW(...)
	 */
	@Test
	public void testVariant_03() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);

		IVariant vrn = addVariant(mac, "TRUE");

		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(vrn, EXPRESSION_ATTRIBUTE,
				InvalidVariantTypeError, "BOOL"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsNoVariant(file);
	}

	/**
	 * create an integer variant containing a variable and a constant
	 */
	@Test
	public void testVariant_04() throws Exception {
		IContextRoot con = createContext("ctx");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"), false);

		saveRodinFileOf(con);

		IMachineRoot mac = createMachine("mac");
		addMachineSees(mac, "ctx");
		addVariables(mac, "V1");
		setConvergent(addEvent(mac, "evt"));
		addInitialisation(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addVariant(mac, "V1+C1");

		saveRodinFileOf(mac);
		
		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ; C1=ℤ", factory);
		containsVariant(file, typeEnv, DEFAULT_LABEL, "V1+C1");
	}

	/**
	 * variants must not refer to disappearing variables
	 */
	@Test
	public void testVariant_05() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "V0");
		addInvariants(abs, makeSList("I0"), makeSList("V0∈ℕ"), false);
		addInitialisation(abs, makeSList("A1"), makeSList("V0 ≔ 0"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(mac, "V1");
		final IEvent evt = addEvent(mac, "evt", makeSList(), makeSList(),
				makeSList(), makeSList(), makeSList());
		setConvergent(evt);
		IVariant vrn = addVariant(mac, "V1+V0");

		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(vrn, EXPRESSION_ATTRIBUTE, 3, 5,
						VariantFreeIdentifierError, "V0"),
				marker(evt, CONVERGENCE_ATTRIBUTE,
						ConvergentEventNoVariantWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariables(file, "V0", "V1");
		containsNoVariant(file);
	}

	/**
	 * convergent events that refine convergent events do not require variants
	 * (bug# 1946656)
	 */
	@Test
	public void testVariant_06() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "V0");
		addInvariants(abs, makeSList("I0"), makeSList("V0∈ℕ"), false);
		addInitialisation(abs, makeSList("A1"), makeSList("V0 ≔ 0"));
		addVariant(abs, "V0");
		setConvergent(addEvent(abs, "evt"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(mac, "V1");
		IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		addEventRefines(evt, "evt");
		IVariant vrn = addVariant(mac, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(vrn, EXPRESSION_ATTRIBUTE,
				NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariables(file, "V0", "V1");
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, DEFAULT_LABEL, "V1");
	}

	/**
	 * if there is no convergent event, then there need not be a variant
	 * (bug# 1946656)
	 */
	@Test
	public void testVariant_07() throws Exception {
		IMachineRoot mac = createMachine("mac");

		addInitialisation(mac);
		setOrdinary(addEvent(mac, "evt"));
		IVariant vrn = addVariant(mac, "1");

		saveRodinFileOf(mac);

		runBuilderCheck(marker(vrn, EXPRESSION_ATTRIBUTE,
				NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariant(file, emptyEnv, DEFAULT_LABEL, "1");
		containsEvents(file, IEvent.INITIALISATION, "evt");
	}
	
	/**
	 * if there is no convergent event, then there need not be a variant
	 */
	@Test
	public void testVariant_08() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "V0");
		addInvariants(abs, makeSList("I0"), makeSList("V0∈ℕ"), false);
		addInitialisation(abs, "V0");
		setAnticipated(addEvent(abs, "evt"));
		setAnticipated(addEvent(abs, "fvt"));

		saveRodinFileOf(abs);

		IMachineRoot mac = createMachine("mac");
		addMachineRefines(mac, "abs");
		addVariables(mac, "V0", "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(mac, "V0", "V1");
		IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		addEventRefines(evt, "evt");
		IEvent fvt = addEvent(mac, "fvt");
		setAnticipated(fvt);
		addEventRefines(fvt, "fvt");
		addVariant(mac, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariables(file, "V0", "V1");
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, DEFAULT_LABEL, "V1");
	}

	/**
	 * create a variant containing an illegal character
	 */
	@Test
	public void testVariant_09_bug2689872() throws Exception {
		IMachineRoot mac = createMachine("mac");

		final IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		addInitialisation(mac);
		IVariant vrn = addVariant(mac, "{x ∣ x /= 0}");

		saveRodinFileOf(mac);
		
		runBuilderCheck(
				marker(vrn, EXPRESSION_ATTRIBUTE, 7, 8,
						LexerError, "/"),
				marker(evt, CONVERGENCE_ATTRIBUTE,
						ConvergentEventNoVariantWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsNoVariant(file);
	}
	
	/**
	 * Create an variant with an empty expression.
	 */
	@Test
	public void testVariant_10() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IVariant vrn = addVariant(mac, "");

		saveRodinFileOf(mac);
		
		runBuilderCheck(marker(vrn, EXPRESSION_ATTRIBUTE, 0,
				1, SyntaxError, "Premature End Of Formula"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsNoVariant(file);
	}

	/**
	 * A variant is not needed even when the INITIALISATION is marked as convergent
	 * (which is faulty and fixed by the static checker).
	 */
	@Test
	public void testVariant_11() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		IEvent init = addInitialisation(mac, "V1");
		setConvergent(init);
		IVariant vrn = addVariant(mac, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(init, CONVERGENCE_ATTRIBUTE, InitialisationNotOrdinaryWarning),
				marker(vrn, EXPRESSION_ATTRIBUTE, NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, DEFAULT_LABEL, "V1");
	}

	/**
	 * There is no risk of conflict between a variant label and a variable with the
	 * same name.
	 */
	@Test
	public void testVariant_12() throws Exception {
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(mac, "V1");
		IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		addVariant(mac, "V1", "V1");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, "V1", "V1");
	}

	/**
	 * There can be a conflict between the default variant label and an invariant
	 * label with the same label. This is a regression introduced in version 3.4.
	 * The variant is then erased by the static checker.
	 */
	@Test
	public void testVariant_13() throws Exception {
		variantInvariantLabelConflict(IVariant.DEFAULT_LABEL);
	}

	/**
	 * There can be a conflict between a variant label and an invariant with the
	 * same label.
	 */
	@Test
	public void testVariant_14() throws Exception {
		variantInvariantLabelConflict("user-defined-label");
	}

	private void variantInvariantLabelConflict(String label) throws CoreException {
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "V1");
		IInvariant inv = addInvariant(mac, label, "V1∈ℕ", false);
		addInitialisation(mac, "V1");
		IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		IVariant vrn = addVariant(mac, label, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(inv, LABEL_ATTRIBUTE, InvariantLabelConflictWarning, label),
				marker(vrn, LABEL_ATTRIBUTE, VariantLabelConflictError, label),
				marker(evt, CONVERGENCE_ATTRIBUTE, ConvergentEventNoVariantWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsNoVariant(file);
	}

	/**
	 * There can be a conflict between the default label of a variant and an event
	 * with the same label. This is a regression introduced in version 3.4. The
	 * event is then erased by the static checker.
	 */
	@Test
	public void testVariant_15() throws Exception {
		variantEventLabelConflict(IVariant.DEFAULT_LABEL);
	}

	/**
	 * There can be a conflict between a variant label and an event with the same
	 * label.
	 */
	@Test
	public void testVariant_16() throws Exception {
		variantEventLabelConflict("user-defined-label");
	}

	private void variantEventLabelConflict(String label) throws CoreException {
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(mac, "V1");
		IEvent evt = addEvent(mac, label);
		setConvergent(evt);
		IVariant vrn = addVariant(mac, label, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(evt, LABEL_ATTRIBUTE, EventLabelConflictError, label),
				marker(vrn, LABEL_ATTRIBUTE, VariantLabelConflictWarning, label),
				marker(vrn, EXPRESSION_ATTRIBUTE, NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, label, "V1");
	}

	/**
	 * There can be a conflict between the default label of two variants.
	 */
	@Test
	public void testVariant_17() throws Exception {
		variantVariantLabelConflict(IVariant.DEFAULT_LABEL);
	}

	/**
	 * There can be a conflict between two variants having the same label.
	 */
	@Test
	public void testVariant_18() throws Exception {
		variantVariantLabelConflict("user-defined-label");
	}

	private void variantVariantLabelConflict(String label) throws CoreException {
		IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IVariant vrn1 = addVariant(mac, label, "1");
		IVariant vrn2 = addVariant(mac, label, "2");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(vrn1, LABEL_ATTRIBUTE, VariantLabelConflictWarning, label),
				marker(vrn2, LABEL_ATTRIBUTE, VariantLabelConflictError, label),
				marker(vrn1, EXPRESSION_ATTRIBUTE, NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariant(file, emptyEnv, label, "1");
	}

	/**
	 * One can have two variants in the same machine.
	 */
	@Test
	public void testVariant_19() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		final IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		addVariant(mac, "vrn1", "1");
		addVariant(mac, "vrn2", "2");

		saveRodinFileOf(mac);

		runBuilderCheck();

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariants(file, emptyEnv, makeSList("vrn1", "vrn2"), makeSList("1", "2"));
	}

	/**
	 * If one of two variants is incorrect, it is ignored, but the valid variant is
	 * kept.
	 */
	@Test
	public void testVariant_20() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		final IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		addVariant(mac, "vrn1", "1");
		IVariant vrn2 = addVariant(mac, "vrn2", "invalid");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(vrn2, EXPRESSION_ATTRIBUTE, 0, 7, UndeclaredFreeIdentifierError, "invalid"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariants(file, emptyEnv, makeSList("vrn1"), makeSList("1"));
	}

	/**
	 * If both variants are incorrect, they are removed and a warning is issued to
	 * indicate that a variant is missing.
	 */
	@Test
	public void testVariant_21() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		final IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		IVariant vrn1 = addVariant(mac, "vrn1", "invalid1");
		IVariant vrn2 = addVariant(mac, "vrn2", "invalid2");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(vrn1, EXPRESSION_ATTRIBUTE, 0, 8, UndeclaredFreeIdentifierError, "invalid1"),
				marker(vrn2, EXPRESSION_ATTRIBUTE, 0, 8, UndeclaredFreeIdentifierError, "invalid2"),
				marker(evt, CONVERGENCE_ATTRIBUTE, ConvergentEventNoVariantWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsNoVariant(file);
	}

	/**
	 * With two valid variants and no convergent event, both variants are marked as
	 * not necessary.
	 */
	@Test
	public void testVariant_22() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		addInitialisation(mac);
		IVariant vrn1 = addVariant(mac, "vrn1", "1");
		IVariant vrn2 = addVariant(mac, "vrn2", "2");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(vrn1, EXPRESSION_ATTRIBUTE, NoConvergentEventButVariantWarning),
				marker(vrn2, EXPRESSION_ATTRIBUTE, NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariants(file, emptyEnv, makeSList("vrn1", "vrn2"), makeSList("1", "2"));
	}

}
