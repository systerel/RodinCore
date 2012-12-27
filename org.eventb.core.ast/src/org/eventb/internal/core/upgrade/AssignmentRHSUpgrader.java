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

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.DefaultSimpleVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

class AssignmentRHSUpgrader extends DefaultSimpleVisitor {

	private final VersionUpgrader upg;
	private final String formulaString;
	private final UpgradeResult<Assignment> result;

	public AssignmentRHSUpgrader(VersionUpgrader upg, String formulaString,
			FormulaFactory ff, UpgradeResult<Assignment> result) {
		this.upg = upg;
		this.formulaString = formulaString;
		this.result = result;
	}

	private <T extends Formula<T>> void makeUpgrade(T expr,
			UpgradeResult<T> localResult) {
		upg.upgrade(formulaString, expr, localResult);
	}

	private void mergeWithResult(final UpgradeResult<?> localResult) {
		VersionUpgrader.copyProblems(localResult, result);
		if (localResult.upgradeNeeded()) {
			result.setUpgradeNeeded(true);
		}
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		final Expression[] expressions = assignment.getExpressions();
		final Expression[] upgraded = new Expression[expressions.length];
		final FormulaFactory factory = result.getFactory();
		boolean changed = false;
		for (int i = 0; i < expressions.length; i++) {
			final UpgradeResult<Expression> localResult = new UpgradeResult<Expression>(
					factory);
			makeUpgrade(expressions[i], localResult);
			mergeWithResult(localResult);
			changed |= localResult.upgradeNeeded();
			upgraded[i] = localResult.getUpgradedFormula();
			if (upgraded[i] == null) {
				upgraded[i] = expressions[i];
				// needed if others do change
			}
		}
		if (changed) {
			result.setUpgradedFormula(factory.makeBecomesEqualTo(assignment
					.getAssignedIdentifiers(), upgraded, null));
		}
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		final Expression set = assignment.getSet();
		final FormulaFactory factory = result.getFactory();
		final UpgradeResult<Expression> localResult = new UpgradeResult<Expression>(
				factory);
		makeUpgrade(set, localResult);
		mergeWithResult(localResult);
		if (localResult.upgradeNeeded()) {
			final Expression upgExpr = localResult.getUpgradedFormula();
			if (upgExpr != null) {
				result.setUpgradedFormula(factory.makeBecomesMemberOf(
						assignment.getAssignedIdentifiers()[0], upgExpr, null));
			}
		}
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		final Predicate condition = assignment.getCondition();
		final FormulaFactory factory = result.getFactory();
		final UpgradeResult<Predicate> localResult = new UpgradeResult<Predicate>(
				factory);
		makeUpgrade(condition, localResult);
		mergeWithResult(localResult);
		if (localResult.upgradeNeeded()) {
			final Predicate upgPred = localResult.getUpgradedFormula();
			if (upgPred != null) {
				result.setUpgradedFormula(factory.makeBecomesSuchThat(
						assignment.getAssignedIdentifiers(), assignment
								.getPrimedIdents(), upgPred, null));
			}
		}
	}

}