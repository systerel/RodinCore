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

import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * @author Nicolas Beauger
 * 
 */
/* package */class WDMediator implements IWDMediator {

	private final FormulaFactory formulaFactory;

	public WDMediator(FormulaFactory formulaFactory) {
		this.formulaFactory = formulaFactory;
	}

	public Predicate addChildrenWD(Predicate initialWD,
			IExtendedFormula formula) {
		final Predicate exprWD = Formula.getWDConjunction(formulaFactory,
				formula.getChildExpressions());
		final Predicate predWD = Formula.getWDConjunction(formulaFactory,
				formula.getChildPredicates());
		final Predicate childWD = Formula.getWDSimplifyC(formulaFactory,
				exprWD, predWD);
		return Formula.getWDSimplifyC(formulaFactory, initialWD, childWD);
	}

	@Override
	public Predicate makeTrueWD() {
		return formulaFactory.makeLiteralPredicate(Formula.BTRUE, null);
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return formulaFactory;
	}

}
