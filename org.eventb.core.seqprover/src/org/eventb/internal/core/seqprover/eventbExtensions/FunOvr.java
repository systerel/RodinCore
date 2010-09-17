/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Fixed rules OV_SETENUM_L, OV_SETENUM_R, OV_L, OV_R
 *******************************************************************************/
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
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FunOvr extends AbstractManualInference implements IVersionedReasoner {

	private static final int VERSION = 1;

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".funOvr";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	public int getVersion() {
		return VERSION;
	}

	@Override
	@ProverRule({"OV_SETENUM_L","OV_SETENUM_R","OV_L", "OV_R"})
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		Predicate predicate = pred;
		if (predicate == null)
			predicate = seq.goal();
		else if (!seq.containsHypothesis(predicate)) {
			return null;
		}

		if (!predicate.isWDStrict(position)) {
			return null; // invalid position: reasoner failure
		}

		final Formula<?> subFormula = predicate.getSubFormula(position);
		// "subFormula" should have the form (f <+ ... <+ g)(G)
		if (!Tactics.isFunOvrApp(subFormula)) {
			return null;
		}
		
		// There will be 2 antecidents
		final IAntecedent[] antecidents = new IAntecedent[2];

		BinaryExpression funAppExp = (BinaryExpression) subFormula;
		Expression G = funAppExp.getRight();
		Expression left = funAppExp.getLeft();
		
		AssociativeExpression funOvr = (AssociativeExpression) left;
		Expression[] children = funOvr.getChildren();
		Expression g = children[children.length - 1];
		
		final FormulaFactory ff = seq.getFormulaFactory();
		if (Lib.isSetExtension(g)
				&& ((SetExtension) g).getMembers().length == 1) {
			// g is {E |-> F}
			Expression[] members = ((SetExtension) g).getMembers();
			Expression F = ((BinaryExpression) members[0]).getRight();
			Expression E = ((BinaryExpression) members[0]).getLeft();

			// Generate the first antecedent
			antecidents[0] = createEqualAntecident(pred, predicate, position,
					E, F, G, ff);
			
			antecidents[1] = createNotEqualAntecident(pred, predicate,
					position, children, E, G, ff);
		}
		else {
			antecidents[0] = createInDomAntecident(pred, predicate, position,
					g, G, ff);
			antecidents[1] = createNotInDomAntecident(pred, predicate, position, children,
					g, G, ff);
		}
		
		return antecidents;
	}

	private IAntecedent createNotInDomAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression[] children,
			Expression g, Expression G, FormulaFactory ff) {

		// Make predicate (domSub(dom(g),f))(G)
		Expression domG = ff.makeUnaryExpression(Expression.KDOM, g, null);
		Expression fG = makeExpressionFOfG(domG, children, G, ff);
		Predicate inferredPred = predicate.rewriteSubFormula(position, fG, ff);
		
		// Make predicate not(G : dom(g))
		Predicate gInDomG = ff.makeRelationalPredicate(Predicate.IN, G,
				domG, null);
		Predicate gNotInDomG = ff.makeUnaryPredicate(Predicate.NOT, gInDomG,
				null);

		return makeAntecedent(sourcePred, inferredPred, gNotInDomG);
	}

	private Expression makeExpressionFOfG(Expression domG, Expression[] children, Expression G, FormulaFactory ff) {
		final List<Expression> newChildren = new ArrayList<Expression>();
		for (int i = 0; i < children.length - 1; ++i) {
			newChildren.add(children[i]);
		}
		final Expression f;
		if (newChildren.size() != 1) {
			f = ff.makeAssociativeExpression(Expression.OVR, newChildren, null);
		} else {
			f = newChildren.get(0);
		}
		// Make predicate (domSub(dom(g),f))(G)
		final Expression fDomSubG = ff.makeBinaryExpression(Expression.DOMSUB,
				domG, f, null);
		return ff.makeBinaryExpression(Expression.FUNIMAGE, fDomSubG, G, null);
	}

	private IAntecedent createInDomAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression g, Expression G, FormulaFactory ff) {
		// Generate the new predicate
		//        P((f <+ ... <+ g)(G)) == P(g(G))
		Expression F = ff.makeBinaryExpression(Expression.FUNIMAGE, g, G, null);
		Predicate inferredPred = predicate.rewriteSubFormula(position, F, ff);

		// Make predicate G : dom(g)
		Expression domH = ff.makeUnaryExpression(Expression.KDOM, g, null);
		Predicate gInDomH = ff.makeRelationalPredicate(Predicate.IN, G,
				domH, null);
		
		return makeAntecedent(sourcePred, inferredPred, gInDomH);
	}

	
	private IAntecedent createNotEqualAntecident(Predicate sourcePred,
			Predicate predicate, IPosition position, Expression[] children,
			Expression E, Expression G, FormulaFactory ff) {
		// Make predicate (domSub({E},f))(G)
		Expression setE = ff.makeSetExtension(E, null);
		Expression fG = makeExpressionFOfG(setE,children, G, ff);
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
			Expression F, Expression G, FormulaFactory ff) {
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
