/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

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
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


/**
 * Implements Bound Identifier Decomposition.
 * The method decomposeBoundIdentifiers decomposes all bound identifiers of a
 * predicate, such that after decomposition there is no bound identifier of a
 * cartesian product type.
 * <p>
 * Example:		∀x·x=1↦2 ⇒ x∈S	becomes: ∀x0,x1·x0↦x1=1↦2 ⇒ x0↦x1∈S
 * </p>
 * 			
 * @author Matthias Konrad
 */
@SuppressWarnings({"unused", "cast"})
public class BoundIdentifierDecomposition extends IdentityTranslator {

	private static class Substitute {
		final Expression expr;
		final int boundIndex;
		
		Substitute(Expression expr, int boundIndex) {
			this.expr = expr;
			this.boundIndex = boundIndex;
		}

	}

	/**
	 * Implements Bound Identifier Decomposition.
	 * <p>
 	 * Example:	∀x·x=1↦2 ⇒ x∈S	becomes: ∀x0,x1·x0↦x1=1↦2 ⇒ x0↦x1∈S
 	 * </p>	
 	 * @param pred the predicate to decomposed
	 * @param ff formula factory used during the decomposition
	 * @return a new predicate, which is the decomposed version of the given
	 * predicate
	 */
	public static Predicate decomposeBoundIdentifiers(Predicate pred,
			FormulaFactory ff) {
		return new BoundIdentifierDecomposition(ff).translate(pred);
	}
	
	%include {FormulaV2.tom}
	
	private final List<Substitute> mapletOffsets;
	private int count;
	
	private BoundIdentifierDecomposition(FormulaFactory ff) {
		super(ff);
		mapletOffsets = new LinkedList<Substitute>();
		count = 0;
	}
	
	private BoundIdentifierDecomposition(FormulaFactory ff, List<Substitute>
			mapletOffsets, int count) {
		super(ff);
		this.mapletOffsets = new LinkedList<Substitute>(mapletOffsets);
		this.count = count;
	}
	
	private Expression translateQExpr(int tag, BoundIdentDecl[] is,
			Predicate pred, Expression expr, Expression parent) {
		final BoundIdentifierDecomposition ic =
				new BoundIdentifierDecomposition(ff, mapletOffsets, count);
		final DecomposedQuant quant = new DecomposedQuant(ff);
		Collections.reverse(Arrays.asList(is));
		final List<Expression> quantifiers = new LinkedList<Expression>();
		for (BoundIdentDecl decl: is) {
			quantifiers.add(0,  
				quant.addQuantifier(decl.getType(), decl.getName(), decl.getSourceLocation()));
		}
		for (Expression quantifier: quantifiers) {
			ic.mapletOffsets.add(0, 
				new Substitute(quantifier, ic.count + quant.offset()));
		} 	
		ic.count += quant.offset();
		final SourceLocation loc = parent.getSourceLocation();
		return ifChanged(parent, quant.makeQuantifiedExpression(
				tag,
				ic.translate(pred),
				ic.translate(expr),
				loc));
	}
	
	@Override
	protected Expression translate(Expression expr){
		SourceLocation loc = expr.getSourceLocation();		
		%match (Expression expr) {
			Cset(is, P, E) -> {
				return translateQExpr(CSET, `is, `P, `E, expr);
			}
			Qunion(is, P, E) -> {
				return translateQExpr(QUNION, `is, `P, `E, expr);
			}
			Qinter(is, P, E) -> {
				return translateQExpr(QINTER, `is, `P, `E, expr);
			}
			BoundIdentifier(idx) -> {
				Substitute p = mapletOffsets.get(`idx);
				return p.expr.shiftBoundIdentifiers(count - p.boundIndex);
			}
		}
		return super.translate(expr);
	}
	
	private Predicate translateQPred(int tag, BoundIdentDecl[] is,
			Predicate pred, Predicate parent) {
		BoundIdentifierDecomposition ic = 
				new BoundIdentifierDecomposition(ff, mapletOffsets, count);
		DecomposedQuant quant = new DecomposedQuant(ff);
		Collections.reverse(Arrays.asList(is));
		List<Expression> quantifiers = new LinkedList<Expression>();
		for (BoundIdentDecl decl: is) {
			quantifiers.add(0,  
				quant.addQuantifier(decl.getType(), decl.getName(), decl.getSourceLocation()));
		}
		for (Expression quantifier: quantifiers) {
			ic.mapletOffsets.add(0,
				new Substitute(quantifier, ic.count + quant.offset()));
		} 	
		ic.count += quant.offset();
		final SourceLocation loc = parent.getSourceLocation();
		return ifChanged(parent,
				quant.makeQuantifiedPredicate(tag, ic.translate(pred), loc));
	}

	@Override
	protected Predicate translate(Predicate pred) {
		%match (Predicate pred) {
			ForAll(is, P) -> {
				return translateQPred(FORALL, `is, `P, pred);
			}
			Exists(is, P) -> {
				return translateQPred(EXISTS, `is, `P, pred);
			}
		}
		return super.translate(pred);
	}
}
