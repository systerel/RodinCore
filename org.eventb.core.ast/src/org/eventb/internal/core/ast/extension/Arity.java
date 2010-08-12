/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IArity;
import org.eventb.core.ast.extension.IExtensionKind;

/**
 * Arity of an operator.
 * <p>
 * Note: for N_ARY arity, select MULTARY_1 then implement/override
 * {@link IExtensionKind#checkPreconditions(Expression[], Predicate[])} to
 * check the arity for the desired n.
 * </p>
 */
public class Arity implements IArity {
	private final int min;
	private final int max;
	
	public Arity(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public int getMin() {
		return min;
	}
	
	@Override
	public int getMax() {
		return max;
	}
	
	@Override
	public boolean check(int nbArgs) {
		return min <= nbArgs && nbArgs <= max;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + max;
		result = prime * result + min;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Arity)) {
			return false;
		}
		Arity other = (Arity) obj;
		if (max != other.max) {
			return false;
		}
		if (min != other.min) {
			return false;
		}
		return true;
	}
}