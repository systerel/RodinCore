/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core;

import org.eventb.internal.pp.core.elements.Clause;

public interface IDispatcher {
	
	public Level getLevel();

	public void nextLevel();

	public boolean contains(Clause clause);
	
//	public void contradiction(IOrigin origin);
	
//	public void removeClause(Clause clause);
	
}
