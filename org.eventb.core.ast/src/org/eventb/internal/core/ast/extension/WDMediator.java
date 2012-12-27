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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.internal.core.ast.wd.FormulaBuilder;

/**
 * @author Nicolas Beauger
 * 
 */
public class WDMediator implements IWDMediator {

	private final FormulaBuilder fb;

	public WDMediator(FormulaBuilder formulaBuilder) {
		this.fb = formulaBuilder;
	}

	@Override
	public Predicate makeTrueWD() {
		return fb.btrue;
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return fb.ff;
	}

}
