/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.PredicateVariable;

/**
 * An implementation of an indexed formula.
 * 
 * <p> An indexed formula contains an Event-B formula associated with an index. 
 * <p>This is particularly useful when dealing with many formulae in an associative formula that are equal 
 * (by a call to the <code>Object.equals(Object)</code>), in which case it is necessary to differentiate between them.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class IndexedFormula<F extends Formula<F>>{

	private F formula;
	private int index;
	
	public IndexedFormula(int index, F formula){
		this.index = index;
		this.formula = formula;
	}
	
	/**
	 * Returns the formula.
	 * @return the formula
	 */
	public F getFormula() {
		return formula;
	}

	/**
	 * Returns the index.
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Returns whether the formula is a variable (predicate variable or free identifier).
	 * @return whether the formula is a variable
	 */
	public boolean isVariable(){
		return formula instanceof FreeIdentifier || formula instanceof PredicateVariable;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IndexedFormula<?>)){
			return false;
		}
		if (this == obj){
			return true;
		}
		IndexedFormula<?> indexedFormula = (IndexedFormula<?>)obj;
		return indexedFormula.formula.equals(formula) && 
				indexedFormula.index == index ;
	}
	
	@Override
	public int hashCode() {
		return formula.hashCode() + 17* index;
	}
	
	@Override
	public String toString() {
		return "IndexedFormula{" + index + ":" + formula + "}";
	}
}
