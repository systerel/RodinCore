/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
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
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.OFTYPE;

import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.SubParsers;

/**
 * @author Nicolas Beauger
 */
/* package */class ToStringMediator implements IToStringMediator {

	private static final char SPACE = ' ';
	
	private static final String[] NO_NAME = new String[0];

	private final int kind;
	protected final AbstractGrammar grammar;
	protected final StringBuilder builder;
	protected final String[] boundNames;
	protected final boolean isRight;
	private final boolean withTypes;
	protected final KindMediator kindMed;

	protected ToStringMediator(int kind, AbstractGrammar grammar,
			StringBuilder builder, String[] boundNames, boolean isRight,
			boolean withTypes, KindMediator kindMed) {
		this.kind = kind;
		this.grammar = grammar;
		this.builder = builder;
		this.boundNames = boundNames;
		this.isRight = isRight;
		this.withTypes = withTypes;
		this.kindMed = kindMed;
	}

	public ToStringMediator(Formula<?> formula, StringBuilder builder,
			String[] boundNames, boolean withTypes, boolean isRight) {
		this.grammar = formula.getFactory().getGrammar();
		this.builder = builder;
		this.boundNames = boundNames;
		this.isRight = isRight;
		this.withTypes = withTypes;

		this.kindMed = new KindMediator(grammar);
		this.kind = formula.getKind(kindMed);
	}

	@Override
	public void append(String string) {
		builder.append(string);
	}

	@Override
	public void subPrint(Formula<?> child, boolean isRightOvr) {
		subPrint(child, isRightOvr, NO_NAME);
	}

	@Override
	public void subPrint(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames) {
		printChild(child, isRightOvr, addedBoundNames, withTypes);
	}

	@Override
	public void subPrintNoPar(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames ) {
		subPrintNoPar(child, isRightOvr, addedBoundNames, withTypes);
	}

	@Override
	public void subPrint(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		printChild(child, isRightOvr, addedBoundNames, withTypesOvr);
	}

	private void subPrintNoPar(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		final int childKind = child.getKind(kindMed);
		printFormula(child, childKind, isRightOvr, addedBoundNames, withTypesOvr, false);
	}
	
	@Override
	public void subPrintWithPar(Formula<?> child) {
		final int childKind = child.getKind(kindMed);
		printFormula(child, childKind, false, NO_NAME, withTypes, true);
	}
	
	private void printChild(Formula<?> child, boolean isRightOvr,
			String[] addedBoundNames, boolean withTypesOvr) {
		final int childKind = child.getKind(kindMed);
		final boolean needsParen;
		if (withTypesOvr && isTypePrintable(child)) {
			needsParen = true;
		} else {
			needsParen = needsParentheses(childKind, isRightOvr);
		}
		printFormula(child, childKind, isRightOvr, addedBoundNames, withTypesOvr, needsParen);
	}

	protected boolean needsParentheses(int childKind, boolean isRightOvr) {
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

	@Override
	public void forward(Formula<?> formula) {
		final int formulaKind = formula.getKind(kindMed);
		printFormula(formula, formulaKind, isRight, NO_NAME, withTypes);
	}

	private String[] addBound(String[] addedBoundNames) {
		if (addedBoundNames.length == 0) {
			return boundNames;
		}
		return catenateBoundIdentLists(boundNames, addedBoundNames);
	}
	
	@Override
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
			final int oftype = grammar.getKind(OFTYPE);
			final IToStringMediator mediator = makeInstance(oftype, isRightOvr,
					withTypesOvr, newBoundNames);
			SubParsers.OFTYPE_PARSER.toString(mediator, (Expression) formula);
			return;
		}
		final IToStringMediator mediator = makeInstance(formulaKind,
				isRightOvr, withTypesOvr, newBoundNames);
		formula.toString(mediator);
	}

	protected IToStringMediator makeInstance(int formulaKind,
			boolean isRightOvr, boolean withTypesOvr,
			final String[] newBoundNames) {
		return new ToStringMediator(formulaKind, grammar, builder,
				newBoundNames, isRightOvr, withTypesOvr, kindMed);
	}

	@Override
	public void appendImage(int lexKind) {
		final boolean spaced = grammar.isOperator(lexKind)
				&& grammar.isSpaced(lexKind);
		appendImage(lexKind, spaced);
	}
	
	@Override
	public void appendImage(int lexKind, boolean withSpaces) {
		// TODO make a cache or compute image of this.kind and check if ==
		final String image = grammar.getImage(lexKind);
		if (withSpaces) {
			builder.append(SPACE);
		}
		builder.append(image);
		if (withSpaces) {
			builder.append(SPACE);
		}
	}

	@Override
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

	@Override
	public boolean isWithTypes() {
		return withTypes;
	}

	// TODO rename method, document it must be called systematically before parser.toString()
	@Override
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
		}
		if (toPrint instanceof ExtendedExpression) {
			return ((ExtendedExpression)toPrint).isAtomic();
		}
		return false;
	}

}
