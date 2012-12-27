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

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IUpgradeResult;
import org.eventb.core.ast.LanguageVersion;
import org.rodinp.core.version.IAttributeModifier;

public class UpgraderV1V2 {


	private static abstract class Upgrader implements IAttributeModifier {
		// Default factory is sufficient for V1 formulae 
		protected static final FormulaFactory ff = FormulaFactory.getDefault();

		protected static final LanguageVersion targetVersion = LanguageVersion.V2;
		
		public Upgrader() {
			// avoid synthetic accessor emulation
		}

		@Override
		public String getNewValue(String formulaString) {
			final IUpgradeResult<?> result = getUpgraded(formulaString);
			if (!result.upgradeNeeded() || result.hasProblem()) {
				return formulaString;
			}
			return getString(result.getUpgradedFormula());
		}

		protected abstract IUpgradeResult<?> getUpgraded(String formulaString);

		protected abstract String getString(Formula<?> formula);
	}

	public static class AssignmentUpgraderV1V2 extends Upgrader {

		@Override
		protected IUpgradeResult<?> getUpgraded(String formulaString) {
			return ff.upgradeAssignment(formulaString, targetVersion);
		}

		@Override
		protected String getString(Formula<?> formula) {
			return formula.toString();
		}

	}

	public static class ExpressionUpgraderV1V2 extends Upgrader {

		@Override
		protected IUpgradeResult<?> getUpgraded(String formulaString) {
			return ff.upgradeExpression(formulaString, targetVersion);
		}

		@Override
		protected String getString(Formula<?> formula) {
			return formula.toString();
		}

	}

	public static class PredicateUpgraderV1V2 extends Upgrader {

		@Override
		protected IUpgradeResult<?> getUpgraded(String formulaString) {
			return ff.upgradePredicate(formulaString, targetVersion);
		}

		@Override
		protected String getString(Formula<?> formula) {
			return formula.toString();
		}

	}

	public static class ExpressionUpgraderV1V2WithTypes extends
			ExpressionUpgraderV1V2 {

		@Override
		protected String getString(Formula<?> formula) {
			return formula.toStringWithTypes();
		}

	}

	public static class PredicateUpgraderV1V2WithTypes extends
			PredicateUpgraderV1V2 {

		@Override
		protected String getString(Formula<?> formula) {
			return formula.toStringWithTypes();
		}

	}

}