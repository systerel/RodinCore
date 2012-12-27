/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.upgrade;

import static org.eventb.core.ast.FormulaFactory.isEventBWhiteSpace;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;

/**
 * @author Nicolas Beauger
 * 
 */
public class VersionUpgraderV1V2 extends VersionUpgrader {

	private static class RewriterV1V2 extends DefaultRewriter {

		private final List<String> reservedNames;

		public RewriterV1V2(FormulaFactory factory, List<String> reservedNames) {
			super(false, factory);
			this.reservedNames = reservedNames;
		}

		@SuppressWarnings("deprecation")
		private static final int getGenericTag(int tag) {
			switch (tag) {
			case Formula.KPRJ1:
				return Formula.KPRJ1_GEN;
			case Formula.KPRJ2:
				return Formula.KPRJ2_GEN;
			case Formula.KID:
				return Formula.KID_GEN;
			default:
				return -1;
			}
		}

		private boolean updateReservedKeywords(
				final BoundIdentDecl[] boundIdentDecls) {
			boolean changed = false;
			for (int index = 0; index < boundIdentDecls.length; index++) {
				final BoundIdentDecl decl = boundIdentDecls[index];
				final String name = decl.getName();
				if (reservedNames.contains(name)) {
					boundIdentDecls[index] = ff.makeBoundIdentDecl(name + "1",
							null, decl.getType());
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public Expression rewrite(QuantifiedExpression expression) {
			final BoundIdentDecl[] boundIdentDecls = expression
					.getBoundIdentDecls();
			boolean changed = updateReservedKeywords(boundIdentDecls);
			if (changed) {
				return ff.makeQuantifiedExpression(expression.getTag(),
						boundIdentDecls, expression.getPredicate(), expression
								.getExpression(), null, expression.getForm());
			}
			return expression;
		}

		@Override
		public Predicate rewrite(QuantifiedPredicate predicate) {
			final BoundIdentDecl[] boundIdentDecls = predicate
					.getBoundIdentDecls();
			final boolean changed = updateReservedKeywords(boundIdentDecls);
			if (changed) {
				return ff.makeQuantifiedPredicate(predicate.getTag(),
						boundIdentDecls, predicate.getPredicate(), null);
			}
			return predicate;
		}

		@Override
		public Expression rewrite(UnaryExpression expression) {
			final int tag = expression.getTag();
			final int genericTag = getGenericTag(tag);
			if (genericTag < 0) {
				return expression;
			}
			final Type type = expression.getType();
			final Expression child = expression.getChild();
			if (child.isATypeExpression()) {
				return ff.makeAtomicExpression(genericTag, null, type);
			} else {
				return makeDomRes(child, genericTag, type);
			}
		}

		private Expression makeDomRes(Expression left, int genTag, Type type) {
			return ff.makeBinaryExpression(Formula.DOMRES, left, ff
					.makeAtomicExpression(genTag, null, type), null);
		}

	}

	private static class UpgradeVisitorV1V2<T extends Formula<T>> extends
			DefaultVisitor {
		private final String formula;
		private final UpgradeResult<T> result;
		private final List<String> reservedNames;

		public UpgradeVisitorV1V2(String formula, UpgradeResult<T> result,
				List<String> reservedNames) {
			this.formula = formula;
			this.result = result;
			this.reservedNames = reservedNames;
		}

		@Override
		public boolean enterKID(UnaryExpression expr) {
			result.setUpgradeNeeded(true);
			return false;
		}

		@Override
		public boolean enterKPRJ1(UnaryExpression expr) {
			result.setUpgradeNeeded(true);
			return false;
		}

		@Override
		public boolean enterKPRJ2(UnaryExpression expr) {
			result.setUpgradeNeeded(true);
			return false;
		}

		private boolean lacksParentheses(Expression expression) {
			final SourceLocation srcLoc = expression.getSourceLocation();
			return !findLeftParen(formula, srcLoc.getStart() - 1)
					|| !findRightParen(formula, srcLoc.getEnd() + 1);
		}

		private static boolean findLeftParen(String form, int maxIndex) {
			for (int i = maxIndex; i >= 0; i--) {
				final char ch = form.charAt(i);
				if (!isEventBWhiteSpace(ch)) {
					return ch == '(';
				}
			}
			return false;
		}

		private static boolean findRightParen(String form, int minIndex) {
			for (int i = minIndex; i < form.length(); i++) {
				final char ch = form.charAt(i);
				if (!isEventBWhiteSpace(ch)) {
					return ch == ')';
				}
			}
			return false;
		}

		private boolean processRelSetExpr(BinaryExpression expr) {
			final Expression left = expr.getLeft();
			if (isRelationalSet(left)) {
				result.setUpgradeNeeded(lacksParentheses(left));
			}
			return !result.upgradeNeeded();
		}

		private boolean isRelationalSet(Expression expr) {
			switch (expr.getTag()) {
			case Formula.REL:
			case Formula.TREL:
			case Formula.SREL:
			case Formula.STREL:
			case Formula.PFUN:
			case Formula.TFUN:
			case Formula.PINJ:
			case Formula.TINJ:
			case Formula.PSUR:
			case Formula.TSUR:
			case Formula.TBIJ:
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean enterREL(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterTREL(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterSREL(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterSTREL(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterPFUN(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterTFUN(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterPINJ(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterTINJ(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterPSUR(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterTSUR(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean enterTBIJ(BinaryExpression expr) {
			return processRelSetExpr(expr);
		}

		@Override
		public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
			if (reservedNames.contains(ident.getName())) {
				result.setUpgradeNeeded(true);
			}
			return true;
		}

		@Override
		public boolean visitFREE_IDENT(FreeIdentifier ident) {
			if (reservedNames.contains(ident.getName())) {
				result.setUpgradeNeeded(true);
				result
						.addProblem(new ASTProblem(ident.getSourceLocation(),
								ProblemKind.NotUpgradableError,
								ProblemSeverities.Error));
				return false;
			}
			return true;
		}
	}

	public VersionUpgraderV1V2(FormulaFactory ff) {
		super(LanguageVersion.V1);
	}

	@Override
	protected <T extends Formula<T>> void checkUpgrade(String formulaString,
			Formula<T> formula, UpgradeResult<T> result) {
		final UpgradeVisitorV1V2<T> upgradeVisitor = new UpgradeVisitorV1V2<T>(
				formulaString, result, getReservedKeywords());
		formula.accept(upgradeVisitor);
	}

	@Override
	protected <T extends Formula<T>> void upgrade(T formula,
			UpgradeResult<T> result) {
		final IFormulaRewriter rewriter = new RewriterV1V2(result.getFactory(),
				getReservedKeywords());
		try {
			final T rewritten = formula.rewrite(rewriter);
			result.setUpgradedFormula(rewritten);
		} catch (Exception e) {
			if (VersionUpgrader.DEBUG) {
				System.out.println("Exception while rewriting formula "
						+ formula);
				e.printStackTrace();
			}
		}
	}

	private static final List<String> RESERVED_NAMES = Arrays
			.asList("partition");

	@Override
	protected List<String> getReservedKeywords() {
		return RESERVED_NAMES;
	}

}
