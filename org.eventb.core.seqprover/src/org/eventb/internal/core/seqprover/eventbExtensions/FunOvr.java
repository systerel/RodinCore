package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FunOvr extends AbstractManualInference {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".funOvr";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		// Generate the anticidents
		IAntecedent[] antecidents = new IAntecedent[2];
		// The goal with the true case

		if (pred == null) {
			Predicate goal = seq.goal();
			Formula subFormula = goal.getSubFormula(position);

			Expression G = ((BinaryExpression) subFormula).getRight();
			AssociativeExpression left = (AssociativeExpression) ((BinaryExpression) subFormula)
					.getLeft();
			Expression[] children = left.getChildren();
			Expression[] members = ((SetExtension) children[children.length - 1])
					.getMembers();

			Expression F = ((BinaryExpression) members[0]).getRight();
			Expression E = ((BinaryExpression) members[0]).getLeft();

			FormulaFactory ff = FormulaFactory.getDefault();

			Predicate newGoal0 = goal.rewriteSubFormula(position, F, ff);
			Predicate newhyp0 = ff.makeRelationalPredicate(Expression.EQUAL, G,
					E, null);
			Set<Predicate> addedHyps0 = new HashSet<Predicate>();
			addedHyps0.add(newhyp0);
			antecidents[0] = ProverFactory.makeAntecedent(newGoal0, addedHyps0,
					null);

			List<Expression> newChildren = new ArrayList<Expression>();
			for (int i = 0; i < children.length - 1; ++i) {
				newChildren.add(children[i]);
			}
			
			Expression f;
			if (newChildren.size() != 1) {
				f = ff.makeAssociativeExpression(Expression.OVR,
					newChildren, null);
			}
			else {
				f = newChildren.get(0);
			}

			Expression fG = ff.makeBinaryExpression(Expression.FUNIMAGE, f, G, null);
			
			Predicate newGoal1 = goal.rewriteSubFormula(position, fG, ff);
			Predicate newhyp1 = ff.makeUnaryPredicate(Expression.NOT, newhyp0, null);
			Set<Predicate> addedHyps1 = new HashSet<Predicate>();
			addedHyps1.add(newhyp1);
			antecidents[1] = ProverFactory.makeAntecedent(newGoal1, addedHyps1,
					null);

			return antecidents;
		} else {
			return null;
		}

	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null) {
			return "ovr " + pred.getSubFormula(position);
		}
		else {
			return "ovr goal";
		}
	}

	@Override
	public boolean isApplicable(Predicate pred, IPosition position) {
		Formula formula = pred.getSubFormula(position);
		if (formula instanceof Expression) {
			return Tactics.isFunOvrApp((Expression) formula);
		}
		return false;
	}

}
