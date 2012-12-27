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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements an automatically resized array, that is an array that can be
 * accessed at any index, without throwing an
 * {@link java.lang.ArrayIndexOutOfBoundsException}.
 * 
 * @author Laurent Voisin
 */
public class Cache<E> extends ArrayList<E> {

	private static final long serialVersionUID = 5451207522829282840L;

	public Cache(int initialCapacity) {
		super(initialCapacity);
	}

	public Cache() {
		super();
	}

	public Cache(Collection<E> c) {
		super(c);
	}

	@Override
	public E get(int index) {
		if (index < size())
			return super.get(index);
		return null;
	}

	@Override
	public E set(int index, E element) {
		final int length = size();
		for (int i = length; i <= index; ++i)
			add(null);
		return super.set(index, element);
	}

}
