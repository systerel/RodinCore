/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate;

import org.eventb.internal.pp.core.elements.Clause;

public interface IResolver {

	/**
	 * Returns the result of this resolution step or <code>null</code>
	 * if no more resolution can be applied.
	 * 
	 * @return
	 */
	public ResolutionResult next();	

	public boolean isInitialized();
	
	public void initialize(Clause matcher);
	
}
