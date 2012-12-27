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

import java.util.Collection;
import java.util.Hashtable;

import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;


/**
 * This is the abstract base class for all symbol tables of the loader.
 *
 * @author François Terrier
 *
 * @param <T>
 */
public class SymbolTable<T extends LiteralDescriptor> {

	private Hashtable<SymbolKey<T>, T> table = new Hashtable<SymbolKey<T>, T>();
	
	public T get(SymbolKey<T> source) {
		T i = table.get(source);
		return i;
	}
	
	public T add(SymbolKey<T> source, T target) {
		assert !table.contains(source);
		
		table.put(source, target);
		return target;
	}
	
	public Collection<T> getAllLiterals() {
		return table.values();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SymbolTable) {
			SymbolTable<?> temp = (SymbolTable<?>) obj;
			return table.equals(temp.table);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return table.toString();
	}
	
}
