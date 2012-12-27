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
import org.eventb.internal.core.ast.AbstractResult;

/**
 * @author Nicolas Beauger
 * 
 */
public class UpgradeResult<T extends Formula<T>> extends AbstractResult
		implements IUpgradeResult<T> {

	private T upgradedFormula;
	private boolean upgradeNeeded;
	private final FormulaFactory factory;

	public UpgradeResult(FormulaFactory factory) {
		this.factory = factory;
	}

	@Override
	public T getUpgradedFormula() {
		return upgradedFormula;
	}

	// returns whether the input string needs upgrade
	@Override
	public boolean upgradeNeeded() {
		return upgradeNeeded;
	}

	public void setUpgradedFormula(T upgradedFormula) {
		this.upgradedFormula = upgradedFormula;
	}
	
	public void setUpgradeNeeded(boolean upgradeNeeded) {
		this.upgradeNeeded = upgradeNeeded;
	}
	
	public FormulaFactory getFactory() {
		return factory;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(super.toString());
		sb.append("upgrade needed: ");
		sb.append(upgradeNeeded);
		sb.append("\n");
		sb.append(upgradedFormula);
		sb.append("\n");
		return sb.toString();
	}
	
}
