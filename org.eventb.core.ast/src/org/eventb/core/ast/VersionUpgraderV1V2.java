/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.FormulaFactory.isEventBWhiteSpace;
import static org.eventb.core.ast.ProblemKind.NotUpgradableError;
import static org.eventb.core.ast.ProblemSeverities.Error;

import java.util.Arrays;
import java.util.List;

import org.eventb.internal.core.ast.DefaultTypeCheckingRewriter;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.upgrade.UpgradeResult;
import org.eventb.internal.core.upgrade.VersionUpgrader;

/**
 * Upgrades a formula from language V1 to language V2.
 * 
 * The check whether upgrade is needed and can be done, is performed with the
 * {@link UpgradeVisitorV1V2}.
 * 
 * The upgrade is performed by rewriting the formula with {@link RewriterV1V2}.
 * 
 * @author Nicolas Beauger
 */
class VersionUpgraderV1V2 extends VersionUpgrader {

	private static class RewriterV1V2 extends DefaultTypeCheckingRewriter {

		private final List<String> reservedNames;

		public RewriterV1V2(FormulaFactory factory, List<String> reservedNames) {
			super(factory);
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

		private String getNotReservedName(String name) {
			if (!reservedNames.contains(name)) {
				return name;
			}
			final String newName = name + "1";
			assert !reservedNames.contains(newName);
			return newName;
		}

		@Override
		public BoundIdentDecl rewrite(BoundIdentDecl src) {
			final String name = getNotReservedName(src.getName());
			final Type type = typeRewriter.rewrite(src.getType());
			return ff.makeBoundIdentDecl(name, null, type);
		}

		@Override
		public Expression rewrite(UnaryExpression src, boolean changed,
				Expression newChild) {
			final int tag = src.getTag();
			final int genericTag = getGenericTag(tag);
			if (genericTag < 0) {
				// not a generic operator
				return super.rewrite(src, changed, newChild);
			}
			final Type type = typeRewriter.rewrite(src.getType());
			if (newChild.isATypeExpression()) {
				return ff.makeAtomicExpression(genericTag, null, type);
			} else {
				return makeDomRes(newChild, genericTag, type);
			}
		}

		private Expression makeDomRes(Expression left, int genTag, Type type) {
			return ff.makeBinaryExpression(DOMRES, left,
					ff.makeAtomicExpression(genTag, null, type), null);
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
				result.addProblem(new ASTProblem(ident.getSourceLocation(),
						NotUpgradableError, Error));
				return false;
			}
			return true;
		}
	}

	public VersionUpgraderV1V2() {
		super(FormulaFactory.getV1Default());
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
		final ITypeCheckingRewriter rewriter = new RewriterV1V2(
				result.getFactory(), getReservedKeywords());
		try {
			final T rewritten = formula.rewrite(rewriter);
			result.setUpgradedFormula(rewritten);
		} catch (Exception e) {
			if (!result.hasProblem()) {
				result.addProblem(new ASTProblem(formula.getSourceLocation(),
						NotUpgradableError, Error));
			}
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
