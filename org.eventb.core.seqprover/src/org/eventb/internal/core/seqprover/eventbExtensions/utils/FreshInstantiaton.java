/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.utils;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * Permits to instantiate the bound identifiers of a quantified predicate with
 * fresh free identifiers.
 * 
 * @author Laurent Voisin
 */
public class FreshInstantiaton {

	private final FreeIdentifier[] freeIdents;
	private final Predicate result;

	/**
	 * Instantiates the given predicate with free identifiers that are fresh in
	 * the given type environment.
	 * 
	 * @param predicate
	 *            the predicate to instantiate
	 * @param initialTypenv
	 *            the type environment to consider for freshness
	 */
	public FreshInstantiaton(QuantifiedPredicate predicate,
			ITypeEnvironment initialTypenv) {
		final ITypeEnvironmentBuilder typenv = initialTypenv.makeBuilder();
		final BoundIdentDecl[] boundIdentDecls = predicate.getBoundIdentDecls();
		this.freeIdents = typenv.makeFreshIdentifiers(boundIdentDecls);
		final FormulaFactory ff = typenv.getFormulaFactory();
		this.result = predicate.instantiate(freeIdents, ff);
	}

	/**
	 * Returns the result of this instantiation.
	 * 
	 * @return the instantiated predicate
	 */
	public Predicate getResult() {
		return result;
	}

	/**
	 * Returns the free identifiers created by this instantiation.
	 * 
	 * @return the fresh free identifiers
	 */
	public FreeIdentifier[] getFreshIdentifiers() {
		return freeIdents;
	}

}
