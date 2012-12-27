/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Identifier;


/**
 * Stream fed from a sorted array of identifiers.
 * 
 * @author Laurent Voisin
 */
public final class ArrayStream extends IdentListMerger {

	final Identifier[] source;
	int curIndex;
	
	protected ArrayStream(Identifier[] source) {
		super(source.getClass().getComponentType());
		this.source = source;
		curIndex = 0;
	}
	
	@Override
	public boolean containsError() {
		return errorFound;
	}

	@Override
	protected int getPotentialLength() {
		return source.length;
	}

	@Override
	protected Identifier[] getMaximalArray() {
		return source;
	}

	@Override
	protected Identifier getNext() {
		if (curIndex < source.length) {
			final Identifier result = source[curIndex ++];
			if (result.getType() == null) {
				errorFound = true;
			}
			return result;
		}
		return null;
	}

}
