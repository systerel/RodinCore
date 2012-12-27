/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerExtentionTests;

import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.GE;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * A prime predicate declared as a mathematical extension. This extension is a
 * unary predicate that takes a non negative integer argument.
 * 
 * @author Laurent Voisin
 */
class PrimePredicate implements IPredicateExtension {

	public static PrimePredicate getInstance() {
		return new PrimePredicate();
	}

	private PrimePredicate() {
		// singleton instance
	}

	@Override
	public String getSyntaxSymbol() {
		return "prime";
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		final FormulaFactory ff = wdMediator.getFormulaFactory();
		final IntegerLiteral zero = ff.makeIntegerLiteral(ZERO, null);
		final Expression child = formula.getChildExpressions()[0];
		return ff.makeRelationalPredicate(GE, zero, child, null);
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public String getId() {
		return "prime";
	}

	@Override
	public String getGroupId() {
		return "prime";
	}

	@Override
	public IExtensionKind getKind() {
		return PARENTHESIZED_UNARY_PREDICATE;
	}

	@Override
	public Object getOrigin() {
		return this;
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// Do nothing
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// Do nothing
	}

	@Override
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator tcMediator) {
		final Expression child = predicate.getChildExpressions()[0];
		tcMediator.sameType(child.getType(), tcMediator.makeIntegerType());
	}

}