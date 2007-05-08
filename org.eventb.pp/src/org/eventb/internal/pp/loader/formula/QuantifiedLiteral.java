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
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class QuantifiedLiteral extends AbstractLabelizableFormula<QuantifiedDescriptor> {
	private boolean isForall;
	private ISignedFormula child;
	private List<TermSignature> definingTerms;
	private int startOffset, endOffset;
	
	public QuantifiedLiteral (boolean isForall, 
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
	
	private List<TermSignature> transform(List<TermSignature> termList, TermVisitorContext flags, VariableTable table) {
		List<TermSignature> result = new ArrayList<TermSignature>();
		for (TermSignature sig : definingTerms) {
			sig.appendTermFromTermList(termList, result, startOffset, endOffset);
		}
		assert termList.isEmpty();
		return result;
	}

	@Override
	protected boolean isLabelizable(LabelManager manager, TermVisitorContext context) {
		return (context.isQuantified?!context.isForall:false)
			|| (manager.isGettingDefinitions()&&context.isQuantified);
	}

	public List<List<ILiteral<?>>> getDefinitionClauses(List<TermSignature> termList, LabelManager manager, List<List<ILiteral<?>>> prefix, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
//		TermVisitorContext newContext = new TermVisitorContext();
//		
//		newContext.isForall = context.isPositive?isForall:!isForall;
//		newContext.isPositive = context.isPositive;
//		newContext.startOffset = startOffset;
//		newContext.endOffset = endOffset;
//		
//		newContext.isQuantified = true;
//		if (manager.isGettingDefinitions() || !context.isForall) manager.setForceLabelize(true);
		
		List<TermSignature> copy = new ArrayList<TermSignature>(termList.size());
		copy.addAll(termList);
		List<TermSignature> quantifiedTermList = transform(copy, context, table);
		return child.getClauses(quantifiedTermList, manager, prefix, table, context, bool);
	}
	
	public ILiteral<?> getLiteral(List<TermSignature> terms, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
		ILiteral<?> result = getLiteral(descriptor.getIndex(), terms, context, table);
		return result;
	}
	

	public void getFinalClauses(Collection<IClause> clauses, LabelManager manager, ClauseFactory factory, BooleanEqualityTable bool, VariableTable table, IVariableContext variableContext, boolean positive) {
		if (positive) {
			ClauseBuilder.debug("----------------");
			ClauseBuilder.debug("Positive definition:");
			getFinalClausesHelper(manager, clauses, factory, false, true, bool, table, variableContext);
		} else {
			ClauseBuilder.debug("----------------");
			ClauseBuilder.debug("Negative definition:");
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
