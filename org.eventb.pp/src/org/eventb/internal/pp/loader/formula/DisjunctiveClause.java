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
import org.eventb.internal.pp.loader.formula.descriptor.DisjunctiveClauseDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public class DisjunctiveClause extends AbstractClause<DisjunctiveClauseDescriptor> {
	
	public DisjunctiveClause(List<SignedFormula<?>> children,
			List<TermSignature> terms, DisjunctiveClauseDescriptor descriptor) {
		super(children,terms,descriptor);
	}
	
	@Override
	ClauseResult getDefinitionClauses(List<TermSignature> terms,
			LabelManager manager, ClauseResult prefix,
			ClauseContext context) {
		ClauseResult result;
		int start = 0;
		if (context.isPositive()) {
			for (SignedFormula<?> child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				prefix = child.getClauses(subIndex, manager, prefix, context);
				start += child.getIndexSize();
			}
			result = prefix;
		} else {
			result = new ClauseResult();
			// we split because it is a conjunction
			for (SignedFormula<?> child : children) {
				List<TermSignature> subIndex = terms.subList(start, start + child.getIndexSize());
				result.addAll(child.getClauses(subIndex, manager, prefix.getCopy(), context));
				start += child.getIndexSize();
			}
		}
		return result;
	}
	
	@Override
	DisjunctiveClauseDescriptor getNewDescriptor(List<IIntermediateResult> result, int index) {
		return new DisjunctiveClauseDescriptor(descriptor.getContext(), result, index);
	}

	private boolean isLabelizable(LabelContext context) {
		if (context.isQuantified()) {
			if (context.isPositive() && context.isPositiveLabel()) return true;
			if (!context.isPositive() && context.isNegativeLabel()) return true;
			else if (!context.isForall()) return true;
		}
		if (context.isEquivalence()) return true;
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

	private boolean getChildContextAndSetLabels(LabelContext context, LabelManager manager) {
		boolean isPositive = context.isPositive();
		for (SignedFormula<?> formula : children) {
			context.setPositive(isPositive);
			formula.getContextAndSetLabels(context, manager);
		}
		return false;
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
		newContext.setDisjunction(true);
		newContext.setQuantified(false);
	}
	
}
