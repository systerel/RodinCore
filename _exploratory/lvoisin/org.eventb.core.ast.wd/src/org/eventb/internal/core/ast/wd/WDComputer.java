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
package org.eventb.internal.core.ast.wd;

import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DIV;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LEQV;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.MOD;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.ast.Formula.QUNION;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultSimpleVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Implementation of the basic computation of WD predicates. Clients shall use
 * this class with <code>new WDComputer(ff).getWDLemma(formula)</code>.
 * 
 */
public class WDComputer extends DefaultSimpleVisitor {

	private FormulaBuilder fb;

	/**
	 * Result of last visit. This field must be set by each
	 * <code>visitXXX()</code> method and read only through method
	 * <code>wd()</code>.
	 */
	private Predicate lemma;

	public WDComputer(FormulaFactory formulaFactory) {
		this.fb = new FormulaBuilder(formulaFactory);
	}

	public Predicate getWDLemma(Formula<?> formula) {
		assert formula.isTypeChecked();
		return wd(formula).flatten(fb.ff);
	}

	private Predicate wd(Formula<?> formula) {
		formula.accept(this);
		return lemma;
	}

	private Predicate wd(Formula<?> left, Formula<?> right) {
		return fb.land(wd(left), wd(right));
	}

	private Predicate wd(Formula<?>... children) {
		final int length = children.length;
		final Predicate[] wds = new Predicate[length];
		for (int i = 0; i < length; i++) {
			wds[i] = wd(children[i]);
		}
		return fb.land(wds);
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		lemma = wd(expression.getChildren());
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		final Predicate[] children = predicate.getChildren();
		switch (predicate.getTag()) {
		case LAND:
			lemma = landWD(children);
			break;
		case LOR:
			lemma = lorWD(children);
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}

	private Predicate lorWD(Predicate[] children) {
		Predicate result = fb.btrue;
		for (int i = children.length - 1; i >= 0; i--) {
			final Predicate child = children[i];
			result = fb.land(wd(child), fb.lor(child, result));
		}
		return result;
	}

	private Predicate landWD(Predicate[] children) {
		Predicate result = fb.btrue;
		for (int i = children.length - 1; i >= 0; i--) {
			final Predicate child = children[i];
			result = fb.land(wd(child), fb.limp(child, result));
		}
		return result;
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		lemma = wd(assignment.getExpressions());
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		lemma = wd(assignment.getSet());
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		final BoundIdentDecl[] primedIdents = assignment.getPrimedIdents();
		lemma = fb.forall(primedIdents, wd(assignment.getCondition()));
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expr) {
		final Expression left = expr.getLeft();
		final Expression right = expr.getRight();
		lemma = fb.land(wd(left), wd(right), binExprWD(expr, left, right));
	}

	private Predicate binExprWD(BinaryExpression expr, Expression left,
			Expression right) {
		switch (expr.getTag()) {
		case DIV:
			return fb.notZero(right);
		case MOD:
			return fb.land(fb.nonNegative(left), fb.positive(right));
		case EXPN:
			return fb.land(fb.nonNegative(left), fb.nonNegative(right));
		case FUNIMAGE:
			return fb.land(fb.inDomain(left, right), fb.partial(left));
		default:
			return fb.btrue;
		}
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		final Predicate left = predicate.getLeft();
		final Predicate right = predicate.getRight();
		switch (predicate.getTag()) {
		case LIMP:
			lemma = fb.land(wd(left), fb.limp(left, wd(right)));
			break;
		case LEQV:
			lemma = fb.land(wd(left), wd(right));
			break;
		default:
			assert false;
			lemma = null;
			break;
		}
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		lemma = wd(expression.getPredicate());
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		lemma = fb.btrue;
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		lemma = fb.btrue;
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		lemma = fb.btrue;
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		lemma = wd(predicate.getChildren());
	}

	@Override
	public void visitPredicateVariable(PredicateVariable predVar) {
		lemma = fb.btrue;
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		final BoundIdentDecl[] decls = expression.getBoundIdentDecls();
		final Predicate pred = expression.getPredicate();
		final Expression expr = expression.getExpression();
		final Predicate childrenWD = fb.forall(decls,//
				fb.land(wd(pred), fb.limp(pred, wd(expr))));
		final Predicate localWD;
		switch (expression.getTag()) {
		case QUNION:
		case CSET:
			localWD = fb.btrue;
			break;
		case QINTER:
			localWD = fb.exists(decls, pred);
			break;
		default:
			assert false;
			localWD = null;
			break;
		}
		lemma = fb.land(childrenWD, localWD);
	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
		final Predicate child = predicate.getPredicate();
		lemma = fb.forall(decls, wd(child));
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		lemma = wd(predicate.getLeft(), predicate.getRight());
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		lemma = wd(expression.getMembers());
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		lemma = wd(predicate.getExpression());
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expr) {
		final Expression child = expr.getChild();
		lemma = fb.land(wd(child), uExprWD(expr, child));
	}

	private Predicate uExprWD(UnaryExpression expr, Expression child) {
		switch (expr.getTag()) {
		case KCARD:
			return fb.finite(child);
		case KMIN:
			return fb.land(fb.notEmpty(child), fb.bounded(child, true));
		case KMAX:
			return fb.land(fb.notEmpty(child), fb.bounded(child, false));
		case KINTER:
			return fb.notEmpty(child);
		default:
			return fb.btrue;
		}
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		lemma = wd(predicate.getChild());
	}

}
