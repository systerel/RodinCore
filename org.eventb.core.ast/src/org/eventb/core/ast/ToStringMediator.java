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
package org.eventb.core.ast;

import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;

import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.AbstractGrammar;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringMediator implements IToStringMediator {

	private static final BoundIdentDecl[] NO_DECL = new BoundIdentDecl[0];
	private static final char SPACE = ' ';
	
	private final int kind;
	protected final FormulaFactory factory;
	protected final StringBuilder builder;
	protected final String[] boundNames;
	protected final boolean isRight;
	private final boolean withTypes;

	public ToStringMediator(int kind, FormulaFactory factory, StringBuilder builder,
			String[] boundNames, boolean withTypes, boolean isRight) {
		this.kind = kind;
		this.factory = factory;
		this.builder = builder;
		this.boundNames = boundNames;
		this.isRight = isRight;
		this.withTypes = withTypes;
	}

	public ToStringMediator(Formula<?> formula, FormulaFactory factory, StringBuilder builder,
			String[] boundNames, boolean withTypes, boolean isRight) {
		this(getKind(formula, factory), factory, builder, boundNames, isRight, withTypes);
	}

	public void append(String string) {
		builder.append(string);
	}

	public void appendSpace() {
		builder.append(SPACE);
	}

	public void subPrint(Formula<?> child, boolean isRightOvr) {
		subPrint(child, isRightOvr, NO_DECL);
	}

	public void subPrint(Formula<?> child, boolean isRightOvr,
			BoundIdentDecl[] boundDecls) {
		printChild(child, isRightOvr, boundDecls, withTypes);
	}

	private void printChild(Formula<?> child, boolean isRightOvr,
			BoundIdentDecl[] boundDecls, boolean withTypesOvr) {
		final int childKind = getKind(child, factory);
		final boolean needsParen = needsParentheses(childKind, isRightOvr);
		printFormula(child, childKind, isRightOvr, boundDecls, withTypesOvr, needsParen);
	}

	protected boolean needsParentheses(int childKind, boolean isRightOvr) {
		final AbstractGrammar grammar = factory.getGrammar();
		return grammar.needsParentheses(isRightOvr, childKind, kind);
	}

	private final void printFormula(Formula<?> formula, int formulaKind, boolean isRightOvr,
			BoundIdentDecl[] boundDecls, boolean withTypesOvr, boolean withParen) {
		if (withParen) {
			builder.append('(');
		}
		printFormula(formula, formulaKind, isRightOvr, boundDecls, withTypesOvr);
		if (withParen) {
			builder.append(')');
		}
	}

	public void forward(Formula<?> formula) {
		forward(formula, withTypes);
	}

	// FIXME same formula => remove argument and avoid recomputing kind
	public void forward(Formula<?> formula, boolean withTypesOvr) {
		final int formulaKind = getKind(formula, factory);
		printFormula(formula, formulaKind, isRight, NO_DECL, withTypesOvr);
	}

	private String[] addBound(BoundIdentDecl[] addedBoundNames) {
		if (addedBoundNames.length == 0) {
			return boundNames;
		}
		return catenateBoundIdentLists(boundNames, addedBoundNames);
	}

	private void printFormula(Formula<?> formula, int formulaKind, boolean isRightOvr,
			BoundIdentDecl[] boundDecls, boolean withTypesOvr) {

		final String[] newBoundNames = addBound(boundDecls);
		final IToStringMediator childMed = makeInstance(formulaKind,
				isRightOvr, withTypesOvr, newBoundNames);
		formula.toString(childMed);
	}

	protected IToStringMediator makeInstance(int formulaKind,
			boolean isRightOvr, boolean withTypesOvr,
			final String[] newBoundNames) {
		return new ToStringMediator(formulaKind, factory, builder,
				newBoundNames, withTypesOvr, isRightOvr);
	}

	public FormulaFactory getFactory() {
		return factory;
	}

	public void appendImage(int operatorKind) {
		// TODO make a cache or compute image of this.kind and check if ==
		final String image = factory.getGrammar().getImage(operatorKind);
		builder.append(image);
	}

	public void appendBoundIdent(int boundIndex) {
		String image = resolveIndex(boundIndex, boundNames);
		if (image == null) {
			// Fallback default in case this can not be resolved.
			builder.append("[[");
			builder.append(boundIndex);
			builder.append("]]");
		} else {
			builder.append(image);
		}
	}

	private static String resolveIndex(int index, String[] boundIdents) {
		if (index < boundIdents.length) {
			return boundIdents[boundIdents.length - index - 1];
		}
		return null;
	}

	public boolean isWithTypes() {
		return withTypes;
	}

	private static int getKind(Formula<?> child, FormulaFactory factory) {
		// TODO could make kind mediator a field
		final KindMediator mediator = new KindMediator(factory.getGrammar());
		return child.getKind(mediator);
	}

	// TODO rename method, document it must be called systematically before parser.toString()
	public int getKind() {
		return kind;
	}
}
