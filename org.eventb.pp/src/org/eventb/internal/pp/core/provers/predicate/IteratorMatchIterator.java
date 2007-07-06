/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.predicate;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.PredicateDescriptor;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.ResetIterator;

public class IteratorMatchIterator implements IMatchIterator {
	// this class has a state

	private ResetIterator<Clause> nonUnitClausesIterator;
	
	public IteratorMatchIterator(ResetIterator<Clause> nonUnitClausesIterator) {
		this.nonUnitClausesIterator = nonUnitClausesIterator;
	}

	// returns the same instance all the time
	public Iterator<Clause> iterator(PredicateDescriptor predicate) {
		nonUnitClausesIterator.reset();
		return new NiceIterator(predicate, nonUnitClausesIterator);
	}

	private static class NiceIterator extends ConditionIterator<Clause> {
		private PredicateDescriptor unit;
		
		NiceIterator(PredicateDescriptor unit, Iterator<Clause> iterator) {
			super(iterator);

			this.unit = unit;
		}

		@Override
		public boolean isSelected(Clause element) {
			return element.matches(unit);
		}
	}
}