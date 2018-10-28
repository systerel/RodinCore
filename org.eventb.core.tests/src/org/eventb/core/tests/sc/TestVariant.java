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
import static org.eventb.core.sc.GraphProblem.ConvergentEventNoVariantWarning;
import static org.eventb.core.sc.GraphProblem.EventLabelConflictError;
import static org.eventb.core.sc.GraphProblem.InitialisationNotOrdinaryWarning;
import static org.eventb.core.sc.GraphProblem.InvalidVariantTypeError;
import static org.eventb.core.sc.GraphProblem.InvariantLabelConflictWarning;
import static org.eventb.core.sc.GraphProblem.NoConvergentEventButVariantWarning;
import static org.eventb.core.sc.GraphProblem.VariantFreeIdentifierError;
import static org.eventb.core.sc.GraphProblem.VariantLabelConflictError;
import static org.eventb.core.sc.GraphProblem.VariantLabelConflictWarning;
import static org.eventb.core.sc.ParseProblem.LexerError;
import static org.eventb.core.sc.ParseProblem.SyntaxError;
import static org.eventb.core.tests.MarkerMatcher.marker;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;

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
		containsVariant(file, emptyEnv, "1");
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
		containsVariant(file, emptyEnv, "{TRUE}");
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
		containsVariant(file, typeEnv, "V1");
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
		containsVariant(file, emptyEnv);
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
		containsVariant(file, typeEnv, "V1+C1");
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
		containsVariant(file, emptyEnv);
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
		containsVariant(file, typeEnv, "V1");
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
		containsVariant(file, emptyEnv, "1");
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
		containsVariant(file, typeEnv, "V1");
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
		containsVariant(file, emptyEnv);
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
		containsVariant(file, emptyEnv);
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
		containsVariant(file, typeEnv, "V1");
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
		containsVariant(file, typeEnv, "V1");
	}

	/**
	 * There can be a name conflict between a variant label and an invariant label
	 * with the same name. This is a regression introduced in version 3.4. The
	 * variant is then erased by the static checker.
	 */
	@Test
	public void testVariant_13() throws Exception {
		String label = IVariant.DEFAULT_LABEL;
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "V1");
		IInvariant inv = addInvariant(mac, label, "V1∈ℕ", false);
		addInitialisation(mac, "V1");
		IEvent evt = addEvent(mac, "evt");
		setConvergent(evt);
		IVariant vrn = addVariant(mac, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(inv, LABEL_ATTRIBUTE, InvariantLabelConflictWarning, label),
				marker(vrn, LABEL_ATTRIBUTE, VariantLabelConflictError, label),
				marker(evt, CONVERGENCE_ATTRIBUTE, ConvergentEventNoVariantWarning, "evt"));

		ISCMachineRoot file = mac.getSCMachineRoot();
		containsVariant(file, emptyEnv);
	}

	/**
	 * There can be a name conflict between a variant label and an event label with
	 * the same name. This is a regression introduced in version 3.4. The event is
	 * then erased by the static checker.
	 */
	@Test
	public void testVariant_14() throws Exception {
		String label = IVariant.DEFAULT_LABEL;
		IMachineRoot mac = createMachine("mac");
		addVariables(mac, "V1");
		addInvariants(mac, makeSList("I1"), makeSList("V1∈ℕ"), false);
		addInitialisation(mac, "V1");
		IEvent evt = addEvent(mac, label);
		setConvergent(evt);
		IVariant vrn = addVariant(mac, "V1");

		saveRodinFileOf(mac);

		runBuilderCheck(//
				marker(evt, LABEL_ATTRIBUTE, EventLabelConflictError, label),
				marker(vrn, LABEL_ATTRIBUTE, VariantLabelConflictWarning, label),
				marker(vrn, EXPRESSION_ATTRIBUTE, NoConvergentEventButVariantWarning));

		ISCMachineRoot file = mac.getSCMachineRoot();
		ITypeEnvironment typeEnv = mTypeEnvironment("V1=ℤ", factory);
		containsVariant(file, typeEnv, "V1");
	}

}
