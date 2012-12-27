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

import org.eventb.internal.pp.loader.formula.ArithmeticFormula.Type;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableHolder;
import org.eventb.internal.pp.loader.predicate.IContext;

/**
 * Arithmetic literals are uniquely identified by their type and
 * defining terms. Defining terms are terms where all variables and constants
 * are replaced by a {@link VariableHolder} as given by {@link TermSignature#getSimpleTerm(List)}.
 *
 * @author François Terrier
 *
 */
public class ArithmeticKey extends SymbolKey<ArithmeticDescriptor> {

	private List<TermSignature> definingTerms;
	private Type type;
	
	public ArithmeticKey(List<TermSignature> definingTerms, Type type) {
		this.definingTerms = definingTerms;
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return definingTerms.hashCode()*3+type.hashCode(); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArithmeticKey) {
			ArithmeticKey temp = (ArithmeticKey) obj;
			return /* index == temp.index && */ definingTerms.equals(temp.definingTerms)
				&& type == temp.type;
		}
		return false;
	}

	@Override
	public String toString() {
		return "A "+definingTerms;
	}

	@Override
	public ArithmeticDescriptor newDescriptor(IContext context) {
		return new ArithmeticDescriptor(context);
	}

}
