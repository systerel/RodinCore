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

public class SplitOrigin extends AbstractInferrenceOrigin {

	private Level level;
	
	public SplitOrigin(List<Clause> parents, Level level) {
		super(parents);
		
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

}
