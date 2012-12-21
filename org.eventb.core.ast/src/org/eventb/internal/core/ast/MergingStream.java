/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.ast;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.Type;


/**
 * Merging streams that merge two other streams.
 * 
 * @author Laurent Voisin
 */
public final class MergingStream extends IdentListMerger {
	
	private IdentListMerger s1;
	private Identifier id1;
	private String name1;
	private int idx1;

	private IdentListMerger s2;
	private Identifier id2;
	private String name2;
	private int idx2;
	
	protected MergingStream(IdentListMerger s1, IdentListMerger s2) {
		super(s1.itemClass);
		assert s1.itemClass == s2.itemClass;
		
		this.s1 = s1;
		this.s2 = s2;
		id1 = s1.getNext();
		name1 = getName(id1);
		idx1 = getIndex(id1);
		id2 = s2.getNext();
		name2 = getName(id2);
		idx2 = getIndex(id2);
	}
	
	@Override
	public boolean containsError() {
		if (! errorFound) {
			errorFound = s1.containsError() || s2.containsError();
		}
		return errorFound;
	}

	private final String getName(Identifier id) {
		if (itemClass != FreeIdentifier.class)
			return null;
		if (id == null)
			return infinity;
		else if (id instanceof FreeIdentifier)
			return ((FreeIdentifier) id).getName();
		else
			return null;
	}

	private final int getIndex(Identifier id) {
		if (itemClass != BoundIdentifier.class)
			return -1;
		if (id == null)
			return Integer.MAX_VALUE;
		else if (id instanceof BoundIdentifier)
			return ((BoundIdentifier) id).getBoundIndex();
		else
			return -1;
	}

	@Override
	protected int getPotentialLength() {
		return s1.getPotentialLength() + s2.getPotentialLength();
	}

	@Override
	protected Identifier[] getMaximalArray() {
		Identifier[] array1 = s1.getMaximalArray();
		Identifier[] array2 = s2.getMaximalArray();
		if (array1.length <= array2.length)
			return array2;
		else
			return array1;
	}

	@Override
	protected Identifier getNext() {
		Identifier result = null;
		final int comparison;
		if (itemClass == FreeIdentifier.class) {
			comparison = name1.compareTo(name2);
		} else {
			comparison = idx1 - idx2;
		}
		if (comparison == 0 && ! errorFound && id1 != null) {
			// Check for a possible error
			final Type type1 = id1.getType();
			final Type type2 = id2.getType();
			errorFound = type1 == null || ! type1.equals(type2);
			// FIXME: in this case we should return a result with null type
		}
		if (comparison <= 0) {
			result = id1;
			id1 = s1.getNext();
			name1 = getName(id1);
			idx1 = getIndex(id1);
		}
		if (comparison >= 0) {
			result = id2;
			id2 = s2.getNext();
			name2 = getName(id2);
			idx2 = getIndex(id2);
		}
		return result;
	}

}
