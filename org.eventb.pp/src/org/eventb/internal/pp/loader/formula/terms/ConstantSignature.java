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
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.formula.ClauseContext;

/**
 * This class represents a constant signature.
 * 
 * @author Francois Terrier
 *
 */
public class ConstantSignature extends AbstractConstantSignature {

	private final String name;
	
	public ConstantSignature(String name, Sort sort) {
		super(sort);
		
		this.name = name;
	}
	
	@Override
	public TermSignature deepCopy() {
		return new ConstantSignature(name, sort);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof ConstantSignature) {
			ConstantSignature temp = (ConstantSignature) obj;
			return name.equals(temp.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ("#"+name).hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Term getTerm(ClauseContext context) {
		return context.getVariableTable().getConstant(name, sort);
	}

}
