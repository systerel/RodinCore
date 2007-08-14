/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.List;

public final class PredicateLiteralDescriptor {
	
	final private int index;
	final private int hashCode;
	
	final private int arity;
	final private int realArity;
	final private boolean isLabel;
	final private List<Sort> sortList;
	
	public PredicateLiteralDescriptor(int index, 
			int arity, int realArity, boolean isLabel,
			List<Sort> sortList) {
		this.index = index;
		this.arity = arity;
		this.realArity = realArity;
		this.isLabel = isLabel;
		this.sortList = sortList;
		
		this.hashCode = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public boolean isCompletePredicate() {
		return arity == realArity && !isLabel;
	}
	
	public int getArity() {
		return arity;
	}
	
	public boolean isLabel() {
		return isLabel;
	}
	
	public List<Sort> getSortList() {
		return sortList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PredicateLiteralDescriptor) {
			PredicateLiteralDescriptor temp = (PredicateLiteralDescriptor) obj;
			return index == temp.index;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public String toString() {
		return (isLabel?"L":"P")+index;
	}

}
