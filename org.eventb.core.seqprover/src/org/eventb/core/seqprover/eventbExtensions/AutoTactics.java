package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.repeat;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;


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
	 * Discharges a sequent whose goal is 'true'.
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
	 * Discharges a sequent containing a 'false' hypothesis.
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
	 * Discharges a sequent whose goal is contained in its hypotheses.
	 * 
	 * @author Farhad Mehta
	 *
	 */
	public static class HypTac extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return BasicTactics.reasonerTac(new Hyp(), EMPTY_INPUT);
		}
	}


	//*************************************************
	//
	//				Simplifying Auto tactics
	//
	//*************************************************

	/**
	 * Simplifies a sequent with an implicative goal using the implication introduction rule.
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
	 * Simplifies a sequent with a universally quantified goal using the for-all introduction rule.
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
				repeat(composeOnAllPending(
						new TrueGoalTac(),
						new FalseHypTac(),
						innerLoop));
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
