/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich, 2008 University of Southampton
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Stefan Hallerstede
 * 
 */
public class StackedCollection<T> extends AbstractCollection<T> {

	protected final Collection<T> supCollection;
	protected final Collection<T> infCollection;

	public StackedCollection(Collection<T> supCollection,
			Collection<T> infCollection) {

		this.supCollection = supCollection;
		this.infCollection = infCollection;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			private boolean sup = supCollection.size() != 0;

			private final Iterator<T> supIterator = supCollection.iterator();
			private final Iterator<T> infIterator = infCollection.iterator();

			public boolean hasNext() {
				return sup ? supIterator.hasNext() : infIterator.hasNext();
			}

			public T next() {
				if (sup && !supIterator.hasNext())
					sup = false;
				return sup ? supIterator.next() : infIterator.next();
			}

			public void remove() {
				if (sup)
					supIterator.remove();
				else
					infIterator.remove();

			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return supCollection.size() + infCollection.size();
	}

}
