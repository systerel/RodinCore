/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
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
import org.eventb.core.ast.Type;

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

	/**
	 * Generates a fresh free identifier for the given type environment.
	 * 
	 * @param typeEnv
	 *            a given type environment
	 * @param baseName
	 *            a base name for the resulting identifier
	 * @param type
	 *            the type of the resulting free identifier
	 * @return a fresh free identifier
	 */
	public static FreeIdentifier genFreshFreeIdent(ITypeEnvironment typeEnv,
			String baseName, Type type) {
		final FormulaFactory ff = typeEnv.getFormulaFactory();
		final BoundIdentDecl bid = ff.makeBoundIdentDecl(baseName, null, type);
		final ITypeEnvironmentBuilder te = typeEnv.makeBuilder();
		final FreeIdentifier[] idents = te
				.makeFreshIdentifiers(new BoundIdentDecl[] { bid });
		return idents[0];
	}
}
