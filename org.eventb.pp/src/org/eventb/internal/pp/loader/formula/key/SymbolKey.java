/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.loader.formula.key;

import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.predicate.AbstractContext;
import org.eventb.internal.pp.loader.predicate.IContext;


/**
 * Symbol keys are used as keys to all symbol tables {@link SymbolTable} in the
 * {@link AbstractContext}.
 * <p>
 * In the loader, sub-formulas that are the same in different places in a predicate
 * or in different predicates are factored by the same {@link LiteralDescriptor}.
 * This class is the key to the hash tables in {@link AbstractContext} that give
 * access to the descriptor. Each kind of sub-formula has its corresponding 
 * descriptor type and therefore its corresponding key type.
 *
 * @author François Terrier
 *
 * @param <T>
 */
public abstract class SymbolKey<T extends LiteralDescriptor> {

	/**
	 * Returns a descriptor for this key.
	 * 
	 * @param context the context
	 * @return a new descriptor
	 */
	public abstract T newDescriptor(IContext context);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public abstract boolean equals(Object obj);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public abstract int hashCode();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();
}
