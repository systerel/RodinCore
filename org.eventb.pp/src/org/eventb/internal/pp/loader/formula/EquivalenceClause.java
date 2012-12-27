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

import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.formula.descriptor.EquivalenceClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class EquivalenceClause extends AbstractClause<EquivalenceClauseDescriptor> {
	
	public EquivalenceClause(List<SignedFormula<?>> children,
			List<TermSignature> terms, EquivalenceClauseDescriptor descriptor) {
		super(children,terms,descriptor);
	}

	@Override
	ClauseResult getDefinitionClauses(List<TermSignature> termList,
			LabelManager manager, ClauseResult prefix, ClauseContext context) {
		ClauseResult result;
		int start = 0;
		if (context.isPositive()) {
			for (SignedFormula<?> child : children) {
				List<TermSignature> subIndex = termList.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, context);
				start += child.getIndexSize();
			}
			result = prefix;
		}
		else {
			boolean first = true;
			for (SignedFormula<?> child : children) {
				if (!first) context.setPositive(true);
				List<TermSignature> subIndex = termList.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, context);
				start += child.getIndexSize();
				first = false;
			}
			context.setPositive(false);
			result = prefix;
		}
		return result;
	}

	@Override
	boolean isEquivalence() {
		return true;
	}
	
	@Override
	EquivalenceClauseDescriptor getNewDescriptor(List<IIntermediateResult> result, int index) {
		return new EquivalenceClauseDescriptor(descriptor.getContext(), result, index);
	}
	
	private boolean isLabelizable(LabelContext context) {
		if (context.isQuantified()) {
			if (context.isPositiveLabel() || context.isNegativeLabel()) return true;
			else if (context.getEquivalenceCount() > 0) return true;
			else if (!context.isForall()) return true;
		}
		if (context.isDisjunction()) return true;
		return false;
	}

	private void addLabels(LabelManager manager, AbstractContext context,  LabelContext newContext) {
		manager.addLabel(this,true);
		newContext.setNegativeLabel(true);
		newContext.setPositiveLabel(true);
		assert manager.hasLabel(this);
	}
	
	private boolean getChildContextAndSetLabels(LabelContext context, LabelManager manager) {
		// we continue
		boolean first = true;
		for (SignedFormula<?> child : children) {
			if (first && !context.isPositive()) context.setPositive(false);
			else context.setPositive(true);
			child.getContextAndSetLabels(context, manager);
			first = false;
		}
		return true;
	}
	
	@Override
	boolean getContextAndSetLabels(LabelContext context, LabelManager manager) {
		LabelContext newContext = new LabelContext();
		if (isLabelizable(context)) {
			addLabels(manager, context, newContext);
		}
		setContextProperties(context, newContext);
		return getChildContextAndSetLabels(newContext, manager);
	}
	
	@Override
	void setContextProperties(AbstractContext context, AbstractContext newContext) {
		newContext.setEquivalenceCount(context.getEquivalenceCount());
		newContext.setForall(context.isForall());
		newContext.setPositive(context.isPositive());
		newContext.setDisjunction(context.isDisjunction());
		newContext.setQuantified(false);
		
		newContext.incrementEquivalenceCount();
	}
	
}