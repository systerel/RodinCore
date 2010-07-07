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
import org.eventb.internal.core.parser.AbstractGrammar;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringMediator implements IToStringMediator {

	private static final BoundIdentDecl[] NO_DECL = new BoundIdentDecl[0];

	protected final FormulaFactory factory;
	protected final int tag;
	protected final StringBuilder builder;
	protected final String[] boundNames;
	protected final boolean isRight;
	private final boolean withTypes;

	public ToStringMediator(FormulaFactory factory, StringBuilder builder,
			String[] boundNames, int tag, boolean withTypes, boolean isRight) {
		this.factory = factory;
		this.tag = tag;
		this.builder = builder;
		this.boundNames = boundNames;
		this.isRight = isRight;
		this.withTypes = withTypes;
	}

	public void append(String string) {
		builder.append(string);
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
		final boolean needsParen = needsParentheses(child, isRightOvr);
		printFormula(child, isRightOvr, boundDecls, withTypesOvr, needsParen);
	}

	protected boolean needsParentheses(Formula<?> child, boolean isRightOvr) {
		// FIXME can only print with latest language version
		final AbstractGrammar grammar = factory.getGrammar();
		return grammar.needsParentheses(isRightOvr, child.getTag(), tag,
				LanguageVersion.LATEST); // FIXME kind
	}

	protected final void printFormula(
			Formula<?> formula, boolean isRightOvr, BoundIdentDecl[] boundDecls,
			boolean withTypesOvr, boolean withParen) {
		if (withParen) {
			builder.append('(');
		}
		printFormula(formula, isRightOvr, boundDecls, withTypesOvr);
		if (withParen) {
			builder.append(')');
		}
	}

	public void forward(Formula<?> formula) {
		forward(formula, withTypes);
	}

	public void forward(Formula<?> formula, boolean withTypesOvr) {
		printFormula(formula, isRight, NO_DECL, withTypesOvr);
	}

	private String[] addBound(BoundIdentDecl[] addedBoundNames) {
		if (addedBoundNames.length == 0) {
			return boundNames;
		}
		return catenateBoundIdentLists(boundNames, addedBoundNames);
	}

	private void printFormula(Formula<?> formula,
			boolean isRightOvr, BoundIdentDecl[] boundDecls,
			boolean withTypesOvr) {

		final String[] newBoundNames = addBound(boundDecls);
		final IToStringMediator childMed = makeInstance(formula, isRightOvr,
				withTypesOvr, newBoundNames);
		formula.toString(childMed);
	}

	protected IToStringMediator makeInstance(Formula<?> child,
			boolean isRightOvr, boolean withTypesOvr,
			final String[] newBoundNames) {
		return new ToStringMediator(factory, builder, newBoundNames, child
				.getTag(), withTypesOvr, isRightOvr);
	}

	public FormulaFactory getFactory() {
		return factory;
	}

	public void appendOperator() {
		final String opImage = factory.getGrammar().getImage(tag); // FIXME kind
		builder.append(opImage);
	}

	public void appendImage(int kind) {
		final String image = factory.getGrammar().getImage(kind);
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

	public int getKind(String operator) {
		return factory.getGrammar().getKind(operator);
	}
}
