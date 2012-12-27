/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
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
 * @author Laurent Voisin
 */
public class TermBuilder {

	private final AbstractContext context;

	private final Vector<VariableSignature> boundVars = new Vector<VariableSignature>();

	public TermBuilder(AbstractContext context) {
		assert context != null;
		this.context = context;
	}

	public TermSignature buildTerm(Expression expr) {
		return process(expr);
	}

	private TermSignature process(Expression expr) {
		if (expr instanceof BinaryExpression) {
			return processBinaryExpression((BinaryExpression) expr);
		}
		if (expr instanceof AssociativeExpression) {
			return processAssociativeExpression((AssociativeExpression) expr);
		}
		if (expr instanceof UnaryExpression) {
			return processUnaryExpression((UnaryExpression) expr);
		}
		if (expr instanceof AtomicExpression) {
			return processAtomicExpression((AtomicExpression) expr);
		}
		if (expr instanceof BoundIdentifier) {
			return processBoundIdentifier((BoundIdentifier) expr);
		}
		if (expr instanceof FreeIdentifier) {
			return processFreeIdentifier((FreeIdentifier) expr);
		}
		if (expr instanceof IntegerLiteral) {
			return processIntegerLiteral((IntegerLiteral) expr);
		}
		throw invalidTerm(expr);
	}

	private RuntimeException invalidTerm(Expression expr) {
		return new IllegalArgumentException("Invalid term: " + expr);
	}

	public TermSignature processBinaryExpression(BinaryExpression expr) {
		final TermSignature left = process(expr.getLeft());
		final TermSignature right = process(expr.getRight());
		switch (expr.getTag()) {
		case Expression.DIV:
			return new DivideSignature(left, right);
		case Expression.EXPN:
			return new ExpnSignature(left, right);
		case Expression.MINUS:
			return new MinusSignature(left, right);
		case Expression.MOD:
			return new ModSignature(left, right);
		default:
			throw invalidTerm(expr);
		}
	}

	public TermSignature processAssociativeExpression(AssociativeExpression expr) {
		final List<TermSignature> children = new ArrayList<TermSignature>();
		for (Expression child : expr.getChildren()) {
			children.add(process(child));
		}
		switch (expr.getTag()) {
		case Expression.MUL:
			return new TimesSignature(children);
		case Expression.PLUS:
			return new PlusSignature(children);
		default:
			throw invalidTerm(expr);
		}
	}

	private TermSignature processUnaryExpression(UnaryExpression expr) {
		final TermSignature child = process(expr.getChild());
		switch (expr.getTag()) {
		case Expression.UNMINUS:
			return new UnaryMinusSignature(child);
		default:
			throw invalidTerm(expr);
		}
	}

	private TermSignature processAtomicExpression(AtomicExpression expr) {
		final Sort sort = new Sort(expr.getType());
		assert sort.equals(Sort.BOOLEAN);
		switch (expr.getTag()) {
		case Expression.TRUE:
			return new TrueConstantSignature(sort);
		default:
			throw invalidTerm(expr);
		}
	}

	private TermSignature processBoundIdentifier(BoundIdentifier ident) {
		switch (ident.getTag()) {
		case Expression.BOUND_IDENT:
			return getVariableSignature(ident.getBoundIndex());
		default:
			throw invalidTerm(ident);
		}
	}

	private TermSignature processFreeIdentifier(FreeIdentifier ident) {
		final Sort sort = new Sort(ident.getType());
		switch (ident.getTag()) {
		case Expression.FREE_IDENT:
			return new ConstantSignature(ident.getName(), sort);
		default:
			throw invalidTerm(ident);
		}
	}

	private TermSignature processIntegerLiteral(IntegerLiteral lit) {
		final Sort sort = new Sort(lit.getType());
		assert sort.equals(Sort.NATURAL);
		switch (lit.getTag()) {
		case Expression.INTLIT:
			return new IntegerSignature(lit.getValue());
		default:
			throw invalidTerm(lit);
		}
	}

	public void pushDecls(BoundIdentDecl[] decls) {
		int revIndex = boundVars.size(); // induction variable for next loop
		for (BoundIdentDecl decl : decls) {
			final Sort sort = new Sort(decl.getType());
			final int varIndex = context.getFreshVariableIndex();
			boundVars.add(new VariableSignature(varIndex, revIndex++, sort));
		}
	}

	public void popDecls(BoundIdentDecl[] decls) {
		boundVars.setSize(boundVars.size() - decls.length);
	}

	public int getNumberOfDecls() {
		return boundVars.size();
	}

	private VariableSignature getVariableSignature(int boundIndex) {
		final int length = boundVars.size();
		assert 0 <= boundIndex && boundIndex < length;
		return boundVars.get(length - boundIndex - 1);
	}

}
