/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added FunOvrGoalTac and FunOvrHypTac tactics
 *     Systerel - added PartitionRewriteTac tactic (math V2)
 *     Systerel - added FiniteHypBoundedGoalTac and OnePoint*Tac
 *     Systerel - modified FindContrHypsTac to use ContrHyps (discharge)
 *     Systerel - added FunImgSimpTac tactic (simplify)
 *     Systerel - added DTDestrWDTac tactic (discharge)
 *     Systerel - added tactics to combine rm and ri
 *     Systerel - added tactic combinators
 ******************************************************************************/
package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.IPosition.ROOT;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.disjToImpl;
import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilFailure;
import static org.eventb.core.seqprover.tactics.BasicTactics.composeUntilSuccess;
import static org.eventb.core.seqprover.tactics.BasicTactics.loopOnAllPending;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.MultiplePredInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AutoImpF;
import org.eventb.internal.core.seqprover.eventbExtensions.ContrHyps;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteHypBoundedGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.GeneralizedModusPonensL1;
import org.eventb.internal.core.seqprover.eventbExtensions.HypOr;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.NegEnum;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.FunAppInDomGoalTac;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.InDomGoalManager;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.MapOvrGoalTac;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.MembershipGoalTac;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.NNFRewritesOnceTac;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.TacticsLib;
import org.eventb.internal.core.seqprover.eventbExtensions.tactics.TDomToCprod.TotalDomToCProdTac;


/**
 * This class contains static internal classes that implement automatic tactics.
 * 
 * <p>
 * Auto tactics are tactics that require no user input and are used to either discharge, simplify, or split the proof tree
 * nodes to which they are applied.
 * </p>
 * 
 * <p>
 * They typically extend the auto and post tactic extension points.
 * </p>
 * 
 * @author Farhad Mehta
 * 
 * @since 1.0
 */
public class AutoTactics {

	private static final EmptyInput EMPTY_INPUT = new EmptyInput();
		

	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics()
	{
		
	}
	
	
	//*************************************************
	//
	//				Discharging Auto tactics
	//
	//*************************************************
	
	
	/**
	 * Discharges any sequent whose goal is 'true'.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class TrueGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new TrueGoal(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges any sequent containing a 'false' hypothesis.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FalseHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new FalseHyp(), EMPTY_INPUT);
		}
	}

	
	/**
	 * Discharges any sequent whose goal is present in its hypotheses.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class GoalInHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new Hyp(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges any sequent whose goal is a disjunction and one of whose disjuncts 
	 * is present in the hypotheses.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class GoalDisjInHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new HypOr(), EMPTY_INPUT);
		}
	}

	
	/**
	 * Discharges a sequent whose goal states that an expression is a
	 * function (i.e. 'E : T1 -/-> T2', where T1 and T2 are type expressions).
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FunGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new IsFunGoal(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges a sequent whose goal states that an expression E has a lower
	 * or a upper bound (e.g. '∃n·(∀x·x ∈ S ⇒ x ≤ n)'), when there is an
	 * hypothesis that states the finiteness of E (i.e. 'finite(S)').
	 * 
	 * @author Nicolas Beauger
	 * @since 1.1
	 * 
	 */
	public static class FiniteHypBoundedGoalTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new FiniteHypBoundedGoal(), EMPTY_INPUT);
		}
	}

	/**
	 * Discharges a sequent by finding contradictory hypotheses.
	 * This tactic tries to find a contradiction using each selected hypothesis that is a negation.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FindContrHypsTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				final Set<Predicate> contrHyps = ContrHyps
						.contradictingPredicates(shyp);
				if (contrHyps != null
						&& ptNode.getSequent().containsHypotheses(contrHyps)) {
					return Tactics.contrHyps(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hypotheses contain no contradicting negations";
		};
	}
	
	/**
	 * Discharges any sequent whose goal denotes a membership to a domain the
	 * value of which is known.
	 * 
	 * @since 2.0
	 */
	public static class InDomGoalTac implements ITactic {

		public Object apply(IProofTreeNode initialNode, IProofMonitor pm) {

			final IProverSequent sequent = initialNode.getSequent();
			final Predicate goal = sequent.goal();
			if (!checkPrecondition(goal)) {
				return "Tactic unapplicable";
			}
			final IProofTreeNode ptNode = TacticsLib.addFunctionalHypotheses(
					initialNode, pm);
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			final InDomGoalManager manager = TacticsLib
					.createInDomManager(goal);
			if (manager.isApplicable(ptNode)) {
				if (manager.applyTactics(ptNode, pm) == null) {
					return null;
				}
			}
			initialNode.pruneChildren();
			return "Tactic fails";
		}

		// Returns true if goal has syntactic form "E: dom(F)"
		private static boolean checkPrecondition(Predicate goal) {
			final Expression element = Lib.getSet(goal);
			return element != null && Lib.isDom(element);
		}

	}

	/**
	 * Discharges any sequent whose goal denotes that a functional image belongs
	 * to a set when this can be derived from the function properties.
	 * @since 2.0
	 */
	public static class FunImgInGoalTac implements ITactic {

		private static final ITactic hypTac = new GoalInHypTac();
		private static final ITactic funGoalTac = new FunGoalTac();

		public Object apply(final IProofTreeNode initialNode, IProofMonitor pm) {
			if (!checkPrecondition(initialNode)) {
				return "Tactic unapplicable";
			}
			if(pm != null && pm.isCanceled()){
				return "Canceled";
			}
			final IProofTreeNode ptNode = TacticsLib.addFunctionalHypotheses(
					initialNode, pm);
			final ITactic tac = composeUntilSuccess(hypTac, funGoalTac);
			if (tac.apply(ptNode, pm) == null) {
				return null;
			}
			initialNode.pruneChildren();
			return "Tactic fails";
		}

		// Returns true if goal has syntactic form "E(F) : S"
		private static boolean checkPrecondition(IProofTreeNode node) {
			final Predicate goal = node.getSequent().goal();
			final Expression element = Lib.getElement(goal);
			return element != null && Lib.isFunApp(element);
		}

	}

	/**
	 * Discharges any sequent that has the form of the WD of a datatype
	 * destructor.
	 * 
	 * @since 2.1
	 */
	public static class DTDestrWDTac implements ITactic {
		
		private static class Appli {
			public static final Appli NOT_APPLI = new Appli(null, null, null);
			
			private final FreeIdentifier id;
			private final IExpressionExtension constr;
			private final IPosition pos;
			
			private Appli(FreeIdentifier id, IExpressionExtension constr, IPosition pos) {
				this.id = id;
				this.constr = constr;
				this.pos = pos;
			}
			
			public static Appli applicable(FreeIdentifier id, IExpressionExtension ext, IPosition pos) {
				return new Appli(id, ext, pos);
			}

		}
		
		private static final ITactic trueGoalTac = new TrueGoalTac();
		private static final ITactic hypTac = new GoalInHypTac();

		@Override
		public Object apply(IProofTreeNode initialNode, IProofMonitor pm) {
			final Predicate goal = initialNode.getSequent().goal();
			final Appli appli = getAppli(goal);
			if (appli == Appli.NOT_APPLI) {
				return "Tactic unapplicable";
			}
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
						
			// dc
			final ITactic dc = Tactics.dtDistinctCase(null, appli.pos);
			final Object resultDC = dc.apply(initialNode, pm);
			if (resultDC != null) {
				return resultDC;
			}
			
			IProofTreeNode node = initialNode.getFirstOpenDescendant();
			final String[] dcHypParams = getDCHypParams(node.getSequent(), appli);
			
			// exI
			final Object resultExI = Tactics.exI(dcHypParams).apply(node, pm);
			if (resultExI != null) {
				return resultExI;
			}

			// true goal, hyp
			final ITactic tac = loopOnAllPending(trueGoalTac, hypTac);
				
			final Object resultTrueHyp = tac.apply(node, pm);
			if (resultTrueHyp != null) {
				return resultTrueHyp;
			}
			if (node.isClosed()) {
				return null;
			}
			initialNode.pruneChildren();
			return "Tactic fails";
		}

		// goal of the form "# bids . id = constr()"
		// where id is a free identifier with datatype type dt
		// where dt has a single constructor 
		private static Appli getAppli(Predicate goal) {
			if (!Lib.isExQuant(goal)) {
				return Appli.NOT_APPLI;
			}
			final Predicate boundPred = Lib.getBoundPredicate(goal);
			if (!Lib.isEq(boundPred)) {
				return Appli.NOT_APPLI;
			}
			final Expression eqLeft = Lib.eqLeft(boundPred);
			if (!Lib.isFreeIdent(eqLeft)) {
				return Appli.NOT_APPLI;
			}
			final Type idType = eqLeft.getType();
			if (!(idType instanceof ParametricType)) {
				return Appli.NOT_APPLI;
			}
			final IExpressionExtension idTypeExtn = ((ParametricType) idType).getExprExtension();
			final Object origin = idTypeExtn.getOrigin();
			if (!(origin instanceof IDatatype)) {
				return Appli.NOT_APPLI;
			}
			final IDatatype dt = (IDatatype) origin;
			final Set<IExpressionExtension> constructors = dt.getConstructors();
			if (constructors.size() != 1) {
				return Appli.NOT_APPLI;
			}
			final Expression eqRight = Lib.eqRight(boundPred);
			if (!(eqRight instanceof ExtendedExpression)) {
				return Appli.NOT_APPLI;
			}
			final IExpressionExtension constrExtn = ((ExtendedExpression) eqRight)
					.getExtension();
			final IExpressionExtension constr = constructors.iterator().next();
			
			if (!constrExtn.equals(constr)) {
				return Appli.NOT_APPLI;
			}
			final IPosition pos = computePos((QuantifiedPredicate) goal);
			return Appli.applicable((FreeIdentifier) eqLeft, constrExtn, pos);
		}

		private static IPosition computePos(QuantifiedPredicate pred) {
			IPosition pos = IPosition.ROOT.getFirstChild();
			for (int i = 0; i < pred.getBoundIdentDecls().length; i++) {
				pos = pos.getNextSibling();
			}
			return pos.getFirstChild();
		}

		private static String[] getDCHypParams(IProverSequent sequent, Appli appli) {
			for (Predicate hyp : sequent.selectedHypIterable()) {
				if (!Lib.isEq(hyp)) {
					continue;
				}
				final Expression eqLeft = Lib.eqLeft(hyp);
				if (!appli.id.equals(eqLeft)) {
					continue;
				}
				final Expression eqRight = Lib.eqRight(hyp);
				if (!(eqRight instanceof ExtendedExpression)) {
					continue;
				}
				final ExtendedExpression constr = (ExtendedExpression) eqRight;
				final IExpressionExtension extn = constr.getExtension();
				if (!appli.constr.equals(extn)) {
					continue;
				}
				final Expression[] children = constr.getChildExpressions();
				final String[] names = getNames(children);
				if (names == null) {
					continue;
				}
				return names;
			}
			return null;
		}

		private static String[] getNames(Expression[] children) {
			final String[] names = new String[children.length];
			for (int i = 0; i < children.length; i++) {
				if (!Lib.isFreeIdent(children[i])) {
					return null;
				}
				names[i] = ((FreeIdentifier) children[i]).getName();
			}
			return names;
		}

	}

	/**
	 * Auto-tactic to discharge a sequent such as :
	 * <code>f(x)∈S, S⊆T ⊦ f(x)∈T</code>
	 * 
	 * @author Emmanuel Billaud
	 * @since 2.3
	 * 
	 */
	public static class MembershipGoalAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return new MembershipGoalTac();
		}
		
	}
	
	//*************************************************
	//
	//				Simplifying Auto tactics
	//
	//*************************************************
	
	/**
	 * Tries to simplify all predicates in a sequent using pre-defined simplification rewritings.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class AutoRewriteTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(AutoRewrites.DEFAULT, EMPTY_INPUT);
		}
	}
	
	/**
	 * Tries to simplify predicates related to types using pre-defined simplification rewritings.
	 * 
	 * @author htson
	 *
	 */
	public static class TypeRewriteTac  extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new TypeRewrites(),EMPTY_INPUT);
		}
	}
	
	/**
	 * Simplifies any sequent with an implicative goal by adding the left hand side of the implication to the hypotheses and making its 
	 * right hand side the new goal.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ImpGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return Tactics.impI();
		}
	}
	
	
	/**
	 * Simplifies any sequent with a universally quantified goal by freeing all its bound variables.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ForallGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return Tactics.allI();
		}
	}
	
	/**
	 * Simplifies a sequent containing (selected) existentially quantified hypotheses by freeing their bound variables.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ExistsHypTac implements ITactic{
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Tactics.exF_applicable(shyp)){
					return Tactics.exF(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hyps contain no existential hyps";
		}
		
	}

	/**
	 * Simplifies a sequent containing (selected) conjunctive hypotheses by separating them.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ConjHypTac implements ITactic{
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Tactics.conjF_applicable(shyp)){
					return Tactics.conjF(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hypotheses contain no conjunctions";
		}
		
	}
	
	/**
	 * Simplifies a sequent by rewriting all selected hypotheses and the goal using a (selected) hypothesis that is an equality
	 * between a free variable and an expression that does not contain the free variable. The used equality remains in the
	 * selected hypotheses to be used again.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class EqHypTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Lib.isEq(shyp)){
					if (Lib.isFreeIdent(Lib.eqLeft(shyp)) &&
							! Arrays.asList(Lib.eqRight(shyp).getFreeIdentifiers()).contains(Lib.eqLeft(shyp))){
						// Try eq and return only if the tactic actually did something.
						if (Tactics.eqE(shyp).apply(ptNode, pm) == null) return null;
					} else if (Lib.isFreeIdent(Lib.eqRight(shyp)) &&
							! Arrays.asList(Lib.eqLeft(shyp).getFreeIdentifiers()).contains(Lib.eqRight(shyp))){
						// Try he and return only if the tactic actually did something.
						if (Tactics.he(shyp).apply(ptNode, pm) == null) return null;
					}
				}

			}
			return "Selected hyps contain no appropriate equalities";
		}
	}
	
	
	/**
	 * Simplifies the (visible) implicative hypotheses in a sequent by removing predicates from their left hand sides that are
	 * (selected) hypotheses. 
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ShrinkImpHypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AutoImpF(), EMPTY_INPUT);
		}
	}
	
	/**
	 * Simplifies (selected) hypotheses of the form 'E={a,b,c}' to 'E={a,c}' after finding the hypothesis 'not(E=b)'.
	 * 
	 * 
	 * @author htson, Farhad Mehta
	 *
	 * TODO : do search in reasoner instead of tactic
	 */
	public static class ShrinkEnumHypTac implements ITactic  {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				// Search for E : {a, ... ,c}
				if (Lib.isInclusion(shyp)) {
					Expression right = ((RelationalPredicate) shyp)
							.getRight();
					if (Lib.isSetExtension(right)) {
						// Looking for not(E = b)
						for (Predicate hyp : ptNode.getSequent()
								.selectedHypIterable()) {
							if (Lib.isNeg(hyp)) {
								Predicate child = ((UnaryPredicate) hyp)
										.getChild();
								if (Lib.isEq(child)) {
									if (negEnum(shyp, hyp)
											.apply(ptNode, pm) == null)
										return null;
								}

							}
						}
					}
				}
			}

			return "Selected hyps contain no appropriate hypotheses";
		}
		
		private static ITactic negEnum(Predicate shyp, Predicate hyp) {
			return BasicTactics.reasonerTac(new NegEnum(), new MultiplePredInput(
					new Predicate[] { shyp, hyp }));
		}
	
	}	
	
	/**
	 * Simplifies all (selected) hypotheses of the form 'P => Q /\ R' into multiple
	 * implications 'P => Q' , 'P => R'.
	 * 
	 * @author htson, Farhad Mehta
	 */
	public static class SplitRightConjImpHypTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				// Search for (P => Q /\ ... /\ R)
				if (Lib.isImp(shyp)) {
					Predicate right = ((BinaryPredicate) shyp)
							.getRight();
					if (Lib.isConj(right)) {
						if (Tactics.impAndRewrites(shyp, IPosition.ROOT).apply(
								ptNode, pm) == null)
							return null;
					}
				}
			}
			return "Selected hyps contain no appropriate hypotheses";
		}
	}

	/**
	 * Simplifies all (selected) hypotheses of the form 'P \/ Q => R' into multiple
	 * implications 'P => R' , 'Q => R'.
	 * 
	 * @author htson, Farhad Mehta
	 */
	public static class SplitLeftDisjImpHypTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				// Search for (P \/ ... \/ Q => R)
				if (Lib.isImp(shyp)) {
					Predicate left = ((BinaryPredicate) shyp)
							.getLeft();
					if (Lib.isDisj(left)) {
						if (Tactics.impOrRewrites(shyp, IPosition.ROOT).apply(
								ptNode, pm) == null)
							return null;
					}
				}
			}
			return "Selected hyps contain no appropriate hypotheses";
		}
	}

	/**
	 * Simplifies all predicates of the form 'partition(S, ...)' into their
	 * expanded form in the goal and all visible hypotheses .
	 * 
	 * @author Nicolas Beauger
	 */
	public static class PartitionRewriteTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			boolean success = false;
			for (Predicate shyp : ptNode.getSequent().visibleHypIterable()) {
				success |= applyPartitionRewrites(ptNode, shyp, pm);
			}
			success |= applyPartitionRewrites(ptNode, null, pm);
			
			if (success)
				return null;
			else
				return "Tactic unapplicable";
		}

		private boolean applyPartitionRewrites(IProofTreeNode ptNode,
				Predicate hyp, IProofMonitor pm) {
			boolean success = false;
			final Predicate pred;
			if (hyp == null) {
				pred = ptNode.getSequent().goal();
			} else {
				pred = hyp;
			}
			final List<IPosition> positions = Tactics
					.partitionGetPositions(pred);
			for (IPosition position : positions) {
				final ITactic partitionRewrites = Tactics
						.partitionRewrites(hyp, position);
				success |= (partitionRewrites.apply(ptNode, pm) == null);
			}
			return success;
		}
	}

	/**
	 * Simplifies expressions of form '(A <<| f)(C)' and similar, where f is
	 * known as a partial function.
	 * 
	 * @author Thomas Muller
	 * @since 1.3
	 */
	public static class FunImgSimpTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().visibleHypIterable()) {
				if (applyFunImgSimplifies(ptNode, shyp, pm)) {
					return null;
				}
			}
			if (applyFunImgSimplifies(ptNode, null, pm))
				return null;
			return "Tactic unapplicable";
		}

		private boolean applyFunImgSimplifies(IProofTreeNode ptNode,
				Predicate hyp, IProofMonitor pm) {
			final IProverSequent sequent = ptNode.getSequent();
			final List<IPosition> positions = Tactics.funImgSimpGetPositions(
					hyp, sequent);
			for (IPosition position : positions) {
				final ITactic t = Tactics.funImgSimplifies(hyp, position);
				if (t.apply(ptNode, pm) == null) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Abstract implementation of an automatic tactic to apply once on a given
	 * collections of predicates. This class is intended to be used to wrap
	 * existing manual tactics.
	 * 
	 * @author Thomas Muller
	 * @since 2.1
	 */
	private static abstract class AbstractPredOnceTac implements ITactic {

		@Override
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			for (Predicate pred : getPredicates(ptNode)) {
				final List<IPosition> pos = getPositions(pred,
						ptNode.getFormulaFactory());
				if (pm != null && pm.isCanceled()) {
					return "Canceled";
				}
				if (pos.size() != 0) {
					if (pred.equals(ptNode.getSequent().goal())) {
						return getTactic(null, pos.get(0)).apply(ptNode, pm);						
					}
					return getTactic(pred, pos.get(0)).apply(ptNode, pm);
				}
			}
			return "Tactic unapplicable";
		}

		/**
		 * Returns the predicates on which the tactic should apply on.
		 * 
		 * @param ptNode
		 *            the current proof tree node
		 * @return the predicates on which the tactic should apply on.
		 */
		protected abstract Iterable<Predicate> getPredicates(
				IProofTreeNode ptNode);

		/**
		 * Returns a list of positions at which the tactic can apply.
		 * 
		 * @param predicate
		 *            the predicate on which the positions are calculated
		 * @param ff
		 *            the formula factory to be used
		 * @return the positions where the current tactic can apply on the given
		 *         predicate
		 */
		protected abstract List<IPosition> getPositions(Predicate predicate,
				FormulaFactory ff);

		/**
		 * Returns the tactic to be applied on a given predicate at a given
		 * position.
		 * 
		 * @param pred
		 *            the predicate on which the tactic shall apply
		 * @param pos
		 *            the position where the tactic can apply
		 * @return the tactic to apply on the given predicate at the given
		 *         position
		 */
		protected abstract ITactic getTactic(Predicate pred, IPosition pos);

	}

	/**
	 * Abstract implementation of the "Remove Membership" tactic to be applied
	 * once on a given predicate.
	 * 
	 * @since 2.1
	 */
	private static abstract class RmOnceTac extends AbstractPredOnceTac {

		protected List<IPosition> getPositions(Predicate pred, FormulaFactory ff) {
			
			return Tactics.rmGetPositions(pred, ff);
		}

		protected ITactic getTactic(Predicate pred, IPosition pos) {
			return Tactics.removeMembership(pred, pos);
		}

	}

	/**
	 * The automatic "Remove Inclusion" tactic to be applied once on a given
	 * predicate.
	 * 
	 * @since 2.1
	 */
	private static abstract class RiOnceTac extends AbstractPredOnceTac {

		@Override
		protected List<IPosition> getPositions(Predicate pred, FormulaFactory ff) {
			return Tactics.riGetPositions(pred);
		}

		@Override
		protected ITactic getTactic(Predicate pred, IPosition pos) {
			return Tactics.removeInclusion(pred, pos);
		}

	}

	/**
	 * The automatic "Remove Membership" tactic to be applied once on a goal.
	 * 
	 * @since 2.1
	 */
	private static class RmGoalOnceAutoTac extends RmOnceTac implements ITactic {

		@Override
		protected Iterable<Predicate> getPredicates(IProofTreeNode ptNode) {
			final Predicate goal = ptNode.getSequent().goal();
			return Collections.singleton(goal);
		}

	}

	/**
	 * The automatic "Remove Inclusion" tactic to be applied once on a goal.
	 * 
	 * @since 2.1
	 */
	private static class RiGoalOnceAutoTac extends RiOnceTac {

		@Override
		protected Iterable<Predicate> getPredicates(IProofTreeNode ptNode) {
			return Collections.singleton(ptNode.getSequent().goal());
		}

	}

	/**
	 * An automatic tactic combining the "RemoveMembership" tactic, and the
	 * "Remove Inclusion" tactic to apply on a goal, and looping on all pending
	 * sub-nodes.
	 * 
	 * @since 2.1
	 */
	public static class RmiGoalAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(new RmGoalOnceAutoTac(),
					new RiGoalOnceAutoTac());
		}

	}

	/**
	 * The automatic "Remove Membership" tactic to be applied on once an
	 * hypothesis.
	 * 
	 * @since 2.1
	 */
	private static class RmHypOnceAutoTac extends RmOnceTac implements ITactic {
		@Override
		protected Iterable<Predicate> getPredicates(IProofTreeNode ptNode) {
			return ptNode.getSequent().selectedHypIterable();
		}
	}

	/**
	 * The automatic "Remove Inclusion" tactic to be applied once on an
	 * hypothesis.
	 * 
	 * @since 2.1
	 */
	private static class RiHypOnceAutoTac extends RiOnceTac {

		@Override
		protected Iterable<Predicate> getPredicates(IProofTreeNode ptNode) {
			return ptNode.getSequent().selectedHypIterable();
		}

	}

	/**
	 * An automatic tactic combining the "RemoveMembership" tactic, and the
	 * "Remove Inclusion" tactic to apply on hypotheses, and looping on all
	 * pending nodes.
	 * 
	 * @since 2.1
	 */
	public static class RmiHypAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(new RmHypOnceAutoTac(),
					new RiHypOnceAutoTac());
		}

	}

	private abstract static class EqvRewritesAutoTac extends
			AbstractPredOnceTac {

		@Override
		protected List<IPosition> getPositions(Predicate predicate,
				FormulaFactory ff) {
			return Tactics.eqvGetPositions(predicate);
		}

		@Override
		protected ITactic getTactic(Predicate hyp, IPosition pos) {
			return Tactics.eqvRewrites(hyp, pos);
		}

	}
	
	private static class EqvRewritesHypOnceTac extends EqvRewritesAutoTac {

		@Override
		protected Iterable<Predicate> getPredicates(IProofTreeNode ptNode) {
			return ptNode.getSequent().selectedHypIterable();
		}
		
	}
	
	private static class EqvRewritesGoalOnceTac extends EqvRewritesAutoTac {

		@Override
		protected Iterable<Predicate> getPredicates(IProofTreeNode ptNode) {
			return Collections.singleton(ptNode.getSequent().goal());
		}
		
	}
	
	/**
	 * An automatic tactic to apply "Remove Equivalence" tactic on hypotheses,
	 * and looping on all pending nodes.
	 * 
	 * @since 2.1
	 */
	public static class EqvRewritesHypAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.loopOnAllPending(new EqvRewritesHypOnceTac());
		}
		
	}
	
	/**
	 * An automatic tactic combining the "RemoveMembership" tactic, and the
	 * "Remove Inclusion" tactic to apply on the goal, and looping on all
	 * pending nodes.
	 * 
	 * @since 2.1
	 */
	public static class EqvRewritesGoalAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.loopOnAllPending(new EqvRewritesGoalOnceTac());
		}
		
	}

	/**
	 * Replace disjunction occurring at the root of the goal by implication.
	 * 
	 * @author Emmanuel Billaud
	 * @since 2.2
	 */
	public static class DisjGoalTac extends AbsractLazilyConstrTactic {

		private static class DisjToImplGoal implements ITactic {

			@Override
			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				final Predicate goal = ptNode.getSequent().goal();
				if (goal.getTag() == Formula.LOR) {
					final ITactic tactic = disjToImpl(null, ROOT);
					return tactic.apply(ptNode, pm);
				}
				return "Tactic unapplicable";
			}

		}

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(composeUntilFailure(new DisjToImplGoal(),
					new ImpGoalTac()));
		}
	}

	/**
	 * @author Emmanuel Billaud
	 * @since 2.3
	 */
	public static class FunAppInDomGoalAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return new FunAppInDomGoalTac();
		}

	}

	//*************************************************
	//
	//				Splitting Auto tactics
	//
	//*************************************************


	/**
	 * Splits a sequent with a conjunctive goal into multiple subgoals.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ConjGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return Tactics.conjI();
		}
	}

	/**
	 * Applies automatically the <code>funOvrGoal</code> tactic to the first
	 * applicable position in the goal.
	 * 
	 * @author Laurent Voisin
	 */
	private static class FunOvrGoalOnceTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			final Predicate goal = ptNode.getSequent().goal();
			final List<IPosition> pos = Tactics.funOvrGetPositions(goal);
			if (pos.size() == 0) {
				return "Tactic unapplicable";
			}
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			return Tactics.funOvr(null, pos.get(0)).apply(ptNode, pm);
		}

	}

	/**
	 * Applies automatically, repeatedly and recursively the
	 * <code>FunOvrGoalOnceTac</code> to the proof subtree rooted at the given
	 * node.
	 * 
	 * @author Laurent Voisin
	 */
	public static class FunOvrGoalTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(new FunOvrGoalOnceTac(), new FunImgSimpTac());
		}

	}
	
	/**
	 * Applies automatically the <code>funOvrHyp</code> tactic to the first
	 * applicable position in the selected hypotheses.
	 * 
	 * @author Laurent Voisin
	 */
	private static class FunOvrHypOnceTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				final List<IPosition> pos = Tactics.funOvrGetPositions(shyp);
				if (pm != null && pm.isCanceled()) {
					return "Canceled";
				}
				if (pos.size() != 0) {
					return Tactics.funOvr(shyp, pos.get(0)).apply(ptNode, pm);
				}
			}
			return "Tactic unapplicable";
		}

	}

	/**
	 * Applies automatically, repeatedly and recursively the
	 * <code>FunOvrHypOnceTac</code> to the proof subtree rooted at the given
	 * node.
	 * 
	 * @author Laurent Voisin
	 */
	public static class FunOvrHypTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return loopOnAllPending(new FunOvrHypOnceTac(), new FunImgSimpTac());
		}

	}
	
	/**
	 * Applies automatically the <code>OnePointGoal</code> tactic to the goal.
	 * 
	 * @author Nicolas Beauger
	 * @since 1.1
	 */
	public static class OnePointGoalTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			final IProverSequent sequent = ptNode.getSequent();
			final Predicate goal = sequent.goal();
			final FormulaFactory ff = sequent.getFormulaFactory();
			if (!Tactics.isOnePointApplicable(goal, ff)) {
				return "Tactic unapplicable";
			}
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			return Tactics.onePointGoal().apply(ptNode, pm);
		}

	}

	/**
	 * Applies automatically the <code>OnePointHyp</code> tactic to the selected
	 * hypotheses.
	 * 
	 * @author Nicolas Beauger
	 * @since 1.1
	 */
	public static class OnePointHypTac implements ITactic {

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			if (pm != null && pm.isCanceled()) {
				return "Canceled";
			}
			final IProverSequent sequent = ptNode.getSequent();
			final FormulaFactory formulaFactory = sequent.getFormulaFactory();
			for (Predicate shyp : sequent.selectedHypIterable()) {
				if (Tactics.isOnePointApplicable(shyp, formulaFactory)) {
					return Tactics.onePointHyp(shyp).apply(ptNode, pm);
				}
				if (pm != null && pm.isCanceled()) {
					return "Canceled";
				}
			}
			return "Tactic unapplicable";
		}

	}

	/**
	 * Puts the sequents of all pending nodes in Negation Normal Form.
	 * 
	 * @author Emmanuel Billaud
	 * @since 2.2
	 */
	public static class NNFRewritesAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.loopOnAllPending(new NNFRewritesOnceTac());
		}

	}

	/**
	 * Remove mapplet overriding of total function in goal (f<+{x↦y}∈A→B).
	 * 
	 * @author Emmanuel Billaud
	 * @since 2.3
	 */
	public static class MapOvrGoalAutoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return new MapOvrGoalTac();
		}

	}

	/**
	 * Re-writes a domain if it can be substituted by a cartesian product. It
	 * applies only in goal matching : <code>x↦y∈dom(g)</code>.
	 * 
	 * @author Emmanuel Billaud
	 * @since 2.3
	 */
	public static class TotalDomToCProdAutoTac extends
			AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return new TotalDomToCProdTac();
		}

	}

	//*************************************************
	//
	//				Mixed
	//
	//*************************************************


	/**
	 * Clarifies the goal of the sequent by repeatedly :
	 * - splitting conjunctions
	 * - simplifying implications and universal quantifiers
	 * - discharging sequents with a true goal, a false hypothesis, and where the goal is contained in the hypotheses
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ClarifyGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			ITactic innerLoop = 
				composeOnAllPending(
					new ConjGoalTac(),
					new ImpGoalTac(),
					new ForallGoalTac());
			ITactic outerLoop =
				loopOnAllPending(
						new TrueGoalTac(),
						new FalseHypTac(),
						innerLoop);
			return outerLoop;
		}
	}

	/**
	 * Simplifies the visible hypotheses and goal in a sequent by replacing
	 * sub-predicates <code>P</code> by <code>⊤</code> (or <code>⊥</code>) if
	 * <code>P</code> (or <code>¬P</code>) appears as hypothesis (global and
	 * local).
	 * 
	 * @author Emmanuel Billaud
	 * @since 2.2
	 */
	public static class GenMPTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new GeneralizedModusPonensL1(),
					EMPTY_INPUT);
		}
	}


	//*************************************************
	//
	//				Selection
	//
	//*************************************************
	
	/**
	 * The lasso tactic.
	 * 
	 * @see Tactics#lasoo()
	 * @since 2.3
	 */
	public static class LassoTac extends AbsractLazilyConstrTactic {

		@Override
		protected ITactic getSingInstance() {
			return Tactics.lasoo();
		}
	}

	
	
	//*************************************************
	//
	//				Helper code
	//
	//*************************************************
	
	
	/**
	 * An abstract class that lazily constructs a tactic and avoids reconstructing it
	 * every time it is applied.
	 * 
	 * <p>
	 * This is particularly useful for tactics that are constructed using the tactic constructors such as 
	 * {@link BasicTactics#compose(ITactic...)} and {@link BasicTactics#repeat(ITactic)}.
	 * </p>
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static abstract class AbsractLazilyConstrTactic implements ITactic{
		
		private ITactic instance = null;
				
		abstract protected ITactic getSingInstance();
		
		public final Object apply(IProofTreeNode ptNode, IProofMonitor pm){
			if (instance == null) 
			{
				instance = getSingInstance(); 
			}
			
			return instance.apply(ptNode, pm);
		}
	}
	
}
