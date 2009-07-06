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
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class FiniteFunRelImg extends EmptyInputReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteFunRelImg";

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@ProverRule("FIN_FUN_IMG_R")
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();

		// goal should have the form finite(r[s])
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		if (!Lib.isRelImg(sPred.getExpression()))
			return null;
		
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];
		
		BinaryExpression expression = (BinaryExpression) sPred.getExpression();
		
		// f : A +-> B, where A and B are from the type of f 
		Expression f = expression.getLeft();
		Type type = f.getType();
		assert type instanceof PowerSetType;
		Type baseType = type.getBaseType();
		assert baseType instanceof ProductType;
		ProductType pType = (ProductType) baseType;
		Type S = pType.getLeft();
		Type T = pType.getRight();
		Expression pFun = ff.makeBinaryExpression(Expression.PFUN, S
				.toExpression(ff), T.toExpression(ff), null);
		Predicate newGoal0 = ff.makeRelationalPredicate(Predicate.IN, f, pFun,
				null);
		antecidents[0] = ProverFactory.makeAntecedent(newGoal0);
		
		// finite(s)
		Expression s = expression.getRight();
		
		Predicate newGoal1 = ff.makeSimplePredicate(Predicate.KFINITE, s, null);
		antecidents[1] = ProverFactory.makeAntecedent(newGoal1);

		return antecidents;
	}

	protected String getDisplayName() {
		return "finite of relational image of a function";
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IAntecedent[] antecidents = getAntecedents(seq);
		if (antecidents == null)
			return ProverFactory.reasonerFailure(this, input,
					"Inference " + getReasonerID()
							+ " is not applicable");

		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				getDisplayName(), antecidents);
	}

}
