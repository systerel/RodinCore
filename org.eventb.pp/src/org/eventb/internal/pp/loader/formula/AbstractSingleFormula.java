/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.LabelManager;
import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * Abstract base class for a single formula. i.e. a literal.
 *
 * @author François Terrier
 *
 * @param <T>
 */
public abstract class AbstractSingleFormula<T extends LiteralDescriptor> extends AbstractFormula<T> {

	public AbstractSingleFormula(List<TermSignature> terms, T descriptor) {
		super(terms, descriptor);
	}

	@Override
	ClauseResult getClauses(List<TermSignature> termList, LabelManager manager,
			ClauseResult prefix, ClauseContext context) {
		if (ClauseBuilder.DEBUG) ClauseBuilder.debugEnter(this);
		Literal<?, ?> literal = getLabelPredicate(termList, context);
		prefix.addLiteralToAllLists(literal);
		if (ClauseBuilder.DEBUG) ClauseBuilder.debugExit(this);
		return prefix;
	}

	@Override
	String toTreeForm(String prefix) {
		return toString() + getTerms().toString();
	}

	@Override
	protected boolean getContextAndSetLabels(LabelContext context, LabelManager manager) {
		return false;
	}
	
}
