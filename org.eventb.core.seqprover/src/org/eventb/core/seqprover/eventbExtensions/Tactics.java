package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.tactics.BasicTactics.compose;
import static org.eventb.core.seqprover.tactics.BasicTactics.onAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.repeat;

import java.util.Arrays;
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
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasoners.Hyp;
import org.eventb.core.seqprover.reasoners.MngHyp;
import org.eventb.core.seqprover.reasoners.Review;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.seqprover.eventbExtensions.AllD;
import org.eventb.internal.core.seqprover.eventbExtensions.AllI;
import org.eventb.internal.core.seqprover.eventbExtensions.Conj;
import org.eventb.internal.core.seqprover.eventbExtensions.Contr;
import org.eventb.internal.core.seqprover.eventbExtensions.Cut;
import org.eventb.internal.core.seqprover.eventbExtensions.DisjE;
import org.eventb.internal.core.seqprover.eventbExtensions.DoCase;
import org.eventb.internal.core.seqprover.eventbExtensions.Eq;
import org.eventb.internal.core.seqprover.eventbExtensions.ExE;
import org.eventb.internal.core.seqprover.eventbExtensions.ExI;
import org.eventb.internal.core.seqprover.eventbExtensions.FalseHyp;
import org.eventb.internal.core.seqprover.eventbExtensions.FunOvr;
import org.eventb.internal.core.seqprover.eventbExtensions.He;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpE;
import org.eventb.internal.core.seqprover.eventbExtensions.ImpI;
import org.eventb.internal.core.seqprover.eventbExtensions.ModusTollens;
import org.eventb.internal.core.seqprover.eventbExtensions.TrueGoal;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter.DisjToImpl;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter.Trivial;
import org.eventb.internal.core.seqprover.eventbExtensions.SimpleRewriter.TypePred;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ContImplHypRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DoubleImplHypRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveNegation;

public class Tactics {

	// Globally applicable tactics

	public static ITactic review(final int reviewerConfidence) {
		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				final ITactic tactic = BasicTactics.reasonerTac(new Review(),
						new Review.Input(pt.getSequent(), reviewerConfidence));
				return tactic.apply(pt, pm);
			}
		};
	}

	public static ITactic lemma(final String lemma) {

		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new Cut(),
						new SinglePredInput(lemma, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
			}

		};
	}

	public static ITactic norm() {
		ITactic Ti = repeat(compose(conjI(), allI(), impI()));
		ITactic T = repeat(compose(hyp(), trivialGoalRewrite(), tautology(),
				contradiction(), hyp(), Ti));
		return repeat(onAllPending(T));
	}

	public static ITactic doCase1(String trueCase, ITypeEnvironment typeEnv) {
		return BasicTactics.reasonerTac(new DoCase(), new SinglePredInput(
				trueCase, typeEnv));
	}

	public static ITactic doCase(final String trueCase) {

		return new ITactic() {

			public Object apply(IProofTreeNode pt, IProofMonitor pm) {
				return (BasicTactics.reasonerTac(new DoCase(),
						new SinglePredInput(trueCase, pt.getSequent()
								.typeEnvironment()))).apply(pt, pm);
			}

		};
	}

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

	public static ITactic contradictGoal() {
		return BasicTactics.reasonerTac(new Contr(), new Contr.Input(null));
	}

	// public static boolean contradictGoal_applicable(Predicate goal){
	// return (! Lib.isFalse(goal));
	// }

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

	public static ITactic removeNegGoal(IPosition position) {
		return BasicTactics.reasonerTac(new RemoveNegation(),
				new RemoveNegation.Input(null, position));
	}

	// public static boolean removeNegGoal_applicable(Predicate goal) {
	// return (new RemoveNegation()).isApplicable(goal);
	// }

	public static ITactic disjToImpGoal() {
		return BasicTactics.reasonerTac(new DisjToImpl(), new DisjToImpl.Input(
				null));
	}

	public static boolean disjToImpGoal_applicable(Predicate goal) {
		return (new DisjToImpl()).isApplicable(goal);
	}

	// Tactics applicable on a hypothesis

	// TODO : change order of input in one of the two places
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

	public static boolean allD_applicable(Predicate hyp) {
		return Lib.isUnivQuant(hyp);
	}

	public static ITactic conjD(Predicate conjHyp) {
		return BasicTactics.reasonerTac(new Conj(), new Conj.Input(conjHyp));
	}

	public static boolean conjD_applicable(Predicate hyp) {
		return Lib.isConj(hyp);
	}

	public static ITactic impE(Predicate impHyp) {
		return BasicTactics.reasonerTac(new ImpE(), new ImpE.Input(impHyp));
	}

	public static boolean impE_applicable(Predicate hyp) {
		return Lib.isImp(hyp);
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

	public static ITactic exE(Predicate exHyp) {
		return BasicTactics.reasonerTac(new ExE(), new ExE.Input(exHyp));
	}

	public static boolean exE_applicable(Predicate hyp) {
		return Lib.isExQuant(hyp);
	}

	public static ITactic removeNegHyp(Predicate hyp, IPosition position) {
		return BasicTactics.reasonerTac(new RemoveNegation(),
				new RemoveNegation.Input(hyp, position));
	}

	// public static boolean removeNegHyp_applicable(Predicate hyp) {
	// return (new RemoveNegation()).isApplicable(hyp);
	// }

	public static ITactic falsifyHyp(Predicate hyp) {
		return BasicTactics.reasonerTac(new Contr(), new Contr.Input(hyp));
	}

	public static boolean falsifyHyp_applicable(Predicate hyp,
			IProverSequent seq) {
		return (!seq.goal().equals(Lib.makeNeg(hyp)));
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
		return compose(BasicTactics.reasonerTac(new AutoRewrites(),
				new EmptyInput()));
	}

	public static ITactic prune() {
		return BasicTactics.prune();
	}

	// public static ITactic mngHyp(ActionType type, Set<Predicate> hypotheses)
	// {
	// return BasicTactics.reasonerTac(
	// new MngHyp(),
	// new MngHyp.Input(type, hypotheses));
	// }

	public static ITactic mngHyp(IHypAction hypAction) {
		return BasicTactics.reasonerTac(new MngHyp(), new MngHyp.Input(
				hypAction));
	}

	// public static ITactic mngHyp(ActionType type, Predicate hypothesis){
	// return mngHyp(type, Collections.singleton(hypothesis));
	// }
	private static ITactic cleanupTac = compose(contradiction(), tautology(),
			hyp(), impI());

	public static ITactic postProcessBeginner() {
		return repeat(onAllPending(cleanupTac));
	}

	public static ITactic postProcessExpert() {
		return repeat(onAllPending(compose(norm(), autoRewriteRules())));
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

	public static boolean isDoubleImplPredicate(Predicate predicate) {
		if (Lib.isImp(predicate)) {
			BinaryPredicate bPred = (BinaryPredicate) predicate;
			if (Lib.isImp(bPred.getRight())) {
				return true;
			}

		}
		return false;
	}

	public static ITactic contImpHyp(Predicate pred, IPosition position) {
		return BasicTactics.reasonerTac(new ContImplHypRewrites(),
				new ContImplHypRewrites.Input(pred, position));
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

	public static List<IPosition> rn_getPositions(Predicate pred) {
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

	public static List<IPosition> rm_getPositions(Predicate pred) {
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
}
