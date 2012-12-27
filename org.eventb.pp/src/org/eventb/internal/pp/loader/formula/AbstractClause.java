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

import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.formula.descriptor.IndexedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.predicate.IIntermediateResult;

public abstract class AbstractClause<T extends IndexedDescriptor> extends AbstractLabelizableFormula<T> {

	protected List<SignedFormula<?>> children;
	
	public AbstractClause(List<SignedFormula<?>> children,
			List<TermSignature> terms, T descriptor) {
		super(terms,descriptor);
		this.children = children;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public String getStringDeps() {
		StringBuffer str = new StringBuffer();
		for (SignedFormula<?> child : children) {
			str.append("["+child.toString()+"] ");
		}
		return str.toString();
	}
	
	public List<SignedFormula<?>> getChildren() {
		return children;
	}
	
	@Override
	final void split() {
		for (SignedFormula<?> child : children) {
			child.split();
		}
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>(getLiteralDescriptor().getResults());
		for (int i = 0; i < children.size(); i++) {
			for (IIntermediateResult current : getLiteralDescriptor().getResults()) {
				if (!contains(current, children.get(i).getFormula().getLiteralDescriptor().getResults(), i)) result.remove(current);
			}
		}
		if (result.size() != descriptor.getResults().size()) {
			if (ClauseBuilder.DEBUG) ClauseBuilder.debug("Splitting "+this+", terms remaining: "+result.toString());
		}
		descriptor = getNewDescriptor(result, descriptor.getIndex());	
	}
	
	abstract T getNewDescriptor(List<IIntermediateResult> result, int index);
	
	private boolean contains(IIntermediateResult original, List<IIntermediateResult> child, int position) {
		for (IIntermediateResult result : child) {
			if (original.getResultList().get(position).equals(result)) return true;
		}
		return false;
	}
	
	@Override
	String toTreeForm(String prefix) {
		StringBuilder str = new StringBuilder();
		str.append(toString()+getTerms()+"\n");
		for (SignedFormula<?> child : children) {
			str.append(child.toTreeForm(prefix+" ")+"\n");
		}
		str.deleteCharAt(str.length()-1);
		return str.toString();
	}
	
	abstract void setContextProperties(AbstractContext context, AbstractContext newContext);		
	
	@Override
	void setClauseContextProperties(AbstractContext context, ClauseContext newContext) {
		setContextProperties(context, newContext);
	}
	
}