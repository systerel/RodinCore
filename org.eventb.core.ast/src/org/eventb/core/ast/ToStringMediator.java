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

import static org.eventb.core.ast.Formula.EMPTYSET;
import static org.eventb.core.ast.Formula.KID_GEN;
import static org.eventb.core.ast.Formula.KPRJ1_GEN;
import static org.eventb.core.ast.Formula.KPRJ2_GEN;
import static org.eventb.core.ast.QuantifiedUtil.catenateBoundIdentLists;

import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.SubParsers;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringMediator implements IToStringMediator {

	private static final char SPACE = ' ';
	
	private static final String[] NO_NAME = new String[0];

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
		this(getKind(formula, factory), factory, builder, boundNames, withTypes, isRight);
	}

	public void append(String string) {
		builder.append(string);
	}

	public void subPrint(Formula<?> child, boolean isRightOvr) {
		subPrint(child, isRightOvr, NO_NAME);
	}

	public void subPrint(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames) {
		printChild(child, isRightOvr, addedBoundNames, withTypes);
	}

	public void subPrintNoPar(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames ) {
		subPrintNoPar(child, isRightOvr, addedBoundNames, withTypes);
	}

	public void subPrint(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		printChild(child, isRightOvr, addedBoundNames, withTypesOvr);
	}

	private void subPrintNoPar(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		final int childKind = getKind(child, factory);
		printFormula(child, childKind, isRightOvr, addedBoundNames, withTypesOvr, false);
	}

	private void printChild(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		final int childKind = getKind(child, factory);
		final boolean needsParen;
		if (withTypesOvr && isTypePrintable(child)) {
			needsParen = true;
		} else {
			needsParen = needsParentheses(childKind, isRightOvr);
		}
		printFormula(child, childKind, isRightOvr, addedBoundNames, withTypesOvr, needsParen);
	}

	protected boolean needsParentheses(int childKind, boolean isRightOvr) {
		final AbstractGrammar grammar = factory.getGrammar();
		return grammar.needsParentheses(isRightOvr, childKind, kind);
	}

	private final void printFormula(Formula<?> formula, int formulaKind, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr, boolean withParen) {
		if (withParen) {
			builder.append('(');
		}
		printFormula(formula, formulaKind, isRightOvr, addedBoundNames, withTypesOvr);
		if (withParen) {
			builder.append(')');
		}
	}

	// FIXME same formula => remove argument and avoid recomputing kind
	public void forward(Formula<?> formula) {
		final int formulaKind = getKind(formula, factory);
		printFormula(formula, formulaKind, isRight, NO_NAME, withTypes);
	}

	private String[] addBound(String[] addedBoundNames) {
		if (addedBoundNames.length == 0) {
			return boundNames;
		}
		return catenateBoundIdentLists(boundNames, addedBoundNames);
	}
	
	public String[] getBoundNames() {
		return boundNames.clone();
	}

	private void printFormula(Formula<?> formula, int formulaKind, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		final String[] newBoundNames = addBound(addedBoundNames);
		printWithBinding(formula, formulaKind, isRightOvr, withTypesOvr,
				newBoundNames);
	}

	private void printWithBinding(Formula<?> formula, int formulaKind,
			boolean isRightOvr, boolean withTypesOvr,
			final String[] newBoundNames) {
		if (withTypesOvr && isTypePrintable(formula)) {
			final IToStringMediator mediator = makeInstance(BMath._TYPING,
					isRightOvr, withTypesOvr, newBoundNames);
			SubParsers.OFTYPE.toString(mediator, (Expression) formula);
			return;
		}
		final IToStringMediator mediator = makeInstance(formulaKind,
				isRightOvr, withTypesOvr, newBoundNames);
		formula.toString(mediator);
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

	public void appendImage(int lexKind) {
		final AbstractGrammar grammar = factory.getGrammar();
		final boolean spaced = grammar.isOperator(lexKind)
				&& grammar.isSpaced(lexKind);
		appendImage(lexKind, spaced);
	}
	
	private void appendImage(int lexKind, boolean withSpaces) {
		// TODO make a cache or compute image of this.kind and check if ==
		final AbstractGrammar grammar = factory.getGrammar();
		final String image = grammar.getImage(lexKind);
		if (withSpaces) {
			builder.append(SPACE);
		}
		builder.append(image);
		if (withSpaces) {
			builder.append(SPACE);
		}
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
	
	// FIXME hard coded tags
	// TODO implement a 'printWithType' option for extensions as well
	private static boolean isTypePrintable(Formula<?> toPrint) {
		switch (toPrint.getTag()) {
		case EMPTYSET:
		case KID_GEN:
		case KPRJ1_GEN:
		case KPRJ2_GEN:
			return toPrint.isTypeChecked();
		default:
			return false;
		}
	}

}
