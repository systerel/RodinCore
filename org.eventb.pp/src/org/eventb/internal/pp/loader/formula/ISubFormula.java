/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This is the basic interface for all subformulas.
 *
 * @author François Terrier
 *
 */
public interface ISubFormula<T extends LiteralDescriptor> {

//	/**
//	 * Returns the clause corresponding to this signed signature
//	 * with the given terms as parameters. Returns <code>null</code> if
//	 * this signature has no associated unit clause. If the <code>isPositive</code>
//	 * flag is set, it returns the positive version of this signature.
//	 * @param termList the associated list of terms
//	 * @param prefix TODO
//	 * @param flags TODO
//	 * @param table TODO
//	 * @return the corresponding unit literal or <code>null</code> if none exists.
//	 */
	public List<List<ILiteral<?>>> getClauses(List<TermSignature> termList, LabelManager manager, List<List<ILiteral<?>>> prefix, TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool);

	public List<List<ILiteral<?>>> getClauses(List<TermSignature> terms, LabelManager manager, VariableTable table, TermVisitorContext flags, BooleanEqualityTable bool);
	
//	/**
//	 * Returns the literal representing this formula. If it is an atomic
//	 * formula, it returns the corresponding atomic literal. If it is a 
//	 * complex formula, it returns the corresponding label.
//	 * 
//	 * For instance, if this subformula is a simple predicate (x ↦ y ∈ P), it
//	 * returns the corresponding {@link PPPredicate}.
//	 * If it is a clause Px ∨ ... ∨ Qy, it returns the corresponding label
//	 * Lx...y ⇔ Px ∨ ... ∨ Qy.
//	 * 
//	 * @param terms
//	 * @param flags TODO
//	 * @param table TODO
//	 * @return
//	 */
	public ILiteral<?> getLiteral(List<TermSignature> terms, TermVisitorContext flags, VariableTable table /*, context*/, BooleanEqualityTable bool);

    /**
     * Returns the size of the index list.
     *
     * @return the size of the index list
     */
	public int getIndexSize();
	
	public T getLiteralDescriptor();
	
	public List<TermSignature> getTerms();
	
	public void split();
	
	public TermVisitorContext getNewContext(TermVisitorContext context);
	
	public boolean hasEquivalenceFirst();
	
	public String toString();

	public String toTreeForm(String prefix);
	
}
