/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.key;

import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;


public abstract class SymbolKey<T extends LiteralDescriptor> {

	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract String toString();

	public abstract T newDescriptor(IContext context);
	
}
