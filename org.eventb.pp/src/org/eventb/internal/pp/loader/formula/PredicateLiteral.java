/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.PredicateDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class PredicateLiteral extends AbstractSingleFormula<PredicateDescriptor> {

	public PredicateLiteral(List<TermSignature> terms, PredicateDescriptor descriptor) {
		super(terms,descriptor);
	}
	 
	public ILiteral<?> getLiteral(List<TermSignature> terms, TermVisitorContext context, VariableTable table, BooleanEqualityTable bool) {
        ILiteral<?> result = getLiteral(descriptor.getIndex(), terms, context, table);
        return result;
	}

	public boolean hasEquivalenceFirst() {
		return false;
	}
}
