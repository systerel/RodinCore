/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.ast.DefaultTypeCheckingRewriter.checkReplacement;
import org.eventb.internal.core.ast.Position;

/*package*/class SingleRewriter {

	final IPosition position;
	final int[] indexes;
	int depth;
	final Formula<?> subFormula;
	final FormulaFactory factory;

	public SingleRewriter(IPosition position, Formula<?> subFormula,
			FormulaFactory factory) {

		this.position = position;
		this.indexes = ((Position) position).indexes;
		this.depth = 0;
		this.subFormula = subFormula;
		this.factory = factory;
	}

	BoundIdentDecl getBoundIdentDecl(BoundIdentDecl src) {
		if (subFormula instanceof BoundIdentDecl) {
			return checkReplacement(src, (BoundIdentDecl) subFormula);
		}
		throw new IllegalArgumentException(
				"New sub-formula should be a bound identifier declaration");
	}

	Expression getExpression(Expression src) {
		if (subFormula instanceof Expression) {
			return checkReplacement(src, (Expression) subFormula);
		}
		throw new IllegalArgumentException(
				"New sub-formula should be an expression");
	}

	Predicate getPredicate(Predicate src) {
		if (subFormula instanceof Predicate) {
			return checkReplacement(src, (Predicate) subFormula);
		}
		throw new IllegalArgumentException(
				"New sub-formula should be a predicate");
	}

	public <T extends Formula<T>> T rewrite(Formula<T> formula) {
		if (depth == indexes.length) {
			return formula.getCheckedReplacement(this);
		}
		int index = indexes[depth++];
		return formula.rewriteChild(index, this);
	}

}
