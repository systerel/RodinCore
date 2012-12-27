/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added makeProofRule(IReasonerDesc, ...)
 *     Systerel - added makeProofRule with needed hypotheses
 *     Systerel - added makeAntecedent with unselected hypotheses
 *     Systerel - added used reasoners to proof dependencies
 *     Systerel - added makeSequent with origin
 *******************************************************************************/
package org.eventb.core.seqprover;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.internal.core.seqprover.ForwardInfHypAction;
import org.eventb.internal.core.seqprover.ProofRule;
import org.eventb.internal.core.seqprover.ProofRule.Antecedent;
import org.eventb.internal.core.seqprover.ProofTree;
import org.eventb.internal.core.seqprover.ProverSequent;
import org.eventb.internal.core.seqprover.ReasonerFailure;
import org.eventb.internal.core.seqprover.SelectionHypAction;

/**
 * Static class with factory methods required to construct various data structures
 * used in the sequent prover.
 * 
 * No predicate variables are allowed in any of these methods.
 * 
 * @author Farhad Mehta
 *
 * @since 1.0
 */
public final class ProverFactory {

	private static class ProofDeps implements IProofDependencies {
		private final ISealedTypeEnvironment usedFreeIdents;
		private final boolean hasDeps;
		private final Set<Predicate> usedHypotheses;
		private final Set<String> introducedFreeIdents;
		private final Predicate goal;
		private final Set<IReasonerDesc> usedReasoners;

		private ProofDeps(ISealedTypeEnvironment usedFreeIdents, boolean hasDeps,
				Set<Predicate> usedHypotheses,
				Set<String> introducedFreeIdents, Predicate goal,
				Set<IReasonerDesc> usedReasoners) {
			this.usedFreeIdents = usedFreeIdents;
			this.hasDeps = hasDeps;
			this.usedHypotheses = usedHypotheses;
			this.introducedFreeIdents = introducedFreeIdents;
			this.goal = goal;
			this.usedReasoners = usedReasoners;
		}

		public Predicate getGoal() {
			return goal;
		}

		public Set<String> getIntroducedFreeIdents() {
			return introducedFreeIdents;
		}

		public ISealedTypeEnvironment getUsedFreeIdents() {
			return usedFreeIdents;
		}

		public Set<Predicate> getUsedHypotheses() {
			return usedHypotheses;
		}

		public boolean hasDeps() {
			return hasDeps;
		}
		
		public Set<IReasonerDesc> getUsedReasoners() {
			return usedReasoners;
		}
	}

	/**
	 * Non-instantiable class
	 */
	private ProverFactory() {
	}
	
	/**
	 * Returns a new reasoner failure object with the given reason.
	 * 
	 * @param generatedBy
	 * 		Reasoner used
	 * @param generatedUsing
	 * 		Reasoner Input used
	 * @param reason
	 * 		Reason for reasoner failure
	 * @return
	 * 		A reasoner failure object with the given reason.
	 */
	public static IReasonerFailure reasonerFailure(
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			String reason){
		return new ReasonerFailure(generatedBy,generatedUsing,reason);
	}
	
	/**
	 * Returns a new proof rule with the given information
	 * 
	 * <p>
	 * This is the most general factory method to construct proof rules. In case the
	 * rule to be constructed is more specific, use of a more specific factory method
	 * is encouraged.
	 * </p>
	 * 
	 * @param generatedBy
	 * 		The reasoner used.
	 * @param generatedUsing
	 * 		The reasoner input used.
	 * @param goal
	 * 		The goal of the proof rule, or <code>null</code> iff the 
	 * 		proof rule is applicable to a sequent with any goal. In the latter case
	 * 		it is permitted that the antecedents may also contain a <code>null</code> goal.
	 * @param neededHyps
	 * 		The hypotheses needed for the proof rule to be applicable, or <code>null</code>
	 * 		iff no hypotheses are needed.
	 * @param confidence
	 * 		The confidence level of the proof rule, or <code>null</code> iff the greatest confidence level
	 * 		(i.e. <code>IConfidence.DISCHARGED_MAX</code>) is to be used.
	 * @param display
	 * 		The display string for the proof rule, or <code>null</code> iff the reasoner id is to be used.
	 * @param antecedents
	 * 		The antecedents of the proof rule, or <code>null</code> iff this rule has no antecedents.
	 * @return
	 * 		A new proof rule with the given information.
	 */
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Set<Predicate> neededHyps,
			Integer confidence,
			String display,
			IAntecedent... antecedents) {
			
		return new ProofRule(
				generatedBy,generatedUsing,
				goal,neededHyps,
				confidence,display,
				antecedents);
	}

	/**
	 * Returns a new proof rule with the given information
	 * 
	 * <p>
	 * This is similar to
	 * {@link #makeProofRule(IReasoner, IReasonerInput, Predicate, Set, Integer, String, IAntecedent...)}
	 * , except that the reasoner is given through its descriptor, which can
	 * later be retrieved by calling {@link IReasonerOutput#getReasonerDesc()}.
	 * </p>
	 * 
	 * @param generatedBy
	 *            A descriptor of the reasoner used
	 * @param generatedUsing
	 *            The reasoner input used
	 * @param goal
	 *            The goal of the proof rule, or <code>null</code> iff the proof
	 *            rule is applicable to a sequent with any goal. In the latter
	 *            case it is permitted that the antecedents may also contain a
	 *            <code>null</code> goal.
	 * @param neededHyps
	 *            The hypotheses needed for the proof rule to be applicable, or
	 *            <code>null</code> iff no hypotheses are needed.
	 * @param confidence
	 *            The confidence level of the proof rule, or <code>null</code>
	 *            iff the greatest confidence level (i.e.
	 *            <code>IConfidence.DISCHARGED_MAX</code>) is to be used.
	 * @param display
	 *            The display string for the proof rule, or <code>null</code>
	 *            iff the reasoner id is to be used.
	 * @param antecedents
	 *            The antecedents of the proof rule, or <code>null</code> iff
	 *            this rule has no antecedents.
	 * @return A new proof rule with the given information.
	 */
	public static IProofRule makeProofRule(IReasonerDesc generatedBy,
			IReasonerInput generatedUsing, Predicate goal,
			Set<Predicate> neededHyps, Integer confidence, String display,
			IAntecedent... antecedents) {

		return new ProofRule(generatedBy, generatedUsing, goal, neededHyps,
				confidence, display, antecedents);
	}
	
	/**
	 * Returns a new proof rule with at most one needed hypothesis and the maximum
	 * confidence
	 * 
	 * @param generatedBy
	 * 		The reasoner used.
	 * @param generatedUsing
	 * 		The reasoner input used.
	 * @param goal
	 * 		The goal of the proof rule, or <code>null</code> iff the 
	 * 		proof rule is applicable to a sequent with any goal.
	 * @param neededHyp
	 * 		The hypothesis needed for the proof rule to be applicable, or <code>null</code>
	 * 		iff no hypotheses are needed.
	 * @param display
	 * 		The display string for the proof rule, or <code>null</code> iff the reasoner id is to be used.
	 * @param antecedents
	 * 		The antecedents of the proof rule, or <code>null</code> iff this rule has no antecedents.
	 * @return
	 * 		A new proof rule with the given information.
	 */
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Predicate neededHyp,
			String display,
			IAntecedent... antecedents) {
		
		final Set<Predicate>  neededHyps;
		if (neededHyp == null) {
			neededHyps = null;
		} else {
			neededHyps = Collections.singleton(neededHyp);
		}
		return makeProofRule(
				generatedBy, generatedUsing,
				goal, neededHyps,
				null, display,
				antecedents);
	}
	
	/**
	 * Returns a new proof rule with a list of needed hypotheses and the maximum
	 * confidence
	 * 
	 * @param generatedBy
	 * 		The reasoner used
	 * @param generatedUsing
	 * 		The reasoner input used
	 * @param goal
	 * 		The goal of the proof rule, or <code>null</code> iff the 
	 * 		proof rule is applicable to a sequent with any goal
	 * @param neededHyps
	 * 		The hypotheses needed for the proof rule to be applicable
	 * @param display
	 * 		The display string for the proof rule, or <code>null</code> iff the reasoner id is to be used
	 * @param antecedents
	 * 		The antecedents of the proof rule, or <code>null</code> iff this rule has no antecedents
	 * @return
	 * 		A new proof rule with the given information.
	 * 
	 * @since 2.0
	 */
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			Set<Predicate> neededHyps,
			String display,
			IAntecedent... antecedents) {
		
		return makeProofRule(
				generatedBy, generatedUsing,
				goal, neededHyps,
				null, display,
				antecedents);
	}

	/**
	 * Returns a new proof rule with needed hypotheses that only contains
	 * hypothesis actions and the maximum confidence
	 * 
	 * <p>
	 * This factory method returns a goal independent rule with one antecedent
	 * containing the given hypothesis actions.
	 * </p>
	 * 
	 * @param generatedBy
	 *            The reasoner used
	 * @param generatedUsing
	 *            The reasoner input used
	 * @param neededHyps
	 *            A set of hypotheses needed by this rule to apply
	 * @param display
	 *            The display string for the proof rule, or <code>null</code>
	 *            iff the reasoner id is to be used
	 * @param hypActions
	 *            The hypothesis actions contained in the rule, or
	 *            <code>null</code> iff there are no hypothesis actions.
	 * @return A new proof rule with the given information.
	 * 
	 * @since 2.0
	 */
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Set<Predicate> neededHyps,
			String display,
			List<IHypAction> hypActions) {
		
		IAntecedent antecedent = makeAntecedent(null, null, null, hypActions);
		return makeProofRule(
				generatedBy,generatedUsing,
				null,neededHyps,null,
				display,new IAntecedent[]{antecedent});
	}
	
	/**
	 * Returns a new proof rule with no needed hypothesis and the maximum
	 * confidence
	 * 
	 * @param generatedBy
	 * 		The reasoner used.
	 * @param generatedUsing
	 * 		The reasoner input used.
	 * @param goal
	 * 		The goal of the proof rule, or <code>null</code> iff the 
	 * 		proof rule is applicable to a sequent with any goal.
	 * @param display
	 * 		The display string for the proof rule, or <code>null</code> iff 
	 * 		the reasoner id is to be used.
	 * @param antecedents
	 * 		The antecedents of the proof rule, or <code>null</code> iff this rule
	 * 		 has no antecedents.
	 * @return
	 * 		A new proof rule with the given information.
	 */
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			Predicate goal,
			String display,
			IAntecedent... antecedents) {
		return makeProofRule(generatedBy,generatedUsing,goal,null,null,display,antecedents);
	}
	
	/**
	 * Returns a new proof rule that only contains hypothesis actions.
	 * 
	 * <p>
	 * This factory method returns a goal independent rule with one antecedent containing
	 * the given hypothesis actions.
	 * </p>
	 * 
	 * @param generatedBy
	 * 		The reasoner used.
	 * @param generatedUsing
	 * 		The reasoner input used.
	 * @param display
	 * 		The display string for the proof rule, or <code>null</code> iff 
	 * 		the reasoner id is to be used.
	 * @param hypActions
	 * 		The hypothesis actions contained in the rule, or <code>null</code> iff 
	 * 		there are no hypothesis actions.
	 * @return
	 * 		A new proof rule with the given information.
	 */
	public static IProofRule makeProofRule (
			IReasoner generatedBy,
			IReasonerInput generatedUsing,
			String display,
			List<IHypAction> hypActions) {
		
		IAntecedent antecedent = makeAntecedent(null, null, null, hypActions);
		return makeProofRule(
				generatedBy,generatedUsing,
				null,null,null,
				display,new IAntecedent[]{antecedent});
	}

	/**
	 * Returns a new antecedent with the given inputs. The constructed
	 * antecedent can then be used to construct a proof rule.
	 * 
	 * <p>
	 * This is a very general factory method to construct antecedents. In case
	 * the antecedent to be constructed is more specific, use of a more specific
	 * factory method is encouraged.
	 * </p>
	 * 
	 * @param goal
	 *            The goal of the antecedent, or <code>null</code> iff the rule
	 *            is intended to be goal independent.
	 * @param addedHyps
	 *            The added hypotheses, or <code>null</code> iff there are no
	 *            added hypotheses.
	 * @param addedFreeIdents
	 *            The added free identifiers, or <code>null</code> iff there are
	 *            no added free identifiers.
	 * @param hypActions
	 *            The hypothesis actions, or <code>null</code> iff there are no
	 *            hypothesis actions.
	 * @return A new antecedent with the given information.
	 */
	public static IAntecedent makeAntecedent(
			Predicate goal,
			Set<Predicate> addedHyps,
			FreeIdentifier[] addedFreeIdents,
			List<IHypAction> hypActions){
		
		return makeAntecedent(goal, addedHyps,
				Collections.<Predicate> emptySet(), addedFreeIdents, hypActions);
	}

	/**
	 * Returns a new antecedent with the given inputs. The constructed
	 * antecedent can then be used to construct a proof rule.
	 * 
	 * <p>
	 * This is the most general factory method to construct antecedents. In case
	 * the antecedent to be constructed is more specific, use of a more specific
	 * factory method is encouraged.
	 * </p>
	 * 
	 * @param goal
	 *            The goal of the antecedent, or <code>null</code> iff the rule
	 *            is intended to be goal independent
	 * @param addedHyps
	 *            The added hypotheses, or <code>null</code> iff there are no
	 *            added hypotheses
	 * @param unselectedHyps
	 *            a subset of added hyps for hypotheses added but not selected,
	 *            or <code>null</code> if there are no unselected added
	 *            hypotheses
	 * @param addedFreeIdents
	 *            The added free identifiers, or <code>null</code> iff there are
	 *            no added free identifiers
	 * @param hypActions
	 *            The hypothesis actions, or <code>null</code> iff there are no
	 *            hypothesis actions
	 * @return A new antecedent with the given information
	 * @since 2.0
	 */
	public static IAntecedent makeAntecedent(
			Predicate goal,
			Set<Predicate> addedHyps,
			Set<Predicate> unselectedHyps,
			FreeIdentifier[] addedFreeIdents,
			List<IHypAction> hypActions){
		
		return new Antecedent(goal, addedHyps, unselectedHyps, addedFreeIdents,
				hypActions);
	}

	/**
	 * Returns a new antecedent with no added free identifiers, and at most one hypothesis
	 * action.
	 * 
	 * @param goal
	 * 		The goal of the antecedent, or <code>null</code> iff the rule is intended
	 * 		to be goal independent.
	 * @param addedHyps
	 * 		The added hypotheses, or <code>null</code> iff there are no added 
	 * 		hypotheses.
	 * @param hypAction
	 * 		The hypothesis action, or <code>null</code> iff there are no hypothesis
	 * 		actions.
	 * @return
	 * 		A new antecedent with the given information.
	 */
	public static IAntecedent makeAntecedent(
			Predicate goal,
			Set<Predicate> addedHyps,
			IHypAction hypAction) {
		
		List<IHypAction> hypActions = null;
		if (hypAction != null){
			hypActions = Collections.singletonList(hypAction);
		}
		return makeAntecedent(goal,addedHyps,null,hypActions);
	}
	
	/**
	 * Returns a new antecedent that may only specify a goal (i.e. with no added 
	 * hypotheses, no added free identifiers and no hypothesis actions.)
	 * 
	 * @param goal
	 * 		The goal of the antecedent, or <code>null</code> iff the rule is intended
	 * 		to be goal independent.
	 * @return
	 * 		A new antecedent with the given information.
	 */
	public static IAntecedent makeAntecedent(Predicate goal) {
		return makeAntecedent(goal,null,null,null);
	}

	
	/**
	 * Returns a new sequent with the given parameters.
	 * 
	 * <p>
	 * Note : <br>
	 * The parameters provided to construct the sequent must be consistent in
	 * order to construct a proper sequent. In particular:
	 * <ul>
	 * <li>All predicates (i.e. all hypotheses and the goal) must be type
	 * checked.
	 * <li>The type environment provided should contain all free identifiers and
	 * carrier sets appearing in the predicates of the sequent and can be used
	 * to successfully type check them.
	 * </ul>
	 * These checks need to be done before calling this method. The behaviour of
	 * the sequent prover is undefined if these checks are not done.
	 * </p>
	 * 
	 * @param typeEnv
	 *            the type environment for the sequent. This parameter must not
	 *            be <code>null</code> It should be ensured that all predicates
	 *            can be type checked using this type environment.
	 * @param hyps
	 *            The set of hypotheses, or <code>null</code> iff this set is
	 *            intended to be empty.
	 * @param goal
	 *            The goal. This parameter must not be <code>null</code>.
	 * @return A new sequent with the given parameters.
	 */
	public static IProverSequent makeSequent(ITypeEnvironment typeEnv,
			Collection<Predicate> hyps, Predicate goal) {
		return makeSequent(typeEnv, hyps, null, goal);
	}
	
	/**
	 * Returns a new sequent with some selected hypotheses from the given
	 * parameters.
	 * 
	 * <p>
	 * Note : <br>
	 * The parameters provided to construct the sequent must be consistent in
	 * order to construct a proper sequent. In particular:
	 * <ul>
	 * <li>All predicates (i.e. all hypotheses and the goal) must be type
	 * checked.
	 * <li>The type environment provided should contain all free identifiers and
	 * carrier sets appearing in the predicates of the sequent and can be used
	 * to successfully type check them.
	 * <li>All hypotheses to be selected must also be present in the set of
	 * hypotheses provided.
	 * </ul>
	 * These checks need to be done before calling this method. The behaviour of
	 * the sequent prover is undefined if these checks are not done.
	 * </p>
	 * 
	 * @param typeEnv
	 *            The type environment for the sequent. This parameter must not
	 *            be <code>null</code> It should be ensured that all predicates
	 *            can be type checked using this type environment.
	 * @param hyps
	 *            The set of hypotheses, or <code>null</code> iff this set is
	 *            intended to be empty.
	 * @param selHyps
	 *            The set of hypotheses to select. The set of hypotheses to
	 *            select should be contained in the set of hypotheses
	 * @param goal
	 *            The goal. This parameter must not be <code>null</code>.
	 * @return A new sequent with the given information.
	 */
	public static IProverSequent makeSequent(ITypeEnvironment typeEnv,
			Collection<Predicate> hyps, Collection<Predicate> selHyps,
			Predicate goal) {
		return makeSequent(typeEnv, hyps, selHyps, goal, null);
	}

	/**
	 * Returns a new sequent with some selected hypotheses from the given
	 * parameters.
	 * 
	 * <p>
	 * Note : <br>
	 * The parameters provided to construct the sequent must be consistent in
	 * order to construct a proper sequent. In particular:
	 * <ul>
	 * <li>All predicates (i.e. all hypotheses and the goal) must be type
	 * checked.
	 * <li>The type environment provided should contain all free identifiers and
	 * carrier sets appearing in the predicates of the sequent and can be used
	 * to successfully type check them.
	 * <li>All hypotheses to be selected must also be present in the set of
	 * hypotheses provided.
	 * </ul>
	 * These checks need to be done before calling this method. The behaviour of
	 * the sequent prover is undefined if these checks are not done.
	 * </p>
	 * 
	 * @param typeEnv
	 *            The type environment for the sequent. This parameter must not
	 *            be <code>null</code> It should be ensured that all predicates
	 *            can be type checked using this type environment.
	 * @param hyps
	 *            The set of hypotheses, or <code>null</code> iff this set is
	 *            intended to be empty.
	 * @param selHyps
	 *            The set of hypotheses to select. The set of hypotheses to
	 *            select should be contained in the set of hypotheses
	 * @param goal
	 *            The goal. This parameter must not be <code>null</code>.
	 * @param origin
	 *            The origin of the sequent, or <code>null</code>.
	 * @return A new sequent with the given information.
	 * @since 2.4
	 */
	public static IProverSequent makeSequent(ITypeEnvironment typeEnv,
			Collection<Predicate> hyps, Collection<Predicate> selHyps,
			Predicate goal, Object origin) {
		return new ProverSequent(typeEnv, hyps, null, selHyps, goal, origin);
	}

	/**
	 * Returns a new proof tree with the given sequent at the root.
	 * 
	 * @param sequent
	 *            the sequent of the root node. 
	 *            This parameter must not be <code>null</code>.
	 * @param origin
	 *            an object describing the origin of the sequent, might be
	 *            <code>null</code>
	 * @return a new proof tree for the given sequent
	 */
	public static IProofTree makeProofTree(IProverSequent sequent, Object origin) {
		return new ProofTree(sequent, origin);
	}

	
	/**
	 * Returns a new select hypotheses action
	 * 
	 * @param toSelect
	 * 		Hypotheses to select (should not be <code>null</code>)
	 * @return
	 * 		A new select hypotheses action
	 */
	public static ISelectionHypAction makeSelectHypAction(Collection<Predicate> toSelect){
		return new SelectionHypAction(ISelectionHypAction.SELECT_ACTION_TYPE,toSelect);
	}

	/**
	 * Returns a new deselect hypotheses action
	 * 
	 * @param toDeselect
	 * 		Hypotheses to deselect (should not be <code>null</code>)
	 * @return
	 * 		A new deselect hypotheses action
	 */
	public static ISelectionHypAction makeDeselectHypAction(Collection<Predicate> toDeselect){
		return new SelectionHypAction(ISelectionHypAction.DESELECT_ACTION_TYPE,toDeselect);
	}

	/**
	 * Returns a new hide hypotheses action
	 * 
	 * @param toHide
	 * 		Hypotheses to hide (should not be <code>null</code>)
	 * @return
	 * 		A new hide hypotheses action
	 */
	public static ISelectionHypAction makeHideHypAction(Collection<Predicate> toHide){
		return new SelectionHypAction(ISelectionHypAction.HIDE_ACTION_TYPE,toHide);
	}

	/**
	 * Returns a new show hypotheses action
	 * 
	 * @param toShow
	 * 		Hypotheses to show (should not be <code>null</code>)
	 * @return
	 * 		A new show hypotheses action
	 */
	public static ISelectionHypAction makeShowHypAction(Collection<Predicate> toShow){
		return new SelectionHypAction(ISelectionHypAction.SHOW_ACTION_TYPE,toShow);
	}
	
	/**
	 * Returns a new forward inference hypothesis action
	 * 
	 * <p>
	 * This is the most general factory method for this construction. In case the
	 * construction is more specific, the use of a more specific factory method
	 * is encouraged.
	 * </p>
	 * 
	 * @param hyps
	 * 		The hypotheses required by the forward inference (should not be <code>null</code>)
	 * @param addedFreeIdents
	 * 		Fresh free identifiers added by the forward inference (should not be <code>null</code>)
	 * @param inferredHyps
	 * 		The inferred hypotheses (should not be <code>null</code>)
	 * @return
	 * 		A new forward inference hypothesis action
	 */
	public static IForwardInfHypAction makeForwardInfHypAction(Collection<Predicate> hyps, FreeIdentifier[] addedFreeIdents, Collection<Predicate> inferredHyps){
		return new ForwardInfHypAction(hyps,addedFreeIdents,inferredHyps);
	}
	
	private final static FreeIdentifier[] NO_FREE_IDENTS = new FreeIdentifier[0];
	
	/**
	 * Returns a new forward inference hypothesis action that does not introduce
	 * free identifiers
	 * 
	 * @param hyps
	 * 		The hypotheses required by the forward inference (should not be <code>null</code>)
	 * @param inferredHyps
	 * 		The inferred hypotheses (should not be <code>null</code>)
	 * @return
	 * 		A new forward inference hypothesis action
	 */
	public static IForwardInfHypAction makeForwardInfHypAction(Collection<Predicate> hyps, Collection<Predicate> inferredHyps){
		return new ForwardInfHypAction(hyps,NO_FREE_IDENTS,inferredHyps);
	}

	/**
	 * Constructs an instance of {@link IProofDependencies} from the values
	 * given as parameters.
	 * 
	 * This is a convenience method. Clients must independently check that the
	 * data provided conforms to the constraints in {@link IProofDependencies}.
	 * 
	 * @param hasDeps
	 * @param goal
	 * @param usedHypotheses
	 * @param usedFreeIdents
	 * @param introducedFreeIdents
	 * @param usedReasoners
	 * @return An instance of {@link IProofDependencies} with the values given as 
	 * 	input parameters
	 * @since 3.0
	 */
	public static IProofDependencies makeProofDependencies(
			final boolean hasDeps,
			final Predicate goal,
			final Set<Predicate> usedHypotheses,
			final ISealedTypeEnvironment usedFreeIdents,
			final Set<String> introducedFreeIdents,
			final Set<IReasonerDesc> usedReasoners) {

		return new ProofDeps(usedFreeIdents, hasDeps, usedHypotheses,
				introducedFreeIdents, goal, usedReasoners);
	}

	/**
	 * Returns a new sequent with some hidden and selected hypotheses from the
	 * given parameters.
	 * 
	 * <p>
	 * Note : <br>
	 * The parameters provided to construct the sequent must be consistent in
	 * order to construct a proper sequent. In particular:
	 * <ul>
	 * <li> All predicates (i.e. all hypotheses and the goal) must be type
	 * checked.
	 * <li> The type environment provided should contain all free identifiers
	 * and carrier sets appearing in the predicates of the sequent and can be
	 * used to successfully type check them.
	 * <li> All hypotheses to be hidden must also be present in the set of
	 * hypotheses provided.
	 * <li> All hypotheses to be selected must also be present in the set of
	 * hypotheses provided.
	 * <li> All hypotheses to be hidden and selected must be disjoint.
	 * </ul>
	 * These checks need to be done before calling this method. The behaviour of
	 * the sequent prover is undefined if these checks are not done.
	 * </p>
	 * 
	 * @param typeEnv
	 *            The type environment for the sequent. This parameter must not
	 *            be <code>null</code>. It should be ensured that all predicates
	 *            can be type checked using this type environment.
	 * @param globalHypSet
	 *            The set of hypotheses, or <code>null</code> iff this set is
	 *            intended to be empty.
	 * @param hiddenHypSet
	 *            The set of hypotheses to hide. The set of hypotheses to hide
	 *            should be contained in the set of hypotheses
	 * @param selectedHypSet
	 *            The set of hypotheses to select. The set of hypotheses to
	 *            select should be contained in the set of hypotheses and
	 *            disjoint from the set of hidden hypotheses.
	 * @param goal
	 *            The goal. This parameter must not be <code>null</code>.
	 * @return A new sequent with the given information.
	 */
	public static IProverSequent makeSequent(ITypeEnvironment typeEnv,
			Set<Predicate> globalHypSet, Set<Predicate> hiddenHypSet,
			Set<Predicate> selectedHypSet, Predicate goal) {
		return makeSequent(typeEnv,globalHypSet, hiddenHypSet, selectedHypSet, goal, null);
	}
	
	/**
	 * Returns a new sequent with some hidden and selected hypotheses from the
	 * given parameters.
	 * 
	 * <p>
	 * Note : <br>
	 * The parameters provided to construct the sequent must be consistent in
	 * order to construct a proper sequent. In particular:
	 * <ul>
	 * <li>All predicates (i.e. all hypotheses and the goal) must be type
	 * checked.
	 * <li>The type environment provided should contain all free identifiers and
	 * carrier sets appearing in the predicates of the sequent and can be used
	 * to successfully type check them.
	 * <li>All hypotheses to be hidden must also be present in the set of
	 * hypotheses provided.
	 * <li>All hypotheses to be selected must also be present in the set of
	 * hypotheses provided.
	 * <li>All hypotheses to be hidden and selected must be disjoint.
	 * </ul>
	 * These checks need to be done before calling this method. The behaviour of
	 * the sequent prover is undefined if these checks are not done.
	 * </p>
	 * 
	 * @param typeEnv
	 *            The type environment for the sequent. This parameter must not
	 *            be <code>null</code>. It should be ensured that all predicates
	 *            can be type checked using this type environment.
	 * @param globalHypSet
	 *            The set of hypotheses, or <code>null</code> iff this set is
	 *            intended to be empty.
	 * @param hiddenHypSet
	 *            The set of hypotheses to hide. The set of hypotheses to hide
	 *            should be contained in the set of hypotheses
	 * @param selectedHypSet
	 *            The set of hypotheses to select. The set of hypotheses to
	 *            select should be contained in the set of hypotheses and
	 *            disjoint from the set of hidden hypotheses.
	 * @param origin
	 *            the origin of the sequent
	 * @param goal
	 *            The goal. This parameter must not be <code>null</code>.
	 * @return A new sequent with the given information.
	 * @since 2.4
	 */
	public static IProverSequent makeSequent(ITypeEnvironment typeEnv,
			Set<Predicate> globalHypSet, Set<Predicate> hiddenHypSet,
			Set<Predicate> selectedHypSet, Predicate goal, Object origin) {
		return new ProverSequent(typeEnv, globalHypSet, hiddenHypSet,
				selectedHypSet, goal, origin);
	}
	
}
