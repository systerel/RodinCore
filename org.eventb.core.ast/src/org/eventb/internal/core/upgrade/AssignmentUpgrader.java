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

import static org.eventb.internal.core.upgrade.VersionUpgrader.copyProblems;

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

/**
 * Upgrades an assignment. It is already known that the assignment needs to be
 * upgraded. This is implemented differently from other kinds of formulas
 * because we cannot rewrite assignments.
 */
class AssignmentUpgrader extends DefaultSimpleVisitor {

	private final VersionUpgrader upg;
	private final UpgradeResult<Assignment> result;
	private final FormulaFactory factory;

	public AssignmentUpgrader(VersionUpgrader upg,
			UpgradeResult<Assignment> result) {
		this.upg = upg;
		this.result = result;
		this.factory = result.getFactory();
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		final FreeIdentifier[] idents = assignment.getAssignedIdentifiers();
		final Expression[] exprs = assignment.getExpressions();
		final FreeIdentifier[] upgIdents = upgradeIdentifiers(idents);
		final Expression[] upgExprs = new Expression[exprs.length];
		for (int i = 0; i < exprs.length; i++) {
			upgExprs[i] = upgradeChild(exprs[i]);
		}
		final Assignment upgAssignment = factory.makeBecomesEqualTo(upgIdents,
				upgExprs, null);
		result.setUpgradedFormula(upgAssignment);
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		final FreeIdentifier[] idents = assignment.getAssignedIdentifiers();
		final FreeIdentifier[] upgIdents = upgradeIdentifiers(idents);
		final Expression set = assignment.getSet();
		final Expression upgSet = upgradeChild(set);
		final Assignment upgAssignment = factory.makeBecomesMemberOf(
				upgIdents[0], upgSet, null);
		result.setUpgradedFormula(upgAssignment);
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		final FreeIdentifier[] idents = assignment.getAssignedIdentifiers();
		final FreeIdentifier[] upgIdents = upgradeIdentifiers(idents);
		final BoundIdentDecl[] primedIdents = assignment.getPrimedIdents();
		final BoundIdentDecl[] upgPrimed = new BoundIdentDecl[primedIdents.length];
		for (int i = 0; i < primedIdents.length; i++) {
			upgPrimed[i] = upgradeChild(primedIdents[i]);
		}
		final Predicate condition = assignment.getCondition();
		final Predicate upgCondition = upgradeChild(condition);
		final BecomesSuchThat upgAssignment = factory.makeBecomesSuchThat(
				upgIdents, upgPrimed, upgCondition, null);
		result.setUpgradedFormula(upgAssignment);
	}

	private FreeIdentifier[] upgradeIdentifiers(FreeIdentifier[] identifiers) {
		final FreeIdentifier[] newIdents = new FreeIdentifier[identifiers.length];
		for (int i = 0; i < identifiers.length; i++) {
			newIdents[i] = (FreeIdentifier) upgradeChild((Expression) identifiers[i]);
		}
		return newIdents;
	}

	private <T extends Formula<T>> T upgradeChild(T formula) {
		final UpgradeResult<T> localResult = new UpgradeResult<T>(result);
		upg.upgrade(formula, localResult);
		copyProblems(localResult, result);
		return localResult.getUpgradedFormula();
	}

}