/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers;

import java.util.Iterator;

import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.search.ConditionIterator;
import org.eventb.internal.pp.core.search.ResetIterator;

public class NonUnitMatcher {
		// this class has a state
		
		private ResetIterator<IClause> nonUnitClausesIterator;
		
		public NonUnitMatcher(ResetIterator<IClause> nonUnitClausesIterator) {
			this.nonUnitClausesIterator = nonUnitClausesIterator;
		}
		
		public Iterator<IClause> iterator(IPredicate predicate, boolean state) {
			nonUnitClausesIterator.reset();
			return new NiceIterator(predicate, nonUnitClausesIterator);
		}
		
		private static class NiceIterator extends ConditionIterator<IClause> {
			private IPredicate unit;
			
			private NiceIterator(IPredicate unit, Iterator<IClause> iterator) {
				super(iterator);
				
				this.unit = unit;
			}
			
			@Override
			public boolean isSelected(IClause element) {
				return element.matches(unit);
			}
		}
	

	}