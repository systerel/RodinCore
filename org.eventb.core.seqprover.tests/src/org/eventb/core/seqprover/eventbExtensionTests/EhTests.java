/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added mathematical extension related tests
 *     Systerel - port to AbstractReasonerTests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.DESELECT_ACTION_TYPE;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L0;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L1;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L2;
import static org.junit.Assert.fail;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerExtensionTests.ExtendedOperators.AssocExt;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level;
import org.junit.Test;

/**
 * Unit tests for the Eh reasoner
 * 
 * @author htson
 */
public class EhTests extends AbstractReasonerTests {

	private static final FormulaFactory FF_WITH_ASSOC = FormulaFactory
			.getInstance(AssocExt.getInstance());

	private final Level level;

	protected EhTests(Level level) {
		super(FF_WITH_ASSOC);
		this.level = level;
	}

	public EhTests() {
		this(L0);
	}

	@Override
	public String getReasonerID() {
		switch (level) {
		case L2:
			return "org.eventb.core.seqprover.eqL2";
		case L1:
			return "org.eventb.core.seqprover.eqL1";
		case L0:
			return "org.eventb.core.seqprover.eq";
		default:
			fail("unknown reasoner level");
			return null;
		}
	}

	@Test
	public void testSuccess() throws UntranslatableException {
		assertReasonerSuccess("0 = 1 ;; 0+1 = 2 |- 1+0+1 = 3", makeInput("0 = 1"),
				"{}[][0+1 = 2][0 = 1 ;; 1+1 = 2] |- 1+1+1 = 3");
		assertReasonerSuccess("0 + 1 = 1 ;; 0+1 = 2 |- 2+0+1 = 3", makeInput("0 + 1 = 1"),
				"{}[][0+1=2][0+1=1 ;; 1=2] |- 2+1=3");
		assertReasonerSuccess("0 + 1 + 2 = 2 + 1 ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 ", makeInput("0 + 1 + 2 = 2 + 1"),
				"{}[][0+1=0+1+2][0+1+2=2+1 ;; 0+1=2+1] |- 2+0+1 = 2+1+3");
		assertReasonerSuccess("1 = 2 ;; 1+1 = 2 |- 1+1+1 = 3", makeInput("1 = 2"),
				"{}[][1+1=2][1 = 2;; 2+2=2] |- 2+2+2=3");
		assertReasonerSuccess("1+2 = 12 |- 0+1+2+3 = 123", makeInput("1+2 = 12"), //
				"{}[][][1+2=12] |- 0+12+3=123");
		// bug 3389537
		assertReasonerSuccess("1●2 = 12 ;; 1●2 + 1●2 = 2 |- 1 + 1●2 + 1 = 1●2", makeInput("1●2 = 12"),
				"{}[][1●2 + 1●2 = 2][1●2 = 12;; 12+12=2] |- 1+12+1=12");
		// bug 3389537
		assertReasonerSuccess("1●2 = 12 ;; 0●1●2●3 = 123 |- 0 = 1 + 1●2●3 + 1", makeInput("1●2 = 12"),
				"{}[][0●1●2●3 = 123][1●2=12;; 0●12●3 = 123] |- 0 = 1 + 12●3 + 1");
		// FR #371
		if (level.from(L1)) {
			// Hypothesis is hidden when rewriting an identifier
			assertReasonerSuccess("x = y + 1 |- x + 1 = 1 + y + 1", makeInput("x = y + 1"),
					"{}[x = y + 1][][] |- y + 1 + 1 = 1 + y + 1");
		} else {
			assertReasonerSuccess("x = y + 1 |- x + 1 = 1 + y + 1", makeInput("x = y + 1"),
					"{}[][][x = y + 1] |- y + 1 + 1 = 1 + y + 1");
		}
		// Behavior is not modified when rewriting a complex expression
		assertReasonerSuccess("y + 1 = x |- x + 1 = 1 + y + 1", makeInput("y + 1 = x"),
				"{}[][][y + 1 = x] |- x + 1 = 1 + x");
	}

	/**
	 * Ensures that we have the correct behavior when the rewritten variable occurs
	 * in a hypothesis which is not selected nor hidden (i.e., default hypothesis).
	 */
	@Test
	public void testBug818() throws Exception {
		final IProverSequent sequent = genFullSeq("x = y + 1 ;; x > 0 ;H; ;S; x = y + 1 |- x + 1 = 1 + y + 1", ff);
		final String expectedHyps;
		final IRulePatcher patcher;
		if (level.from(L2)) {
			// x occurs in 'x > 0', just deselect the equality
			expectedHyps = "{}[][x = y + 1 ;; x > 0][]";
			patcher = new RulePatcher();
		} else if (level.from(L1)) {
			// the equality gets hidden despite the default hypothesis 'x > 0'
			expectedHyps = "{}[x = y + 1][x > 0][]";
			patcher = NO_PATCH;
		} else {
			// old behavior that does not deselect the equality
			expectedHyps = "{}[][x > 0][x = y + 1]";
			patcher = NO_PATCH;
		}
		assertReasonerSuccess(sequent, makeInput("x = y + 1"), //
				patcher, expectedHyps + " |- y + 1 + 1 = 1 + y + 1");
	}

	@Test
	public void testFailure() throws UntranslatableException {
		// eqHyp not present
		assertReasonerFailure("⊤ |- ⊤", makeInput("1=2"), "Nonexistent hypothesis: 1=2");
		// eqHyp not an equality
		assertReasonerFailure("⊤ |- ⊥", makeInput("⊤"), "Unsupported hypothesis: ⊤");
		// nothing to do
		assertReasonerFailure("1=2 ;; ⊤ |- ⊤", makeInput("1=2"), "Nothing to rewrite");
		// nothing to do
		assertReasonerFailure("1=2 ;; 1=1 ;; 2=2 |- ⊤", makeInput("1=2"), "Nothing to rewrite");
	}

	protected HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = genPred(predImage, ff);
		return new HypothesisReasoner.Input(pred);
	}

	/**
	 * Replaces any DESELECT action on the input predicate by a HIDE action.
	 */
	protected class RulePatcher implements IRulePatcher {

		private Predicate inputPred;

		/**
		 * Returns the same rule, with any DESELECT action on the input predicate
		 * replaced by a HIDE action
		 */
		public IProofRule patchRule(IProofRule rule, IReasonerInput input) {
			inputPred = ((HypothesisReasoner.Input) input).getPred();

			var newAntes = stream(rule.getAntecedents()).map(this::patchAntecedent).toArray(IAntecedent[]::new);
			return makeProofRule(rule.generatedBy(), rule.generatedUsing(), rule.getGoal(), rule.getNeededHyps(),
					rule.getConfidence(), rule.getDisplayName(), newAntes);
		}

		private IAntecedent patchAntecedent(IAntecedent ante) {
			var newActions = ante.getHypActions().stream().map(this::patchHypAction).collect(toList());
			return makeAntecedent(ante.getGoal(), ante.getAddedHyps(), ante.getUnselectedAddedHyps(),
					ante.getAddedFreeIdents(), newActions);
		}

		private IHypAction patchHypAction(IHypAction action) {
			if (action.getActionType() == DESELECT_ACTION_TYPE) {
				var hyps = ((ISelectionHypAction) action).getHyps();
				if (hyps.size() == 1 && hyps.contains(inputPred)) {
					return makeHideHypAction(hyps);
				}
			}
			return action;
		}
	}

}
