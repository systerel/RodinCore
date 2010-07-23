/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate.iterators;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.ResetIterator;

/**
 * Match iterable that returns all clauses matching the given predicate
 * from a {@link ResetIterator}.
 *
 * @author François Terrier
 *
 */
public class IteratorMatchIterable implements IMatchIterable {
	// this class has a state

	private ResetIterator<Clause> nonUnitClausesIterator;
	
	public IteratorMatchIterable(ResetIterator<Clause> nonUnitClausesIterator) {
		this.nonUnitClausesIterator = nonUnitClausesIterator;
	}

	// returns the same instance all the time
	@Override
	public Iterator<Clause> iterator(PredicateLiteralDescriptor predicate, boolean isPositive) {
		nonUnitClausesIterator.reset();
		return new NiceIterator(predicate, isPositive, nonUnitClausesIterator);
	}

	private static class NiceIterator extends ConditionIterator<Clause> {
		private PredicateLiteralDescriptor unit;
		private boolean isPositive;
		
		NiceIterator(PredicateLiteralDescriptor unit, boolean isPositive, Iterator<Clause> iterator) {
			super(iterator);

			this.isPositive = isPositive;
			this.unit = unit;
		}

		@Override
		public boolean isSelected(Clause element) {
			return element.matches(unit,isPositive);
		}
	}
}