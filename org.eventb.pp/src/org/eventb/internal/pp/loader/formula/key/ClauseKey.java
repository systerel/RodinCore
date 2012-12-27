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
package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;

/**
 * Clauses are uniquely identified by the formulas inside them without considering terms.
 *
 * @author François Terrier
 *
 * @param <T>
 */
public abstract class ClauseKey<T extends IndexedDescriptor> extends SymbolKey<T> {

	private List<SignedFormula<?>> signatures;
	
	public ClauseKey(List<SignedFormula<?>> signatures) {
		this.signatures = signatures;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ClauseKey) {
			ClauseKey<?> temp = (ClauseKey<?>) obj;
			return signatures.equals(temp.signatures);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return signatures.hashCode();
	}

	@Override
	public String toString() {
		return signatures.toString();
	}

}
