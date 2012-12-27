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

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class QuantifiedFormula extends AbstractLabelizableFormula<QuantifiedDescriptor> {
	private boolean isForall;
	private SignedFormula<?> child;
	private List<TermSignature> definingTerms;
	private int startOffset, endOffset;
	
	public QuantifiedFormula (boolean isForall, 
			SignedFormula<?> child, List<TermSignature> definingTerms, List<TermSignature> instanceTerms,
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
	ClauseResult getDefinitionClauses(List<TermSignature> termList, LabelManager manager, ClauseResult prefix, ClauseContext context) {
		List<TermSignature> quantifiedTermList = transform(termList);
		return child.getClauses(quantifiedTermList, manager, prefix, context);
	}
	
	@Override
	void split() {
		child.split();
	}

	private boolean isLabelizable(LabelContext context) {
		if (context.isQuantified()) {
			if (context.isPositive() && context.isPositiveLabel()) return true;
			if (!context.isPositive() && context.isNegativeLabel()) return true;
			else if (!context.isForall()) return true;
		}
		return false;
	}
	
	private void addLabels(LabelManager manager, LabelContext context, LabelContext newContext) {
		if (context.isEquivalence()) {
			manager.addLabel(this, true);
			manager.addLabel(this, false);
			newContext.setPositiveLabel(true);
			newContext.setNegativeLabel(true);
		}
		else {
			if (!context.isPositiveLabel() && !context.isNegativeLabel()) {
				manager.addLabel(this, context.isPositive());
				if (context.isPositive()) newContext.setNegativeLabel(true);
				else newContext.setPositiveLabel(true);
			}
			if (	(!context.isPositive() && context.isPositiveLabel())
				|| 	(context.isPositive() && context.isNegativeLabel())) {
				manager.addLabel(this, !context.isPositive());
				newContext.setPositiveLabel(true);
			}
			if (	(context.isPositive() && context.isPositiveLabel())
				|| 	(!context.isPositive() && context.isNegativeLabel())) {
				manager.addLabel(this, context.isPositive());
				newContext.setNegativeLabel(true);
			}
			if (context.isPositiveLabel()) newContext.setPositiveLabel(true);
			if (context.isNegativeLabel()) newContext.setNegativeLabel(true);
		}
		assert manager.hasLabel(this);
	}
	
	@Override
	boolean getContextAndSetLabels(LabelContext context, LabelManager manager) {
		LabelContext newContext = new LabelContext();
		if (isLabelizable(context)) {
			addLabels(manager, context, newContext);
		}
		setContextProperties(context, newContext);
		return child.getContextAndSetLabels(newContext, manager);
	}
	
	@Override
	String toTreeForm(String prefix) {
		StringBuilder str = new StringBuilder();
		str.append(toString()+definingTerms.toString()+getTerms().toString()+"\n");
		str.append(child.toTreeForm(prefix+"  "));
		return str.toString();
	}

	void switchSign() {
		this.isForall = !this.isForall;
		child.negate();
	}
	
	void setContextProperties(AbstractContext context, AbstractContext newContext) {
		newContext.setPositive(context.isPositive());
		newContext.setDisjunction(context.isDisjunction());
		newContext.setQuantified(true);
		newContext.setEquivalenceCount(context.getEquivalenceCount());
		newContext.setForall(context.isPositive()?isForall:!isForall);
	}

	@Override
	void setClauseContextProperties(AbstractContext context, ClauseContext newContext) {
		setContextProperties(context, newContext);
		
		newContext.setStartOffset(startOffset);
		newContext.setEndOffset(endOffset);
	}
	
}
