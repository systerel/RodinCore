/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.extension.ExtensionFactory.makeChildTypes;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;

import java.util.Arrays;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.StandardGroup;

/**
 * @author Nicolas Beauger
 *
 */
public class Cond implements IExpressionExtension {

	private Cond() {
		// singleton
	}
	
	private static final Cond INSTANCE = new Cond();
	
	public static Cond getCond() {
		return INSTANCE;
	}
	
	private static final String COND_ID = "Cond Id";
	private static final String COND_SYMBOL = "COND";
	

	@Override
	public String getSyntaxSymbol() {
		return COND_SYMBOL;
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		// WD(cond) & (cond => WD(e1)) & (not cond => WD(e2))
		final FormulaFactory ff = wdMediator.getFormulaFactory();
		final Predicate cond = formula.getChildPredicates()[0];
		final Predicate wdCond = cond.getWDPredicate(ff);
		final Expression[] exprs = formula.getChildExpressions();
		final Predicate wdE1 = exprs[0].getWDPredicate(ff);
		final Predicate wdE2 = exprs[1].getWDPredicate(ff);
		final Predicate condE1 = ff.makeBinaryPredicate(LIMP, cond, wdE1, null);
		final UnaryPredicate notCond = ff.makeUnaryPredicate(NOT, cond, null);
		final Predicate notCondE2 = ff.makeBinaryPredicate(LIMP, notCond, wdE2,
				null);

		return ff.makeAssociativePredicate(LAND,
				Arrays.<Predicate> asList(wdCond, condE1, notCondE2), null);
	}

	@Override
	public boolean conjoinChildrenWD() {
		return false;
	}

	@Override
	public String getId() {
		return COND_ID; // TODO plugin ids everywhere
	}

	@Override
	public String getGroupId() {
		return StandardGroup.CLOSED.getId();
	}

	@Override
	public IExtensionKind getKind() {
		return makePrefixKind(EXPRESSION,
				makeChildTypes(PREDICATE, EXPRESSION, EXPRESSION));
	}

	@Override
	public Object getOrigin() {
		return null;
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// no compatibilities
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// no priorities
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		final Expression e1 = childExprs[0];
		final Expression e2 = childExprs[1];
		if (e1.getType().equals(e2.getType())) {
			return e1.getType();
		} else {
			return null;
		}
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		final Expression e1 = childExprs[0];
		final Expression e2 = childExprs[1];

		return proposedType.equals(e1.getType()) && proposedType.equals(e2.getType());
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		final Expression[] childExprs = expression.getChildExpressions();
		final Expression e1 = childExprs[0];
		final Expression e2 = childExprs[1];
		
		final Type type = tcMediator.newTypeVariable();
		tcMediator.sameType(type, e1.getType());
		tcMediator.sameType(type, e2.getType());
		return type;
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

}
