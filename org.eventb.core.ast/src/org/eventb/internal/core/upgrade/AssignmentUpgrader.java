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
package org.eventb.internal.core.upgrade;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultSimpleVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

class AssignmentUpgrader extends DefaultSimpleVisitor {

	private final VersionUpgrader upg;
	private final String formulaString;
	private final UpgradeResult<Assignment> result;

	public AssignmentUpgrader(VersionUpgrader upg, String formulaString,
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

	private boolean upgradeAssignedIdentifiers(FormulaFactory factory,
			FreeIdentifier[] identifiers, FreeIdentifier[] upgradedIdentifiers) {
		boolean changed = false;
		for (int i = 0; i < identifiers.length; i++) {
			final UpgradeResult<Expression> localResult = new UpgradeResult<Expression>(
					factory);
			makeUpgrade(identifiers[i], localResult);
			mergeWithResult(localResult);
			changed |= localResult.upgradeNeeded();
			upgradedIdentifiers[i] = (FreeIdentifier) localResult
					.getUpgradedFormula();
			if (upgradedIdentifiers[i] == null) {
				upgradedIdentifiers[i] = identifiers[i];
				// needed if others do change
			}
		}
		return changed;
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		final Expression[] expressions = assignment.getExpressions();
		final Expression[] upgraded = new Expression[expressions.length];
		final FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();
		final FreeIdentifier[] upgradedIdentifiers = new FreeIdentifier[identifiers.length];
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
		
		boolean idChanged = upgradeAssignedIdentifiers(factory, identifiers,
				upgradedIdentifiers);
		if (changed || idChanged) {
			result.setUpgradedFormula(factory.makeBecomesEqualTo(
					upgradedIdentifiers, upgraded, null));
		}
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		final Expression set = assignment.getSet();
		final FreeIdentifier[] identifiers = assignment
				.getAssignedIdentifiers();
		final FreeIdentifier[] upgradedIdentifiers = new FreeIdentifier[identifiers.length];
		final FormulaFactory factory = result.getFactory();
		final UpgradeResult<Expression> localResult = new UpgradeResult<Expression>(
				factory);
		makeUpgrade(set, localResult);
		mergeWithResult(localResult);
		boolean changed = localResult.upgradeNeeded();

		boolean idChanged = upgradeAssignedIdentifiers(factory, identifiers,
				upgradedIdentifiers);
		if (changed || idChanged) {
			final Expression upgExpr = localResult.getUpgradedFormula();
			if (upgExpr != null) {
				result.setUpgradedFormula(factory.makeBecomesMemberOf(
						upgradedIdentifiers[0], upgExpr, null));
			}
		}
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		final Predicate condition = assignment.getCondition();
		final FreeIdentifier[] identifiers = assignment
				.getAssignedIdentifiers();
		final FreeIdentifier[] upgradedIdentifiers = new FreeIdentifier[identifiers.length];
		final BoundIdentDecl[] primedIdentifiers = assignment.getPrimedIdents();
		final BoundIdentDecl[] upgradedPrimedIdentifiers = new BoundIdentDecl[primedIdentifiers.length];
		final FormulaFactory factory = result.getFactory();
		
		//condition
		final UpgradeResult<Predicate> predResult = new UpgradeResult<Predicate>(
				factory);
		makeUpgrade(condition, predResult);
		mergeWithResult(predResult);
		boolean changed = predResult.upgradeNeeded();

		// primed identifiers
		for (int i = 0; i < primedIdentifiers.length; i++) {
			final UpgradeResult<BoundIdentDecl> localResult = new UpgradeResult<BoundIdentDecl>(
					factory);
			makeUpgrade(primedIdentifiers[i], localResult);
			mergeWithResult(localResult);
			changed |= localResult.upgradeNeeded();
			upgradedPrimedIdentifiers[i] = localResult.getUpgradedFormula();
			if (upgradedPrimedIdentifiers[i] == null) {
				upgradedPrimedIdentifiers[i] = primedIdentifiers[i];
				// needed if others do change
			}
		}
		
		// assigned identifiers
		boolean idChanged = upgradeAssignedIdentifiers(factory, identifiers,
				upgradedIdentifiers);
		if (changed || idChanged) {
			final Predicate upgPred = predResult.getUpgradedFormula();
			if (upgPred != null) {
				result.setUpgradedFormula(factory.makeBecomesSuchThat(
						upgradedIdentifiers, upgradedPrimedIdentifiers,
						upgPred, null));
			}
		}
	}

}