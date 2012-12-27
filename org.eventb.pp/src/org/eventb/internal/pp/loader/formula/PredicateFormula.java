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

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class PredicateFormula extends AbstractSingleFormula<PredicateDescriptor> {

	private final boolean isMembership;

	public PredicateFormula(List<TermSignature> terms,
			PredicateDescriptor descriptor, boolean isMembership) {
		super(terms, descriptor);
		this.isMembership = isMembership;
	}
	 
	@Override
	Literal<?, ?> getLabelPredicate(List<TermSignature> terms, ClauseContext context) {
		List<TermSignature> newList = descriptor.getSimplifiedList(terms);
		PredicateLiteralDescriptor predicateDescriptor =
			getPredicateDescriptor(context.getPredicateTable(),
			descriptor.getIndex(), newList.size(), terms.size(), false, isMembership, getSortList(newList), descriptor.getSort());
		return AbstractLabelizableFormula.getLabelPredicateHelper(predicateDescriptor, newList, context);
	}
	
	@Override
	void split() {
		return;
	}
}
