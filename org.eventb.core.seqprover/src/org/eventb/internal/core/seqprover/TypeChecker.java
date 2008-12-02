/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
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
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Encapsulates all the verifications to be made to ensure that a rule can
 * safely be applied to a sequent in terms of well-formedness and type-checking.
 * 
 * @author Laurent Voisin
 */
public class TypeChecker {

	private ITypeEnvironment typenv;
	private boolean typenvChanged;

	// True if added identifiers were indeed fresh
	private boolean addedIdentsAreFresh = true;

	// True if a type-check error was detected
	private boolean typeCheckError = false;

	public TypeChecker(ITypeEnvironment typenv) {
		this.typenv = typenv;
	}

	public final void addIdents(FreeIdentifier[] idents) {
		if (idents == null || idents.length == 0)
			return;
		if (!typenvChanged) {
			typenvChanged = true;
			typenv = typenv.clone();
		}
		for (FreeIdentifier ident : idents) {
			addIdent(ident);
		}
	}

	private void addIdent(FreeIdentifier ident) {
		final String name = ident.getName();
		final Type type = ident.getType();
		if (type == null) {
			ProverChecks.checkFailure(" Identifier " + name
					+ " is not type checked.");
		}
		final Type knownType = typenv.getType(name);
		if (knownType == null) {
			typenv.add(ident);
			return;
		}
		ProverChecks.checkFailure(" Identifier " + name
				+ " is not fresh in type environment " + typenv);
		addedIdentsAreFresh = false;
		if (!knownType.equals(ident.getType())) {
			ProverChecks.checkFailure(" Free Identifier " + name + " of type "
					+ type + " is incompatible with the type environment "
					+ typenv);
			typeCheckError = true;
		}
	}

	public final ITypeEnvironment getTypeEnvironment() {
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
