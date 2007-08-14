/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.inferrers;

import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;

public interface IInferrer {

	public void inferFromEquivalenceClause(EquivalenceClause clause);
	
	public void inferFromDisjunctiveClause(DisjunctiveClause clause);
	
}
