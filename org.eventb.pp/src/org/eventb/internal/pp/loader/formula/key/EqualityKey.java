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

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

/**
 * Equality literals are uniquely identified by their sort.
 *
 * @author François Terrier
 *
 */
public class EqualityKey extends SymbolKey<EqualityDescriptor> {

	private Sort sort;
	
	public EqualityKey(Sort sort) {
		this.sort = sort;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof EqualityKey) {
			EqualityKey temp = (EqualityKey) obj;
			return sort.equals(temp.sort);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return "E".hashCode() * 31 + sort.hashCode();
	}
	
	@Override
	public String toString() {
		return sort.toString();
	}

	@Override
	public EqualityDescriptor newDescriptor(IContext context) {
		return new EqualityDescriptor(context, sort);
	}

}
