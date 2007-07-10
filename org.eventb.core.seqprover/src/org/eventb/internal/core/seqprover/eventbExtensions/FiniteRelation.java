package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

public class FiniteRelation extends SingleExprInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteRelation";

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();

		// goal should have the form finite(r) where r is a relation
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		if (!Lib.isRelation(sPred.getExpression()))
			return null;
		
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];
		
		Expression r = sPred.getExpression();
		Type type = r.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();
		assert baseType instanceof ProductType;
		ProductType productType = (ProductType) baseType;
		Type S = productType.getLeft();
		Type T = productType.getRight();
		
		// finite(S)
		Predicate newGoal0 = ff.makeSimplePredicate(Predicate.KFINITE, S
				.toExpression(ff), null);
		antecidents[0] = ProverFactory.makeAntecedent(newGoal0);

		// finite(S)
		Predicate newGoal1 = ff.makeSimplePredicate(Predicate.KFINITE, T
				.toExpression(ff), null);
		antecidents[1] = ProverFactory.makeAntecedent(newGoal1);

		return antecidents;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		
		Predicate goal = seq.goal();
		if (!Lib.isFinite(goal))
			return ProverFactory.reasonerFailure(this, input,
					"Goal is not a finiteness");
		SimplePredicate sPred = (SimplePredicate) goal;
		if (!Lib.isRelation(sPred.getExpression()))
			return ProverFactory.reasonerFailure(this, input,
				"Goal is not a finiteness of a relation");
		
		Expression r = ((SimplePredicate) goal).getExpression();
		
		if (!(input instanceof SingleExprInput))
			return ProverFactory.reasonerFailure(this, input,
					"Expected a single expression input");

		if (((SingleExprInput) input).hasError()) {
			return ProverFactory.reasonerFailure(this, input,
					((SingleExprInput) input).getError());
		}
		Expression relation = ((SingleExprInput) input).getExpression();

		if (!Lib.isSetOfRelation(relation)) {
			return ProverFactory.reasonerFailure(this, input,
				"Expected a set of all relations S ↔ T");
		}

		// There will be 3 antecidents
		IAntecedent[] antecidents = new IAntecedent[3];
		
		Expression S = ((BinaryExpression) relation).getLeft();
		Expression T = ((BinaryExpression) relation).getRight();
		
		// r : S <-> T
		Predicate newGoal0 = ff.makeRelationalPredicate(Predicate.IN, r,
				relation, null);
		antecidents[0] = ProverFactory.makeAntecedent(newGoal0);
		
		// finite(S)
		Predicate newGoal1 = ff.makeSimplePredicate(Predicate.KFINITE, S, null);
		antecidents[1] = ProverFactory.makeAntecedent(newGoal1);

		// finite(S)
		Predicate newGoal2 = ff.makeSimplePredicate(Predicate.KFINITE, T, null);
		antecidents[2] = ProverFactory.makeAntecedent(newGoal2);

		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				goal,
				"finite of relation",
				antecidents);
		
		return reasonerOutput;
	}

}
