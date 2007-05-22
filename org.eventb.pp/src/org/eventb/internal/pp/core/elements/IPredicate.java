/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.BitSet;

/**
 * TODO comment
 *
 * @author François Terrier
 *
 */
public interface IPredicate extends ILiteral<IPredicate> {

	public int getIndex();
	
//	public List<IEquality> getConditions(IPredicate predicate);
	
//	public boolean matches(IPredicate predicate);
	
//	public boolean contains(IPredicate predicate);

	public boolean isPositive();
	
	public void setBit(BitSet set);
	
	public boolean updateInstantiationCount(IPredicate predicate);
	
	public void resetInstantiationCount();
	
	
	public ILiteralDescriptor getDescriptor();
}
