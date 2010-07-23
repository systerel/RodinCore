/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.tracing;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;

/**
 * Implementation of {@link IOrigin} for clauses that are derived from
 * several other clauses. This implementation computes the level lazily.
 *
 * @author François Terrier
 *
 */
public class ClauseOrigin extends AbstractInferrenceOrigin {

	public ClauseOrigin(List<Clause> parents) {
		super(parents);
	}
	
	public ClauseOrigin(Clause parent) {
		super(parent);
	}

	Level level = null;
	
	@Override
	public Level getLevel() {
		if (level != null) return level;
		
		Level result = null;
		for (Clause clause : parents) {
			if (result == null || result.isAncestorOf(clause.getLevel())) {
				result = clause.getLevel();
			}
		}
		level = result;
		return result;
	}
	
}
