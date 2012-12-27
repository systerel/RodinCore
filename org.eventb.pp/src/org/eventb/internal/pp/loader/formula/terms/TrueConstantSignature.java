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
package org.eventb.internal.pp.loader.formula.terms;

import org.eventb.internal.pp.core.elements.Sort;

public class TrueConstantSignature extends ConstantSignature {

	public TrueConstantSignature(Sort sort) {
		super("TRUE", sort);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof TrueConstantSignature) {
			TrueConstantSignature temp = (TrueConstantSignature) obj;
			return super.equals(temp);
		}
		return false;
	}

}
