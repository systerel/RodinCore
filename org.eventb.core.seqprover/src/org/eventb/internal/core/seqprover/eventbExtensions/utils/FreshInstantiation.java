/*******************************************************************************
 * Copyright (c) 2012, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.utils;

import static java.lang.Math.min;

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
public class FreshInstantiation {

	private static final String[] NO_NAMES = new String[0];

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
	public FreshInstantiation(QuantifiedPredicate predicate,
			ITypeEnvironment initialTypenv) {
		this(predicate, initialTypenv, NO_NAMES);
	}

	/**
	 * Instantiates the given predicate with free identifiers that are fresh in the
	 * given type environment and based on the provided names.
	 *
	 * If not enough names are provided for all bound identifiers, the remaining
	 * fresh names will be based on the corresponding bound identifiers
	 * declarations. If too many names are provided, the extra ones will be ignored.
	 *
	 * @param predicate     the predicate to instantiate
	 * @param initialTypenv the type environment to consider for freshness
	 * @param names         the base names to use to make fresh names
	 */
	public FreshInstantiation(QuantifiedPredicate predicate, ITypeEnvironment initialTypenv, String[] names) {
		final ITypeEnvironmentBuilder typenv = initialTypenv.makeBuilder();
		final FormulaFactory ff = typenv.getFormulaFactory();
		final BoundIdentDecl[] boundIdentDecls = predicate.getBoundIdentDecls();
		for (int i = 0; i < min(boundIdentDecls.length, names.length); i++) {
			boundIdentDecls[i] = ff.makeBoundIdentDecl(names[i], boundIdentDecls[i].getSourceLocation(),
					boundIdentDecls[i].getType());
		}
		this.freeIdents = typenv.makeFreshIdentifiers(boundIdentDecls);
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
