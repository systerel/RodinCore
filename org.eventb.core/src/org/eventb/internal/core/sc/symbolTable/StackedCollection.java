/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - redesign of symbol table
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Concatenate two collections without copying them.
 * 
 * @author Stefan Hallerstede
 */
public class StackedCollection<T> extends AbstractCollection<T> {

	protected final Collection<T> supCollection;
	protected final Collection<T> infCollection;

	/**
	 * Creates a new concatenation of the two collection without copying them.
	 * The two collections must not share any implementation as they are
	 * iterated in parallel.
	 * 
	 * @param supCollection
	 *            the first collection
	 * @param infCollection
	 *            the second collection
	 */
	public StackedCollection(Collection<T> supCollection,
			Collection<T> infCollection) {
		this.supCollection = supCollection;
		this.infCollection = infCollection;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private final Iterator<T> supIterator = supCollection.iterator();
			private final Iterator<T> infIterator = infCollection.iterator();

			private boolean inSup = supIterator.hasNext();

			@Override
			public boolean hasNext() {
				return inSup ? supIterator.hasNext() : infIterator.hasNext();
			}

			@Override
			public T next() {
				final T result = inSup ? supIterator.next() : infIterator
						.next();
				inSup &= supIterator.hasNext();
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}

		};
	}

	@Override
	public int size() {
		return supCollection.size() + infCollection.size();
	}

}
