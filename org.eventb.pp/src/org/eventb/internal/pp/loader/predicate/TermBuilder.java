/*******************************************************************************
 * Copyright (c) 2006,2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.terms.ConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.DivideSignature;
import org.eventb.internal.pp.loader.formula.terms.ExpnSignature;
import org.eventb.internal.pp.loader.formula.terms.IntegerSignature;
import org.eventb.internal.pp.loader.formula.terms.MinusSignature;
import org.eventb.internal.pp.loader.formula.terms.ModSignature;
import org.eventb.internal.pp.loader.formula.terms.PlusSignature;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TimesSignature;
import org.eventb.internal.pp.loader.formula.terms.TrueConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.UnaryMinusSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;

/**
 * This class is the builder for terms.
 * 
 * @author François Terrier
 * 
 */
public class TermBuilder {

	private final Stack<NormalizedFormula> results;

	public TermBuilder(Stack<NormalizedFormula> results) {
		assert results != null;
		this.results = results;
	}

	public TermSignature buildTerm(Expression expr) {
		return process(expr);
	}

	public TermSignature process(Expression expr) {
		switch (expr.getTag()) {
		case Expression.DIV:
			return processDIV((BinaryExpression) expr);
		case Expression.EXPN:
			return processEXPN((BinaryExpression) expr);
		case Expression.MINUS:
			return processMINUS((BinaryExpression) expr);
		case Expression.MOD:
			return processMOD((BinaryExpression) expr);
		case Expression.MUL:
			return processMUL((AssociativeExpression) expr);
		case Expression.PLUS:
			return processPLUS((AssociativeExpression) expr);
		case Expression.UNMINUS:
			return processUNMINUS((UnaryExpression) expr);
		case Expression.BOUND_IDENT:
			return processBOUND_IDENT((BoundIdentifier) expr);
		case Expression.FREE_IDENT:
			return processFREE_IDENT((FreeIdentifier) expr);
		case Expression.INTLIT:
			return processINTLIT((IntegerLiteral) expr);
		case Expression.TRUE:
			return processTRUE((AtomicExpression) expr);
			// TODO case of predefined sets BOOL and INTEGER
		default:
			throw new IllegalArgumentException("Invalid term: " + expr);
		}
	}

	public TermSignature processDIV(BinaryExpression expr) {
		final TermSignature left = process(expr.getLeft());
		final TermSignature right = process(expr.getRight());
		return new DivideSignature(left, right);
	}

	public TermSignature processEXPN(BinaryExpression expr) {
		final TermSignature left = process(expr.getLeft());
		final TermSignature right = process(expr.getRight());
		return new ExpnSignature(left, right);
	}

	public TermSignature processMINUS(BinaryExpression expr) {
		final TermSignature left = process(expr.getLeft());
		final TermSignature right = process(expr.getRight());
		return new MinusSignature(left, right);
	}

	public TermSignature processMOD(BinaryExpression expr) {
		final TermSignature left = process(expr.getLeft());
		final TermSignature right = process(expr.getRight());
		return new ModSignature(left, right);
	}

	public TermSignature processMUL(AssociativeExpression expr) {
		final List<TermSignature> children = new ArrayList<TermSignature>();
		for (Expression child : expr.getChildren()) {
			children.add(process(child));
		}
		return new TimesSignature(children);
	}

	public TermSignature processPLUS(AssociativeExpression expr) {
		final List<TermSignature> children = new ArrayList<TermSignature>();
		for (Expression child : expr.getChildren()) {
			children.add(process(child));
		}
		return new PlusSignature(children);
	}

	public TermSignature processUNMINUS(UnaryExpression expr) {
		final TermSignature child = process(expr.getChild());
		return new UnaryMinusSignature(child);
	}

	public TermSignature processBOUND_IDENT(BoundIdentifier ident) {
		final int index = getIndex(ident.getBoundIndex());
		return new VariableSignature(getStartIndex(ident.getBoundIndex()),
				index, new Sort(ident.getType()));
	}

	public TermSignature processFREE_IDENT(FreeIdentifier ident) {
		return new ConstantSignature(ident.getName(), new Sort(ident.getType()));
	}

	public TermSignature processINTLIT(IntegerLiteral lit) {
		assert new Sort(lit.getType()).equals(Sort.NATURAL);
		return new IntegerSignature(lit.getValue());
	}

	public TermSignature processTRUE(AtomicExpression expr) {
		return new TrueConstantSignature(new Sort(expr.getType()));
	}

	private int getIndex(int boundIndex) {
		int tmp = 0;
		int i = results.size() - 1;

		while (i > 0) {
			tmp = tmp + results.get(i).getBoundIdentDecls().length;
			i--;
		}
		return tmp - 1 - boundIndex;
	}

	private int getStartIndex(int boundIndex) {
		int tmp = boundIndex;
		int i = results.size() - 1;

		while (tmp >= results.get(i).getBoundIdentDecls().length) {
			tmp = tmp - results.get(i).getBoundIdentDecls().length;
			i--;
		}
		return (results.get(i).getBoundIdentDecls().length - tmp - 1)
				+ results.get(i).getStartAbsolute();
	}
}
