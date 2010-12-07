/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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

import org.eventb.internal.pp.core.elements.AtomicPredicateLiteral;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.PredicateTable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.VariableTable;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public abstract class AbstractLabelizableFormula<T extends IndexedDescriptor>
		extends AbstractFormula<T> {

	public AbstractLabelizableFormula(List<TermSignature> terms, T descriptor) {
		super(terms, descriptor);
	}
	
	final public ClauseResult getFinalClauses(LabelManager manager, BooleanEqualityTable bool, VariableTable table, PredicateTable predicateTable, boolean positive)  {
		ClauseContext context = new ClauseContext(table,bool,predicateTable);
		if (isEquivalence()) context.incrementEquivalenceCount();
		// positive part of label
		context.setPositive(isEquivalence()?true:positive);
		ClauseContext newContext1 = new ClauseContext(table,bool,predicateTable);
		setClauseContextProperties(context, newContext1);
		ClauseResult positiveLiterals = getDefinitionClauses(descriptor.getUnifiedResults(), manager, new ClauseResult(), newContext1);
		
		context.setPositive(isEquivalence()?true:!positive);
		ClauseContext newContext2 = new ClauseContext(table,bool,predicateTable);
		setClauseContextProperties(context, newContext2);
		Literal<?, ?> posLiteral = getLabelPredicate(descriptor.getUnifiedResults(), newContext2);
		
		positiveLiterals.prefixLiteralToAllLists(posLiteral);
		positiveLiterals.setEquivalence(isEquivalence());
		return positiveLiterals;
	}
	
	@Override
	final ClauseResult getClauses(List<TermSignature> termList, LabelManager manager, ClauseResult prefix, ClauseContext context) {
		if (ClauseBuilder.DEBUG) ClauseBuilder.debugEnter(this);
		ClauseResult result;
		if (manager.hasLabel(this)) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Manager contains label for " +this );
			Literal<?, ?> literal = getLabelPredicate(termList, context);
			prefix.addLiteralToAllLists(literal);
			result = prefix;
		} else {
			ClauseContext newContext = new ClauseContext(context.getVariableTable(),context.getBooleanTable(),context.getPredicateTable());
			setClauseContextProperties(context, newContext);
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug(this + " can be simplified");
			result = getDefinitionClauses(termList, manager, prefix, newContext);
		}
		if (ClauseBuilder.DEBUG) ClauseBuilder.debugExit(this);
		return result;
	}
	
	abstract void setClauseContextProperties(AbstractContext context, ClauseContext newContext);
	
	abstract ClauseResult getDefinitionClauses(List<TermSignature> termList, LabelManager manager, ClauseResult prefix, ClauseContext context);
	
	@Override
	final Literal<?, ?> getLabelPredicate(List<TermSignature> terms, ClauseContext context) {
		List<TermSignature> newList = descriptor.getSimplifiedList(terms);
		PredicateLiteralDescriptor predicateDescriptor =
			getPredicateDescriptor(context.getPredicateTable(),
			descriptor.getIndex(), newList.size(), terms.size(), true, false, getSortList(newList), null);
		return AbstractLabelizableFormula.getLabelPredicateHelper(predicateDescriptor, newList, context);
	}
	
	boolean isEquivalence() {
		return false;
	}
	
	protected static Literal<?, ?> getLabelPredicateHelper(PredicateLiteralDescriptor descriptor, List<TermSignature> terms, ClauseContext context) {
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Simplified term list for " + descriptor + " is: " + terms);
		Literal<?, ?> result = getPredicateLiteral(descriptor, context, terms);
		if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Creating literal from " + descriptor + ": " + result);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Literal<?, ?> getPredicateLiteral(PredicateLiteralDescriptor descriptor, ClauseContext context, List<TermSignature> newList) {
		Literal<?, ?> result;
		if (newList.size() == 0) {
			result = new AtomicPredicateLiteral(descriptor, context.isPositive());
		} 
		else {
			List<Term> newTerms = getTermsFromTermSignature(newList, context);
			result = new ComplexPredicateLiteral(descriptor, context.isPositive(), (List)newTerms);
		}
		return result;
	}
	
}
