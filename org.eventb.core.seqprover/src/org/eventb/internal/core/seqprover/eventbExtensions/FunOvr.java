package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FunOvr extends AbstractManualInference {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".funOvr";

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	@ProverRule({"OV_L", "OV_R"})
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();

		Formula<?> subFormula = predicate.getSubFormula(position);

		// "subFormula" should have the form (f <+ ... <+ h)(G)
		if (!Tactics.isFunOvrApp(subFormula))
			return null;
		
		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		BinaryExpression funAppExp = (BinaryExpression) subFormula;
		Expression G = funAppExp.getRight();
		Expression left = funAppExp.getLeft();
		
		AssociativeExpression funOvr = (AssociativeExpression) left;
		Expression[] children = funOvr.getChildren();
		Expression h = children[children.length - 1];
		
		if (Lib.isSetExtension(h)
				&& ((SetExtension) h).getMembers().length == 1) {
			// g is {E |-> F}
			Expression[] members = ((SetExtension) h).getMembers();
			Expression F = ((BinaryExpression) members[0]).getRight();
			Expression E = ((BinaryExpression) members[0]).getLeft();

			// Generate the first antecedent
			antecidents[0] = createEqualAntecident(pred, predicate, position,
					E, F, G);
			
			antecidents[1] = createNotEqualAntecident(pred, predicate,
					position, children, E, G);
		}
		else {
			antecidents[0] = createInDomAntecident(pred, predicate, position,
					h, G);
			antecidents[1] = createNotInDomAntecident(pred, predicate, position, children,
					h, G);
		}
		
		return antecidents;
	}

	private IAntecedent createNotInDomAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression[] children,
			Expression h, Expression G) {
		Expression fG = makeExpressionFOfG(children, G);

		Predicate inferredPred = predicate.rewriteSubFormula(position, fG, ff);
		
		// Make predicate not(G : dom(h))
		Expression domH = ff.makeUnaryExpression(Expression.KDOM, h, null);
		Predicate gInDomH = ff.makeRelationalPredicate(Predicate.IN, G,
				domH, null);
		Predicate gNotInDomH = ff.makeUnaryPredicate(Predicate.NOT, gInDomH,
				null);

		return makeAntecedent(sourcePred, inferredPred, gNotInDomH);
	}

	private Expression makeExpressionFOfG(Expression[] children, Expression G) {
		List<Expression> newChildren = new ArrayList<Expression>();
		for (int i = 0; i < children.length - 1; ++i) {
			newChildren.add(children[i]);
		}
		Expression f;
		if (newChildren.size() != 1) {
			f = ff.makeAssociativeExpression(Expression.OVR, newChildren,
					null);
		} else {
			f = newChildren.get(0);
		}
		return ff.makeBinaryExpression(Expression.FUNIMAGE, f, G,
				null);
	}

	private IAntecedent createInDomAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression h, Expression G) {
		// Generate the new predicate
		//        P((f <+ ... <+ h)(G)) == P(h(G))
		Expression F = ff.makeBinaryExpression(Expression.FUNIMAGE, h, G, null);
		Predicate inferredPred = predicate.rewriteSubFormula(position, F, ff);

		// Make predicate G : dom(h)
		Expression domH = ff.makeUnaryExpression(Expression.KDOM, h, null);
		Predicate gInDomH = ff.makeRelationalPredicate(Predicate.IN, G,
				domH, null);
		
		return makeAntecedent(sourcePred, inferredPred, gInDomH);
	}

	
	private IAntecedent createNotEqualAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression[] children,
			Expression E, Expression G) {
		Expression fG = makeExpressionFOfG(children, G);

		Predicate inferredPred = predicate.rewriteSubFormula(position, fG, ff);
		
		// Make predicate not(G = E)
		Predicate gEqualsE = ff.makeRelationalPredicate(Predicate.EQUAL, G,
				E, null);
		Predicate gNotEqualsE = ff.makeUnaryPredicate(Predicate.NOT, gEqualsE,
				null);

		return makeAntecedent(sourcePred, inferredPred, gNotEqualsE);
	}

	private IAntecedent createEqualAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression E,
			Expression F, Expression G) {
		// Generate the new predicate
		//        P((f <+ ... <+ {E |-> F})(G)) == P(F)
		Predicate inferredPred = predicate.rewriteSubFormula(position, F, ff);

		// Make predicate G = E
		Predicate gEqualsE = ff.makeRelationalPredicate(Predicate.EQUAL, G,
				E, null);
		
		return makeAntecedent(sourcePred, inferredPred, gEqualsE);
	}

	@Override
	protected String getDisplayName() {
		return "ovr";
	}

}
