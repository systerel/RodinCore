package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FunConvInterImg extends AbstractManualInference {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".funConvInterImg";

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();

		Formula subFormula = predicate.getSubFormula(position);

		// "subFormula" should have the form f~[S /\ ... /\ T]
		if (!Tactics.isFunConvInterImgApp(subFormula))
			return null;
		
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		BinaryExpression funAppExp = (BinaryExpression) subFormula;
		
		// Get f
		Expression left = funAppExp.getLeft();
		UnaryExpression fConverse = (UnaryExpression) left;
		Expression f = fConverse.getChild(); 

		// f : A +-> B (from type of f)
		Type type = f.getType();
		assert type instanceof PowerSetType;
		PowerSetType powerType = (PowerSetType) type;
		Type baseType = powerType.getBaseType();
		assert baseType instanceof ProductType;
		ProductType pType = (ProductType) baseType;
		Type A = pType.getLeft();
		Type B = pType.getRight();
		Expression typeA = A.toExpression(ff);
		Expression typeB = B.toExpression(ff);
		Expression typeFun = ff.makeBinaryExpression(Expression.PFUN, typeA, typeB, null);
		Predicate pred1 = ff.makeRelationalPredicate(Predicate.IN, f,
				typeFun, null);
		
		antecidents[0] = ProverFactory.makeAntecedent(pred1);
		
		AssociativeExpression right = (AssociativeExpression) funAppExp.getRight();
		antecidents[1] = createDistributedAntecident(pred, predicate, position, fConverse, right.getChildren());
		return antecidents;
	}

	private IAntecedent createDistributedAntecident(Predicate sourcePred, Predicate predicate,
			IPosition position, Expression fConverse, Expression[] children) {
		// make f~[S] /\ ... /\ f~[T]
		Expression [] newChildren = new Expression[children.length];
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeBinaryExpression(Expression.RELIMAGE,
					fConverse, children[i], null);
		}
		Expression newSubformula = ff.makeAssociativeExpression(Expression.BINTER, newChildren, null);
		
		// Rewrite the predicate
		Predicate inferredPred = predicate.rewriteSubFormula(position, newSubformula, ff);

		return makeAntecedent(sourcePred, inferredPred);
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null) {
			return "fun. conv. inter. image " + pred.getSubFormula(position);
		}
		else {
			return "fun. conv. inter. image in goal";
		}
	}

}
