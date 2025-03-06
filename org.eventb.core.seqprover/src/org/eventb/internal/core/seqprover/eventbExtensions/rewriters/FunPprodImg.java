/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.PPROD;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

/**
 * Replaces function application of a parallel product with its definition.
 *
 * @author Guillaume Verdier
 */
public class FunPprodImg extends AbstractManualRewrites {

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".funPprodImg";
	}

	@Override
	@ProverRule("SIMP_FUNIMAGE_PPROD")
	public Predicate rewrite(Predicate pred, IPosition position) {
		Formula<?> formula = pred.getSubFormula(position);
		if (formula == null || formula.getTag() != FUNIMAGE) {
			return null;
		}
		var funImg = (BinaryExpression) formula;
		if (funImg.getLeft().getTag() != PPROD) {
			return null;
		}
		var fb = new FormulaBuilder(pred.getFactory());
		var pprod = (BinaryExpression) funImg.getLeft();
		var arg = funImg.getRight();
		Expression replacement;
		if (arg.getTag() == MAPSTO) {
			var maplet = (BinaryExpression) arg;
			replacement = fb.mapsTo(//
					fb.funimg(pprod.getLeft(), maplet.getLeft()),
					fb.funimg(pprod.getRight(), maplet.getRight()));
		} else {
			replacement = fb.mapsTo(//
					fb.funimg(pprod.getLeft(), fb.funimg(fb.prj1((ProductType) arg.getType()), arg)),
					fb.funimg(pprod.getRight(), fb.funimg(fb.prj2((ProductType) arg.getType()), arg)));
		}
		return pred.rewriteSubFormula(position, replacement);
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "parallel product fun. image";
	}

}
