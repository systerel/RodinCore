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
import org.eventb.internal.pp.core.elements.PPEquality;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.ClauseBuilder;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.EqualityDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class represents a signature for a predicate of the form I1 = I2 or I1 &ne; I2,
 * where I1,I2 are identifiers. One object of this class should exist per
 * equality or inequality for which the {@link Sort} is equal. The
 * number of index lists represents the number of times this particular
 * predicate is used in the original sequent. 
 *
 * Remark that the equality is symmetric.
 *
 * @author François Terrier
 *
 */
public class EqualityLiteral extends AbstractSingleFormula<EqualityDescriptor> {

	
	public EqualityLiteral(List<TermSignature> terms,
			EqualityDescriptor descriptor) {
		super(terms,descriptor);
	}

	public ILiteral<?> getLiteral(List<TermSignature> termList, TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		assert termList.size() == 2;
		List<Term> terms = getTermsFromTermSignature(termList, flags, table);
		ILiteral<?> result = new PPEquality(terms.get(0),terms.get(1),flags.isPositive);
		ClauseBuilder.debug("Creating literal from "+this+": "+result);
		return result;
	}

	public boolean hasEquivalenceFirst() {
		return false;
	}
}
