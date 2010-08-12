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

import org.eventb.core.ast.extension.IArity;

/**
 * @author Nicolas Beauger
 *
 */
public class ArityCoverage extends Arity {

	public static final ArityCoverage NONE = new ArityCoverage(0, 0);
	public static final ArityCoverage ONE = new ArityCoverage(1, 1);
	public static final ArityCoverage TWO = new ArityCoverage(2, 2);
	public static final ArityCoverage ANY = new ArityCoverage(0, MAX_ARITY);
	public static final ArityCoverage ONE_OR_MORE = new ArityCoverage(1, MAX_ARITY);
	public static final ArityCoverage TWO_OR_MORE = new ArityCoverage(2, MAX_ARITY);
	
	
	public ArityCoverage(int min, int max) {
		super(min, max);
	}

	public boolean isDisjoint(IArity other) {
		return getMin() > other.getMax()
				|| other.getMin() > getMax();
	}
	
	public boolean contains(IArity other) {
		return getMin() <= other.getMin()
				&& other.getMax() <= getMax();
	}

	
}
