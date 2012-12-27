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
package org.eventb.internal.pp.loader.formula.key;

import java.util.List;

import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.descriptor.QuantifiedDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableHolder;
import org.eventb.internal.pp.loader.predicate.IContext;

/**
 * This class represents an entry for a quantified predicate of the form 
 * {∀,∃}x,y,z·Lstu, where x,y,z are variables and s,t,u are terms. It serves
 * as a key in the quantified predicate symbol table. If two {@link QuantifiedLiteralKey}
 * are equal, they refer to the same quantified descriptor in the table.
 * <p>
 * Quantified literals are uniquely identified by the formula that they range over
 * and the defining terms inside this formula. Defining terms are terms where all unquantifed
 * variables are replaced by a {@link VariableHolder} as given by method 
 * {@link TermSignature#getUnquantifiedTerm(int, int, List)}.
 *
 * @author François Terrier
 */
public class QuantifiedLiteralKey extends SymbolKey<QuantifiedDescriptor> {

	private boolean isForall;
	private SignedFormula<?> signature;
	private List<TermSignature> terms;

	public QuantifiedLiteralKey(SignedFormula<?> signature, List<TermSignature> terms, boolean isForall) {
		this.isForall = isForall;
		this.signature = signature;
		this.terms = terms;
	}
	
	/**
	 * Returns the signed signature for this literal.
	 * 
	 * @return the signed signature for this literal
	 */
	public SignedFormula<?> getSignature() {
		return signature;
	}
	
	public List<TermSignature> getTerms() {
		return terms;
	}
	
	public boolean isForall() {
		return isForall;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof QuantifiedLiteralKey) {
			QuantifiedLiteralKey temp = (QuantifiedLiteralKey) obj;
			return signature.equals(temp.signature) && terms.equals(temp.terms)
			&& isForall == temp.isForall;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (signature.hashCode() * 31 + terms.hashCode()) * 2
		+ (isForall?1:0);
	}
	
	@Override
	public String toString() {
		return (isForall?"∀":"∃")+" "+signature+terms;
	}

	@Override
	public QuantifiedDescriptor newDescriptor(IContext context) {
		return new QuantifiedDescriptor(context, context.getNextLiteralIdentifier()/*,getTerms()*/);
	}
	
}
