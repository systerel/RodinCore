/*******************************************************************************
 * Copyright (c) 2007, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added partition tactic (math V2)
 *     Systerel - added rm for UPTO and Arith and OnePoint tactics
 *     Systerel - added Total Domain Substitution
 *     Systerel - added contrHyps() (CNTR)
 *     Systerel - fixed rules FIN_FUN_*
 *     Systerel - implemented rules FUNIMG_SET_DOMSUB_L and FUNIMG_DOMSUB_L
 *     Systerel - implemented DATATYPE_DISTINCT_CASE and DATATYPE_INDUCTION
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensions;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.eventb.core.ast.Formula.BCOMP;
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BOUND_IDENT_DECL;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.DOMSUB;
import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FCOMP;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.KUNION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LEQV;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.OVR;
import static org.eventb.core.ast.Formula.PPROD;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;
import static org.eventb.core.ast.Formula.RANRES;
import static org.eventb.core.ast.Formula.RANSUB;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.ast.IPosition.ROOT;
import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;
import static org.eventb.internal.core.seqprover.eventbExtensions.DTReasonerHelper.isDatatypeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.reasoners.MngHyp;
import org.eventb.core.seqprover.reasoners.Review;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstrExpr;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.eventb.internal.core.seqprover.eventbExtensions.AllmpD;
import org.eventb.internal.core.seqprover.eventbExtensions.AllmtD;
import org.eventb.internal.core.seqprover.eventbExtensions.CardComparison;
import org.eventb.internal.core.seqprover.eventbExtensions.CardUpTo;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.ConjF;
import org.eventb.internal.core.seqprover.eventbExtensions.ContrHyps;
import org.eventb.internal.core.seqprover.eventbExtensions.ContrL1;
import org.eventb.internal.core.seqprover.eventbExtensions.Cut;
import org.eventb.internal.core.seqprover.eventbExtensions.DTDistinctCase;
import org.eventb.internal.core.seqprover.eventbExtensions.DTInduction;
import org.eventb.internal.core.seqprover.eventbExtensions.DerivEqualInterv;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.eventb.internal.core.seqprover.eventbExtensions.DoCase;
import org.eventb.internal.core.seqprover.eventbExtensions.EqHe;
import org.eventb.internal.core.seqprover.eventbExtensions.EqL2;
import org.eventb.internal.core.seqprover.eventbExtensions.EqvLR;
import org.eventb.internal.core.seqprover.eventbExtensions.EqvRL;
import org.eventb.internal.core.seqprover.eventbExtensions.ExF;
import org.eventb.internal.core.seqprover.eventbExtensions.ExI;
import org.eventb.internal.core.seqprover.eventbExtensions.ExponentiationStep;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteCompset;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteDom;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunConv;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunDom;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunRan;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunRelImg;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteFunction;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteInter;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteMax;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteMin;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteNegative;
import org.eventb.internal.core.seqprover.eventbExtensions.FinitePositive;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteRan;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteRelImg;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteRelation;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSet;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteSetMinus;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteUnion;
import org.eventb.internal.core.seqprover.eventbExtensions.FunCompImg;
import org.eventb.internal.core.seqprover.eventbExtensions.FunDprodImg;
import org.eventb.internal.core.seqprover.eventbExtensions.FunImageGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.FunInterImg;
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;
import org.eventb.internal.core.seqprover.eventbExtensions.FunSetMinusImg;
import org.eventb.internal.core.seqprover.eventbExtensions.FunSingletonImg;
import org.eventb.internal.core.seqprover.eventbExtensions.He;
import org.eventb.internal.core.seqprover.eventbExtensions.HeL2;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpCase;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpE;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.ModusTollens;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointRule;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites.Input;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AndOrDistRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ArithRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ArithRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.BCompDefRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.CardDefRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.CompImgRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.CompUnionDistRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ContImplHypRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ConvRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DisjunctionToImplicationRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomCompRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomDistLeftRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomDistRightRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DomRanUnionDistRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DoubleImplHypRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqualCardRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqualFunImgDefRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.EqvRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FiniteDefRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimpImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimplifies;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunPprodImg;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpAndRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpOrRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.InclusionSetMinusLeftRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.InclusionSetMinusRightRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.LocalEqRewrite;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.MinMaxDefRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PartitionRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RanCompRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RanDistLeftRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RanDistRightRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RelImgUnionLeftRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RelImgUnionRightRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RelOvrRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusionUniversal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegationRewriterImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.SetEqlRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.SetMinusRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.StrictInclusionRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TotalDomFacade;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.UnionInterDistRewrites;
import org.eventb.internal.core.seqprover.reasonerInputs.PFunSetInput;

/**
 * This class contains static methods that wrap Event-B reasoner extensions into
 * tactics. In many cases, applicability methods are also included that
 * implement a quick check to see if the tactic may be applicable in a
 * particular situation.
 * 
 * @author Farhad Mehta, htson
 * 
 * TODO : complete comments.
 * FIXME Split this huge class in several classes
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class Tactics {

	private static final EmptyInput EMPTY_INPUT = new EmptyInput();

	private static final List<IPosition> NO_POSITIONS = emptyList();

	private static final List<IPosition> POSITION_ROOT = singletonList(ROOT);

	// Globally applicable tactics

	/**
	 * The review tactic.
	 * 
	 * @param reviewerConfidence
	 * 			The reviewer confidence to use.
	 * @return
	 * 			The resulting tactic
	 */
	public static ITactic review(final int reviewerConfidence) {
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				final ITactic tactic = BasicTactics.reasonerTac(new Review(),
						new Review.Input(pt.getSequent(), reviewerConfidence));
				return tactic.apply(pt, pm);
			}
		};
	}

	/**
	 * The add lemma tactic.
	 * 
	 * Introduces a lemma (and its well definedness condition) at a given open proof tree node.
	 * 
	 * @param lemma
	 * 		The lemma to introduce as a String.
	 * @return
	 * 		The resulting lemma tactic.
	 */
	public static ITactic lemma(final String lemma) {

		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new Cut(),
						new SinglePredInput(lemma, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
			}

		};
	}
	
	/**
	 * The insert lemma tactic.
	 * 
	 * Inserts a lemma (and its well definedness condition) at a given proof tree node. This
	 * proof tree node need not be open.
	 * 
	 * @param lemma
	 * 		The lemma to insert as a String.
	 * @return
	 * 		The resulting tactic.
	 */
	public static ITactic insertLemma(final String lemma) {

		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				
				// Try to generate a proof rule.
				IReasonerOutput reasonerOutput = (new Cut()).apply(
						pt.getSequent(),
						new SinglePredInput(lemma, pt.getSequent().typeEnvironment()), pm);
				
				if (! (reasonerOutput instanceof IProofRule)) {
					// reasoner failed.
					return reasonerOutput;
				}
				
				IProofRule rule = (IProofRule) reasonerOutput;
				
				// Get the proof skeleton at the node.
				IProofSkeleton skel = pt.copyProofSkeleton();
				// Prune the node.
				pt.pruneChildren();
				// apply the rule
				boolean success = pt.applyRule(rule);
				if (success){
					// Get the node where the proof skeleton should be rebuilt.
					IProofTreeNode continuation = pt.getChildNodes()[pt.getChildNodes().length - 1];
					assert continuation.isOpen();
					return BasicTactics.reuseTac(skel).apply(continuation, pm);
				}else{
					// reconstruct the orignal tree
					BasicTactics.reuseTac(skel).apply(pt, pm);
					return "Lemma could not be inserted";
				}
			}

		};
	}
	
	/**
	 * The abstract expression tactic.
	 * 
	 * Abstracts an expression (and introduces its well definedness condition) into a proof.
	 * 
	 * @param expression
	 * 		The expression to abstract as a String.
	 * @return
	 * 		The resulting Abstract Expression tactic.
	 */
	public static ITactic abstrExpr(final String expression) {

		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new AbstrExpr(),
						new AbstrExpr.Input(expression, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
			}

		};
	}
	
	public static ITactic abstrExprThenEq(final String expression) {
		return abstrExprThenEq(expression, EqHe.Level.L1);
	}

	/**
	 * Old version of {@link #abstrExprThenEq(String)} that uses the level 0 of Eq.
	 *
	 * This should not be used in new code. It is only used for backwards
	 * compatibility in old tests.
	 *
	 * @param expression expression on which the tactics are applied
	 * @return tactics ae then eq
	 * @since 3.6
	 */
	public static ITactic abstrExprThenEqL0(final String expression) {
		return abstrExprThenEq(expression, EqHe.Level.L0);
	}

	// TODO : Find a better way to do this. Maybe have a combined reasoner.
	private static ITactic abstrExprThenEq(final String expression, final EqHe.Level level) {
		
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				
				// Apply the abstract expression tactic
				var aeInput = new AbstrExpr.Input(expression, pt.getSequent().typeEnvironment());
				Object result = BasicTactics.reasonerTac(new AbstrExpr(), aeInput).apply(pt, pm);
				
				// Check if it was successful
				if (result != null) return result;
				
				// Get the introduced equality
				IAntecedent[] antecedents = pt.getRule().getAntecedents();
				assert antecedents.length != 0;
				IAntecedent lastAntecedent = antecedents[antecedents.length - 1];
								
				Predicate eqHyp = getLastAddedHyp(lastAntecedent);
				
				if (eqHyp == null ||  ! Lib.isEq(eqHyp)){
					pt.pruneChildren();
					return "Unexpected Behaviour from AE reasoner";
				}
				
				// Get the node where Eq should be applied
				IProofTreeNode node = pt.getChildNodes()[antecedents.length - 1];
				// apply Eq
				result = he(eqHyp, level).apply(node, pm);
				
				// Check if it was successful
				if (result != null) {
					// the reason the he is unsuccessful is only that the abstracted expression does not
					// occur in the hyps or the goal.
					// In this case, this tactic is unsuccessful. Undo the ae
					pt.pruneChildren();
					return "Expression " + aeInput.getExpression() + " does not occur in goal or selected hypotheses.";
				}
				
				// Immediately deselect the introduced equality so that
				// the autoEQ tactic does not reverse the last eq.
				
				ISelectionHypAction deselectHypAction = ProverFactory.makeDeselectHypAction(Collections.singleton(eqHyp));
				assert node.getChildNodes().length == 1;
				node = node.getChildNodes()[0];
				mngHyp(deselectHypAction).apply(node, pm);
				
				return null;
			}

		};
	}
	
	
	/**
	 * Returns the last added hypothesis of the given antecedent, or <code>null</code> if the antecedent
	 * contains no added hypotheses.
	 * 
	 * @param antecedent
	 * 		the given antecedent
	 * @return the last added hypothesis of the given antecedent, or <code>null</code> if the antecedent
	 * contains no added hypotheses.
	 * 		
	 */
	private static Predicate getLastAddedHyp(IAntecedent antecedent){
		Predicate last = null;
		for (Predicate addedHyp : antecedent.getAddedHyps()) {
			last = addedHyp;
		}
		return last;
	}

	/**
	 * The do case tactic.
	 * 
	 * Introduces a case distinction on a predicate into a proof.
	 * 
	 * @param trueCase
	 * 		The true case of the case distinction as a String.
	 * @return
	 * 		The resulting do case tactic.
	 */
	public static ITactic doCase(final String trueCase) {

		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new DoCase(),
						new SinglePredInput(trueCase, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
			}

		};
	}

	/**
	 * The lasoo tactic.
	 * 
	 * This tactic selects all hypotheses that have free identifiers in common
	 * with the current goal and currently selected hypotheses.
	 * 
	 * @return
	 * 		The lasoo tactic.
	 */
	public static ITactic lasoo() {

		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				IProverSequent seq = pt.getSequent();
				Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
				freeIdents.addAll(Arrays
						.asList(seq.goal().getFreeIdentifiers()));
				for (Predicate hyp : seq.selectedHypIterable()) {
					freeIdents.addAll(Arrays.asList(hyp.getFreeIdentifiers()));
				}

				Set<Predicate> hypsToSelect = ProverLib.hypsFreeIdentsSearch(
						seq, freeIdents);
				removeHiddenAndSelectedHyps(hypsToSelect, seq);
				if (hypsToSelect.isEmpty())
					return "No more hypotheses found";
				return (mngHyp(ProverFactory.makeSelectHypAction(hypsToSelect)))
						.apply(pt, pm);
			}

		};
	}

	private static void removeHiddenAndSelectedHyps(Set<Predicate> hyps,
			IProverSequent sequent) {
		final Iterator<Predicate> iter = hyps.iterator();
		while (iter.hasNext()) {
			final Predicate hyp = iter.next();
			if (sequent.isHidden(hyp) || sequent.isSelected(hyp)) {
				iter.remove();
			}
		}
	}

	/**
	 * Runs the auto formula rewriter that normalizes all predicates of the
	 * sequent at the given node.
	 * 
	 * @since 2.3
	 */
	public static ITactic autoRewrite() {
		return BasicTactics.reasonerTac(AutoRewrites.DEFAULT, EMPTY_INPUT);
	}
	
	// Tactics applicable on the goal

	/**
	 * The contradict goal tactic.
	 * 
	 * 
	 * 
	 * @return
	 * 		The contradict goal tactic.
	 */
	public static ITactic contradictGoal() {
		return BasicTactics.reasonerTac(new ContrL1(), new ContrL1.Input(null));
	}

	public static boolean contradictGoal_applicable(IProofTreeNode node) {
		Predicate goal = node.getSequent().goal();
		final FormulaFactory ff = node.getFormulaFactory();
		if (goal.equals(DLib.False(ff)))
			return false;
		Predicate negGoal = DLib.makeNeg(goal);
		if (negGoal.equals(DLib.True(ff)))
			return false;
		// if (Predicate.containsPredicate(
		// node.getSequent().selectedHypotheses(),
		// negGoal));
		return true;

	}

	public static ITactic impI() {
		return BasicTactics.reasonerTac(new ImpI(), EMPTY_INPUT);
	}

	public static boolean impI_applicable(Predicate goal) {
		return Lib.isImp(goal);
	}

	public static ITactic conjI() {
		return BasicTactics.reasonerTac(new Conj(), new Conj.Input(null));
	}

	public static boolean conjI_applicable(Predicate goal) {
		return Lib.isConj(goal);
	}

	public static ITactic allI() {
		return reasonerTac(new AllI(), new AllI.Input());
	}

	/**
	 * Returns the tactic for {@link AllI} with an input.
	 *
	 * The input should be the user-provided, comma-separated list of identifiers to
	 * use to generate fresh names for the bound identifiers.
	 *
	 * @param input user input
	 * @return the tactic for the introduction of universal quantification
	 * @since 3.7
	 */
	public static ITactic allI(String input) {
		return (pt, pm) -> reasonerTac(new AllI(), new AllI.Input(input, pt.getFormulaFactory())).apply(pt, pm);
	}

	public static boolean allI_applicable(Predicate goal) {
		return Lib.isUnivQuant(goal);
	}

	public static ITactic exI(final String... witnesses) {
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(pt
						.getSequent().goal());
				return (BasicTactics.reasonerTac(new ExI(),
						new MultipleExprInput(witnesses, boundIdentDecls,
								typeEnv))).apply(pt, pm);
			}

		};
	}

	public static boolean exI_applicable(Predicate goal) {
		return Lib.isExQuant(goal);
	}

	// Tactics applicable on a hypothesis

	public static ITactic allD(final Predicate univHyp,
			final String... instantiations) {
		final Predicate pred = univHyp;
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				final AllD.Input input = new AllD.Input(pred, typeEnv, instantiations);
				return (BasicTactics.reasonerTac(new AllD(), input)).apply(pt,
						pm);
			}
		};
	}
	
	/**
	 * Tactic for instantiating a univaersally quantified implicative hypothesis and performing a modus ponens
	 * on it in one step. 
	 * 
	 * @param univHyp
	 * @param instantiations
	 * @return
	 * 		the tactic
	 */
	public static ITactic allmpD(final Predicate univHyp,
			final String... instantiations) {
		final Predicate pred = univHyp;
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				final AllD.Input input = new AllD.Input(pred, typeEnv, instantiations);
				return (BasicTactics.reasonerTac(new AllmpD(), input)).apply(pt,
						pm);
			}
		};
	}
	
	/**
	 * Tactic for instantiating a universally quantified implicative hypothesis
	 * and performing a modus tollens on it in one step.
	 * 
	 * @param univHyp
	 * @param instantiations
	 * @return the tactic
	 * @since 2.2
	 */
	public static ITactic allmtD(final Predicate univHyp,
			final String... instantiations) {
		final Predicate pred = univHyp;
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				final ITypeEnvironment typeEnv = pt.getSequent()
						.typeEnvironment();
				final AllD.Input input = new AllD.Input(pred, typeEnv,
						instantiations);
				return (BasicTactics.reasonerTac(new AllmtD(), input)).apply(
						pt, pm);
			}
		};
	}

	public static boolean allD_applicable(Predicate hyp) {
		return Lib.isUnivQuant(hyp);
	}

	/**
	 * Tells if it is possible to instantiate the given hypothesis and further
	 * apply modus ponens, or modus tollens on it in one step.
	 * 
	 * @param hyp
	 *            the hypothesis on which we want to apply the tactic
	 * @return <code>true</code> if the tactics allmpD and allmtD are applicable
	 *         on the selected hypothesis
	 * FIXME change the name of this method as it is now also used for the
	 *        modus tollens
	 */
	public static boolean allmpD_applicable(Predicate hyp) {
		if (Lib.isUnivQuant(hyp)) {
			QuantifiedPredicate forall = (QuantifiedPredicate) hyp;
			if (Lib.isImp(forall.getPredicate()))
				return true;
		}
		return false;
	}

	public static ITactic conjF(Predicate conjHyp) {
		return BasicTactics.reasonerTac(new ConjF(), new ConjF.Input(conjHyp));
	}

	public static boolean conjF_applicable(Predicate hyp) {
		return Lib.isConj(hyp);
	}
	
	
	public static ITactic impE(Predicate impHyp) {
		return BasicTactics.reasonerTac(new ImpE(), new ImpE.Input(impHyp));
	}

	public static boolean impE_applicable(Predicate hyp) {
		return Lib.isImp(hyp);
	}
	
	/**
	 * @since 2.2
	 */
	public static ITactic impCase(Predicate impHyp) {
		return BasicTactics.reasonerTac(new ImpCase(),
										new ImpCase.Input(impHyp));
	}

	/**
	 * @since 2.2
	 */
	public static boolean impCase_applicable(Predicate hyp) {
		return Lib.isImp(hyp);
	}

	public static ITactic disjE(Predicate disjHyp) {
		return BasicTactics.reasonerTac(new DisjE(), new DisjE.Input(disjHyp));
	}

	public static boolean disjE_applicable(Predicate hyp) {
		if (Lib.isDisj(hyp)) {
			return true;
		}
		if (Lib.isInclusion(hyp)) {
			Expression right = ((RelationalPredicate) hyp).getRight();
			return Lib.isUnion(right) || (Lib.isSetExtension(right) && !Lib.isSingletonSet(right));
		}
		return false;
	}

	public static ITactic eqE(Predicate eqHyp) {
		return BasicTactics.reasonerTac(new EqL2(), new HypothesisReasoner.Input(
				eqHyp));
	}

	public static boolean eqE_applicable(Predicate hyp) {
		return Lib.isEq(hyp);
	}

	/**
	 * @since 3.1
	 */
	public static ITactic eqvRL(Predicate eqHyp) {
		return BasicTactics.reasonerTac(new EqvRL(),
				new HypothesisReasoner.Input(eqHyp));
	}

	/**
	 * @since 3.1
	 */
	public static ITactic eqvLR(Predicate eqHyp) {
		return BasicTactics.reasonerTac(new EqvLR(),
				new HypothesisReasoner.Input(eqHyp));
	}

	/**
	 * @since 3.1
	 */
	public static boolean eqv_applicable(Predicate hyp) {
		return Lib.isEqv(hyp);
	}

	/**
	 * @since 2.3
	 */
	public static ITactic localEqRewrite(Predicate predicate,
			IPosition position, Predicate equality) {
		return BasicTactics.reasonerTac(new LocalEqRewrite(),
				new LocalEqRewrite.Input(predicate, position, equality));
	}

	public static boolean exF_applicable(Predicate hyp) {
		return Lib.isExQuant(hyp);
	}
	
	public static ITactic exF(Predicate exHyp) {
		return BasicTactics.reasonerTac(new ExF(), new ExF.Input(exHyp));
	}

	/**
	 * Returns the "free existential" tactic for a given hypothesis and user input.
	 *
	 * The input should be the user-provided, comma-separated list of identifiers to
	 * use to generate fresh names for bound identifiers that will be freed.
	 *
	 * @param exHyp the hypothesis on which the tactic will apply
	 * @param input user input
	 * @return the tactic "free existential"
	 * @since 3.7
	 */
	public static ITactic exF(Predicate exHyp, String input) {
		return (pt, pm) -> reasonerTac(new ExF(), new ExF.Input(exHyp, input, pt.getFormulaFactory())).apply(pt, pm);
	}

	/**
	 * @since 2.0
	 */
	public static ITactic removeNeg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveNegation(),
				new RemoveNegation.Input(hyp, position));
	}

	public static ITactic falsifyHyp(Predicate hyp) {
		return BasicTactics.reasonerTac(new ContrL1(), new ContrL1.Input(hyp));
	}

	public static boolean falsifyHyp_applicable(Predicate hyp,
			IProverSequent seq) {
		return (!seq.goal().equals(DLib.makeNeg(hyp)));
	}
	
	/**
	 * @since 1.2
	 */
	public static ITactic contrHyps(Predicate hyp) {
		return BasicTactics.reasonerTac(new ContrHyps(), new HypothesisReasoner.Input(hyp));
	}


	// Misc tactics

	public static ITactic hyp() {
		return BasicTactics.reasonerTac(new Hyp(), EMPTY_INPUT);
	}

	public static ITactic prune() {
		return BasicTactics.prune();
	}

	public static ITactic mngHyp(ISelectionHypAction hypAction) {
		return BasicTactics.reasonerTac(new MngHyp(), new MngHyp.Input(hypAction));
	}

	public static ITactic afterLasoo(final ITactic tactic) {
		return new ITactic() {

			@Override
			public Object apply(IProofTreeNode pt, IProofMonitor pm) {

				lasoo().apply(pt, pm);
				final IProofTreeNode firstOpenDescendant = pt
						.getFirstOpenDescendant();
				Object output = tactic.apply(firstOpenDescendant, pm);
				if (output == null) {
					// tactic was successful
					return null;
				} else { // revert proof tree
					prune().apply(pt, pm);
					return output;
				}

			}

		};
	}

	public static ITactic doubleImpHyp(Predicate pred, IPosition position) {
		return BasicTactics.reasonerTac(new DoubleImplHypRewrites(),
				new DoubleImplHypRewrites.Input(pred, position));
	}

	public static List<IPosition> doubleImpHypGetPositions(Predicate hyp) {
		return hyp.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryPredicate predicate) {
				return isDoubleImplPredicate(predicate);
			}
		});
	}

	public static boolean isDoubleImplPredicate(Predicate predicate) {
		if (Lib.isImp(predicate)) {
			BinaryPredicate bPred = (BinaryPredicate) predicate;
			if (Lib.isImp(bPred.getRight())) {
				return true;
			}

		}
		return false;
	}


	/**
	 * Returns a tactic to rewrite an implicative sub-predicate, occurring in an
	 * hypothesis, to its contrapositive.
	 * 
	 * @param hyp
	 *            an hypothesis predicate that contains the sub-predicate to
	 *            rewrite
	 * @param position
	 *            position of the sub-predicate to rewrite
	 * @return a tactic to rewrite an implicative sub-predicate to its
	 *         contrapositive
	 */
	public static ITactic contImpHyp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ContImplHypRewrites(),
				new ContImplHypRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner
	 * {@link ContImplHypRewrites} to a predicate.
	 *
	 * @param hyp a predicate
	 * @return a list of applicable positions
	 * @since 3.6
	 */
	public static List<IPosition> contImpHypGetPositions(Predicate hyp) {
		return hyp.getPositions(new DefaultFilter() {
			@Override
			public boolean select(BinaryPredicate predicate) {
				return predicate.getTag() == LIMP;
			}
		});
	}

	public static boolean isFunOvrApp(Formula<?> subFormula) {
		if (Lib.isFunApp(subFormula)) {
			Expression left = ((BinaryExpression) subFormula).getLeft();
			if (Lib.isOvr(left)) {
				return true;
			}
		}
		return false;
	}

	public static List<IPosition> funOvrGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				return isFunOvrApp(expression);
			}
		});
		Lib.removeWDUnstrictPositions(positions, predicate);
		return positions;
	}
	
	/**
	 * @since 2.0
	 */
	public static ITactic funOvr(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunOvr(), new FunOvr.Input(hyp,
				position));
	}

	public static ITactic he(Predicate hyp) {
		return BasicTactics.reasonerTac(new HeL2(), new HypothesisReasoner.Input(
				hyp));
	}

	// Only used for backwards compatibility in internal methods needing He level 0
	private static ITactic he(Predicate hyp, EqHe.Level level) {
		return BasicTactics.reasonerTac(new He(level), new HypothesisReasoner.Input(
				hyp));
	}

	public static ITactic modusTollens(Predicate impHyp) {
		return BasicTactics.reasonerTac(new ModusTollens(),
				new ModusTollens.Input(impHyp));
	}

	/**
	 * Tells if the tactic RemoveNegation can apply to the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return <code>true</code> if the tactic is applicable,
	 *         <code>false</code> otherwise
	 * @since 3.0
	 */
	public static boolean isRemoveNegationApplicable(Predicate pred) {
		return new RemoveNegationRewriterImpl(false)
				.isApplicableOrRewrite(pred);
	}

	/**
	 * @since 3.0
	 */
	public static List<IPosition> rnGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {
			@Override
			public boolean select(UnaryPredicate predicate) {
				if (predicate.getTag() == NOT) {
					Predicate child = predicate.getChild();
					switch (child.getTag()) {
					case EQUAL:
						RelationalPredicate rPred = (RelationalPredicate) child;
						return rPred.getLeft().getTag() == EMPTYSET || rPred.getRight().getTag() == EMPTYSET;
					case LAND:
					case LOR:
					case BTRUE:
					case BFALSE:
					case NOT:
					case LIMP:
					case EXISTS:
					case FORALL:
						return true;
					}
				}
				return false;
			}

		});
	}

	/**
	 * @param pred
	 *            the predicate from which we retrieve the positions where the
	 *            tactic "Remove Membership" can apply
	 * @return the positions where the tactic "Remove Membership" can apply on
	 *         the given predicate
	 * @since 3.0
	 */
	public static List<IPosition> rmGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {
			@Override
			public boolean select(RelationalPredicate predicate) {
				return isRemoveMembershipApplicable(predicate);
			}
		});
	}
	
	/**
	 * Tells if the tactic RemoveMembership can apply to the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return <code>true</code> if the tactic is applicable,
	 *         <code>false</code> otherwise
	 * @since 3.0
	 */
	public static boolean isRemoveMembershipApplicable(Predicate pred) {
		return RemoveMembership.isApplicableTo(pred);
	}

	public static ITactic removeMembership(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(RemoveMembership.DEFAULT,
				new RemoveMembership.Input(hyp, position));
	}

	public static List<IPosition> riGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate predicate) {
				return predicate.getTag() == SUBSETEQ;
			}

		});
	}

	public static ITactic removeInclusion(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveInclusion(),
				new RemoveInclusion.Input(hyp, position));
	}
	
	public static List<IPosition> sirGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate predicate) {
				return predicate.getTag() == SUBSET;
			}

		});
	}

	public static ITactic removeStrictInclusion(Predicate hyp,
			IPosition position) {
		return BasicTactics.reasonerTac(new StrictInclusionRewrites(),
				new StrictInclusionRewrites.Input(hyp, position));
	}
	
	public static List<IPosition> disjToImplGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativePredicate predicate) {
				return predicate.getTag() == LOR;
			}

		});

	}

	public static ITactic disjToImpl(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new DisjunctionToImplicationRewrites(),
				new DisjunctionToImplicationRewrites.Input(hyp, position));
	}

	/**
	 * Return the tactic "implication with conjunction rewrites"
	 * {@link ImpAndRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "implication with conjunction rewrites"
	 * @author htson
	 */
	public static ITactic impAndRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ImpAndRewrites(),
				new ImpAndRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "implication with
	 * conjunction rewrites" {@link ImpAndRewrites} to a predicate.
	 * <p>
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> impAndGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryPredicate predicate) {
				return predicate.getTag() == LIMP && Lib.isConj(predicate.getRight());
			}

		});
	}


	/**
	 * Return the tactic "implication with disjunction rewrites"
	 * {@link ImpOrRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "implication with disjunction rewrites"
	 * @author htson
	 */
	public static ITactic impOrRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ImpOrRewrites(),
				new ImpOrRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "implication with
	 * disjunction rewrites" {@link ImpOrRewrites} to a predicate.
	 * <p>
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> impOrGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryPredicate predicate) {
				return predicate.getTag() == LIMP && Lib.isDisj(predicate.getLeft());
			}

		});
	}

	/**
	 * Return the list of applicable positions of the tactic "relational image
	 * apply to union rewrites" {@link RelImgUnionRightRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> relImgUnionRightGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				return expression.getTag() == RELIMAGE && expression.getRight().getTag() == BUNION;
			}

		});
	}


	/**
	 * Return the tactic "relational image apply to union rewrites"
	 * {@link RelImgUnionRightRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "relational image apply to union rewrites"
	 * @author htson
	 */
	public static ITactic relImgUnionRightRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RelImgUnionRightRewrites(),
				new RelImgUnionRightRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "set equality
	 * rewrites" {@link SetEqlRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> setEqlGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate pred) {
				if (pred.getTag() == Predicate.EQUAL) {
					Expression left = pred.getLeft();
					Type type = left.getType();
					return type instanceof PowerSetType;
				}
				return super.select(pred);
			}

		});
	}

	/**
	 * Return the tactic "set equality rewrites" {@link SetEqlRewrites} which is
	 * applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "set equality rewrites"
	 * @author htson
	 */
	public static ITactic setEqlRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new SetEqlRewrites(),
				new SetEqlRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "equivalence
	 * rewrites" {@link EqvRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> eqvGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryPredicate pred) {
				return pred.getTag() == LEQV;
			}

		});
	}


	/**
	 * Return the tactic "equivalence rewrites" {@link EqvRewrites} which is
	 * applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "equivalence rewrites"
	 * @author htson
	 */
	public static ITactic eqvRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new EqvRewrites(),
				new EqvRewrites.Input(hyp, position));
	}


	/**
	 * Utility method to check if the tactic "function apply to intersection
	 * image" {@link FunInterImg} is applicable for the formula.
	 * <p>
	 * 
	 * @param formula
	 *            a formula
	 * @return <code>true</code> if the tactic is applicable and
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isFunInterImgApp(Formula<?> formula) {
		return new FunInterImg().isApplicable(formula);
	}


	/**
	 * Return the tactic "function apply to intersection image"
	 * {@link FunInterImg} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "function apply to intersection image"
	 * @author htson
	 */
	public static ITactic funInterImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunInterImg(),
				new FunInterImg.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "function apply to
	 * intersection image" {@link FunInterImg} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> funInterImgGetPositions(Predicate predicate) {
		return new FunInterImg().getPositions(predicate, true);
	}


	/**
	 * Utility method to check if the tactic "function apply to set minus image"
	 * {@link FunSetMinusImg} is applicable for the formula.
	 * <p>
	 * 
	 * @param formula
	 *            a formula
	 * @return <code>true</code> if the tactic is applicable and
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isFunSetMinusImgApp(Formula<?> formula) {
		return new FunSetMinusImg().isApplicable(formula);
	}


	/**
	 * Return the list of applicable positions of the tactic "function apply to
	 * set minus image" {@link FunSetMinusImg} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> funSetMinusImgGetPositions(Predicate predicate) {
		return new FunSetMinusImg().getPositions(predicate, true);
	}


	/**
	 * Return the tactic "function apply to set minus image"
	 * {@link FunSetMinusImg} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "function apply to set minus image"
	 * @author htson
	 */
	public static ITactic funSetMinusImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunSetMinusImg(),
				new FunSetMinusImg.Input(hyp, position));
	}

	/**
	 * Return the list of applicable positions of the tactic "function apply to
	 * singleton set image" {@link FunSetMinusImg} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> funSingletonImgGetPositions(
			Predicate predicate) {
		return new FunSingletonImg().getPositions(predicate, false);
	}

	/**
	 * Return the tactic "function apply to singleton set image"
	 * {@link FunSingletonImg} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "function apply to singleton set image"
	 * @author htson
	 */
	public static ITactic funSingletonImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunSingletonImg(),
				new FunSingletonImg.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "converse relation
	 * rewrites" {@link ConvRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> convGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(UnaryExpression expression) {
				if (expression.getTag() == CONVERSE) {
					switch (expression.getChild().getTag()) {
					case BUNION:
					case BINTER:
					case FCOMP:
					case DOMRES:
					case DOMSUB:
					case RANRES:
					case RANSUB:
						return true;
					}
				}
				return false;
			}

		});
	}


	/**
	 * Return the tactic "converse relation rewrites" {@link ConvRewrites} which
	 * is applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "converse relation rewrites"
	 * @author htson
	 */
	public static ITactic convRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ConvRewrites(),
				new ConvRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "domain
	 * distribution left rewrites" {@link DomDistLeftRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> domDistLeftGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (expression.getTag() == DOMRES || expression.getTag() == DOMSUB) {
					Expression left = expression.getLeft();
					return left.getTag() == BUNION || left.getTag() == BINTER;
				}
				return false;
			}

		});
	}


	/**
	 * Return the tactic "domain distribution left rewrites"
	 * {@link DomDistLeftRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "domain distribution left rewrites"
	 * @author htson
	 */
	public static ITactic domDistLeftRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new DomDistLeftRewrites(),
				new DomDistLeftRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "domain
	 * distribution right rewrites" {@link DomDistRightRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> domDistRightGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (expression.getTag() == DOMRES || expression.getTag() == DOMSUB) {
					Expression right = expression.getRight();
					return right.getTag() == BUNION || right.getTag() == BINTER;
				}
				return false;
			}

		});
	}


	/**
	 * Return the tactic "domain distribution right rewrites"
	 * {@link DomDistRightRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "domain distribution right rewrites"
	 * @author htson
	 */
	public static ITactic domDistRightRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new DomDistRightRewrites(),
				new DomDistRightRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "range
	 * distribution right rewrites" {@link RanDistRightRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> ranDistRightGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (expression.getTag() == RANRES || expression.getTag() == RANSUB) {
					Expression right = expression.getRight();
					return right.getTag() == BUNION || right.getTag() == BINTER;
				}
				return false;
			}

		});
	}


	/**
	 * Return the tactic "range distribution right rewrites"
	 * {@link RanDistRightRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "range distribution right rewrites"
	 * @author htson
	 */
	public static ITactic ranDistRightRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RanDistRightRewrites(),
				new RanDistRightRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "range
	 * distribution left rewrites" {@link RanDistLeftRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> ranDistLeftGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (expression.getTag() == RANRES || expression.getTag() == RANSUB) {
					Expression left = expression.getLeft();
					return left.getTag() == BUNION || left.getTag() == BINTER;
				}
				return false;
			}

		});
	}


	/**
	 * Return the tactic "range distribution left rewrites"
	 * {@link RanDistLeftRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "range distribution left rewrites"
	 * @author htson
	 */
	public static ITactic ranDistLeftRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RanDistLeftRewrites(),
				new RanDistLeftRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "set minus
	 * rewrites" {@link SetMinusRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> setMinusGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				if (expression.getTag() == SETMINUS) {
					final Expression left = expression.getLeft();
					if (left.isATypeExpression()) {
						switch (expression.getRight().getTag()) {
						case BUNION:
						case BINTER:
						case SETMINUS:
							return true;
						}
					}
				}
				return false;
			}

		});
	}


	/**
	 * Return the tactic "set minus rewrites" {@link SetMinusRewrites} which is
	 * applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "set minus rewrites"
	 * @author htson
	 */
	public static ITactic setMinusRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new SetMinusRewrites(),
				new SetMinusRewrites.Input(hyp, position));
	}

	
	/**
	 * Return the list of applicable positions of the tactic "And/Or
	 * distribution rewrites" {@link AndOrDistRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> andOrDistGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativePredicate pred) {
				return pred.getTag() == LAND || pred.getTag() == LOR;
			}

		});
		
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			AssociativePredicate aPred = (AssociativePredicate) predicate.getSubFormula(position);
			int tag = aPred.getTag() == LAND ? LOR : LAND;
			IPosition child = position.getFirstChild();
			for (Predicate childPred : aPred.getChildren()) {
				if (childPred.getTag() == tag) {
					results.add(child);
				}
				child = child.getNextSibling();
			}
		}
		
		return results; 
	}


	/**
	 * Return the tactic "And/Or distribution rewrites"
	 * {@link AndOrDistRewrites} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "And/Or disttribution rewrites"
	 * @author htson
	 */
	public static ITactic andOrDistRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new AndOrDistRewrites(),
				new AndOrDistRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "Union/Intersection
	 * distribution rewrites" {@link UnionInterDistRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> unionInterDistGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativeExpression expression) {
				return expression.getTag() == BUNION || expression.getTag() == BINTER;
			}

		});
		
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			AssociativeExpression aExp = ((AssociativeExpression) predicate.getSubFormula(position));
			int tag = aExp.getTag() == BUNION ? BINTER : BUNION;
			IPosition child = position.getFirstChild();
			for (Expression childExpr : aExp.getChildren()) {
				if (childExpr.getTag() == tag) {
					results.add(child);
				}
				child = child.getNextSibling();
			}
		}
		
		return results; 
	}


	/**
	 * Return the tactic "Union/Intersection distribution rewrites"
	 * {@link UnionInterDistRewrites} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "Union/Intersection disttribution rewrites"
	 * @author htson
	 */
	public static ITactic unionInterDistRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new UnionInterDistRewrites(),
				new UnionInterDistRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "Composition/Union
	 * distribution rewrites" {@link CompUnionDistRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> compUnionDistGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativeExpression expression) {
				return expression.getTag() == FCOMP;
			}

		});
		
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			AssociativeExpression expr = (AssociativeExpression) predicate.getSubFormula(position);
			IPosition child = position.getFirstChild();
			for (Expression childExpr : expr.getChildren()) {
				if (childExpr.getTag() == BUNION) {
					results.add(child);
				}
				child = child.getNextSibling();
			}
		}
		
		return results; 
	}


	/**
	 * Return the tactic "Composition/Union distribution rewrites"
	 * {@link CompUnionDistRewrites} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "Composition/Union disttribution rewrites"
	 * @author htson
	 */
	public static ITactic compUnionDistRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new CompUnionDistRewrites(),
				new CompUnionDistRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "relational image
	 * of an union rewrites" {@link RelImgUnionLeftRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> relImgUnionLeftGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(BinaryExpression expression) {
				return expression.getTag() == RELIMAGE && expression.getLeft().getTag() == BUNION;
			}

		});
	}


	/**
	 * Return the tactic "relational image of an union rewrites"
	 * {@link RelImgUnionLeftRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "relational image of an union rewrites"
	 * @author htson
	 */
	public static ITactic relImgUnionLeftRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RelImgUnionLeftRewrites(),
				new RelImgUnionLeftRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "domain or range /
	 * union distribution rewrites" {@link DomRanUnionDistRewrites} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> domRanUnionDistGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(UnaryExpression expression) {
				return (expression.getTag() == KDOM || expression.getTag() == KRAN)
						&& expression.getChild().getTag() == BUNION;
			}

		});
	}


	/**
	 * Return the tactic "domain or range with union distribution rewrites"
	 * {@link DomRanUnionDistRewrites} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "domain or range with union distribution rewrites"
	 * @author htson
	 */
	public static ITactic domRanUnionDistRewrites(Predicate hyp,
			IPosition position) {
		return BasicTactics.reasonerTac(new DomRanUnionDistRewrites(),
				new DomRanUnionDistRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "remove inclusion
	 * (universal) rewrites" {@link RemoveInclusionUniversal} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> riUniversalGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate pred) {
				return pred.getTag() == SUBSETEQ;
			}

		});
	}


	/**
	 * Return the tactic "remove inclusion (universal) rewrites"
	 * {@link RemoveInclusionUniversal} which is applicable to a
	 * hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "remove inclusion (universal) rewrites"
	 * @author htson
	 */
	public static ITactic removeInclusionUniversal(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveInclusionUniversal(),
				new RemoveInclusionUniversal.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "relation
	 * overriding rewrites" {@link RelOvrRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> relOvrGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativeExpression expression) {
				return expression.getTag() == OVR;
			}

		});
		
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			IPosition child = position.getFirstChild();
			Formula<?> subFormula = predicate.getSubFormula(child);
			while (subFormula != null) {
				if (!child.isFirstChild()) {
					results.add(child);
				}
				child = child.getNextSibling();
				subFormula = predicate.getSubFormula(child);
			}
		}
		
		return results; 
	}


	/**
	 * Return the tactic "relation overriding rewrites" {@link RelOvrRewrites}
	 * which is applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "relation overriding rewrites"
	 * @author htson
	 */
	public static ITactic relOvr(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RelOvrRewrites(),
				new RelOvrRewrites.Input(hyp, position));
	}


	/**
	 * Returns the list of applicable positions of the tactic "composition image
	 * rewrites" {@link CompImgRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> compImgGetPositions(Predicate predicate) {
		return CompImgRewrites.getPositions(predicate);
	}

	/**
	 * Return the tactic "composition image rewrites" {@link CompImgRewrites}
	 * which is applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "composition image rewrites"
	 * @author htson
	 */
	public static ITactic compImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new CompImgRewrites(),
				new CompImgRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "domain
	 * manipulation with composition rewrites" {@link DomCompRewrites} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> domCompGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativeExpression expression) {
				return expression.getTag() == FCOMP;
			}

		});
		
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			var fcompExpr = (AssociativeExpression) predicate.getSubFormula(position);
			Expression[] children = fcompExpr.getChildren();
			for (int i = 0; i < children.length - 1; i++) {
				if (children[i].getTag() == DOMRES || children[i].getTag() == DOMSUB) {
					results.add(position.getChildAtIndex(i));
				}
			}
		}
		
		return results; 
	}

	
	/**
	 * Return the tactic "domain manipulation with composition rewrites"
	 * {@link DomCompRewrites} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "domain manipulation with composition rewrites"
	 * @author htson
	 */
	public static ITactic domComp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new DomCompRewrites(),
				new DomCompRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "range
	 * manipulation with composition rewrites" {@link DomCompRewrites} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> ranCompGetPositions(Predicate predicate) {
		List<IPosition> positions = predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(AssociativeExpression expression) {
				return expression.getTag() == FCOMP;
			}

		});
		
		List<IPosition> results = new ArrayList<IPosition>();
		for (IPosition position : positions) {
			var fcompExpr = (AssociativeExpression) predicate.getSubFormula(position);
			Expression[] children = fcompExpr.getChildren();
			for (int i = 1; i < children.length; i++) {
				if (children[i].getTag() == RANRES || children[i].getTag() == RANSUB) {
					results.add(position.getChildAtIndex(i));
				}
			}
		}
		
		return results; 
	}


	/**
	 * Return the tactic "range manipulation with composition rewrites"
	 * {@link RanCompRewrites} which is applicable to a hypothesis at a given
	 * position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "range manipulation with composition rewrites"
	 * @author htson
	 */
	public static ITactic ranComp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RanCompRewrites(),
				new RanCompRewrites.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "function
	 * composition image" {@link FunCompImg} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> funCompImgGetPositions(Predicate predicate) {
		return new FunCompImg().getPositions(predicate, false);
	}
	
	/**
	 * Tells if the the tactic "function composition image" {@link FunCompImg}
	 * is applicable to the given position.
	 * <p>
	 * 
	 * @param expression
	 *            an expression
	 * @return <code>true</code> if the tactic "function composition image" is
	 *         applicable to the given expression, <code>false</code> otherwise
	 * @since 2.0
	 */
	public static boolean isFunCompImgApplicable(Expression expression) {
		return new FunCompImg().isApplicable(expression);
	}

	
	/**
	 * Return the tactic "function composition image" {@link FunCompImg} which
	 * is applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "function composition image"
	 * @author htson
	 */
	public static ITactic funCompImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunCompImg(),
				new FunCompImg.Input(hyp, position));
	}

	/**
	 * The class for "Failure tactic" that always fails.
	 * <p>
	 * 
	 * @author htson
	 */
	public static class FailureTactic implements ITactic {

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return "Not applicable";
		}

	}

	/**
	 * Return the list of applicable positions of the tactic "finite Set"
	 * {@link FiniteSet} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteSetGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate))
			return POSITION_ROOT;
		else
			return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite Set" {@link FiniteSet} which has the input
	 * expression.
	 * <p>
	 * 
	 * @param sequent
	 *            the current prover sequent
	 * @param expressionImage
	 *            the global input from the Proof Control View
	 * @return The tactic "finite set"
	 * @author htson
	 */
	public static ITactic finiteSet(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics.reasonerTac(new FiniteSet(), new SingleExprInput(
				expressionImage, sequent.typeEnvironment()));
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * intersection" {@link FiniteInter} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteInterGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			switch (((SimplePredicate) predicate).getExpression().getTag()) {
			case BINTER:
			case KINTER:
			case QINTER:
				return POSITION_ROOT;
			}
		}
		return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite of Intersection" {@link FiniteInter}.
	 * <p>
	 * 
	 * @return The tactic "finite of intersection"
	 * @author htson
	 */
	public static ITactic finiteInter() {
		return BasicTactics.reasonerTac(new FiniteInter(), EMPTY_INPUT);
	}

	/**
	 * Return the list of applicable positions of the tactic "finite of union" to a
	 * predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @see FiniteUnion
	 * @since 3.6
	 */
	public static List<IPosition> finiteUnionGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			switch (((SimplePredicate) predicate).getExpression().getTag()) {
			case BUNION:
			case KUNION:
			case QUNION:
				return POSITION_ROOT;
			}
		}
		return NO_POSITIONS;
	}

	/**
	 * Return the tactic "Finite of union".
	 *
	 * @return The tactic "finite of union"
	 * @see FiniteUnion
	 * @since 3.6
	 */
	public static ITactic finiteUnion() {
		return BasicTactics.reasonerTac(new FiniteUnion(), EMPTY_INPUT);
	}

	/**
	 * Return the list of applicable positions of the tactic "finite of set
	 * minus" {@link FiniteSetMinus} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteSetMinusGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isSetMinus(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite of set minus" {@link FiniteSetMinus}.
	 * <p>
	 * 
	 * @return The tactic "finite of set minus"
	 * @author htson
	 */
	public static ITactic finiteSetMinus() {
		return BasicTactics.reasonerTac(new FiniteSetMinus(), EMPTY_INPUT);
	}

	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * relation" {@link FiniteRelation} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteRelationGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRelation(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}



	/**
	 * Return the tactic "Finite of relation" {@link FiniteRelation} which has
	 * the input expression.
	 * <p>
	 * 
	 * @param sequent
	 *            the current prover sequent
	 * @param expressionImage
	 *            the global input from the Proof Control View
	 * @return The tactic "finite of relation"
	 * @author htson
	 */
	public static ITactic finiteRelation(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics
				.reasonerTac(new FiniteRelation(), new SingleExprInput(
						expressionImage, sequent.typeEnvironment()));
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * relational image" {@link FiniteRelImg} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteRelImgGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRelImg(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite of relational image" {@link FiniteRelImg}.
	 * <p>
	 * 
	 * @return The tactic "finite of relational image"
	 * @author htson
	 */
	public static ITactic finiteRelImg() {
		return BasicTactics.reasonerTac(new FiniteRelImg(), EMPTY_INPUT);
	}


	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * range of a relation" {@link FiniteRan} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteRanGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRan(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	
	/**
	 * Return the tactic "Finite of range of a relation" {@link FiniteRan}.
	 * <p>
	 * 
	 * @return The tactic "finite of range of a relation"
	 * @author htson
	 */
	public static ITactic finiteRan() {
		return BasicTactics.reasonerTac(new FiniteRan(), EMPTY_INPUT);
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * domain of a relation" {@link FiniteRan} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteDomGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isDom(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite of domain of a relation" {@link FiniteDom}.
	 * <p>
	 * 
	 * @return The tactic "finite of domain of a relation"
	 * @author htson
	 */
	public static ITactic finiteDom() {
		return BasicTactics.reasonerTac(new FiniteDom(), EMPTY_INPUT);
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * function" {@link FiniteFunction} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteFunctionGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRelation(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite of function" {@link FiniteFunction} which has
	 * the input expression.
	 * <p>
	 * 
	 * @param sequent
	 *            the current prover sequent
	 * @param expressionImage
	 *            the global input from the Proof Control View
	 * @return The tactic "finite of function"
	 * @author htson
	 */
	public static ITactic finiteFunction(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics
				.reasonerTac(new FiniteFunction(), new PFunSetInput(
						expressionImage, sequent.typeEnvironment()));
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * function converse" {@link FiniteFunConv} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteFunConvGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRelation(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	
	/**
	 * Return the tactic "Finite of function converse" {@link FiniteFunConv}
	 * which has the input expression.
	 * <p>
	 * 
	 * @param sequent
	 *            the current prover sequent
	 * @param expressionImage
	 *            the global input from the Proof Control View
	 * @return The tactic "finite of function converse"
	 * @author htson
	 */
	public static ITactic finiteFunConv(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics
				.reasonerTac(new FiniteFunConv(), new PFunSetInput(
						expressionImage, sequent.typeEnvironment()));
	}


	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * relational image of a function " {@link FiniteFunRelImg} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteFunRelImgGetPositions(
			Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRelImg(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	/**
	 * Return the tactic "Finite of relational image of a function "
	 * {@link FiniteFunRelImg}.
	 * <p>
	 * 
	 * @return The tactic "finite of relational image of a function"
	 * @author htson
	 * @since 1.2
	 */
	public static ITactic finiteFunRelImg(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics
				.reasonerTac(new FiniteFunRelImg(), new PFunSetInput(
						expressionImage, sequent.typeEnvironment()));
	}


	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * range of a function" {@link FiniteFunRan} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteFunRanGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isRan(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	/**
	 * Return the tactic "Finite of range of a function" {@link FiniteFunRan}
	 * which has the input expression.
	 * <p>
	 * 
	 * @param sequent
	 *            the current prover sequent
	 * @param expressionImage
	 *            the global input from the Proof Control View
	 * @return The tactic "finite of range of a function"
	 * @author htson
	 */
	public static ITactic finiteFunRan(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics
			.reasonerTac(new FiniteFunRan(), new PFunSetInput(
				expressionImage, sequent.typeEnvironment()));
	}


	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * domain of a function" {@link FiniteFunDom} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteFunDomGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isDom(((SimplePredicate) predicate).getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	
	/**
	 * Return the tactic "Finite of domain of a function" {@link FiniteFunDom}
	 * which has the input expression.
	 * <p>
	 * 
	 * @param sequent
	 *            the current prover sequent
	 * @param expressionImage
	 *            the global input from the Proof Control View
	 * @return The tactic "finite of domain of a function"
	 * @author htson
	 */
	public static ITactic finiteFunDom(IProverSequent sequent,
			String expressionImage) {
		return BasicTactics
			.reasonerTac(new FiniteFunDom(), new PFunSetInput(
				expressionImage, sequent.typeEnvironment()));
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite minimum"
	 * {@link FiniteMin} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteMinGetPositions(Predicate predicate) {
		if (new FiniteMin().isApplicable(predicate)) {
			return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	/**
	 * Tells if the tactic "finite minimum" is applicable {@link FiniteMin} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return <code>true</code> if the tactic "finite minimum" is applicable
	 * @since 2.0
	 */
	public static boolean finiteMinIsApplicable(Predicate predicate) {
		return new FiniteMin().isApplicable(predicate);
	}
	
	/**
	 * Return the tactic "Finite minimum" {@link FiniteMin}.
	 * <p>
	 * 
	 * @return The tactic "finite minimum"
	 * @author htson
	 */
	public static ITactic finiteMin() {
		return BasicTactics.reasonerTac(new FiniteMin(), EMPTY_INPUT);
	}

	
	/**
	 * Return the list of applicable positions of the tactic "finite maximum"
	 * {@link FiniteMax} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteMaxGetPositions(Predicate predicate) {
		if (new FiniteMax().isApplicable(predicate)) {
			return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}
	
	/**
	 * Tells if the tactic "finite maximum" is applicable {@link FiniteMax} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return <code>true</code> if the tactic "finite maximum" is applicable
	 * @since 2.0
	 */
	public static boolean finiteMaxIsApplicable(Predicate predicate) {
		return new FiniteMax().isApplicable(predicate);
	}


	/**
	 * Return the tactic "Finite maximum" {@link FiniteMax}.
	 * <p>
	 * 
	 * @return The tactic "finite maximum"
	 * @author htson
	 */
	public static ITactic finiteMax() {
		return BasicTactics.reasonerTac(new FiniteMax(), EMPTY_INPUT);
	}


	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * set of non-positive numbers" {@link FiniteNegative} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finiteNegativeGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isSetOfIntegers(((SimplePredicate) predicate)
					.getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}

	
	/**
	 * Return the tactic "Finite of set of non-positive numbers"
	 * {@link FiniteNegative}.
	 * <p>
	 * 
	 * @return The tactic "finite of set of non-positive numbers"
	 * @author htson
	 */
	public static ITactic finiteNegative() {
		return BasicTactics.reasonerTac(new FiniteNegative(), EMPTY_INPUT);
	}


	/**
	 * Return the list of applicable positions of the tactic "finite of
	 * set of non-negative numbers" {@link FinitePositive} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> finitePositiveGetPositions(Predicate predicate) {
		if (Lib.isFinite(predicate)) {
			if (Lib.isSetOfIntegers(((SimplePredicate) predicate)
					.getExpression()))
				return POSITION_ROOT;
		}
		return NO_POSITIONS;
	}


	/**
	 * Return the tactic "Finite of set of non-negative numbers"
	 * {@link FinitePositive}.
	 * <p>
	 * 
	 * @return The tactic "finite of set of non-negative numbers"
	 * @author htson
	 */
	public static ITactic finitePositive() {
		return BasicTactics.reasonerTac(new FinitePositive(), EMPTY_INPUT);
	}

	/**
	 * Returns the list of applicable positions of the tactic "finite of set
	 * comprehension" to a goal predicate.
	 *
	 * @param predicate predicate
	 * @return a list of applicable positions
	 * @see FiniteCompset
	 * @since 3.7
	 */
	public static List<IPosition> finiteCompsetGetPositions(Predicate predicate) {
		return FiniteCompset.isApplicable(predicate) ? POSITION_ROOT : NO_POSITIONS;
	}

	/**
	 * Returns the tactic "Finite of Set Comprehension" {@link FiniteCompset}.
	 *
	 * @return The tactic "finite of set comprehension"
	 * @since 3.7
	 */
	public static ITactic finiteCompset() {
		return BasicTactics.reasonerTac(new FiniteCompset(), EMPTY_INPUT);
	}

	/**
	 * Return the list of applicable positions of the tactic "cardinality
	 * arithmetic comparison" {@link CardComparison} to a goal predicate.
	 * <p>
	 * 
	 * @param goal
	 *            a goal predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> cardComparisonGetPositions(Predicate goal) {
		return CardComparison.getRootPositions(goal);
	}
	
	/**
	 * Tells if the tactic "cardinality arithmetic comparison" is applicable
	 * {@link CardComparison} to a predicate.
	 * <p>
	 * 
	 * @param goal
	 *            a goal predicate
	 * @return <code>true</code> if the tactic "cardinality arithmetic
	 *         comparison" is applicable
	 * @since 2.0
	 */
	public static boolean isCardComparisonApplicable(Predicate goal) {
		return CardComparison.isApplicable(goal);
	}

	/**
	 * Return the tactic "arithmetic comparison of cardinality rewrites"
	 * {@link CardComparison} which is applicable to a hypothesis at a
	 * given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "arithmetic comparison of cardinality rewrites"
	 * @author htson
	 * @deprecated this tactic is only applicable to the ROOT position on the goal:
	 *             the values of {@code hyp} and {@code position} are ignored; use
	 *             {@link #cardComparison()}
	 */
	@Deprecated
	public static ITactic cardComparison(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new CardComparison(), EMPTY_INPUT);
	}

	/**
	 * Returns the tactic "arithmetic comparison of cardinality rewrites"
	 * {@link CardComparison} which is applicable to the goal at the root position.
	 *
	 * @return The tactic "arithmetic comparison of cardinality rewrites"
	 * @since 3.8
	 */
	public static ITactic cardComparison() {
		return BasicTactics.reasonerTac(new CardComparison(), EMPTY_INPUT);
	}

	/**
	 * Return the list of applicable positions of the tactic "cardinality of
	 * range of numbers" {@link CardUpTo} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> cardUpToGetPositions(Predicate predicate) {
		return new CardUpTo().getPositions(predicate, true);
	}

	
	/**
	 * Return the tactic "cardinality of range of numbers" {@link CardUpTo}
	 * which is applicable to a hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "cardinality of range of numbers"
	 * @author htson
	 */
	public static ITactic cardUpToRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new CardUpTo(),
				new CardUpTo.Input(hyp, position));
	}


	/**
	 * Return the list of applicable positions of the tactic "rewrite inclusion
	 * with set minus on the left" {@link InclusionSetMinusLeftRewrites} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 * @since 2.0
	 */
	public static List<IPosition> inclusionSetMinusLeftRewritesGetPositions(
			Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate pred) {
				return pred.getTag() == SUBSETEQ && Lib.isSetMinus(pred.getLeft());
			}

		});
	}

	/**
	 * Return the list of applicable positions of the tactic "rewrite inclusion
	 * with set minus on the right" {@link InclusionSetMinusRightRewrites} to a
	 * predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author htson
	 */
	public static List<IPosition> inclusionSetMinusRightRewritesGetPositions(
			Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate pred) {
				return pred.getTag() == SUBSETEQ && Lib.isSetMinus(pred.getRight());
			}

		});
	}


	/**
	 * Return the tactic "rewrites inclusion with set minus on the left"
	 * {@link InclusionSetMinusRightRewrites} which is applicable to a
	 * hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "rewrites inclusion with set minus on the left"
	 * @author htson
	 * @since 2.0
	 */
	public static ITactic inclusionSetMinusLeftRewrites(Predicate hyp,
			IPosition position) {
		return BasicTactics.reasonerTac(new InclusionSetMinusLeftRewrites(),
				new InclusionSetMinusLeftRewrites.Input(hyp, position));
	}

	/**
	 * Return the tactic "rewrites inclusion with set minus on the right"
	 * {@link InclusionSetMinusRightRewrites} which is applicable to a
	 * hypothesis at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "rewrites inclusion with set minus on the right"
	 * @author htson
	 */
	public static ITactic inclusionSetMinusRightRewrites(Predicate hyp,
			IPosition position) {
		return BasicTactics.reasonerTac(new InclusionSetMinusRightRewrites(),
				new InclusionSetMinusRightRewrites.Input(hyp, position));
	}

	/**
	 * Return the list of applicable positions of the tactic
	 * "partition rewrites" {@link PartitionRewrites} to a predicate.
	 * <p>
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @author Nicolas Beauger
	 */
	public static List<IPosition> partitionGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(MultiplePredicate pred) {
				return pred.getTag() == KPARTITION;
			}

		});
	}
	
	/**
	 * Return the tactic "rewrites partition" {@link PartitionRewrites} which is
	 * applicable to a predicate at a given position.
	 * <p>
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "rewrites partition"
	 */
	public static ITactic partitionRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new PartitionRewrites(),
				new PartitionRewrites.Input(hyp, position));
	}

	/**
	 * Returns the arithmetic rewriter.
	 * <p>
	 * Since 3.0 there is no FormulaFactory anymore since the rewriter use the
	 * factory of the original formula.
	 * </p>
	 * 
	 * @return the arithmetic rewriter
	 * @since 3.0
	 */
	public static IFormulaRewriter getArithRewriter() {
		return new ArithRewriterImpl();
	}

	/**
	 * Return the list of applicable positions of the tactic
	 * "arithmetic rewrites" {@link ArithRewrites} to a predicate.
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of applicable positions
	 * @since 3.0
	 */
	public static List<IPosition> arithGetPositions(Predicate predicate) {
		final IFormulaRewriter rewriter = new ArithRewriterImpl();
		return predicate.getPositions(new DefaultFilter() {
			@Override
			public boolean select(BinaryExpression expr) {
				return rewriter.rewrite(expr) != expr;
			}

			@Override
			public boolean select(AssociativeExpression expr) {
				return rewriter.rewrite(expr) != expr;
			}

			@Override
			public boolean select(RelationalPredicate pred) {
				return rewriter.rewrite(pred) != pred;
			}
		});
	}

	/**
	 * Return the tactic "arithmetic rewrites" {@link ArithRewrites} which is
	 * applicable to a predicate at a given position.
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            a position
	 * @return The tactic "arithmetic rewrites"
	 * @since 1.1
	 */
	public static ITactic arithRewrites(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ArithRewrites(),
				new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns whether the one-point rule is applicable to the given predicate.
	 * 
	 * @param predicate
	 *            a predicate to check
	 * @return <code>true</code> iff one-point rules is applicable to the given
	 *         predicate
	 * @since 3.0
	 */
	public static boolean isOnePointApplicable(Predicate predicate) {
		return OnePointRule.isApplicable(predicate);
	}

	/**
	 * Returns the tactic "one-point rule" {@link OnePointRule} which is
	 * applicable to the goal of a sequent.
	 * 
	 * @return the tactic "one-point rule on goal"
	 * @since 1.1
	 */
	public static ITactic onePointGoal() {
		return BasicTactics.reasonerTac(new OnePointRule(),
				new OnePointRule.Input(null));
	}

	/**
	 * Returns the tactic "one-point rule" {@link OnePointRule} which is
	 * applicable to a hypothesis of a sequent.
	 * 
	 * @param hyp
	 *            a hypothesis on which one-point rule is applicable
	 * @return the tactic "one-point rule on hyp"
	 * @since 1.1
	 */
	public static ITactic onePointHyp(Predicate hyp) {
		return BasicTactics.reasonerTac(new OnePointRule(),
				new OnePointRule.Input(hyp));
	}

	/**
	 * Returns the tactic "total domain substitution" for a given substitute.
	 * It is applicable to any predicate of a sequent.
	 * 
	 * @param hyp a hypothesis, or <code>null</code> to specify the goal
	 * @param position a valid position of an expression in the specified predicate
	 * @param substitute a substitute to the specified expression
	 * @return the tactic "total domain substitution"
	 * 
	 * @since 1.1
	 */
	public static ITactic totalDomRewrites(Predicate hyp, IPosition position,
			Expression substitute) {
		return TotalDomFacade.getTactic(hyp, position, substitute);
	}

	/**
	 * Returns a set of possible total domain substitutions for the given
	 * expression in the given sequent.
	 * 
	 * @param sequent
	 *            a sequent
	 * @param expression
	 *            an expression to substitute
	 * @return a set of substitutes (empty if none was found)
	 * 
	 * @since 1.1
	 */
	public static Set<Expression> totalDomGetSubstitutions(
			IProverSequent sequent, Expression expression) {
		return TotalDomFacade.getSubstitutions(sequent, expression);
	}

	/**
	 * Returns the tactic "Functional Image Simplification" for a given position
	 * where this tactic can apply.
	 * 
	 * @param hyp
	 *            the hypothesis to rewrite or <code>null</code> if the goal
	 *            shall be rewritten
	 * @param position
	 *            a valid position of an expression in the goal or hypothesis
	 * @return the tactic "Functional Image Simplification"
	 * @since 2.0
	 */
	public static ITactic funImgSimplifies(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunImgSimplifies(),
				new Input(hyp, position));
	}

	/**
	 * Returns a set of positions where the rewriter funImgSimpRewrites can
	 * apply.
	 * 
	 * @param hyp
	 *            the hypothesis to rewrite or <code>null</code> if the goal
	 *            shall be rewritten
	 * @param sequent
	 *            the current sequent
	 * @return a set of positions (empty if the tactic is not applicable)
	 * 
 	 * @since 2.0
	 */
	public static List<IPosition> funImgSimpGetPositions(Predicate hyp,
			IProverSequent sequent) {
		return FunImgSimpImpl.getApplicablePositions(hyp, sequent);
	}

	/**
	 * Tells if the tactic funImgSimpRewrites can apply.
	 * 
	 * @param funImgExpr
	 *            the expression to rewrite. It is a functional image expression
	 *            where the tactic funImgSimpRewrites could apply
	 * @param sequent
	 *            the current sequent
	 * @return <code>true</code> if the tactic is not applicable,
	 *         <code>false</code> otherwise
	 * 
	 * @since 2.0
	 */
	public static boolean isFunImgSimpApplicable(Expression funImgExpr,
			IProverSequent sequent) {
		return FunImgSimpImpl.isApplicable(funImgExpr, sequent);
	}
	
	
	/**
	 * Returns the tactic "Functional Image Goal" for a given position
	 * where this tactic can apply.
	 * 
	 * @param hyp
	 *            the hypothesis of the form f ∈ S1 op S2
	 * @param position
	 *            a valid position of an function application f(E) in the goal
	 * @return the tactic "Functional Image Goal"
	 * @since 2.0
	 */
	public static ITactic funImgGoal(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunImageGoal(),
				new FunImageGoal.Input(hyp, position));
	}
	
	/**
	 * Returns the list of applicable positions of the tactic "Datatype Distinct
	 * Case" {@link DTDistinctCase} to the given predicate.
	 * 
	 * @param predicate a predicate
	 * @return a list of positions (empty if the tactic is not applicable)
	 * @since 2.0
	 * @deprecated use either {@link #dtDCGetPositions(Predicate)} or
	 *             {@link #dtInducGetPositions(Predicate)}
	 */
	@Deprecated
	public static List<IPosition> dtDCInducGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {
			@Override
			public boolean select(FreeIdentifier identifier) {
				return isDatatypeType(identifier.getType());
			}
		});
	}

	/**
	 * Returns the list of applicable positions of the tactic "Datatype Distinct
	 * Case" {@link DTDistinctCase} to the given predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of positions (empty if the tactic is not applicable)
	 * @since 3.6
	 */
	public static List<IPosition> dtDCGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {
			@Override
			public boolean select(FreeIdentifier identifier) {
				return isDatatypeType(identifier.getType());
			}
		});
	}

	/**
	 * Determines if the tactic "Datatype Induction"
	 * {@link DTInduction} is applicable to the given predicate.
	 *
	 * @param predicate a predicate
	 * @return whether the tactic is applicable
	 * @since 3.6
	 */
	public static List<IPosition> dtInducGetPositions(Predicate predicate) {
		if (predicate.getTag() != FORALL) {
			return NO_POSITIONS;
		}
		List<IPosition> result = new ArrayList<>();
		IPosition current = IPosition.ROOT.getFirstChild();
		Formula<?> child = predicate.getSubFormula(current);
		while (child.getTag() == BOUND_IDENT_DECL) {
			if (isDatatypeType(((BoundIdentDecl) child).getType())) {
				result.add(current);
			}
			current = current.getNextSibling();
			child = predicate.getSubFormula(current);
		}
		return result;
	}

	/**
	 * Returns the tactic "Datatype Distinct Case" for a given position where
	 * this tactic can be applied.
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            the position of the application
	 * @return the tactic "Datatype Distinct Case"
	 * @since 2.0
	 */
	public static ITactic dtDistinctCase(Predicate hyp, IPosition position) {
		return reasonerTac(new DTDistinctCase(), new DTDistinctCase.Input(hyp, position));
	}

	/**
	 * Returns the tactic "Datatype Distinct Case" for a given position where this
	 * tactic can be applied, with an input.
	 *
	 * The input should be the user-provided, comma-separated list of identifiers to
	 * use to generate fresh names for the constructor parameters.
	 *
	 * @param hyp      a hypothesis or <code>null</code> if the application happens
	 *                 in goal
	 * @param position the position of the application
	 * @param input    user input
	 * @return the tactic "Datatype Distinct Case"
	 * @since 3.7
	 */
	public static ITactic dtDistinctCase(Predicate hyp, IPosition position, String input) {
		return (pt, pm) -> reasonerTac(new DTDistinctCase(),
				new DTDistinctCase.Input(hyp, position, input, pt.getFormulaFactory())).apply(pt, pm);
	}

	/**
	 * Returns the tactic "Datatype Induction" for a given position where this
	 * tactic can be applied.
	 * 
	 * @param hyp
	 *            a hypothesis or <code>null</code> if the application happens
	 *            in goal
	 * @param position
	 *            the position of the application
	 * @return the tactic "Datatype Induction"
	 * @since 2.0
	 * @deprecated use {@link #dtInduction(IPosition)}
	 */
	@Deprecated
	public static ITactic dtInduction(Predicate hyp, IPosition position) {
		if (hyp == null) {
			return dtInduction(position);
		} else {
			throw new IllegalArgumentException("Induction is only applicable to the goal");
		}
	}

	/**
	 * Returns the tactic "Datatype Induction".
	 *
	 * @return the tactic "Datatype Induction"
	 * @since 3.6
	 */
	public static ITactic dtInduction(IPosition position) {
		return BasicTactics.reasonerTac(new DTInduction(), new DTInduction.Input(null, position));
	}
	
	/**
	 * Returns the tactic {@link FiniteDefRewrites} for a given position where
	 * it can be applied.
	 * 
	 * @since 2.1
	 */
	public static ITactic finiteDef(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FiniteDefRewrites(),
				new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the tactic
	 * {@link FiniteDefRewrites} to the given predicate.
	 * 
	 * @param predicate
	 *            a predicate
	 * @return a list of positions (empty if the tactic is not applicable)
	 * @since 2.1
	 */
	public static List<IPosition> finiteDefGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {

			@Override
			public boolean select(SimplePredicate pred) {
				return Lib.isFinite(pred);
			}
			
		});
	}

	/**
	 * Returns the tactic for the {@link CardDefRewrites} reasoner for a given
	 * position where it can be applied.
	 *
	 * @param hyp      a hypothesis or {@code null} if the application is in goal
	 * @param position the position of the application
	 * @return the tactic "Cardinal definition"
	 * @since 3.6
	 */
	public static ITactic cardDef(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new CardDefRewrites(), new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner
	 * {@link CardDefRewrites} to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.6
	 */
	public static List<IPosition> cardDefGetPositions(Predicate predicate) {
		return predicate.inspect(new DefaultInspector<IPosition>() {
			@Override
			public void inspect(RelationalPredicate pred, IAccumulator<IPosition> accumulator) {
				if (pred.getTag() == EQUAL) {
					if (pred.getLeft().getTag() == KCARD) {
						accumulator.add(accumulator.getCurrentPosition().getFirstChild());
					}
					if (pred.getRight().getTag() == KCARD) {
						accumulator.add(accumulator.getCurrentPosition().getChildAtIndex(1));
					}
				}
			}
		});
	}

	/**
	 * Returns the tactic for the {@link EqualCardRewrites} reasoner for a given
	 * position where it can be applied.
	 *
	 * @param hyp      a hypothesis or {@code null} if the application is in goal
	 * @param position the position of the application
	 * @return the tactic "Simplify cardinal equality"
	 * @since 3.6
	 */
	public static ITactic equalCard(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new EqualCardRewrites(), new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner
	 * {@link EqualCardRewrites} to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.6
	 */
	public static List<IPosition> equalCardGetPositions(Predicate predicate) {
		return predicate.inspect(new DefaultInspector<IPosition>() {
			@Override
			public void inspect(RelationalPredicate pred, IAccumulator<IPosition> accumulator) {
				if (pred.getTag() == EQUAL) {
					if (pred.getLeft().getTag() == KCARD && pred.getRight().getTag() == KCARD) {
						accumulator.add(accumulator.getCurrentPosition());
					}
				}
			}
		});
	}

	/**
	 * Returns the tactic for the {@link MinMaxDefRewrites} reasoner for a given
	 * position where it can be applied.
	 *
	 * @param hyp      a hypothesis or {@code null} if the application is in goal
	 * @param position the position of the application
	 * @return the tactic "Min/max definition"
	 * @since 3.6
	 */
	public static ITactic minMaxDef(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new MinMaxDefRewrites(), new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner
	 * {@link MinMaxDefRewrites} to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.6
	 */
	public static List<IPosition> minMaxDefGetPositions(Predicate predicate) {
		return predicate.inspect(new DefaultInspector<IPosition>() {
			@Override
			public void inspect(RelationalPredicate pred, IAccumulator<IPosition> accumulator) {
				if (pred.getTag() == EQUAL) {
					if (pred.getLeft().getTag() == KMIN || pred.getLeft().getTag() == KMAX) {
						accumulator.add(accumulator.getCurrentPosition().getFirstChild());
					}
					if (pred.getRight().getTag() == KMIN || pred.getRight().getTag() == KMAX) {
						accumulator.add(accumulator.getCurrentPosition().getChildAtIndex(1));
					}
				}
			}
		});
	}

	/**
	 * Returns the tactic for the {@link BCompDefRewrites} reasoner for a given
	 * position where it can be applied.
	 *
	 * @param hyp      a hypothesis or {@code null} if the application is in goal
	 * @param position the position of the application
	 * @return the tactic "Backward composition definition"
	 * @since 3.6
	 */
	public static ITactic bcompDef(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new BCompDefRewrites(), new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner
	 * {@link BCompDefRewrites} to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.6
	 */
	public static List<IPosition> bcompDefGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {
			@Override
			public boolean select(AssociativeExpression expr) {
				return expr.getTag() == BCOMP;
			}
		});
	}

	/**
	 * Returns the tactic for the {@link EqualFunImgDefRewrites} reasoner for a
	 * given position where it can be applied.
	 *
	 * @param hyp      a hypothesis or {@code null} if the application is in goal
	 * @param position the position of the application
	 * @return the tactic "functional image equality definition"
	 * @since 3.7
	 */
	public static ITactic equalFunImgDef(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new EqualFunImgDefRewrites(), new AbstractManualRewrites.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner
	 * {@link EqualFunImgDefRewrites} to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.7
	 */
	public static List<IPosition> equalFunImgDefGetPositions(Predicate predicate) {
		return predicate.inspect(new DefaultInspector<IPosition>() {
			@Override
			public void inspect(RelationalPredicate pred, IAccumulator<IPosition> accumulator) {
				if (pred.getTag() == EQUAL) {
					if (pred.getLeft().getTag() == FUNIMAGE) {
						accumulator.add(accumulator.getCurrentPosition().getFirstChild());
					}
					if (pred.getRight().getTag() == FUNIMAGE) {
						accumulator.add(accumulator.getCurrentPosition().getChildAtIndex(1));
					}
				}
			}
		});
	}

	/**
	 * Returns the tactic for the {@link ExponentiationStep} reasoner for a given position
	 * where it can be applied.
	 *
	 * @param hyp      a hypothesis or {@code null} if the application is in goal
	 * @param position the position of the application
	 * @return the tactic "definition of exponentiation step"
	 * @since 3.7
	 */
	public static ITactic exponentiationStep(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ExponentiationStep(), new ExponentiationStep.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner {@link ExponentiationStep}
	 * to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.7
	 */
	public static List<IPosition> exponentiationStepGetPositions(Predicate predicate) {
		return new ExponentiationStep().getPositions(predicate, true);
	}

	/**
	 * Returns the tactic for the {@link FunDprodImg} reasoner for a given position.
	 *
	 * @param hyp      a hypothesis or <code>null</code> if the application happens
	 *                 in goal
	 * @param position a position pointing on a functional image which function is a
	 *                 direct product
	 * @return The tactic "direct product fun. image"
	 * @since 3.8
	 */
	public static ITactic funDprodImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunDprodImg(), new FunDprodImg.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner {@link FunDprodImg}
	 * to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.8
	 */
	public static List<IPosition> funDprodImgGetPositions(Predicate predicate) {
		return new FunDprodImg().getPositions(predicate, false);
	}

	/**
	 * Returns the tactic for the {@link FunPprodImg} reasoner for a given position.
	 *
	 * @param hyp      a hypothesis or <code>null</code> if the application happens
	 *                 in goal
	 * @param position a position pointing on a functional image which function is a
	 *                 parallel product
	 * @return The tactic "parallel product fun. image"
	 * @since 3.8
	 */
	public static ITactic funPprodImg(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunPprodImg(), new FunPprodImg.Input(hyp, position));
	}

	/**
	 * Returns the list of applicable positions of the reasoner {@link FunPprodImg}
	 * to a predicate.
	 *
	 * @param predicate a predicate
	 * @return a list of applicable positions
	 * @since 3.8
	 */
	public static List<IPosition> funPprodImgGetPositions(Predicate predicate) {
		return predicate.getPositions(new DefaultFilter() {
			@Override
			public boolean select(BinaryExpression expression) {
				return expression.getTag() == FUNIMAGE && expression.getLeft().getTag() == PPROD;
			}
		});
	}

	/**
	 * Returns the tactic for the {@link DerivEqualInterv} reasoner.
	 *
	 * @param hyp a hypothesis (must not be {@code null})
	 * @return The tactic "equality of intervals"
	 * @since 3.8
	 */
	public static ITactic derivEqualInterv(Predicate hyp) {
		return BasicTactics.reasonerTac(new DerivEqualInterv(), new DerivEqualInterv.Input(hyp));
	}

	/**
	 * Checks if reasoner {@link DerivEqualInterv} is applicable to a predicate.
	 *
	 * @param predicate a predicate
	 * @return whether the reasoner is applicable to the given predicate
	 * @since 3.8
	 */
	public static boolean derivEqualIntervApplicable(Predicate predicate) {
		return DerivEqualInterv.isApplicable(predicate);
	}

}
