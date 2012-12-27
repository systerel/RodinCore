/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.provers.arithmetic;

import java.util.Set;

import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.Clause;

public final class ArithmeticProver implements IProverModule {

	@Override
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProverResult contradiction(Level oldLevel, Level newLevel,
			Set<Level> dependencies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProverResult next(boolean force) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerDumper(Dumper dumper) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeClause(Clause clause) {
		// TODO Auto-generated method stub

	}

}
