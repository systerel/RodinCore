/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.indexers;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.rodinp.core.indexer.IDeclaration;

public class IdentTable {

	private final Map<FreeIdentifier, IDeclaration> table;
	private final FormulaFactory ff;

	public IdentTable(FormulaFactory ff) {
		this.table = new HashMap<FreeIdentifier, IDeclaration>();
		this.ff = ff;
	}

	public void addIdents(FreeIdentifier[] idents, SymbolTable symbolTable) {
		for (FreeIdentifier ident : idents) {
			final FreeIdentifier unprimed = getUnprimed(ident, ff);
			final String name = unprimed.getName();
			final IDeclaration declaration = symbolTable.lookup(name);
			if (declaration != null) {
				put(unprimed, declaration);
			}
		}
	}

	private static FreeIdentifier getUnprimed(FreeIdentifier ident, FormulaFactory formulaFactory) {
		if (ident.isPrimed()) {
			return ident.withoutPrime(formulaFactory);
		}
		return ident;
	}

	private static FreeIdentifier getPrimed(FreeIdentifier ident, FormulaFactory formulaFactory) {
		if (!ident.isPrimed()) {
			return ident.withPrime(formulaFactory);
		}
		return ident;
	}

	private void put(FreeIdentifier ident, IDeclaration declaration) {
		table.put(ident, declaration);
	}

	// the name argument must not be empty
	public static String getUnprimedName(String name, FormulaFactory formulaFactory) {
		final FreeIdentifier ident = formulaFactory.makeFreeIdentifier(name, null);
		return getUnprimed(ident, formulaFactory).getName();
	}
	
	public static String getPrimedName(String name, FormulaFactory formulaFactory) {
		final FreeIdentifier ident = formulaFactory.makeFreeIdentifier(name, null);
		return getPrimed(ident, formulaFactory).getName();
	}
	
	public IDeclaration get(FreeIdentifier ident) {
		final FreeIdentifier unprimed = getUnprimed(ident, ff);
		return table.get(unprimed);
	}

	public boolean isEmpty() {
		return table.isEmpty();
	}

}