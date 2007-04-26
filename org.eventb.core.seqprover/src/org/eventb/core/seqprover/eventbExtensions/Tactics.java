package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.tactics.BasicTactics.compose;
import static org.eventb.core.seqprover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.repeat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
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
import org.eventb.internal.core.seqprover.eventbExtensions.AutoImpF;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.ConjF;
import org.eventb.internal.core.seqprover.eventbExtensions.Contr;
import org.eventb.internal.core.seqprover.eventbExtensions.Cut;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.eventb.internal.core.seqprover.eventbExtensions.DoCase;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;
import org.eventb.internal.core.seqprover.eventbExtensions.ExE;
import org.eventb.internal.core.seqprover.eventbExtensions.ExF;
import org.eventb.internal.core.seqprover.eventbExtensions.ExI;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;
import org.eventb.internal.core.seqprover.eventbExtensions.He;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpE;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.ModusTollens;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter.DisjToImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter.Trivial;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter.TypePred;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ContImplHypRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DoubleImplHypRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;


/**
 * This class contains static methods that wrap Event-B reasoner extensions into
 * tactics. In many cases, applicability methods are also incuded that implement a
 * quick check to see if the tactic may be applicable in a particular situation.
 * 
 * 
 * @author Farhad Mehta
 * @author htson
 * 
 * TODO : complete comments.
 */
public class Tactics {

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
	 * Introduces a lemma (and its well definedness condition) into a proof.
	 * 
	 * @param lemma
	 * 		The lemma to introduce as a String.
	 * @return
	 * 		The resulting lemma tactic.
	 */
	public static ITactic lemma(final String lemma) {

		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new Cut(),
						new SinglePredInput(lemma, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
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

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new AbstrExpr(),
						new SingleExprInput(expression, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
			}

		};
	}
	
	public static ITactic abstrExprThenEq(final String expression) {
		
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				
				// Apply the abstract expression tactic
				Object result = abstrExpr(expression).apply(pt, pm);
				
				// Check if it was successful
				if (result != null) return result;
				
				// Get the introduced equality
				IAntecedent[] antecedents = pt.getRule().getAntecedents();
				assert antecedents.length != 0;
				IAntecedent lastAntecedent = antecedents[antecedents.length - 1];
				Set<Predicate> addedHyps = lastAntecedent.getAddedHyps();
				assert addedHyps.size() == 1;
				Predicate eqHyp = addedHyps.iterator().next();
				assert Lib.isEq(eqHyp);
				
				// Get the node where Eq should be applied
				IProofTreeNode node = pt.getChildNodes()[antecedents.length - 1];
				// apply Eq
				result = he(eqHyp).apply(node, pm);
				
				// Check if it was successful
				if (result != null) return result;
				
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
	 * The normalize tactic.
	 * 
	 * This is a combination of applying some simple tactics (conjI,allI,impI,hyp...)
	 * repeatedly in order to simplify the structure of a subgoal.
	 * 
	 * @return
	 * 		The normalize tactic.
	 */
	public static ITactic norm() {
		ITactic Ti = repeat(compose(conjI(), allI(), impI()));
		ITactic T = repeat(compose(hyp(), trivialGoalRewrite(), tautology(),
				contradiction(), hyp(), Ti));
		return repeat(onAllPending(T));
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
				for (Predicate hyp : seq.selectedHypIterable()) {
					hypsToSelect.remove(hyp);
				}
				if (hypsToSelect.isEmpty())
					return "No more hypotheses found";
				return (mngHyp(ProverFactory.makeSelectHypAction(hypsToSelect)))
						.apply(pt, pm);
			}

		};
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
		return BasicTactics.reasonerTac(new Contr(), new Contr.Input(null));
	}

	public static boolean contradictGoal_applicable(IProofTreeNode node) {
		Predicate goal = node.getSequent().goal();
		if (goal.equals(Lib.False))
			return false;
		Predicate negGoal = Lib.makeNeg(goal);
		if (negGoal.equals(Lib.True))
			return false;
		// if (Predicate.containsPredicate(
		// node.getSequent().selectedHypotheses(),
		// negGoal));
		return true;

	}

	public static ITactic impI() {
		return BasicTactics.reasonerTac(new ImpI(), new EmptyInput());
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
		return BasicTactics.reasonerTac(new AllI(), new EmptyInput());
	}

	public static boolean allI_applicable(Predicate goal) {
		return Lib.isUnivQuant(goal);
	}

	public static ITactic exI(final String... witnesses) {
		return new ITactic() {

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

	/**
	 * Returns a tactic to remove a top-level negation operator in the current
	 * goal.
	 * 
	 * @return a tactic to remove a top-level negation operator in the current
	 *         goal.
	 * @deprecated use <code>removeNegGoal(IPosition.ROOT)</code> instead.
	 * @see #removeNegGoal(IPosition)
	 */
	@Deprecated
	public static ITactic removeNegGoal() {
		return BasicTactics.reasonerTac(new SimpleRewriter.RemoveNegation(),
				new SimpleRewriter.RemoveNegation.Input(null));
	}

	/**
	 * Tells whether the <code>removeNegGoal()</code> tactic is applicable to
	 * the given goal.
	 * 
	 * @param goal
	 *            the goal to test for applicability
	 * @return <code>true</code> iff the <code>removeNegGoal()</code> tactic
	 *         is applicable
	 * @deprecated use
	 *             <code>rnGetPositions(goal).contains(IPosition.ROOT)</code>
	 * @see #rnGetPositions(Predicate)
	 */
	@Deprecated
	public static boolean removeNegGoal_applicable(Predicate goal) {
		return (new SimpleRewriter.RemoveNegation()).isApplicable(goal);
	}
	
	public static ITactic removeNegGoal(IPosition position) {
		return BasicTactics.reasonerTac(new RemoveNegation(),
				new RemoveNegation.Input(null, position));
	}

	public static ITactic disjToImpGoal() {
		return BasicTactics.reasonerTac(new DisjToImpl(), new DisjToImpl.Input(
				null));
	}

	public static boolean disjToImpGoal_applicable(Predicate goal) {
		return (new DisjToImpl()).isApplicable(goal);
	}

	// Tactics applicable on a hypothesis

	public static ITactic allD(final Predicate univHyp,
			final String... instantiations) {
		final Predicate pred = univHyp;
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				ITypeEnvironment typeEnv = pt.getSequent().typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(pred);
				final AllD.Input input = new AllD.Input(instantiations,
						boundIdentDecls, typeEnv, pred);
				return (BasicTactics.reasonerTac(new AllD(), input)).apply(pt,
						pm);
			}

		};
	}

	public static ITactic allDThenImpE(final Predicate univHyp,
			final String... instantiations) {
		final Predicate pred = univHyp;
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				IProverSequent seq = pt.getSequent();
				ITypeEnvironment typeEnv = seq.typeEnvironment();
				BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(pred);
				final AllD.Input input = new AllD.Input(instantiations,
						boundIdentDecls, typeEnv, pred);

				AllD allD = new AllD();
				IReasonerOutput output = allD.apply(seq, input, pm);

				if (output instanceof IReasonerFailure) {
					return output;
				}
				if (output == null)
					return "! Plugin returned null !";
				if (!(output instanceof IProofRule))
					return output;
				IAntecedent[] antecedents = ((IProofRule) output)
						.getAntecedents();
				assert antecedents.length == 2;
				Set<Predicate> addedHyps = antecedents[1].getAddedHyps();
				assert addedHyps.size() == 1;
				if (!pt.isOpen()) return "Root already has children";
				if (!pt.applyRule((IProofRule) output)) 
					return "Rule "+((IProofRule) output).getDisplayName()+" is not applicable";

				// Find the new hypothesis and try to apply impE.
				IProofTreeNode[] openDescendants = pt.getOpenDescendants();
				IProofTreeNode node = openDescendants[openDescendants.length - 1];
				return impE(addedHyps.iterator().next()).apply(node, pm);
			}

		};
	}

	public static boolean allD_applicable(Predicate hyp) {
		return Lib.isUnivQuant(hyp);
	}

	public static ITactic conjF(Predicate conjHyp) {
		return BasicTactics.reasonerTac(new ConjF(), new ConjF.Input(conjHyp));
	}

	public static boolean conjF_applicable(Predicate hyp) {
		return Lib.isConj(hyp);
	}
	
	
	/**
	 * This tactic tries to split a conjunction in the selected hyps.
	 * 
	 * @return the tactic
	 */
	public static ITactic conjD_auto(){
		return new ITactic(){

			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
					if (conjF_applicable(shyp)){
						return conjF(shyp).apply(ptNode, pm);
					}
				}
				return "Selected hyps contain no conjunctions";
			}
		};
	}

	public static ITactic impE(Predicate impHyp) {
		return BasicTactics.reasonerTac(new ImpE(), new ImpE.Input(impHyp));
	}

	public static boolean impE_applicable(Predicate hyp) {
		return Lib.isImp(hyp);
	}
	
	/**
	 * This tactic tries to automatically apply an impE or he for an implicative selected hyp 
	 * where the right hand side of the implication is contained in the hyps.
	 *  
	 * @return the tactic
	 */
	public static ITactic impE_auto(){
		return new ITactic(){
			
			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
					if (Lib.isImp(shyp) &&
							ptNode.getSequent().containsHypotheses(Lib.breakPossibleConjunct(Lib.impLeft(shyp)))){
						return impE(shyp).apply(ptNode, pm);
					}
				}
				return "Selected hyps contain no appropriate implications";
			}
		};
	}

	public static ITactic disjE(Predicate disjHyp) {
		return BasicTactics.reasonerTac(new DisjE(), new DisjE.Input(disjHyp));
	}

	public static boolean disjE_applicable(Predicate hyp) {
		return Lib.isDisj(hyp);
	}

	public static ITactic eqE(Predicate eqHyp) {
		return BasicTactics.reasonerTac(new Eq(), new SinglePredInput(eqHyp));
	}

	public static boolean eqE_applicable(Predicate hyp) {
		return Lib.isEq(hyp);
	}

	/**
	 * This tactic tries to automatically apply an eqE or he for an equality selected hyp 
	 * where one side of the equality is a free variable and the other side is an expression
	 * that doesn't contain the free variable.
	 *  
	 * @return the tactic
	 */
	public static ITactic eqE_auto(){
		return new ITactic(){

			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
					if (Lib.isEq(shyp)){
						if (Lib.isFreeIdent(Lib.eqLeft(shyp)) &&
								! Arrays.asList(Lib.eqRight(shyp).getFreeIdentifiers()).contains(Lib.eqLeft(shyp))){
							// Try eq and return only if the tactic actually did something.
							if (eqE(shyp).apply(ptNode, pm) == null) return null;
						} else if (Lib.isFreeIdent(Lib.eqRight(shyp)) &&
								! Arrays.asList(Lib.eqLeft(shyp).getFreeIdentifiers()).contains(Lib.eqRight(shyp))){
							// Try he and return only if the tactic actually did something.
							if (he(shyp).apply(ptNode, pm) == null) return null;
						}
					}
					
				}
				return "Selected hyps contain no appropriate equalities";
			}
		};
	}
	
	/**
	 * @param exHyp
	 * @return
	 * 
	 * @deprecated use {@link #exF(Predicate)} instead.
	 */
	@Deprecated
	public static ITactic exE(Predicate exHyp) {
		return BasicTactics.reasonerTac(new ExE(), new ExE.Input(exHyp));
	}

	public static boolean exF_applicable(Predicate hyp) {
		return Lib.isExQuant(hyp);
	}
	
	public static ITactic exF(Predicate exHyp) {
		return BasicTactics.reasonerTac(new ExF(), new ExF.Input(exHyp));
	}

	/**
	 * Returns a tactic to remove a top-level negation operator in the given
	 * hypothesis.
	 * 
	 * @return a tactic to remove a top-level negation operator in the given
	 *         hypothesis
	 * @deprecated use <code>removeNegHyp(IPosition.ROOT)</code> instead.
	 * @see #removeNegHyp(IPosition)
	 */
	@Deprecated
	public static ITactic removeNegHyp(Predicate hyp) {
		return BasicTactics.reasonerTac(new SimpleRewriter.RemoveNegation(),
				new SimpleRewriter.RemoveNegation.Input(hyp));
	}

	/**
	 * Tells whether the <code>removeNegHyp()</code> tactic is applicable to
	 * the given hypothesis.
	 * 
	 * @param hyp
	 *            the hypothesis to test for applicability
	 * @return <code>true</code> iff the <code>removeNegHyp()</code> tactic
	 *         is applicable
	 * @deprecated use <code>rnGetPositions(hyp).contains(IPosition.ROOT)</code>
	 * @see #rnGetPositions(Predicate)
	 */
	@Deprecated
	public static boolean removeNegHyp_applicable(Predicate hyp) {
		return (new SimpleRewriter.RemoveNegation()).isApplicable(hyp);
	}

	public static ITactic removeNegHyp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveNegation(),
				new RemoveNegation.Input(hyp, position));
	}

	public static ITactic falsifyHyp(Predicate hyp) {
		return BasicTactics.reasonerTac(new Contr(), new Contr.Input(hyp));
	}

	public static boolean falsifyHyp_applicable(Predicate hyp,
			IProverSequent seq) {
		return (!seq.goal().equals(Lib.makeNeg(hyp)));
	}
	
	/**
	 * This tactic tries to find a contradiction for a negated hyp in the selected hyps.
	 * 
	 * @return the tactic
	 */
	public static ITactic falsifyHyp_auto(){
		return new ITactic(){

			public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
				for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
					if (Lib.isNeg(shyp) &&
							ptNode.getSequent().containsHypotheses(Lib.breakPossibleConjunct(Lib.negPred(shyp)))){
						return falsifyHyp(shyp).apply(ptNode, pm);
					}
				}
				return "Selected hyps contain no contradicting negations";
			}
		};
	}


	// Misc tactics

	public static ITactic hyp() {
		return BasicTactics.reasonerTac(new Hyp(), new EmptyInput());
	}

	public static ITactic tautology() {
		return BasicTactics.reasonerTac(new TrueGoal(), new EmptyInput());
	}

	public static ITactic contradiction() {
		return BasicTactics.reasonerTac(new FalseHyp(), new EmptyInput());
	}

	public static ITactic trivial() {
		return compose(hyp(), trivialGoalRewrite(), tautology(), hyp());
	}

	public static ITactic trivialGoalRewrite() {
		return compose(BasicTactics.reasonerTac(new Trivial(),
				new Trivial.Input(null)), BasicTactics.reasonerTac(
				new TypePred(), new TypePred.Input(null)));
	}

	public static ITactic autoRewriteRules() {
		return BasicTactics.reasonerTac(new AutoRewrites(),new EmptyInput());
	}

	public static ITactic prune() {
		return BasicTactics.prune();
	}

	public static ITactic mngHyp(ISelectionHypAction hypAction) {
		return BasicTactics.reasonerTac(new MngHyp(), new MngHyp.Input(hypAction));
	}

	private static ITactic cleanupTac = compose(contradiction(), tautology(),
			hyp(), impI());

	public static ITactic postProcessBeginner() {
		return repeat(onAllPending(cleanupTac));
	}

	// It is important that conjD_auto() is called sometime before falsifyHyp_auto()
	// and impE_auto()
	public static ITactic postProcessExpert() {
		return repeat(onAllPending(compose(
				new AutoRewriteTac(),
				// autoRewriteRules() already incorporates what conjD_auto() does
				// conjD_auto(),
				falsifyHyp_auto(),
				eqE_auto(),
				// impE_auto(),
				new AutoImpFTac(),
				new AutoExFTac(),
				new NormTac()
				)));
	}

	public static ITactic afterLasoo(final ITactic tactic) {
		return new ITactic() {

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
	 * @deprecated use <code>contImpHyp(hyp, position)</code> instead. The
	 *             change was caused by this method having a weird name.
	 */
	@Deprecated
	public static ITactic mpImpHyp(Predicate hyp, IPosition position) {
		return contImpHyp(hyp, position);
	}

	public static ITactic contImpHyp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new ContImplHypRewrites(),
				new ContImplHypRewrites.Input(hyp, position));
	}

	public static boolean isFunOvrApp(Formula subFormula) {
		// It should be the top most predicate
		// if (position.isRoot())
		// return false;
		//		

		if (Lib.isFunApp(subFormula)) {
			Expression left = ((BinaryExpression) subFormula).getLeft();
			if (Lib.isOrv(left)) {
				Expression[] children = ((AssociativeExpression) left)
						.getChildren();
				Expression last = children[children.length - 1];
				if (last instanceof SetExtension) {
					Expression[] expressions = ((SetExtension) last)
							.getMembers();

					if (expressions.length == 1) {
						if (expressions[0] instanceof BinaryExpression
								&& expressions[0].getTag() == Expression.MAPSTO) {
							return true;
						}
					}
				}

			}
		}
		return false;
	}

	public static ITactic funOvrGoal(IPosition position) {
		return BasicTactics.reasonerTac(new FunOvr(), new FunOvr.Input(null,
				position));
	}

	public static ITactic funOvrHyp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new FunOvr(), new FunOvr.Input(hyp,
				position));
	}

	public static ITactic he(Predicate hyp) {
		return BasicTactics.reasonerTac(new He(), new SinglePredInput(hyp));
	}

	public static ITactic modusTollens(Predicate impHyp) {
		return BasicTactics.reasonerTac(new ModusTollens(),
				new ModusTollens.Input(impHyp));
	}

	public static boolean isParentTopLevelPredicate(Predicate pred,
			IPosition pos) {
		IPosition tmp = pos.getParent();

		while (!tmp.isRoot()) {
			Formula subFormula = pred.getSubFormula(tmp);
			if (subFormula instanceof QuantifiedExpression)
				return false;
			if (subFormula instanceof Predicate)
				return false;
			tmp = tmp.getParent();
		}
		return true;
	}

	public static List<IPosition> rnGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {
			@Override
			public boolean select(UnaryPredicate predicate) {
				if (predicate.getTag() == Predicate.NOT) {
					Predicate child = predicate.getChild();
					if (child instanceof RelationalPredicate) {
						RelationalPredicate rPred = (RelationalPredicate) child;
						if (rPred.getTag() == Predicate.EQUAL) {
							Expression right = rPred.getRight();
							Expression left = rPred.getLeft();
							if (right instanceof AtomicExpression) {
								AtomicExpression aExp = (AtomicExpression) right;
								if (aExp.getTag() == Expression.EMPTYSET)
									return true;
							}
							if (left instanceof AtomicExpression) {
								AtomicExpression aExp = (AtomicExpression) right;
								if (aExp.getTag() == Expression.EMPTYSET)
									return true;
							}
						}
					}
					if (child instanceof AssociativePredicate) {
						return true;
					}
					if (child.equals(Lib.True) || child.equals(Lib.False)) {
						return true;
					}
					if (Lib.isNeg(child)) {
						return true;
					}
					if (Lib.isImp(child)) {
						return true;
					}
					if (Lib.isExQuant(child)) {
						return true;
					}
					if (Lib.isUnivQuant(child)) {
						return true;
					}
				}
				return super.select(predicate);
			}

		});
	}

	public static List<IPosition> rmGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate predicate) {
				if (predicate.getTag() == Predicate.IN) {
					Expression left = predicate.getLeft();
					Expression right = predicate.getRight();
					int rTag = right.getTag();
					int lTag = left.getTag();
					if (left instanceof BinaryExpression
							&& lTag == Expression.MAPSTO
							&& right instanceof BinaryExpression
							&& rTag == Expression.CPROD) {
						return true;
					}
					if (right instanceof UnaryExpression
							&& rTag == Expression.POW) {
						return true;
					}
					if (right instanceof AssociativeExpression
							&& (rTag == Expression.BUNION || rTag == Expression.BINTER)) {
						return true;
					}
					if (right instanceof BinaryExpression
							&& rTag == Expression.SETMINUS) {
						return true;
					}
					if (right instanceof SetExtension) {
						return true;
					}
					if (right instanceof UnaryExpression
							&& (rTag == Expression.KUNION || rTag == Expression.KINTER)) {
						return true;
					}
					if (right instanceof QuantifiedExpression
							&& (rTag == Expression.QUNION || rTag == Expression.QINTER)) {
						return true;
					}
					if (right instanceof UnaryExpression
							&& (rTag == Expression.KDOM || rTag == Expression.KRAN)) {
						return true;
					}
					if (right instanceof UnaryExpression
							&& rTag == Expression.CONVERSE) {
						return true;
					}
					if (right instanceof BinaryExpression
							&& (rTag == Expression.DOMRES || rTag == Expression.DOMSUB)) {
						return true;
					}
					if (right instanceof BinaryExpression
							&& (rTag == Expression.RANRES || rTag == Expression.RANSUB)) {
						return true;
					}
				}
				return super.select(predicate);
			}

		});
	}

	public static ITactic removeMembership(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveMembership(),
				new RemoveMembership.Input(hyp, position));
	}

	public static List<IPosition> riGetPositions(Predicate pred) {
		return pred.getPositions(new DefaultFilter() {

			@Override
			public boolean select(RelationalPredicate predicate) {
				if (predicate.getTag() == Predicate.SUBSETEQ) {
					return true;
				}
				return super.select(predicate);
			}

		});

	}

	public static ITactic removeInclusion(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveInclusion(),
				new RemoveInclusion.Input(hyp, position));
	}
	
	public static class NormTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return norm().apply(ptNode, pm);
		}
		
	}
	
	public static class AutoRewriteTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return autoRewriteRules().apply(ptNode, pm);
		}
		
	}
	
	public static class AutoImpFTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return BasicTactics.reasonerTac(new AutoImpF(), new EmptyInput()).apply(ptNode, pm);
		}	
	}
	
	public static class AutoExFTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			for (Predicate shyp : ptNode.getSequent().selectedHypIterable()) {
				if (exF_applicable(shyp)){
					return exF(shyp).apply(ptNode, pm);
				}
			}
			return "Selected hyps contain no existential hyps";
		}
		
	}

	public static class AutoFalsifyHypTac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return falsifyHyp_auto().apply(ptNode, pm);
		}
		
	}

	public static class AutoEqETac implements ITactic{

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return eqE_auto().apply(ptNode, pm);
		}
		
	}

}
