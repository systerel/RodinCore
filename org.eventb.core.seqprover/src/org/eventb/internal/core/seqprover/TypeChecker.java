/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;

/**
 * Encapsulates all the verifications to be made to ensure that a rule can
 * safely be applied to a sequent in terms of well-formedness and type-checking.
 * 
 * TODO Rodin 3.0: Take advantage of improved AST library
 * 
 * @author Laurent Voisin
 */
public class TypeChecker {

	private ISealedTypeEnvironment typenv;
	private boolean typenvChanged;

	// True if added identifiers were indeed fresh
	private boolean addedIdentsAreFresh = true;

	// True if a type-check error was detected
	private boolean typeCheckError = false;

	public TypeChecker(ISealedTypeEnvironment typenv) {
		this.typenv = typenv;
	}

	public final void addIdents(FreeIdentifier[] idents) {
		if (idents == null || idents.length == 0)
			return;
		if (!typenvChanged) {
			typenvChanged = true;
		}
		ITypeEnvironmentBuilder tEnv = this.typenv.makeBuilder();
		for (FreeIdentifier ident : idents) {
			addIdent(ident,tEnv);
		}
		typenv = tEnv.makeSnapshot();
	}

	private void addIdent(FreeIdentifier ident, ITypeEnvironmentBuilder tEnv) {
		final String name = ident.getName();
		final Type type = ident.getType();
		if (type == null) {
			ProverChecks.checkFailure(" Identifier " + name
					+ " is not type checked.");
		}
		final Type knownType = tEnv.getType(name);
		if (knownType == null) {
			tEnv.add(ident);
			return;
		}
		ProverChecks.checkFailure(" Identifier " + name
				+ " is not fresh in type environment " + tEnv);
		addedIdentsAreFresh = false;
		if (!knownType.equals(ident.getType())) {
			ProverChecks.checkFailure(" Free Identifier " + name + " of type "
					+ type + " is incompatible with the type environment "
					+ tEnv);
			typeCheckError = true;
		}
	}

	public final ISealedTypeEnvironment getTypeEnvironment() {
		return typenv;
	}

	public final void checkFormulas(Iterable<? extends Formula<?>> formulas) {
		if (formulas == null) {
			return;
		}
		for (Formula<?> formula : formulas) {
			checkFormula(formula);
		}
	}

	public final void checkFormulaMaybeNull(Formula<?> formula) {
		if (formula == null)
			return;
		checkFormula(formula);
	}

	public final void checkFormula(Formula<?> formula) {
		if (!formula.isWellFormed()) {
			ProverChecks.checkFailure(" Formula " + formula
					+ " is not well formed.");
			typeCheckError = true;
			return;
		}
		if (!formula.isTypeChecked()) {
			ProverChecks.checkFailure(" Formula " + formula
					+ " is not type checked.");
			typeCheckError = true;
			return;
		}
		for (FreeIdentifier ident : formula.getFreeIdentifiers()) {
			checkIdent(ident);
		}
		// FIXME also check for carrier sets used for typing somewhere in the
		// formula (free or bound identifier, empty set, etc.)
	}

	private void checkIdent(FreeIdentifier ident) {
		if (typenv.contains(ident)) {
			// OK, fast exit
			return;
		}
		final String name = ident.getName();
		final Type type = ident.getType();
		final Type knownType = typenv.getType(name);
		if (knownType == null) {
			ProverChecks.checkFailure(" Free Identifier " + name + " of type "
					+ type + " is not defined in the type environment "
					+ typenv);
			typeCheckError = true;
			return;
		}
		if (!knownType.equals(type)) {
			ProverChecks.checkFailure(" Free Identifier " + name + " of type "
					+ type + " is incompatible with the type environment "
					+ typenv);
			typeCheckError = true;
			return;
		}
	}

	public final boolean hasNewTypeEnvironment() {
		return typenvChanged;
	}

	public final boolean areAddedIdentsFresh() {
		return addedIdentsAreFresh;
	}

	public final boolean hasTypeCheckError() {
		return typeCheckError;
	}

}
