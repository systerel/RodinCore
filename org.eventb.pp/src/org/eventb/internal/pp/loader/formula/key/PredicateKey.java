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
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.predicate.IContext;

/**
 * A predicate literal is uniquely identified by its sort without considering terms.
 *
 * @author François Terrier
 *
 */
public class PredicateKey extends SymbolKey<PredicateDescriptor> {

	private Sort sort;
	
	public PredicateKey(Sort sort) {
		this.sort = sort;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof PredicateKey) {
			PredicateKey temp = (PredicateKey) obj;
			return sort.equals(temp.sort);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return "P".hashCode() * 31 + sort.hashCode();
	}

	@Override
	public String toString() {
		return sort.toString();
	}

	@Override
	public PredicateDescriptor newDescriptor(IContext context) {
		return new PredicateDescriptor(context, context.getNextLiteralIdentifier(), sort);
	}
}
