/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

// LITERALS are immutable !
public interface ILiteral<T extends ILiteral<T>> {

	public List<Term> getTerms();
	
//	public ILiteralDescriptor getDescriptor();
	
//	public boolean isPositive();
	
	public T getInverse();
	
	public T substitute(Map<AbstractVariable, ? extends Term> map);
	
	
	// TODO shouldnt'it be somewhere else ?
	public T getCopyWithNewVariables(IVariableContext context, 
			HashMap<AbstractVariable, AbstractVariable> substitutionsMap);
	
	
	// true if one of the term is quantified
	public boolean isQuantified();
	
	// true if is quantified and all quantified terms are forall
	public boolean isForall();
	
	// true if all terms are constant or quantified
	public boolean isConstant();
	
	public String toString(HashMap<Variable, String> variableMap);

	
	
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map);
	
	public int hashCodeWithDifferentVariables();
}
