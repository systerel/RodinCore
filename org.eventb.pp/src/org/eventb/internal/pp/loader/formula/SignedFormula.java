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
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This represents a signed formula. It does not have an index list.
 *
 * @author François Terrier
 *
 */
public class SignedFormula<T extends LiteralDescriptor> implements ISignedFormula {
	//TODO voir les assert
	
	private ISubFormula<T> child;
	private boolean isPositive;
	
	public SignedFormula(ISubFormula<T> child, boolean isPositive) {
		
		this.child = child;
		this.isPositive = isPositive;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof SignedFormula) {
			SignedFormula<?> temp = (SignedFormula) obj;
			return isPositive == temp.isPositive && child.equals(temp.child);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return child.hashCode() * 2 + (isPositive?1:0);
	}

	public boolean isPositive() {
		return isPositive;
	}

	public List<List<ILiteral<?>>> getClauses(List<TermSignature> termList, LabelManager manager, List<List<ILiteral<?>>> prefix, VariableTable table, TermVisitorContext flags, BooleanEqualityTable bool) {
		if (!isPositive) flags.isPositive = !flags.isPositive;
		List<List<ILiteral<?>>> result = child.getClauses(termList, manager, prefix, flags, table, bool);
		if (!isPositive) flags.isPositive = !flags.isPositive;
		return result;
	}

	public int getIndexSize() {
		return child.getIndexSize();
	}

	public ISubFormula<?> getFormula() {
		return child;
	}

	public void switchSign() {
		isPositive = !isPositive;
	}
	
	public void getFinalClauses(Collection<IClause> clauses, LabelManager manager, 
			ClauseFactory factory, BooleanEqualityTable bool, VariableTable table, IVariableContext context, IOrigin origin) {
		TermVisitorContext flags = new TermVisitorContext(hasEquivalenceFirst());
		if (!isPositive) flags.isPositive = !flags.isPositive;
		List<List<ILiteral<?>>> result = child.getClauses(child.getTerms(), manager, table, flags, bool);
		if (!isPositive) flags.isPositive = !flags.isPositive;
		for (List<ILiteral<?>> list : result) {
			IClause clause;
			if (flags.isEquivalence && list.size() > 1) clause = factory.newEqClauseWithCopy(origin,list, context);
			else clause = factory.newDisjClauseWithCopy(origin,list, context);
			// we set the original predicate as this is not a definition
			clauses.add(clause);
			ClauseBuilder.debug("New clause: "+clause);
		}
	}

	public void split() {
		child.split();
	}

	@Override
	public String toString() {
		return (isPositive?"":"not ")+child.toString();
	}

//	public void setFlags(TermVisitorContext context) {
//		child.getNewContext(context);
//		context.isPositive = isPositive;
//	}

	public String toTreeForm(String prefix) {
		return prefix+(isPositive?"":"not ")+child.toTreeForm(prefix);
	}

	public boolean hasEquivalenceFirst() {
		return child.hasEquivalenceFirst();
	}
}
