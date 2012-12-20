/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.junit.Assert.assertSame;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.SourceLocation;

public class SourceLocationOriginChecker extends SourceLocationChecker {

	final Object expected;
	
	public SourceLocationOriginChecker(Object expected) {
		this.expected = expected;
	}

	@Override
	protected boolean enterFormula(Formula<?> formula) {
		visitFormula(formula, false);
		return true;
	}

	@Override
	protected boolean exitFormula(Formula<?> formula) {
		return true;
	}

	@Override
	protected boolean visitFormula(Formula<?> formula, boolean push) {
		SourceLocation loc = formula.getSourceLocation();
		assertSame("Unexpected origin", expected, loc.getOrigin());
		return true;
	}
}
