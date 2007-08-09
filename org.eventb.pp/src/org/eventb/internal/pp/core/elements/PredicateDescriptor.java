/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

public final class PredicateDescriptor {
	
	final private int index;
	final private boolean positive;
	final private int hashCode;
	
	public PredicateDescriptor(int index, boolean positive) {
		this.index = index;
		this.positive = positive;
		
		this.hashCode = 37*index + (positive?1:0);
	}
	
	public boolean isPositive() {
		return positive;
	}
	
	public int getIndex() {
		return index;
	}
	
	public PredicateDescriptor getInverse() {
		return new PredicateDescriptor(index, !positive);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PredicateDescriptor) {
			PredicateDescriptor temp = (PredicateDescriptor) obj;
			return index == temp.index && positive == temp.positive;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public String toString() {
		return (positive?"":"¬")+"P"+index;
	}

}
