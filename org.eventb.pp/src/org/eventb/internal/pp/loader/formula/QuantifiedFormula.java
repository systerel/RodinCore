/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class QuantifiedFormula extends AbstractLabelizableFormula<QuantifiedDescriptor> {
	private boolean isForall;
	private ISignedFormula child;
	private List<TermSignature> definingTerms;
	private int startOffset, endOffset;
	
	public QuantifiedFormula (boolean isForall, 
			ISignedFormula child, List<TermSignature> definingTerms, List<TermSignature> instanceTerms,
			QuantifiedDescriptor descriptor,
			int startOffset, int endOffset) {
		super (instanceTerms, descriptor);
		this.child = child;
		this.isForall = isForall;
		this.definingTerms = definingTerms;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}
	
	
	public boolean isForall() {
		return isForall;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append(isForall?"∀ ":"∃ ");
		str.append("["+startOffset+"-"+endOffset+"]");
		str.append(descriptor.toString());
		return str.toString();
	}
 
	@Override
	public String getStringDeps() {
		StringBuffer str = new StringBuffer();
		str.append("["+child.toString()+"] ");
		return str.toString();
	}

	public boolean isPositive() {
		return child.isPositive();
	}
	
	private List<TermSignature> transform(List<TermSignature> termList) {
		List<TermSignature> result = new ArrayList<TermSignature>();
		List<TermSignature> copy = new ArrayList<TermSignature>(termList.size());
		copy.addAll(termList);
		for (TermSignature sig : definingTerms) {
			sig.appendTermFromTermList(copy, result, startOffset, endOffset);
		}
		assert copy.isEmpty();
		return result;
	}

	@Override
	protected boolean isLabelizable(LabelManager manager, TermVisitorContext context) {
		return (context.isQuantified?!context.isForall:false)
			|| (manager.isGettingDefinitions()&&context.isQuantified);
	}

	public List<List<Literal<?,?>>> getDefinitionClauses(List<TermSignature> termList, LabelManager manager, List<List<Literal<?,?>>> prefix, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
//		TermVisitorContext newContext = new TermVisitorContext();
//		
//		newContext.isForall = context.isPositive?isForall:!isForall;
//		newContext.isPositive = context.isPositive;
//		newContext.startOffset = startOffset;
//		newContext.endOffset = endOffset;
//		
//		newContext.isQuantified = true;
//		if (manager.isGettingDefinitions() || !context.isForall) manager.setForceLabelize(true);

		List<TermSignature> quantifiedTermList = transform(termList);
		return child.getClauses(quantifiedTermList, manager, prefix, table, context, bool);
	}
	
	public Literal<?,?> getLiteral(List<TermSignature> terms, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		Literal<?,?> result = getLiteral(descriptor.getIndex(), terms, context, table);
		return result;
	}
	

	public void getFinalClauses(Collection<Clause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable table, IVariableContext variableContext, boolean positive) {
		if (positive) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Positive definition:");
			getFinalClausesHelper(manager, clauses, factory, false, true, bool, table, variableContext);
		} else {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("----------------");
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Negative definition:");
			getFinalClausesHelper(manager, clauses, factory, true, false, bool, table, variableContext);
		}
	}

	public void split() {
		child.split();
	}

	public TermVisitorContext getNewContext(TermVisitorContext context) {
		TermVisitorContext newContext = new TermVisitorContext(context.isEquivalence);
		
		newContext.isForall = context.isPositive?isForall:!isForall;
		newContext.isPositive = context.isPositive;
		newContext.startOffset = startOffset;
		newContext.endOffset = endOffset;
		newContext.isQuantified = true;
		
		return newContext;
		
//		child.setFlags(context);
//		context.isQuantified = true;
//		context.isForall = isForall;
//		context.quantifierOffset = lastQuantifiedOffset;
	}

	
	public String toTreeForm(String prefix) {
		StringBuilder str = new StringBuilder();
		str.append(toString()+definingTerms.toString()+getTerms().toString()+"\n");
		str.append(child.toTreeForm(prefix+"  "));
		return str.toString();
	}


	public boolean isEquivalence() {
		return false;
	}


	public boolean hasEquivalenceFirst() {
		return child.hasEquivalenceFirst();
	}

}
