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
package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This represents a signed formula. It does not have an index list.
 * 
 * @author François Terrier
 * 
 */
public class SignedFormula<T extends LiteralDescriptor> {
	// TODO voir les assert

	private final AbstractFormula<T> child;
	private boolean isPositive;

	public SignedFormula(AbstractFormula<T> child, boolean isPositive) {

		this.child = child;
		this.isPositive = isPositive;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof SignedFormula) {
			SignedFormula<?> temp = (SignedFormula<?>) obj;
			return isPositive == temp.isPositive && child.equals(temp.child);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return child.hashCode() * 2 + (isPositive ? 1 : 0);
	}

	public boolean isPositive() {
		return isPositive;
	}

	ClauseResult getClauses(List<TermSignature> termList,
			LabelManager manager, ClauseResult prefix,
			ClauseContext context) {
		if (!isPositive) context.setPositive(!context.isPositive());
		ClauseResult result = child.getClauses(termList, manager, prefix, context);
		if (!isPositive) context.setPositive(!context.isPositive());
		return result;
	}

	int getIndexSize() {
		return child.getIndexSize();
	}

	public AbstractFormula<?> getFormula() {
		return child;
	}

	public void negate() {
		if (child instanceof QuantifiedFormula) {
			assert isPositive;
			((QuantifiedFormula)child).switchSign();
		}
		else isPositive = !isPositive;
	}

	public ClauseResult getFinalClauses(LabelManager manager, BooleanEqualityTable bool,
			VariableTable table, PredicateTable predicateTable) {
		// 1 we first split the formula
		child.split();
		// 2 we then set the labels
		LabelContext visitor = new LabelContext();
		boolean isEquivalence = getContextAndSetLabels(visitor, manager);
		// 3 we get the literals
		ClauseContext flags = new ClauseContext(table,bool,predicateTable);
		if (isEquivalence) flags.incrementEquivalenceCount();
		ClauseResult result = getClauses(child.getTerms(), manager, new ClauseResult(), flags);
		result.setEquivalence(isEquivalence);
		return result;
	}
	
	void split() {
		child.split();
	}

	@Override
	public String toString() {
		return (isPositive ? "" : "not ") + child.toString();
	}

	boolean getContextAndSetLabels(LabelContext context, LabelManager manager) {
		if (!isPositive) {
			context.setPositive(!context.isPositive());
		}
		return child.getContextAndSetLabels(context, manager);
	}

	public String toTreeForm(String prefix) {
		return prefix + (isPositive ? "" : "not ") + child.toTreeForm(prefix);
	}

}