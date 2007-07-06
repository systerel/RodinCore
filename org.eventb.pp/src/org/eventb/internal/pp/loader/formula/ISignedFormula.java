/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * Basic interface for signed formulas.
 *
 * @author François Terrier
 */
public interface ISignedFormula {

	/**
	 * Returns <code>true</code> if this signature is positive,
	 * <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this signature is positive,
	 * <code>false</code> otherwise
	 */
	public boolean isPositive();
	
//    /**
//     * This method returns the clauses associated to a normalized formula,
//     * with the given terms as parameters.
//     *
//     * @param terms the associated list of terms
//     * @param manager the label manager
//     * @param table TODO
//     * @param flags TODO
//     * @return the corresponding unit literal or <code>null</code> if 
//     * none exists.
//     */
//	public List<List<Literal>> getClauses(List<TermSignature> terms, LabelManager manager, VariableTable table);
	
	public void switchSign();
	
	public ISubFormula<?> getFormula();
	
	
//	 PUBLIC used by clausebuilder
	// - when called on a labelizable formula, this method uses unified term list
	// - when called on a signed formula, it uses terms recorded in the formula instance
	public void getFinalClauses(Collection<Clause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable table, IVariableContext context, IOrigin origin);

	public List<List<Literal<?,?>>> getClauses(List<TermSignature> termList, LabelManager manager, List<List<Literal<?,?>>> prefix, VariableTable table, TermVisitorContext flags, BooleanEqualityTable bool);

	public void split();
	
	public int getIndexSize();

	public boolean hasEquivalenceFirst();
	
//	public void setFlags(TermVisitorContext context);
	
	
	public String toTreeForm(String prefix);
}
