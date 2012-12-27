/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved all type-checking code to class TypeChecker
 *     Systerel - added checking methods about predicate variables
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * This class contains a collection of static methods that clients can use to check that the input they provide to
 * the sequent prover fulfills the expectations the sequent prover has on them. 
 * 
 * <p>
 * The intention of this class is to make explicit (programmatically) the assumptions that the sequent prover makes on
 * the inputs its gets from its environment. The methods included here may be used by clients in test cases and assertion
 * checks.
 * </p>
 * 
 * <p>
 * The methods return debug trace messages in case one of their checks fail.
 * </p>
 * 
 * <p>
 * For the moment clients are requested not to depend on the code here since it is currently not entirely stable.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class ProverChecks {
	
	// *******************************************************************************************
	// Debug trace setup code :
	// *******************************************************************************************

	
	/**
	 * Debug flag for <code>PROVER_CHECKS_TRACE</code>
	 */
	public static boolean DEBUG;
	
	static void checkFailure(String message){
		if (DEBUG)
			System.out.println("Prover Check Failure: " + message);
	}

	// *******************************************************************************************
	// Public Methods :
	// *******************************************************************************************

	/**
	 * Checks that the given predicate does not contain predicate variables.
	 * 
	 * @return <code>true</code> if the predicate contains no predicate
	 *         variables, <code>false</code> otherwise.
	 */
	public static boolean checkNoPredicateVariable(Predicate pred) {
		if (pred != null && pred.hasPredicateVariable()) {
			Util.log(null, "Unexpected predicate variable found in " + pred);
			return false;
		}
		return true;
	}

	/**
	 * Checks that the given predicates do not contain predicate variables.
	 * 
	 * @param predicates
	 *            the predicates to check
	 * @return <code>true</code> if no predicate contains predicate variables,
	 *         <code>false</code> otherwise.
	 */
	public static boolean checkNoPredicateVariable(
			Collection<Predicate> predicates) {
		if (predicates == null) {
			return true;
		}
		for (final Predicate pred : predicates) {
			if (!checkNoPredicateVariable(pred)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the assumptions made on a given sequent by the sequent prover.
	 * 
	 * <p>
	 * The following checks are performed:
	 * <ul> 
	 * <li> All predicates present in this sequent are well formed, type checked, and well
	 * typed with respect to the type environment of this sequent.
	 * <li> All selected and hidden hypotheses are contained in the hypotheses and that
	 * they are disjoint.
	 * </ul>
	 * </p>
	 * 
	 * @param seq
	 * 			the sequent to check
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */	
	public static boolean checkSequent(IProverSequent seq){
		if (! checkSequentPredicates(seq)) return false;
		if (! checkSequentSelection(seq)) return false;
		return true;
	}

	/**
	 * Generates the logical justifications for the given proof rule.
	 * 
	 * <p>
	 * The justifications of a proof rule are expressed as a list of sequents. The first justification in this
	 * list is the <em>main</em> justification for the rule and is always present. The rest of the justifications
	 * relate to {@link IForwardInfHypAction}s and occur in the same order of the antecedents, and within each antecedent,
	 * in the same order which they appear. 
	 * </p>
	 * 
	 * <p>
	 * It is assumed that the given rule passes all tests in {@link #checkRule(IProofRule)}.
	 * </p>
	 * 
	 * <p>
	 * The sequent prover, and clients providing Reasoners may use this method to generate logical and WD justifications for 
	 * the proof rules they generate in order to re-check their logical content independently.
	 * </p>
	 * 
	 * <p>
	 * WARINING : The return type of this method is subject to change in the near future. 
	 * </p>
	 * 
	 * @param rule
	 * 			the proof rule for which justifications to be generated.
	 * @param ff
	 * 			the formula factory to use
	 * @return
	 * 			the generated justifications.
	 * TODO : Generate WD justifications too. This required that the weaker well definedness operator 'D' is
	 * 			implemented in the AST.
	 * TODO : Maybe choose a better structure for returning justifications 
	 */
	public static List<IProverSequent> genRuleJustifications(IProofRule rule, FormulaFactory ff){

		List<IProverSequent> justifications = new ArrayList<IProverSequent>();
		final DLib lib = mDLib(ff);
		final ITypeEnvironmentBuilder typeEnv = lib.makeTypeEnvironment();

		// Get G_r and H_r
		Predicate g_r = rule.getGoal();
		boolean goalIndependent = (rule.getGoal() == null);
		if (goalIndependent){
			g_r = lib.False();
		}

		Set<Predicate> h_r = rule.getNeededHyps();

		// Enrich type environment using Gr and Hr
		for (Predicate hyp : h_r) {
			typeEnv.addAll(hyp.getFreeIdentifiers());
		}


		final IAntecedent[] antecedents = rule.getAntecedents();
		final Predicate[] antecedentParts = new Predicate[antecedents.length];
		for (int i = 0; i < antecedents.length; i++) {

			final FreeIdentifier[] i_a = antecedents[i].getAddedFreeIdents();
			final Set<Predicate> h_a = antecedents[i].getAddedHyps();
			Predicate g_a = antecedents[i].getGoal();
			if (g_a == null){
				if (goalIndependent){
					g_a = lib.False();
				}
				else
				{
					return null;
				}
			}

			antecedentParts[i] = lib.makeUnivQuant(i_a, lib.makeImpl(h_a, g_a));
			typeEnv.addAll(antecedentParts[i].getFreeIdentifiers());

			// Compute the forward inference justifications

			for (IHypAction hypAction : antecedents[i].getHypActions()) {
				if (hypAction instanceof IForwardInfHypAction) {
					IForwardInfHypAction fwdInf = (IForwardInfHypAction) hypAction;
					// Set<Predicate> hyps = union(h_r,h_a,fwdInf.getHyps());
					Set<Predicate> hyps = new LinkedHashSet<Predicate>(h_r);
					hyps.addAll(h_a);
					hyps.addAll(fwdInf.getHyps());
					Predicate goal = lib.makeExQuant(fwdInf.getAddedFreeIdents(), lib.makeConj(fwdInf.getInferredHyps()));
					ITypeEnvironmentBuilder localTypeEnv = lib.makeTypeEnvironment();
					for (Predicate hyp : h_r) {
						localTypeEnv.addAll(hyp.getFreeIdentifiers());
					}
					localTypeEnv.addAll(goal.getFreeIdentifiers());
					justifications.add(ProverFactory.makeSequent(localTypeEnv, hyps, hyps, goal));
				}
			}
		}

		Predicate goal = lib.makeImpl(Arrays.asList(antecedentParts), g_r);

		typeEnv.addAll(goal.getFreeIdentifiers());

		final IProverSequent mainJustification = ProverFactory.makeSequent(typeEnv , h_r, h_r, goal);
		justifications.add(0, mainJustification);
		return justifications;
	}	

	// *******************************************************************************************
	// Private Methods :
	// *******************************************************************************************

	/**
	 * Checks that all predicates in this sequent are well formed, type checked, and well
	 * typed with respect to the type environment of this sequent.
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @param seq
	 * 			the sequent to check
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */
	private static boolean checkSequentPredicates(IProverSequent seq) {
		final TypeChecker checker = new TypeChecker(
				seq.typeEnvironment());
		checker.checkFormula(seq.goal());
		checker.checkFormulas(seq.hypIterable());
		return !checker.hasTypeCheckError();
	}

	/**
	 * Checks that all selected and hidden hypotheses are contained in the hypotheses and that
	 * they are disjoint.
	 * <p>
	 * This method also returns debug trace messages in case one of these checks fails.
	 * </p>
	 * 
	 * @param seq
	 * 			the sequent to check
	 * 
	 * @return <code>true</code> iff all checks pass.
	 */
	private static boolean checkSequentSelection(IProverSequent seq){
		for (Predicate selHyp : seq.selectedHypIterable()){
			if (! seq.containsHypothesis(selHyp)){
				checkFailure(" Hypothesis " + selHyp +
				" is selected, but not pressent.");
				return false;
			}
			if (seq.isHidden(selHyp)){
				checkFailure(" Hypothesis " + selHyp +
				" is selected, and also hidden.");
				return false;
			}
		}
		for (Predicate hidHyp : seq.hiddenHypIterable()){
			if (! seq.containsHypothesis(hidHyp)){
				checkFailure(" Hypothesis " + hidHyp +
				" is hidden, but not pressent.");
				return false;
			}
		}
		return true;
	}

}
