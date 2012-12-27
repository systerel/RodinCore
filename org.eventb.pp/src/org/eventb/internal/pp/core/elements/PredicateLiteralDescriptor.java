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
package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a descriptor for a predicate literal.
 * <p>
 * Holds several information about a particular predicate. There exists 
 * only one instance of a predicate literal descriptor per index, and this
 * instance can be retrieved using {@link PredicateTable}. Literal descriptors
 * must be created using {@link PredicateTable} as well, to ensure the unique
 * instance per index property.
 *
 * @author François Terrier
 *
 */
public final class PredicateLiteralDescriptor {
	final private int index;
	final private int arity;
	final private int originalArity;
	final private boolean isLabel;
	final private boolean isMembership;
	final private List<Sort> sortList = new ArrayList<Sort>();
	
	PredicateLiteralDescriptor(int index, 
			int arity, int originalArity, boolean isLabel, boolean isMembership,
			List<Sort> sortList) {
		assert sortList == null || sortList.size() == arity;
		assert !(isLabel && isMembership); 
		this.index = index;
		this.arity = arity;
		this.originalArity = originalArity;
		this.isLabel = isLabel;
		this.isMembership = isMembership;
		
		if (sortList != null) this.sortList.addAll(sortList);
	}
	
	/**
	 * Returns the index of the predicate.
	 * 
	 * @return the index of the predicate
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Returns <code>true</code> if this descriptor represents a 
	 * genuine membership predicate, <code>false</code> otherwise.
	 * <p>
	 * A predicate is a genuine membership if it is an original predicate (no label)
	 * that has not been simplified by the loader (the original arity 
	 * corresponds to the arity of the predicate).
	 * 
	 * @return <code>true</code> if this descriptor represents a 
	 * genuine membership predicate, <code>false</code> otherwise
	 */
	public boolean isGenuineMembership() {
		return isMembership && arity == originalArity && !isLabel;
	}
	
	/**
	 * Returns the arity of the predicate.
	 * 
	 * @return the arity of the predicate
	 */
	public int getArity() {
		return arity;
	}
	
	/**
	 * Returns <code>true</code> if this predicate is a label,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this predicate is a label,
	 * <code>false</code> otherwise
	 */
	public boolean isLabel() {
		return isLabel;
	}
	
	/**
	 * Returns the list of sorts corresponding to this predicate.
	 * <p>
	 * The size of the sort list corresponds to the arity.
	 * 
	 * @return the list of sorts corresponding to this predicate
	 */
	public List<Sort> getSortList() {
		return sortList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final PredicateLiteralDescriptor other = (PredicateLiteralDescriptor) obj;
		return index == other.index;
	}

	@Override
	public int hashCode() {
		return index;
	}
	
	@Override
	public String toString() {
		return (isLabel?"L":"P")+index;
	}

}
