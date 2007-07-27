package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.loopOnAllPending;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.eventb.internal.core.seqprover.eventbExtensions.AutoImpF;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.HypOr;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.IsFunGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpAndRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ImpOrRewrites;


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


	//*************************************************
	//
	//				Simplifying Auto tactics
	//
	//*************************************************

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
			return BasicTactics.reasonerTac(new ImpI(), EMPTY_INPUT);
		}
	}
	
	
	/**
	 * Simplifies any sequent with a universally quantified goal by freeing all universally quantified variables.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ForallGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AllI(), EMPTY_INPUT);
		}
	}

	/**
	 * Tries to simplify all predicates in a sequent using some pre-defined simplification rewritings.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class AutoRewriteTac  extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AutoRewrites(),EMPTY_INPUT);
		}
	}

	
	/**
	 * Simplifies a sequent by finding contradictory hypotheses and initiating a proof by contradiction.
	 * This tactic tries to find a contradiction using each selected hypothesis that is a negation.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class FindContrHypsTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Lib.isNeg(shyp) &&
						ptNode.getSequent().containsHypotheses(Lib.breakPossibleConjunct(Lib.negPred(shyp)))){
					return Tactics.falsifyHyp(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hypotheses contain no contradicting negations";
		};
	}

	
	/**
	 * @author Farhad Mehta
	 *
	 */
	public static class AutoImpFTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new AutoImpF(), EMPTY_INPUT);
		}
	}
	
	// *********************
	

	public static class AutoExFTac implements ITactic{
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (Tactics.exF_applicable(shyp)){
					return Tactics.exF(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hyps contain no existential hyps";
		}
		
	}

	public static class AutoEqETac implements ITactic{
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return Tactics.eqE_auto().apply(ptNode, pm);
		}
		
	}

	public static class AutoNegEnumTac implements ITactic {
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return Tactics.negEnum_auto().apply(ptNode, pm);
		}
	
	}

	/**
	 * The class for "Automatic implication hypothesis with conjunction
	 * right" {@link ImpAndRewrites}.
	 * <p>
	 * 
	 * @author htson
	 */
	public static class AutoImpAndHypTac implements ITactic {
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return Tactics.autoImpAndRight().apply(ptNode, pm);
		}
	
	}

	/**
	 * The class for "Automatic implication hypothesis with disjunctive
	 * left" {@link ImpOrRewrites}.
	 * <p>
	 * 
	 * @author htson
	 */
	public static class AutoImpOrHypTac implements ITactic {
	
		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return Tactics.autoImpOrRight().apply(ptNode, pm);
		}
	
	}

	
	

	
	//*************************************************
	//
	//				Splitting Auto tactics
	//
	//*************************************************


	/**
	 * Splits a sequent with a conjunctive goal using the conjunction introduction rule.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class ConjGoalTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new Conj(), new Conj.Input(null));
		}
	}

	
	//*************************************************
	//
	//				Misc
	//
	//*************************************************


	/**
	 * Clarifies the goal of the sequent by repeatedly :
	 * - splitting conjunctions
	 * - simplifying implications and universal quantifiers
	 * - discharging sequents with a true goal, a false hyposis, and where the goal is contained in the hypotheses
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
	private static abstract class AbsractLazilyConstrTactic implements ITactic{
		
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
