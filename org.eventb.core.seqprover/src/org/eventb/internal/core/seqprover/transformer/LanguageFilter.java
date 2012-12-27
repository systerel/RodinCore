/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import java.util.BitSet;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.transformer.ISequentTranslator;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * Implements sequent filtering to a restricted language.
 * 
 * As concerns parametric type filtering, we do not test every expression of the
 * sequent, but only the expressions that can introduce new types, that is:
 * <ul>
 * <li>free identifiers,</li>
 * <li>bound identifier declarations,</li>
 * <li>generic atomic expressions, such as empty set,</li>
 * <li>empty set extensions (a variation on the preceding one).</li>
 * </ul>
 * 
 * @author Laurent Voisin
 */
public class LanguageFilter implements ISequentTranslator,
		IFormulaInspector<Object> {

	private static final Predicate[] NO_AXIOMS = new Predicate[0];

	private final BitSet toFilterOut;

	// Set to true by the visitor when encountering an operator to filter
	private boolean found;

	public LanguageFilter(int[] tags) {
		toFilterOut = new BitSet();
		for (int tag : tags) {
			toFilterOut.set(tag);
		}
	}

	@Override
	public FormulaFactory getTargetFormulaFactory() {
		return FormulaFactory.getDefault();
	}

	@Override
	public Predicate[] getAxioms() {
		return NO_AXIOMS;
	}

	@Override
	public Predicate transform(ITrackedPredicate tpred) {
		final Predicate pred = tpred.getPredicate();
		if (hasParamTypeFreeIdent(pred) || containsOperatorToFilter(pred)) {
			return null;
		}
		return pred;
	}

	private boolean hasParamTypeFreeIdent(Predicate pred) {
		for (final FreeIdentifier ident : pred.getFreeIdentifiers()) {
			if (bearsParamType(ident)) {
				return true;
			}
		}
		return false;
	}

	private boolean bearsParamType(Expression expr) {
		return containsParamType(expr.getType());
	}

	private boolean containsParamType(Type type) {
		if (type instanceof PowerSetType) {
			return containsParamType(type.getBaseType());
		}
		if (type instanceof ProductType) {
			final ProductType ptype = (ProductType) type;
			return containsParamType(ptype.getLeft())
					|| containsParamType(ptype.getRight());
		}
		if (type instanceof ParametricType) {
			return true;
		}
		return false;
	}

	private boolean containsOperatorToFilter(Predicate pred) {
		found = false;
		pred.inspect(this);
		return found;
	}

	private void doInspect(Formula<?> formula, IAccumulator<Object> acc) {
		final int tag = formula.getTag();
		if (toFilterOut.get(tag)) {
			setFound(acc);
		}
	}

	public void setFound(IAccumulator<Object> acc) {
		found = true;
		acc.skipAll();
	}

	@Override
	public void inspect(AssociativeExpression expr, IAccumulator<Object> acc) {
		doInspect(expr, acc);
	}

	@Override
	public void inspect(AssociativePredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(AtomicExpression expr, IAccumulator<Object> acc) {
		if (bearsParamType(expr)) {
			setFound(acc);
			return;
		}
		doInspect(expr, acc);
	}

	@Override
	public void inspect(BinaryExpression expr, IAccumulator<Object> acc) {
		doInspect(expr, acc);
	}

	@Override
	public void inspect(BinaryPredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(BoolExpression expr, IAccumulator<Object> acc) {
		doInspect(expr, acc);
	}

	@Override
	public void inspect(BoundIdentDecl decl, IAccumulator<Object> acc) {
		if (containsParamType(decl.getType())) {
			setFound(acc);
			return;
		}
		doInspect(decl, acc);
	}

	@Override
	public void inspect(BoundIdentifier identifier, IAccumulator<Object> acc) {
		doInspect(identifier, acc);
	}

	@Override
	public void inspect(ExtendedExpression expr, IAccumulator<Object> acc) {
		setFound(acc);
	}

	@Override
	public void inspect(ExtendedPredicate pred, IAccumulator<Object> acc) {
		setFound(acc);
	}

	@Override
	public void inspect(FreeIdentifier identifier, IAccumulator<Object> acc) {
		doInspect(identifier, acc);
	}

	@Override
	public void inspect(IntegerLiteral literal, IAccumulator<Object> acc) {
		doInspect(literal, acc);
	}

	@Override
	public void inspect(LiteralPredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(MultiplePredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(PredicateVariable pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(QuantifiedExpression expr, IAccumulator<Object> acc) {
		doInspect(expr, acc);
	}

	@Override
	public void inspect(QuantifiedPredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(RelationalPredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(SetExtension expr, IAccumulator<Object> acc) {
		if (bearsParamType(expr)) {
			setFound(acc);
			return;
		}
		doInspect(expr, acc);
	}

	@Override
	public void inspect(SimplePredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

	@Override
	public void inspect(UnaryExpression expr, IAccumulator<Object> acc) {
		doInspect(expr, acc);
	}

	@Override
	public void inspect(UnaryPredicate pred, IAccumulator<Object> acc) {
		doInspect(pred, acc);
	}

}
