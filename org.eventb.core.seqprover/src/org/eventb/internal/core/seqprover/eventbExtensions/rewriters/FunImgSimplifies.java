/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunImgSimpImpl.getNeededHyp;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

@ProverRule( { "SIMP_FUNIMAGE_DOMRES", "SIMP_FUNIMAGE_DOMSUB",
		"SIMP_FUNIMAGE_RANRES", "SIMP_FUNIMAGE_RANSUB",
		"SIMP_FUNIMAGE_SETMINUS" })
public class FunImgSimplifies extends AbstractManualRewrites implements
		IVersionedReasoner {

	private static final int VERSION = 0;

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".funImgSimplifies";

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	public int getVersion() {
		return VERSION;
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public Set<Predicate> getNeededHyps(IProverSequent seq, Predicate pred,
			IPosition position) {
		final Expression fExpr = getFunction(pred, position);
		if (fExpr == null) {
			return null;
		}
		final Predicate neededHyp = getNeededHyp(fExpr, seq);
		if (neededHyp == null){
			return null;
		}
		return Collections.singleton(neededHyp);
	}

	/*
	 * retrieve F out of (E op F)(G) where op ∈ {◁, ⩤, ▷, ⩥} 
	 * or F out of (F ∖ E)(G)
	 */
	private Expression getFunction(Predicate pred, IPosition position) {
		if (position == null) {
			return null;
		}
		final Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Expression.FUNIMAGE) {
			return null;
		}
		final BinaryExpression funImageExpr = (BinaryExpression) subFormula;
		final Expression left = funImageExpr.getLeft();
		final int tag = left.getTag();
		return extractRestrictedFunction(left, tag);
	}

	private Expression extractRestrictedFunction(final Expression fun,
			final int tag) {
		if (tag == Expression.DOMRES || tag == Expression.DOMSUB) {
			return ((BinaryExpression) fun).getRight();
		}
		if (tag == Expression.RANRES || tag == Expression.RANSUB
		|| tag == Expression.SETMINUS) {
			return ((BinaryExpression) fun).getLeft();
		}
		return null;
	}

	@Override
	protected Predicate rewrite(Predicate pred, IPosition position) {
		final Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Expression.FUNIMAGE) {
			return null;
		}
		final BinaryExpression funImage = (BinaryExpression) subFormula;
		final Expression G = funImage.getRight();
		final Expression replacement = ff.makeBinaryExpression(
				Expression.FUNIMAGE, getFunction(pred, position), G, null);
		return pred.rewriteSubFormula(position, replacement, ff);
	}

	@Override
	protected String getDisplayName(Predicate hyp, IPosition position) {
		if (hyp == null)
			return "Functional image simplification in goal";
		return "Functional image simplification in hyp ("
				+ hyp.getSubFormula(position) + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

}
