/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
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
public class SignedFormula<T extends LiteralDescriptor> {
	// TODO voir les assert

	private AbstractFormula<T> child;
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

	List<List<Literal<?, ?>>> getClauses(List<TermSignature> termList,
			LabelManager manager, List<List<Literal<?, ?>>> prefix,
			VariableTable table, TermVisitorContext flags,
			BooleanEqualityTable bool) {
		if (!isPositive)
			flags.isPositive = !flags.isPositive;
		List<List<Literal<?, ?>>> result = child.getClauses(termList, manager,
				prefix, flags, table, bool);
		if (!isPositive)
			flags.isPositive = !flags.isPositive;
		return result;
	}

	int getIndexSize() {
		return child.getIndexSize();
	}

	public AbstractFormula<?> getFormula() {
		return child;
	}

	public void switchSign() {
		if (child instanceof QuantifiedFormula) {
			assert isPositive;
			((QuantifiedFormula)child).switchSign();
		}
		else isPositive = !isPositive;
	}

	public ClauseResult getFinalClauses(LabelManager manager, BooleanEqualityTable bool,
			VariableTable table) {
		// 1 we first split the formula
		child.split();
		// 2 we then set the labels
		LabelVisitor visitor = new LabelVisitor();
		visitor.isPositive = isPositive;
		boolean isEquivalence = child.getContextAndSetLabels(visitor, manager);
		
		// 3 we get the literals
		TermVisitorContext flags = new TermVisitorContext(isEquivalence);
		List<List<Literal<?, ?>>> literalLists = new ArrayList<List<Literal<?, ?>>>();
		literalLists.add(new ArrayList<Literal<?, ?>>());
		List<List<Literal<?, ?>>> result = getClauses(child.getTerms(), manager, literalLists, table, flags, bool);
		
		return new ClauseResult(isEquivalence, result);
	}

	void split() {
		child.split();
	}

	@Override
	public String toString() {
		return (isPositive ? "" : "not ") + child.toString();
	}

	boolean getContextAndSetLabels(LabelVisitor context, LabelManager manager) {
		if (!isPositive) {
			context.isPositive = !context.isPositive;
		}
		return child.getContextAndSetLabels(context, manager);
	}

	public String toTreeForm(String prefix) {
		return prefix + (isPositive ? "" : "not ") + child.toTreeForm(prefix);
	}

}